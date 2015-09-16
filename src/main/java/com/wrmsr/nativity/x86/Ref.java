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

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.xml.XmlEscapers;
import com.wrmsr.nativity.util.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wrmsr.nativity.util.Maps.buildMapToList;
import static com.wrmsr.nativity.x86.Ref.Util.arrayIterable;
import static com.wrmsr.nativity.x86.Ref.Util.buildChildElementListMap;
import static com.wrmsr.nativity.x86.Ref.Util.filterOutNull;
import static com.wrmsr.nativity.x86.Ref.Util.getAttributeOrNull;
import static com.wrmsr.nativity.x86.Ref.Util.getBinByteAttributeOrNegativeOne;
import static com.wrmsr.nativity.x86.Ref.Util.getByteAttributeOrNegativeOne;
import static com.wrmsr.nativity.x86.Ref.Util.getOne;
import static com.wrmsr.nativity.x86.Ref.Util.getOneOrThrow;
import static com.wrmsr.nativity.x86.Ref.Util.getOneText;
import static com.wrmsr.nativity.x86.Ref.Util.iterChildElements;
import static com.wrmsr.nativity.x86.Ref.Util.iterChildNodes;
import static com.wrmsr.nativity.x86.Ref.Util.parseHexByte;
import static com.wrmsr.nativity.x86.Ref.Util.parseSeparatedHexBytes;
import static com.wrmsr.nativity.x86.Ref.Util.quoteAndEscapeStr;

public class Ref
{
    public enum Flags
    {
        C(0, "Carry flag Status"),
        P(2, "Parity flag Status"),
        A(4, "Adjust flag Status"),
        Z(6, "Zero flag Status"),
        S(7, "Sign flag Status"),
        T(8, "Trap flag (single step) Control"),
        I(9, "Interrupt enable flag Control"),
        D(10, "Direction flag Control"),
        O(11, "Overflow flag Status"),
        IOPL1(12, "I/O privilege level (286+ only), always 1 on 8086 and 186 System"),
        IOPL2(13, "Second bit of IOPL"),
        NT(14, "Nested task flag (286+ only), always 1 on 8086 and 186 System");

        public final int bit;
        public final String note;

        Flags(int bit, String note)
        {
            this.bit = bit;
            this.note = note;
        }

        public static EnumSet<Flags> getSet(String str)
        {
            EnumSet<Flags> set = EnumSet.noneOf(Flags.class);
            for (int i = 0; i < str.length(); ++i) {
                set.add(valueOf(str.substring(i, i + 1).toUpperCase()));
            }
            return set;
        }
    }

    public enum EFlags
    {
        RF(16, "Resume flag (386+ only)	System"),
        VM(17, "Virtual 8086 mode flag (386+ only) System"),
        AC(18, "Alignment check (486SX+ only) System"),
        VIF(19, "Virtual interrupt flag (Pentium+) System"),
        VIP(20, "Virtual interrupt pending (Pentium+) System"),
        ID(21, "Able to use CPUID instruction (Pentium+) System");

        public final int bit;
        public final String note;

        EFlags(int bit, String note)
        {
            this.bit = bit;
            this.note = note;
        }
    }

    public enum FPUFlags
    {
        _0,
        _1,
        _2,
        _3;

        public static FPUFlags get(String str)
        {
            if (str == null) {
                return null;
            }
            switch (str) {
                case "0":
                case "a":
                case "A":
                    return _0;
                case "1":
                case "b":
                case "B":
                    return _1;
                case "2":
                case "c":
                case "C":
                    return _2;
                case "3":
                case "d":
                case "D":
                    return _3;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public static EnumSet<FPUFlags> getSet(String str)
        {
            EnumSet<FPUFlags> set = EnumSet.noneOf(FPUFlags.class);
            for (int i = 0; i < str.length(); ++i) {
                set.add(get(str.substring(i, i + 1)));
            }
            return set;
        }
    }

    public static class Operand
    {
        public final String text;
        public final RegisterNumber registerNumber;
        public final Group group;
        public final Type type;
        public final Address address;
        public final boolean noDepend;
        public final boolean noDisplayed;

        protected Syntax syntax;

        public void setSyntax(Syntax syntax)
        {
            if (this.syntax != null) {
                throw new IllegalStateException();
            }
            this.syntax = syntax;
        }

        public Syntax getSyntax()
        {
            return syntax;
        }

        public Operand(String text, RegisterNumber registerNumber, Group group, Type type, Address address, boolean noDepend, boolean noDisplayed)
        {
            this.text = text;
            this.registerNumber = registerNumber;
            this.group = group;
            this.type = type;
            this.address = address;
            this.noDepend = noDepend;
            this.noDisplayed = noDisplayed;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).omitNullValues()
                    .add("text", quoteAndEscapeStr(text))
                    .add("registerNumber", registerNumber)
                    .add("group", group)
                    .add("type", type)
                    .add("address", address)
                    .add("noDepend", (noDepend ? true : null))
                    .add("noDisplayed", (noDisplayed ? true : null))
                    .toString();
        }

        public enum RegisterNumber
        {
            _0,
            _1,
            _2,
            _3,
            _4,
            _5,
            _6,
            _7,
            _8,
            _9,
            _10,
            _11,
            _12,
            _13,
            _14,
            _15,
            _8B,
            _174,
            _175,
            _176,
            _C0000081,
            _C0000082,
            _C0000084,
            _C0000102,
            _C0000103;

            public static RegisterNumber get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf("_" + str.trim());
            }
        }

        public enum Group
        {
            GEN, MMX, XMM, SEG, X87FPU, CTRL, SYSTABP, MSR, DEBUG, XCR;

            public static Group get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }

        public enum Address
        {
            A("address. The instruction has no ModR/M byte; the address of the operand is encoded in the instruction; no base register, index register, or scaling factor can be applied (for example, far JMP (EA))."),
            BA("addressed by DS:EAX, or by rAX in 64-bit mode (only 0F01C8 MONITOR)."),
            BB("addressed by DS:eBX+AL, or by rBX+AL in 64-bit mode (only XLAT). (This code changed from single B in revision 1.00)"),
            BD("addressed by DS:eDI or by RDI (only 0FF7 MASKMOVQ and 660FF7 MASKMOVDQU) (This code changed from YD (introduced in 1.00) in revision 1.02)"),
            C("The reg field of the ModR/M byte selects a control register (only MOV (0F20, 0F22))."),
            D("The reg field of the ModR/M byte selects a debug register (only MOV (0F21, 0F23))."),
            E("A ModR/M byte follows the opcode and specifies the operand. The operand is either a general-purpose register or a memory address. If it is a memory address, the address is computed from a segment register and any of the following values: a base register, an index register, a scaling factor, or a displacement."),
            ES("(Implies  E). A ModR/M byte follows the opcode and specifies the operand. The operand is either a x87 FPU stack register or a memory address. If it is a memory address, the address is computed from a segment register and any of the following values: a base register, an index register, a scaling factor, or a displacement."),
            EST("(Implies  E). A ModR/M byte follows the opcode and specifies the x87 FPU stack register."),
            F("rFLAGS register."),
            G("The reg field of the ModR/M byte selects a general register (for example, AX (000))."),
            H("The r/m field of the ModR/M byte always selects a general register, regardless of the mod field (for example, MOV (0F20))."),
            I("Immediate data. The operand value is encoded in subsequent bytes of the instruction."),
            J("The instruction contains a relative offset to be  to the instruction pointer register (for example, JMP (E9), LOOP))."),
            M("The ModR/M byte may refer only to memory: mod != 11bin (BOUND, LEA, CALLF, JMPF, LES, LDS, LSS, LFS, LGS, CMPXCHG8B, CMPXCHG16B, F20FF0 LDDQU)."),
            N("The R/M field of the ModR/M byte selects a packed quadword MMX technology register."),
            O("The instruction has no ModR/M byte; the offset of the operand is coded as a word, double word or quad word (depending on address size attribute) in the instruction. No base register, index register, or scaling factor can be applied (only MOV  (A0, A1, A2, A3))."),
            P("The reg field of the ModR/M byte selects a packed quadword MMX technology register."),
            Q("A ModR/M byte follows the opcode and specifies the operand. The operand is either an MMX technology register or a memory address. If it is a memory address, the address is computed from a segment register and any of the following values: a base register, an index register, a scaling factor, and a displacement."),
            R("The mod field of the ModR/M byte may refer only to a general register (only MOV (0F20-0F24, 0F26))."),
            S("The reg field of the ModR/M byte selects a segment register (only MOV (8C, 8E))."),
            SC("Stack operand, used by instructions which either push an operand to the stack or pop an operand from the stack. Pop-like instructions are, for example, POP, RET, IRET, LEAVE. Push-like are, for example, PUSH, CALL, INT. No Operand type is provided along with this method because it depends on source/destination operand(s)."),
            T("The reg field of the ModR/M byte selects a test register (only MOV (0F24, 0F26))."),
            U("The R/M field of the ModR/M byte selects a 128-bit XMM register."),
            V("The reg field of the ModR/M byte selects a 128-bit XMM register."),
            W("A ModR/M byte follows the opcode and specifies the operand. The operand is either a 128-bit XMM register or a memory address. If it is a memory address, the address is computed from a segment register and any of the following values: a base register, an index register, a scaling factor, and a displacement"),
            X("Memory addressed by the DS:eSI or by RSI (only MOVS, CMPS, OUTS, and LODS). In 64-bit mode, only 64-bit (RSI) and 32-bit (ESI) address sizes are supported. In non-64-bit modes, only 32-bit (ESI) and 16-bit (SI) address sizes are supported."),
            Y("Memory addressed by the ES:eDI or by RDI (only MOVS, CMPS, INS, STOS, and SCAS). In 64-bit mode, only 64-bit (RDI) and 32-bit (EDI) address sizes are supported. In non-64-bit modes, only 32-bit (EDI) and 16-bit (DI) address sizes are supported. The implicit ES segment register cannot be overriden by a segment prefix."),

            Z("The instruction has no ModR/M byte; the three least-significant bits of the opcode byte selects a general-purpose register"),

            S2("The two bits at bit index three of the opcode byte selects one of original four segment registers (for example, PUSH ES)."),
            S30("The three least-significant bits of the opcode byte selects segment register SS, FS, or GS (for example, LSS)."),
            S33("The three bits at bit index three of the opcode byte selects segment register FS or GS (for example, PUSH FS).");

            public final String note;

            Address(String note)
            {
                this.note = note;
            }

            public static Address get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf(str.toUpperCase().trim());
            }

            public static boolean isInline(Address addr)
            {
                if (addr == null) {
                    return false;
                }
                switch (addr) {
                    case Z:
                    case S2:
                    case S30:
                    case S33:
                        return true;
                    default:
                        return false;
                }
            }
        }

        public enum Type
        {
            A("Two one-word operands in memory or two double-word operands in memory, depending on operand-size attribute (only BOUND)."),
            B("Byte, regardless of operand-size attribute."),
            BCD("Packed-BCD. Only x87 FPU instructions (for example, FBLD)."),
            BS("simplified bsq Byte, sign-extended to the size of the destination operand."),
            BSQ("Replaced by bs (Byte, sign-extended to 64 bits.)"),
            BSS("Byte, sign-extended to the size of the stack pointer (for example, PUSH (6A))."),
            C("Byte or word, depending on operand-size attribute. (unused even by Intel?)"),
            D("Doubleword, regardless of operand-size attribute."),
            DI("Doubleword-integer. Only x87 FPU instructions (for example, FIADD)."),
            DQ("Double-quadword, regardless of operand-size attribute (for example, CMPXCHG16B)."),
            DQP("combines d and qp Doubleword, or quadword, promoted by REX.W in 64-bit mode (for example, MOVSXD)."),
            DR("Double-real. Only x87 FPU instructions (for example, FADD)."),
            DS("Doubleword, sign-extended to 64 bits (for example, CALL (E8)."),
            E("x87 FPU environment (for example, FSTENV)."),
            ER("Extended-real. Only x87 FPU instructions (for example, FLD)."),
            P("32-bit or 48-bit pointer, depending on operand-size attribute (for example, CALLF (9A)."),
            PI("Quadword MMX technology data."),
            PD("128-bit packed double-precision floating-point data."),
            PS("128-bit packed single-precision floating-point data."),
            PSQ("64-bit packed single-precision floating-point data."),
            PT("replaced by ptp (80-bit far pointer.)"),
            PTP("32-bit or 48-bit pointer, depending on operand-size attribute, or 80-bit far pointer, promoted by REX.W in 64-bit mode (for example, CALLF (FF /3))."),
            Q("Quadword, regardless of operand-size attribute (for example, CALL (FF /2))."),
            QI("Qword-integer. Only x87 FPU instructions (for example, FILD)."),
            QP("Quadword, promoted by REX.W (for example, IRETQ)."),
            S("Changed to 6-byte pseudo-descriptor, or 10-byte pseudo-descriptor in 64-bit mode (for example, SGDT)."),
            SD("Scalar element of a 128-bit packed double-precision floating data."),
            SI("Doubleword integer register (e. g., eax). (unused even by Intel?)"),
            SR("Single-real. Only x87 FPU instructions (for example, FADD)."),
            SS("Scalar element of a 128-bit packed single-precision floating data."),
            ST("x87 FPU state (for example, FSAVE)."),
            STX("x87 FPU and SIMD state (FXSAVE and FXRSTOR)."),
            T("Replaced by ptp 10-byte far pointer."),
            V("Word or doubleword, depending on operand-size attribute (for example, INC (40), PUSH (50))."),
            VDS("Combines v and ds Word or doubleword, depending on operand-size attribute, or doubleword, sign-extended to 64 bits for 64-bit operand size."),
            VQ("Quadword (default) or word if operand-size prefix is used (for example, PUSH (50))."),
            VQP("Combines v and qp Word or doubleword, depending on operand-size attribute, or quadword, promoted by REX.W in 64-bit mode."),
            VS("Word or doubleword sign extended to the size of the stack pointer (for example, PUSH (68))."),
            W("Word, regardless of operand-size attribute (for example, ENTER)."),
            WI("Word-integer. Only x87 FPU instructions (for example, FIADD)."),
            VA("Word or doubleword, according to address-size attribute (only REP and LOOP families)."),
            DQA("Doubleword or quadword, according to address-size attribute (only REP and LOOP families)."),
            WA("Word, according to address-size attribute (only JCXZ instruction)."),
            WO("Word, according to current operand size (e. g., MOVSW instruction)."),
            WS("Word, according to current stack size (only PUSHF and POPF instructions in 64-bit mode)."),
            DA("Doubleword, according to address-size attribute (only JECXZ instruction)."),
            DO("Doubleword, according to current operand size (e. g., MOVSD instruction)."),
            QA("Quadword, according to address-size attribute (only JRCXZ instruction)."),
            QS("Quadword, according to current stack size (only PUSHFQ and POPFQ instructions).");

            public final String note;

            Type(String note)
            {
                this.note = note;
            }

            public static Type get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }
    }

    public static class Note
    {

        public final String brief;
        public final String detailed;

        public Note(String brief, String detailed)
        {
            this.brief = brief;
            this.detailed = detailed;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).omitNullValues()
                    .add("brief", quoteAndEscapeStr(brief))
                    .add("detailed", quoteAndEscapeStr(detailed))
                    .toString();
        }
    }

    public static class Syntax
    {

        public final String mnemonic;
        public final Mod mod;
        public final Operand[] srcOperands;
        public final Operand[] dstOperands;

        protected Entry entry;

        public void setEntry(Entry entry)
        {
            if (this.entry != null) {
                throw new IllegalStateException();
            }
            this.entry = entry;
        }

        public Entry getEntry()
        {
            return entry;
        }

        public Iterable<Operand> getSrcOperands()
        {
            return arrayIterable(srcOperands);
        }

        public Iterable<Operand> getDstOperands()
        {
            return arrayIterable(dstOperands);
        }

        public Iterable<Operand> getOperands()
        {
            return Iterables.concat(getSrcOperands(), getDstOperands());
        }

        public Syntax(String mnemonic, Mod mod, Operand[] srcOperands, Operand[] dstOperands)
        {
            this.mnemonic = mnemonic;
            this.mod = mod;
            this.srcOperands = srcOperands;
            this.dstOperands = dstOperands;
            for (Operand operand : srcOperands) {
                operand.setSyntax(this);
            }
            for (Operand operand : dstOperands) {
                operand.setSyntax(this);
            }
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).omitNullValues()
                    .add("mnemonic", quoteAndEscapeStr(mnemonic))
                    .add("mod", mod)
                    .add("srcOperands", (srcOperands != null && srcOperands.length > 0) ? Arrays.toString(srcOperands) : null)
                    .add("dstOperands", (dstOperands != null && dstOperands.length > 0) ? Arrays.toString(dstOperands) : null)
                    .toString();
        }

        public enum Mod
        {
            nomem, mem
        }
    }

    public static class Entry
    {
        public final Byte prefixByte;
        public final byte[] bytes;
        public final Byte secondaryByte;

        public final Set<Group> groups;
        public final ProcessorCode processorStart;
        public final ProcessorCode processorEnd;
        public final InstructionExtension instructionExtension;

        public final byte[] aliasBytes;
        public final byte[] partialAliasBytes;

        public final Syntax[] syntaxes;

        public final boolean isValidWithLockPrefix;
        public final boolean isUndocumented;
        public final boolean isParticular;
        public final boolean isModRMRegister;

        public final byte opcodeExtension;
        public final byte fpush;
        public final byte fpop;

        // r+ = a Z arg, unfold in trie
        public final EnumSet<BitFields> bitFields;
        public final Mod mod;
        public final Attr attr;
        public final Ring ring;
        public final Mode mode;
        public final Documentation documentation;

        // Note that if a flag is present in both Defined and Undefined column, the flag fits in under further conditions, which are not described by this reference.
        public final FlagSet<Flags> flags;
        public final boolean conditionallyModifiesFlags;

        public final FlagSet<FPUFlags> fpuFlags;

		/*
        protected Object ref;
		protected Object docPartAliasRef;
		protected Object docRef;
		protected Object doc1632Ref;
		protected Object doc64Ref;
		protected Object ringRef;
		*/

        public final Note note;

        public Byte getPrefixByte()
        {
            return prefixByte;
        }

        public Iterable<Byte> getBytes()
        {
            return arrayIterable(bytes);
        }

        public Byte getSecondaryByte()
        {
            return secondaryByte;
        }

        public int size()
        {
            return bytes.length;
        }

        public Iterable<Syntax> getSyntaxes()
        {
            // FIXME: eager final
            return arrayIterable(syntaxes);
        }

        public Iterable<Byte> getAliasBytes()
        {
            if (aliasBytes == null) {
                return null;
            }
            return arrayIterable(aliasBytes);
        }

        public Iterable<Byte> getPartialAliasBytes()
        {
            if (partialAliasBytes == null) {
                return null;
            }
            return arrayIterable(partialAliasBytes);
        }

        public Entry(
                Byte prefixByte,
                byte[] bytes,
                Byte secondaryByte,
                Set<Group> groups,
                ProcessorCode processorStart,
                ProcessorCode processorEnd,
                InstructionExtension instructionExtension,
                byte[] aliasBytes,
                byte[] partialAliasBytes,
                Syntax[] syntaxes,
                boolean isValidWithLockPrefix,
                boolean isUndocumented,
                boolean isParticular,
                boolean isModRMRegister,
                byte opcodeExtension,
                byte fpush,
                byte fpop,
                EnumSet<BitFields> bitFields,
                Mod mod,
                Attr attr,
                Ring ring,
                Mode mode,
                Documentation documentation,
                FlagSet<Flags> flags,
                boolean conditionallyModifiesFlags,
                FlagSet<FPUFlags> fpuFlags,
                Note note)
        {
            this.prefixByte = prefixByte;
            this.bytes = bytes;
            this.secondaryByte = secondaryByte;
            this.groups = groups;
            this.processorStart = processorStart;
            this.processorEnd = processorEnd;
            this.instructionExtension = instructionExtension;
            this.aliasBytes = aliasBytes;
            this.partialAliasBytes = partialAliasBytes;
            this.syntaxes = syntaxes;
            this.isValidWithLockPrefix = isValidWithLockPrefix;
            this.isUndocumented = isUndocumented;
            this.isParticular = isParticular;
            this.isModRMRegister = isModRMRegister;
            this.opcodeExtension = opcodeExtension;
            this.fpush = fpush;
            this.fpop = fpop;
            this.bitFields = bitFields;
            this.mod = mod;
            this.attr = attr;
            this.ring = ring;
            this.mode = mode;
            this.documentation = documentation;
            this.flags = flags;
            this.conditionallyModifiesFlags = conditionallyModifiesFlags;
            this.fpuFlags = fpuFlags;
            this.note = note;
            for (Syntax syntax : syntaxes) {
                syntax.setEntry(this);
            }
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this).omitNullValues()

                    .add("prefixByte", prefixByte != null ? Hex.hexdump(prefixByte) : null)
                    .add("bytes", Hex.hexdump(bytes))
                    .add("secondaryByte", secondaryByte != null ? Hex.hexdump(secondaryByte) : null)

                    .add("groups", groups)
                    .add("processorStart", processorStart)
                    .add("processorEnd", processorEnd)
                    .add("instructionExtension", instructionExtension)

                    .add("aliasBytes", aliasBytes != null ? Hex.hexdump(aliasBytes) : null)
                    .add("partialAliasBytes", partialAliasBytes != null ? Hex.hexdump(partialAliasBytes) : null)

                    .add("syntaxes", Arrays.toString(syntaxes))

                    .add("isValidWithLockPrefix", (isValidWithLockPrefix ? true : null))
                    .add("isUndocumented", (isUndocumented ? true : null))
                    .add("isParticular", (isParticular ? true : null))
                    .add("isModRMRegister", (isModRMRegister ? true : null))

                    .add("opcodeExtension", (opcodeExtension >= 0 ? opcodeExtension : null))
                    .add("fpush", (fpush > 0 ? fpush : null))
                    .add("fpop", (fpop > 0 ? fpop : null))

                    .add("bitFields", bitFields.size() > 0 ? bitFields : null)
                    .add("mod", mod)
                    .add("attr", attr)
                    .add("ring", ring)
                    .add("mode", mode != Mode.DEFAULT ? mode : null)
                    .add("documentation", documentation != Documentation.DEFAULT ? documentation : null)

                    .add("flags", (flags != null && !flags.isEmpty()) ? flags : null)
                    .add("conditionallyModifiesFlags", conditionallyModifiesFlags ? true : null)
                    .add("fpuFlags", (fpuFlags != null && !fpuFlags.isEmpty()) ? fpuFlags : null)

                    .add("note", note)

                    .toString();
        }

        public enum ProcessorCode
        {
            _8086(0),
            _80186(1),
            _80286(2),
            _80386(3),
            _80486(4),
            P1(5),
            P1MMX(6),
            PPRO(7),
            PII(8),
            PIII(9),
            P4(10),
            CORE1(11),
            CORE2(12),
            COREI7(13),
            ITANIUM(99);

            public final int value;

            ProcessorCode(int value)
            {
                this.value = value;
            }

            public static ProcessorCode get(int value)
            {
                for (ProcessorCode code : values()) {
                    if (code.value == value) {
                        return code;
                    }
                }
                throw new IllegalArgumentException();
            }

            public static ProcessorCode get(String str)
            {
                if (str == null) {
                    return null;
                }
                return get(Integer.parseInt(str));
            }
        }

        public enum Mod
        {
            nomem, mem
        }

        public enum Attr
        {
            INVD("the opcode is invalid"),
            UNDEF("the behaviour of the opcode is always undefined (e. g., SALC)"),
            NULL("(only prefixes): the prefix has no meaning (no operation)"),
            NOP("nop (nop instructions and obsolete x87 FPU instructions): the instruction is treated as integer NOP instruction; it should contain a reference to and the title with the source (doc_ref attribute should be used)"),
            ACC("the opcode is optimized for the accumulator (e.g., 0x04 or 0x05) -->"),
            SERIAL("serial: the opcode is serializing (CPUID; IRET; RSM; MOV Ddqp; WRMSR; INVD, INVLPG, WBINWD; LGDT; LLDT; LIDT; LTR; LMSW)"),
            SERIAL_COND("same as serial, but under further conditions (only MOV Cq)"),
            DELAYSINT("the opcode delays recognition of interrupts until after execution of the next instruction (only POP SS)"),
            DELAYSINT_COND("same as delaysint, but under further conditions (only STI)");

            public final String note;

            Attr(String note)
            {
                this.note = note;
            }

            public static Attr get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }

        public enum Ring
        {
            _3, _2, _1, _0, F;

            public static Ring get(String str)
            {
                if (str == null) {
                    return null;
                }
                switch (str) {
                    case "3":
                        return _3;
                    case "2":
                        return _2;
                    case "1":
                        return _1;
                    case "0":
                        return _0;
                    case "f":
                        return F;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }

        public enum Group
        {
            PREFIX(0),
            SEGREG(1), // segment register
            BRANCH(1),
            COND(2), // conditional
            X87FPU(1),
            CONTROL(2), // (only WAIT)
            OBSOL(0), // obsolete
            GEN(0), // general
            DATAMOV(1), // data movement
            STACK(1),
            CONVER(1), // type conversion
            ARITH(1), // arithmetic
            BINARY(2),
            DECIMAL(2),
            LOGICAL(1),
            SHFTROT(1), // shift&rotate
            BIT(1), // bit manipulation
            BREAK(1), // interrupt
            STRING(1), // (means that the instruction can make use of the REP family prefixes)
            INOUT(1), // I/O
            FLGCTRL(1), // flag control
            SYSTEM(0),
            TRANS(1), // transitional (implies sensitivity to operand-size attribute)
            COMPAR(1), // comparison
            LDCONST(1), // load constant
            CONV(1), // conversion
            SM(0), // x87 FPU and SIMD state management
            SHIFT(0),
            UNPACK(0), // unpacking
            SIMDFP(0), // SIMD single-precision floating-point
            SHUNPCK(1), // shuffle&unpacking
            SIMDINT(0), // 64-bit SIMD integer
            MXCSRSM(0), // MXCSR state management
            CACHECT(0), // cacheability control
            FETCH(0), // prefetch
            ORDER(0), // instruction ordering
            PCKSCLR(0), // packed and scalar double-precision floating-point
            PCKSP(0), // packed single-precision floating-point
            SYNC(0), // agent synchronization
            STRTXT(0); // string and text processing

            public final int tier;

            Group(int tier)
            {
                this.tier = tier;
            }

            public static Group get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }

        public enum InstructionExtension
        {
            MMX, SSE1, SSE2, SSE3, SSSE3, SSE41, SSE42, VMX, SMX;

            public static InstructionExtension get(String str)
            {
                if (str == null) {
                    return null;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }

        public enum Mode
        {
            R("valid in real, protected and 64-bit mode; SMM is not taken into account"),
            P("valid only in protected and 64-bit mode; SMM is not taken into account"),
            E("valid only in 64-bit mode; SMM is not taken into account"),
            S("valid only in SMM (only RSM)");

            public final String note;

            Mode(String note)
            {
                this.note = note;
            }

            public static Mode DEFAULT = R;

            public static Mode get(String str)
            {
                if (str == null) {
                    return DEFAULT;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }

        public enum Documentation
        {
            D("fully documented; it can contain a reference to and a title with the chapter, where the instruction is documented, if it may be unclear (doc_ref attribute should be used)"),
            M("only marginally (e.g., meaning of prefix 66hex when used with SSE instruction extensions)"),
            U("undocumented at all; it should contain a reference to and a title with the source (e.g., SALC, INT1) (doc_ref attribute should be used)");

            public final String note;

            Documentation(String note)
            {
                this.note = note;
            }

            public static Documentation DEFAULT = D;

            public static Documentation get(String str)
            {
                if (str == null) {
                    return DEFAULT;
                }
                return valueOf(str.toUpperCase().trim());
            }
        }

        // r+, sr, sre in operands
        public enum BitFields
        {
            OPERAND_SIZE("w means bit w (bit index 0, operand size) is present; may be combined with bits d or s. 04 ADD"),
            SIGN_EXTEND("s means bit s (bit index 1, Sign-extend) is present; may be combined with bit w. 6B IMUL"),
            DIRECTION("d means bit d (bit index 1, Direction) is present; may be combined with bit w. 00 ADD"),
            CONDITION("tttn means bit field tttn (4 bits, bit index 0, condition). Used only with conditional instructions. 70 JO"),
            MEMORY_FORMAT("means bit field MF (2 bits, bit index 1, memory format); used only with x87 FPU instructions coded with second floating-point instruction format. DA/0 FIADD ;");

            public final String note;

            BitFields(String note)
            {
                this.note = note;
            }
        }

        public static class FlagSet<T extends Enum<T>>
        {
            public final EnumSet<T> tested;
            public final EnumSet<T> modified;
            public final EnumSet<T> defined;
            public final EnumSet<T> undefined;
            public final EnumSet<T> set;
            public final EnumSet<T> unset;

            public FlagSet(EnumSet<T> tested, EnumSet<T> modified, EnumSet<T> defined, EnumSet<T> undefined, EnumSet<T> set, EnumSet<T> unset)
            {
                this.tested = tested;
                this.modified = modified;
                this.defined = defined;
                this.undefined = undefined;
                this.set = set;
                this.unset = unset;
            }

            public boolean isEmpty()
            {
                return (tested == null || tested.isEmpty()) &&
                        (modified == null || modified.isEmpty()) &&
                        (defined == null || defined.isEmpty()) &&
                        (undefined == null || undefined.isEmpty()) &&
                        (set == null || set.isEmpty()) &&
                        (unset == null || unset.isEmpty());
            }

            @Override
            public String toString()
            {
                return Objects.toStringHelper(this).omitNullValues()
                        .add("tested", tested)
                        .add("modified", modified)
                        .add("defined", defined)
                        .add("undefined", undefined)
                        .add("set", set)
                        .add("unset", unset)
                        .toString();
            }
        }
    }

    public static class Parsing
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

    public static class Util
    {
        private Util()
        {
        }

        public static ContiguousSet<Integer> xrange(int start, int end)
        {
            return ContiguousSet.create(Range.closedOpen(start, end), DiscreteDomain.integers());
        }

        public static ContiguousSet<Integer> xrange(int end)
        {
            return xrange(0, end);
        }

        public static Iterable<Node> iterNodeList(final NodeList nodeList)
        {
            return Iterables.transform(xrange(nodeList.getLength()), (index) -> nodeList.item(index));
        }

        public static Iterable<Node> iterChildNodes(Node node)
        {
            return iterNodeList(node.getChildNodes());
        }

        public static Iterable<Element> iterChildElements(Node node)
        {
            return Iterables.transform(
                    Iterables.filter(iterChildNodes(node), (childNode) -> childNode.getNodeType() == Node.ELEMENT_NODE),
                    (childNode) -> (Element) childNode);
        }

        public static Map<String, List<Element>> buildChildElementListMap(Node node)
        {
            return buildMapToList(iterChildElements(node), (childElement) -> childElement.getTagName());
        }

        public static byte parseHexByte(String str)
                throws NumberFormatException
        {
            if (str.startsWith("0x")) {
                str = str.substring(2);
            }
            return (byte) Integer.parseInt(str, 16);
        }

        public static byte[] parseSeparatedHexBytes(String str, String sep)
        {
            if (str == null) {
                return null;
            }
            String[] strs = str.split(sep);
            byte[] bytes = new byte[strs.length];
            for (int i = 0; i < strs.length; ++i) {
                bytes[i] = parseHexByte(strs[i]);
            }
            return bytes;
        }

        public static <K, V> V getOne(Map<K, List<V>> listMap, K key, V defaultValue)
        {
            List<V> list = listMap.get(key);
            if (list != null && list.size() == 1) {
                return list.get(0);
            }
            return defaultValue;
        }

        public static <K, V> V getOne(Map<K, List<V>> listMap, K key)
        {
            return getOne(listMap, key, null);
        }

        public static <K, V> V getOneOrThrow(Map<K, List<V>> listMap, K key)
        {
            List<V> list = listMap.get(key);
            if (list != null && list.size() == 1) {
                return list.get(0);
            }
            throw new IllegalStateException();
        }

        public static <K, V> V getFirst(Map<K, List<V>> listMap, K key, V defaultValue)
        {
            List<V> list = listMap.get(key);
            if (list != null && list.size() >= 1) {
                return list.get(0);
            }
            return defaultValue;
        }

        public static <K, V> V getFirst(Map<K, List<V>> listMap, K key)
        {
            return getFirst(listMap, key, null);
        }

        public static <K, V> V getFirstOrThrow(Map<K, List<V>> listMap, K key)
        {
            List<V> list = listMap.get(key);
            if (list != null && list.size() >= 1) {
                return list.get(0);
            }
            throw new IllegalStateException();
        }

        public static <E> Iterable<E> filterOutNull(Iterable<E> it)
        {
            return Iterables.filter(it, (e) -> e != null);
        }

        public static String getOneText(Map<String, List<Element>> listMap, String key, String defaultValue)
        {
            List<Element> list = listMap.get(key);
            if (list == null) {
                return defaultValue;
            }
            if (list.size() != 1) {
                throw new IllegalStateException();
            }
            return list.get(0).getTextContent();
        }

        public static String getOneText(Map<String, List<Element>> listMap, String key)
        {
            return getOneText(listMap, key, null);
        }

        public static String getAttributeOrNull(Element ele, String name)
        {
            return ele.hasAttribute(name) ? ele.getAttribute(name) : null;
        }

        public static byte getByteAttributeOrNegativeOne(Element ele, String name)
        {
            return ele.hasAttribute(name) ? Byte.parseByte(ele.getAttribute(name)) : -1;
        }

        public static byte getBinByteAttributeOrNegativeOne(Element ele, String name)
        {
            return ele.hasAttribute(name) ? Byte.parseByte(ele.getAttribute(name), 2) : -1;
        }

        public static int getIntAttributeOrNegativeOne(Element ele, String name)
        {
            return ele.hasAttribute(name) ? Integer.parseInt(ele.getAttribute(name)) : -1;
        }

        public static int getHexIntAttributeOrNegativeOne(Element ele, String name)
        {
            return ele.hasAttribute(name) ? Integer.parseInt(ele.getAttribute(name), 16) : -1;
        }

        public static long getHexLongAttributeOrNegativeOne(Element ele, String name)
        {
            return ele.hasAttribute(name) ? Long.parseLong(ele.getAttribute(name), 16) : -1;
        }

        public static Map<String, String> getAttributeMap(Element ele)
        {
            Map<String, String> map = Maps.newHashMap();
            NamedNodeMap atts = ele.getAttributes();
            for (int i = 0; i < atts.getLength(); ++i) {
                Node att = atts.item(i);
                map.put(att.getNodeName(), att.getTextContent());
            }
            return map;
        }

        public static String quoteAndEscapeStr(String str)
        {
            if (str == null) {
                return null;
            }
            return String.format("'%s'", XmlEscapers.xmlContentEscaper().escape(str));
        }

        public static <T> Iterator<T> arrayIterator(final T[] arr)
        {
            return new Iterator<T>()
            {
                private int pos = 0;

                @Override
                public boolean hasNext()
                {
                    return pos < arr.length;
                }

                @Override
                public T next()
                {
                    return arr[pos++];
                }
            };
        }

        public static <T> Iterable<T> arrayIterable(final T[] arr)
        {
            return new Iterable<T>()
            {
                @Override
                public Iterator<T> iterator()
                {
                    return arrayIterator(arr);
                }
            };
        }

        public static Iterator<Byte> arrayIterator(final byte[] arr)
        {
            return new Iterator<Byte>()
            {
                private int pos = 0;

                @Override
                public boolean hasNext()
                {
                    return pos < arr.length;
                }

                @Override
                public Byte next()
                {
                    return arr[pos++];
                }
            };
        }

        public static Iterable<Byte> arrayIterable(final byte[] arr)
        {
            return new Iterable<Byte>()
            {
                @Override
                public Iterator<Byte> iterator()
                {
                    return arrayIterator(arr);
                }
            };
        }

        public static byte[] toByteArray(List<Byte> bytesList)
        {
            byte[] bytes = new byte[bytesList.size()];
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = bytesList.get(i);
            }
            return bytes;
        }

        public static byte[] toByteArray(Iterable<Byte> bytesIt)
        {
            return toByteArray(Lists.newArrayList(bytesIt));
        }
    }
}
