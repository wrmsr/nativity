/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wrmsr.nativity.x86;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.wrmsr.nativity.x86.Ref.Entry;
import static com.wrmsr.nativity.x86.Ref.FPUFlags;
import static com.wrmsr.nativity.x86.Ref.Flags;
import static com.wrmsr.nativity.x86.Ref.Note;
import static com.wrmsr.nativity.x86.Ref.Operand;
import static com.wrmsr.nativity.x86.Ref.Syntax;
import static com.wrmsr.nativity.x86.Utils.buildChildElementListMap;
import static com.wrmsr.nativity.x86.Utils.filterOutNull;
import static com.wrmsr.nativity.x86.Utils.getAttributeOrNull;
import static com.wrmsr.nativity.x86.Utils.getBinByteAttributeOrNegativeOne;
import static com.wrmsr.nativity.x86.Utils.getByteAttributeOrNegativeOne;
import static com.wrmsr.nativity.x86.Utils.getOne;
import static com.wrmsr.nativity.x86.Utils.getOneOrThrow;
import static com.wrmsr.nativity.x86.Utils.getOneText;
import static com.wrmsr.nativity.x86.Utils.iterChildElements;
import static com.wrmsr.nativity.x86.Utils.iterChildNodes;
import static com.wrmsr.nativity.x86.Utils.parseHexByte;
import static com.wrmsr.nativity.x86.Utils.parseSeparatedHexBytes;

public class Parsing
{
    protected static Operand parseOperand(Element ele)
    {
        Operand.RegisterNumber nr = Operand.RegisterNumber.get(getAttributeOrNull(ele, "registerNumber"));
        Operand.Group group = Operand.Group.get(getAttributeOrNull(ele, "group"));
        boolean noDepend = "no".equals(getAttributeOrNull(ele, "depend"));
        boolean noDisplayed = "no".equals(getAttributeOrNull(ele, "displayed"));

        List<Node> textNodes = Lists.newArrayList(Iterables.filter(
                iterChildNodes(ele), (node) -> node.getNodeType() == Node.TEXT_NODE));
        if (textNodes.size() > 1) {
            throw new IllegalStateException();
        }
        String text = textNodes.size() > 0 ? textNodes.get(0).getTextContent() : null;

        Map<String, List<Element>> childEleListsByName = buildChildElementListMap(ele);

        String typeStr = getAttributeOrNull(ele, "type");
        String t = getOneText(childEleListsByName, "t");
        if (typeStr != null && t != null) {
            throw new IllegalStateException();
        }
        Operand.Type type = Operand.Type.get(typeStr != null ? typeStr : t);

        String addressStr = getAttributeOrNull(ele, "address");
        String a = getOneText(childEleListsByName, "a");
        if (addressStr != null && a != null) {
            throw new IllegalStateException();
        }
        Operand.Address address = Operand.Address.get(addressStr != null ? addressStr : a);

        return new Operand(
                text,
                nr,
                group,
                type,
                address,
                noDepend,
                noDisplayed
        );
    }

    protected static Syntax parseSyntax(Element ele)
    {
        if (ele == null || ele.getChildNodes().getLength() < 1) {
            return null;
        }

        Map<String, List<Element>> childEleListsByName = buildChildElementListMap(ele);

        String mnemonic = getOneOrThrow(childEleListsByName, "mnem").getTextContent();
        String modStr = getAttributeOrNull(ele, "mod");
        Syntax.Mod mod = modStr != null ? Syntax.Mod.valueOf(modStr) : null;
        List<Operand> srcs = Lists.newArrayList(filterOutNull(Iterables.transform(
                childEleListsByName.getOrDefault("src", ImmutableList.of()), Parsing::parseOperand)));
        List<Operand> dsts = Lists.newArrayList(filterOutNull(Iterables.transform(
                childEleListsByName.getOrDefault("dst", ImmutableList.of()), Parsing::parseOperand)));

        return new Syntax(
                mnemonic,
                mod,
                srcs.toArray(new Operand[0]),
                dsts.toArray(new Operand[0])
        );
    }

    protected static Note parseNote(Element ele)
    {
        if (ele == null) {
            return null;
        }
        Map<String, List<Element>> childElesByName = buildChildElementListMap(ele);
        String brief = getOneText(childElesByName, "brief");
        if (brief != null) {
            brief = brief.replaceAll("\\s+", " ");
        }
        String det = getOneText(childElesByName, "det");
        if (det != null) {
            det = det.replaceAll("\\s+", " ");
        }
        return new Note(brief, det);
    }

    protected static Pair<Entry.FlagSet<Flags>, Boolean> parseEntryFlags(Element ele)
            throws Exception
    {
        Map<String, List<Element>> childEleListsByName = buildChildElementListMap(ele);

        EnumSet<Flags> testedFlags = null;
        String testFStr = getOneText(childEleListsByName, "test_f");
        if (testFStr != null) {
            testedFlags = Flags.getSet(testFStr);
        }
        EnumSet<Flags> modifiedFlags = null;
        String modifFStr = getOneText(childEleListsByName, "modif_f");
        if (modifFStr != null) {
            modifiedFlags = Flags.getSet(modifFStr);
        }

        boolean conditionallyModifiesFlags = false;
        EnumSet<Flags> definedFlags = null;
        Element defF = getOne(childEleListsByName, "def_f");
        if (defF != null) {
            definedFlags = Flags.getSet(defF.getTextContent());
            conditionallyModifiesFlags |= "yes".equals(getAttributeOrNull(defF, "cond"));
        }
        EnumSet<Flags> undefinedFlags = null;
        Element undefF = getOne(childEleListsByName, "undef_f");
        if (undefF != null) {
            undefinedFlags = Flags.getSet(undefF.getTextContent());
            conditionallyModifiesFlags |= "yes".equals(getAttributeOrNull(undefF, "cond"));
        }

        EnumSet<Flags> setFlags = null;
        EnumSet<Flags> unsetFlags = null;
        String fValsStr = getOneText(childEleListsByName, "f_vals");
        if (fValsStr != null) {
            setFlags = EnumSet.noneOf(Flags.class);
            unsetFlags = EnumSet.noneOf(Flags.class);
            for (int i = 0; i < fValsStr.length(); ++i) {
                String s = fValsStr.substring(i, i + 1);
                Flags flag = Flags.valueOf(s.toUpperCase());
                if (Character.isUpperCase(s.charAt(0))) {
                    setFlags.add(flag);
                }
                else {
                    unsetFlags.add(flag);
                }
            }
        }

        return new ImmutablePair<>(
                new Entry.FlagSet<>(
                        testedFlags,
                        modifiedFlags,
                        definedFlags,
                        undefinedFlags,
                        setFlags,
                        unsetFlags
                ),
                conditionallyModifiesFlags
        );
    }

    protected static Entry.FlagSet<FPUFlags> parseEntryFPUFlags(Element ele)
            throws Exception
    {
        Map<String, List<Element>> childEleListsByName = buildChildElementListMap(ele);

        EnumSet<FPUFlags> testedFPUFlags = null;
        String testFStr = getOneText(childEleListsByName, "test_f_fpu");
        if (testFStr != null) {
            testedFPUFlags = FPUFlags.getSet(testFStr);
        }
        EnumSet<FPUFlags> modifiedFPUFlags = null;
        String modifFStr = getOneText(childEleListsByName, "modif_f_fpu");
        if (modifFStr != null) {
            modifiedFPUFlags = FPUFlags.getSet(modifFStr);
        }

        EnumSet<FPUFlags> definedFPUFlags = null;
        Element defF = getOne(childEleListsByName, "def_f_fpu");
        if (defF != null) {
            definedFPUFlags = FPUFlags.getSet(defF.getTextContent());
        }
        EnumSet<FPUFlags> undefinedFPUFlags = null;
        Element undefF = getOne(childEleListsByName, "undef_f_fpu");
        if (undefF != null) {
            undefinedFPUFlags = FPUFlags.getSet(undefF.getTextContent());
        }

        EnumSet<FPUFlags> setFPUFlags = null;
        EnumSet<FPUFlags> unsetFPUFlags = null;
        String fValsStr = getOneText(childEleListsByName, "f_vals_fpu");
        if (fValsStr != null) {
            setFPUFlags = EnumSet.noneOf(FPUFlags.class);
            unsetFPUFlags = EnumSet.noneOf(FPUFlags.class);
            for (int i = 0; i < fValsStr.length(); ++i) {
                String s = fValsStr.substring(i, i + 1);
                FPUFlags flag = FPUFlags.get(s.toUpperCase());
                if (Character.isUpperCase(s.charAt(0))) {
                    setFPUFlags.add(flag);
                }
                else {
                    unsetFPUFlags.add(flag);
                }
            }
        }

        return new Entry.FlagSet<>(
                testedFPUFlags,
                modifiedFPUFlags,
                definedFPUFlags,
                undefinedFPUFlags,
                setFPUFlags,
                unsetFPUFlags
        );
    }

    protected static Entry parseEntry(Element ele, byte[] bytes)
            throws Exception
    {
        Map<String, List<Element>> childEleListsByName = buildChildElementListMap(ele);

        List<Element> syntaxEle = (childEleListsByName.getOrDefault("syntax", ImmutableList.of()));
        List<Syntax> syntaxes = Lists.newArrayList(filterOutNull(
                Iterables.transform(syntaxEle, Parsing::parseSyntax)));

        ImmutableSet.Builder<Entry.Group> groupsBuilder = ImmutableSet.builder();
        for (String key : new String[] {"grp1", "grp2", "grp3"}) {
            groupsBuilder.addAll(Iterables.<Element, Entry.Group>transform(
                    childEleListsByName.getOrDefault(key, ImmutableList.of()),
                    (Element childEle) -> Entry.Group.get(childEle.getTextContent())));
        }

        String prefixStr = getOneText(childEleListsByName, "pref");
        Byte prefixByte = !Strings.isNullOrEmpty(prefixStr) ? parseHexByte(prefixStr) : null;

        String secOpcd = getOneText(childEleListsByName, "sec_opcd");
        Byte secondaryByte = !Strings.isNullOrEmpty(secOpcd) ? parseHexByte(secOpcd) : null;

        Entry.ProcessorCode processorStart = Entry.ProcessorCode.get(getOneText(childEleListsByName, "proc_start"));
        Entry.ProcessorCode processorEnd = Entry.ProcessorCode.get(getOneText(childEleListsByName, "proc_end"));
        Entry.InstructionExtension instructionExtension = Entry.InstructionExtension.get(getOneText(childEleListsByName, "instr_ext"));

        byte[] aliasBytes = parseSeparatedHexBytes(getAttributeOrNull(ele, "alias"), "_");
        byte[] partialAliasBytes = parseSeparatedHexBytes(getAttributeOrNull(ele, "alias"), "_");

        boolean lock = "yes".equals(getAttributeOrNull(ele, "lock"));
        boolean isUndoc = "yes".equals(getAttributeOrNull(ele, "is_undoc"));
        boolean isParticular = "yes".equals(getAttributeOrNull(ele, "is_particular"));
        boolean r = "yes".equals(getAttributeOrNull(ele, "r"));

        byte direction = getByteAttributeOrNegativeOne(ele, "direction");
        byte signExt = getByteAttributeOrNegativeOne(ele, "sign-ext");
        byte opSize = getByteAttributeOrNegativeOne(ele, "op_size");
        byte tttn = getBinByteAttributeOrNegativeOne(ele, "tttn");
        byte memFormat = getBinByteAttributeOrNegativeOne(ele, "mem_format");

        EnumSet<Entry.BitFields> bitFields = EnumSet.noneOf(Entry.BitFields.class);
        if (direction >= 0) {
            bitFields.add(Entry.BitFields.DIRECTION);
        }
        if (signExt >= 0) {
            bitFields.add(Entry.BitFields.SIGN_EXTEND);
        }
        if (opSize >= 0) {
            bitFields.add(Entry.BitFields.OPERAND_SIZE);
        }
        if (tttn >= 0) {
            bitFields.add(Entry.BitFields.CONDITION);
        }
        if (memFormat >= 0) {
            bitFields.add(Entry.BitFields.MEMORY_FORMAT);
        }

        String opcodeExtensionStr = getOneText(childEleListsByName, "opcd_ext");
        byte opcodeExtension = opcodeExtensionStr != null ? Byte.parseByte(opcodeExtensionStr, 16) : -1;
        byte fpush = "yes".equals(getAttributeOrNull(ele, "fpush")) ? (byte) 1 : (byte) 0;
        String fpopStr = getAttributeOrNull(ele, "fpop");
        byte fpop = "once".equals(fpopStr) ? (byte) 1 : "twice".equals(fpopStr) ? (byte) 2 : 0;

        String modStr = getAttributeOrNull(ele, "mod");
        Entry.Mod mod = modStr != null ? Entry.Mod.valueOf(modStr) : null;
        Entry.Attr attr = Entry.Attr.get(getAttributeOrNull(ele, "attr"));
        Entry.Ring ring = Entry.Ring.get(getAttributeOrNull(ele, "ring"));
        Entry.Mode mode = Entry.Mode.get(getAttributeOrNull(ele, "mode"));
        Entry.Documentation documentation = Entry.Documentation.get(getAttributeOrNull(ele, "documentation"));
        Note note = parseNote(getOne(childEleListsByName, "note"));

        Pair<Entry.FlagSet<Flags>, Boolean> flagsPair = parseEntryFlags(ele);
        Entry.FlagSet<FPUFlags> fpuFlags = parseEntryFPUFlags(ele);

        return new Entry(
                prefixByte,
                bytes,
                secondaryByte,
                groupsBuilder.build(),
                processorStart,
                processorEnd,
                instructionExtension,
                aliasBytes,
                partialAliasBytes,
                syntaxes.toArray(new Syntax[0]),
                lock,
                isUndoc,
                isParticular,
                r,
                opcodeExtension,
                fpush,
                fpop,
                bitFields,
                mod,
                attr,
                ring,
                mode,
                documentation,
                flagsPair.getLeft(),
                flagsPair.getRight(),
                fpuFlags,
                note
        );
    }

    protected static void parseEntries(Element ele, byte[] bytes, List<Entry> entries)
            throws Exception
    {
        for (Element childEle : iterChildElements(ele)) {
            if ("entry".equals(childEle.getTagName())) {
                entries.add(parseEntry(childEle, bytes));
            }
        }
    }

    protected static void parseOpcodes(Element ele, byte[] bytes, List<Entry> entries)
            throws Exception
    {
        for (Element childEle : iterChildElements(ele)) {
            if ("pri_opcd".equals(childEle.getTagName())) {
                byte childByte = parseHexByte(childEle.getAttribute("value"));
                parseEntries(childEle, ArrayUtils.addAll(bytes, new byte[] {childByte}), entries);
            }
        }
    }

    protected static void parseRoot(Element ele, List<Entry> entries)
            throws Exception
    {
        for (Element childEle : iterChildElements(ele)) {
            if ("one-byte".equals(childEle.getTagName())) {
                parseOpcodes(childEle, new byte[0], entries);
            }
            else if ("two-byte".equals(childEle.getTagName())) {
                parseOpcodes(childEle, new byte[] {0x0F}, entries);
            }
        }
    }

    protected static void parseRoot(Document doc, List<Entry> entries)
            throws Exception
    {
        parseRoot(doc.getDocumentElement(), entries);
    }
}
