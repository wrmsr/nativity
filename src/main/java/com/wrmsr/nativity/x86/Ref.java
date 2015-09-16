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
import com.google.common.collect.Iterables;
import com.wrmsr.nativity.util.Hex;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static com.wrmsr.nativity.x86.Util.arrayIterable;
import static com.wrmsr.nativity.x86.Util.quoteAndEscapeStr;

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
}
