/*
 * This file is distributed under the University of Illinois Open Source
 * License. See LICENSE-LLVM for details.
 */
package com.wrmsr.nativity.x86;

public class LLVM
{
    public static class OperandSpecifier
    {
        public final byte encoding;
        public final byte type;

        public OperandSpecifier(byte encoding, byte type)
        {
            this.encoding = encoding;
            this.type = type;
        }
    }

    public static class InstructionSpecifier
    {
        public final short operands;

        public InstructionSpecifier(short operands)
        {
            this.operands = operands;
        }
    }

    public enum Encoding
    {
        ENCODING_NONE(""),
        ENCODING_REG("Register operand in ModR/M byte."),
        ENCODING_RM("R/M operand in ModR/M byte."),
        ENCODING_RM_CD2("R/M operand with CDisp scaling of 2"),
        ENCODING_RM_CD4("R/M operand with CDisp scaling of 4"),
        ENCODING_RM_CD8("R/M operand with CDisp scaling of 8"),
        ENCODING_RM_CD16("R/M operand with CDisp scaling of 16"),
        ENCODING_RM_CD32("R/M operand with CDisp scaling of 32"),
        ENCODING_RM_CD64("R/M operand with CDisp scaling of 64"),
        ENCODING_VVVV("Register operand in VEX.vvvv byte."),
        ENCODING_WRITEMASK("Register operand in EVEX.aaa byte."),
        ENCODING_CB("1-byte code offset (possible new CS value)"),
        ENCODING_CW("2-byte"),
        ENCODING_CD("4-byte"),
        ENCODING_CP("6-byte"),
        ENCODING_CO("8-byte"),
        ENCODING_CT("10-byte"),
        ENCODING_IB("1-byte immediate"),
        ENCODING_IW("2-byte"),
        ENCODING_ID("4-byte"),
        ENCODING_IO("8-byte"),
        ENCODING_RB("(AL..DIL, R8L..R15L) Register code added to the opcode byte"),
        ENCODING_RW("(AX..DI, R8W..R15W)"),
        ENCODING_RD("(EAX..EDI, R8D..R15D)"),
        ENCODING_RO("(RAX..RDI, R8..R15)"),
        ENCODING_FP("Position on floating-point stack in ModR/M byte."),
        ENCODING_Iv("Immediate of operand size"),
        ENCODING_Ia("Immediate of address size"),
        ENCODING_Rv("Register code of operand size added to the opcode byte"),
        ENCODING_DUP("Duplicate of another operand; ID is encoded in type"),
        ENCODING_SI("Source index; encoded in OpSize/Adsize prefix"),
        ENCODING_DI("Destination index; encoded in prefixes");

        public final String description;

        Encoding(String description)
        {
            this.description = description;
        }
    }

    public enum Type
    {
        TYPE_NONE(""),
        TYPE_REL8("1-byte immediate address"),
        TYPE_REL16("2-byte"),
        TYPE_REL32("4-byte"),
        TYPE_REL64("8-byte"),
        TYPE_PTR1616("2+2-byte segment+offset address"),
        TYPE_PTR1632("2+4-byte"),
        TYPE_PTR1664("2+8-byte"),
        TYPE_R8("1-byte register operand"),
        TYPE_R16("2-byte"),
        TYPE_R32("4-byte"),
        TYPE_R64("8-byte"),
        TYPE_IMM8("1-byte immediate operand"),
        TYPE_IMM16("2-byte"),
        TYPE_IMM32("4-byte"),
        TYPE_IMM64("8-byte"),
        TYPE_IMM3("1-byte immediate operand between 0 and 7"),
        TYPE_IMM5("1-byte immediate operand between 0 and 31"),
        TYPE_AVX512ICC("1-byte immediate operand for AVX512 icmp"),
        TYPE_UIMM8("1-byte unsigned immediate operand"),
        TYPE_RM8("1-byte register or memory operand"),
        TYPE_RM16("2-byte"),
        TYPE_RM32("4-byte"),
        TYPE_RM64("8-byte"),
        TYPE_M("Memory operand"),
        TYPE_M8("1-byte"),
        TYPE_M16("2-byte"),
        TYPE_M32("4-byte"),
        TYPE_M64("8-byte"),
        TYPE_LEA("Effective address"),
        TYPE_M128("16-byte (SSE/SSE2)"),
        TYPE_M256("256-byte (AVX)"),
        TYPE_M1616("2+2-byte segment+offset address"),
        TYPE_M1632("2+4-byte"),
        TYPE_M1664("2+8-byte"),
        TYPE_SRCIDX8("1-byte memory at source index"),
        TYPE_SRCIDX16("2-byte memory at source index"),
        TYPE_SRCIDX32("4-byte memory at source index"),
        TYPE_SRCIDX64("8-byte memory at source index"),
        TYPE_DSTIDX8("1-byte memory at destination index"),
        TYPE_DSTIDX16("2-byte memory at destination index"),
        TYPE_DSTIDX32("4-byte memory at destination index"),
        TYPE_DSTIDX64("8-byte memory at destination index"),
        TYPE_MOFFS8("1-byte memory offset (relative to segment base)"),
        TYPE_MOFFS16("2-byte"),
        TYPE_MOFFS32("4-byte"),
        TYPE_MOFFS64("8-byte"),
        TYPE_SREG("Byte with single bit set: 0 = ES, 1 = CS, 2 = SS, 3 = DS, 4 = FS, 5 = GS"),
        TYPE_M32FP("32-bit IEE754 memory floating-point operand"),
        TYPE_M64FP("64-bit"),
        TYPE_M80FP("80-bit extended"),
        TYPE_ST("Position on the floating-point stack"),
        TYPE_MM64("8-byte MMX register"),
        TYPE_XMM("XMM register operand"),
        TYPE_XMM32("4-byte XMM register or memory operand"),
        TYPE_XMM64("8-byte"),
        TYPE_XMM128("16-byte"),
        TYPE_XMM256("32-byte"),
        TYPE_XMM512("64-byte"),
        TYPE_VK1("1-bit"),
        TYPE_VK2("2-bit"),
        TYPE_VK4("4-bit"),
        TYPE_VK8("8-bit"),
        TYPE_VK16("16-bit"),
        TYPE_VK32("32-bit"),
        TYPE_VK64("64-bit"),
        TYPE_XMM0("Implicit use of XMM0"),
        TYPE_SEGMENTREG("Segment register operand"),
        TYPE_DEBUGREG("Debug register operand"),
        TYPE_CONTROLREG("Control register operand"),
        TYPE_BNDR("MPX bounds register"),
        TYPE_Mv("Memory operand of operand size"),
        TYPE_Rv("Register operand of operand size"),
        TYPE_IMMv("Immediate operand of operand size"),
        TYPE_RELv("Immediate address of operand size"),
        TYPE_DUP0("Duplicate of operand 0"),
        TYPE_DUP1("operand 1"),
        TYPE_DUP2("operand 2"),
        TYPE_DUP3("operand 3"),
        TYPE_DUP4("operand 4"),
        TYPE_M512("512-bit FPU/MMX/XMM/MXCSR state");

        public final String description;

        Type(String description)
        {
            this.description = description;
        }
    }

    public static class ModRmDecision
    {
        public final byte modRmType;
        public final short instructionIds;

        public ModRmDecision(byte modRmType, short instructionIds)
        {
            this.modRmType = modRmType;
            this.instructionIds = instructionIds;
        }
    }

    public static class OpcodeDecision
    {
        public final ModRmDecision[] modRmDecisions;

        public OpcodeDecision(ModRmDecision[] modRmDecisions)
        {
            this.modRmDecisions = modRmDecisions;
        }
    }

    public static class ContextDecision
    {
        public final OpcodeDecision[] opcodeDecisions[];

        public ContextDecision(OpcodeDecision[][] opcodeDecisions)
        {
            this.opcodeDecisions = opcodeDecisions;
        }
    }

    public enum OperandConstraint
    {
        TIED_TO,
        EARLY_CLOBBER;
    }

    public enum OperandFlags
    {
        LOOKUP_PTR_REG_CLASS,
        PREDICATE,
        OPTIONAL_DEF;
    }

    public enum OperandType
    {
        OPERAND_UNKNOWN,
        OPERAND_IMMEDIATE,
        OPERAND_REGISTER,
        OPERAND_MEMORY,
        OPERAND_PCREL,
        OPERAND_FIRST_TARGET;
    }

    public static class MCOperandInfo
    {
        public final short RegClass;
        public final byte Flags;
        public final byte OperandType;
        public final int Constraints;

        public MCOperandInfo(short regClass, byte flags, byte operandType, int constraints)
        {
            RegClass = regClass;
            Flags = flags;
            OperandType = operandType;
            Constraints = constraints;
        }
    }

    public enum Flag
    {
        VARIADIC,
        HAS_OPTIONAL_DEF,
        PSEUDO,
        RETURN,
        CALL,
        BARRIER,
        TERMINATOR,
        BRANCH,
        INDIRECT_BRANCH,
        COMPARE,
        MOVE_IMM,
        BITCAST,
        SELECT,
        DELAY_SLOT,
        FOLDABLE_AS_LOAD,
        MAY_LOAD,
        MAY_STORE,
        PREDICABLE,
        NOT_DUPLICABLE,
        UNMODELED_SIDE_EFFECTS,
        COMMUTABLE,
        CONVERTIBLE_TO_3_ADDR,
        USES_CUSTOM_INSERTER,
        HAS_POST_ISEL_HOOK,
        REMATERIALIZABLE,
        CHEAP_AS_A_MOVE,
        EXTRA_SRC_REG_ALLOC_REQ,
        EXTRA_DEF_REG_ALLOC_REQ,
        REG_SEQUENCE,
        EXTRACT_SUBREG,
        INSERT_SUBREG,
        CONVERGENT;
    }

    public static class MCInstrDesc
    {
        public final short opcode;
        public final short numOperands;
        public final char numDefs;
        public final char size;
        public final short schedClass;
        public final long flags;
        public final long tsFlags;
        public final short[] implicitUses;
        public final short[] implicitDefs;
        public final MCOperandInfo opInfo;
        public final long deprecatedFeature;

        public MCInstrDesc(short opcode, short numOperands, char numDefs, char size, short schedClass, long flags, long tsFlags, short[] implicitUses, short[] implicitDefs, MCOperandInfo opInfo, long deprecatedFeature)
        {
            this.opcode = opcode;
            this.numOperands = numOperands;
            this.numDefs = numDefs;
            this.size = size;
            this.schedClass = schedClass;
            this.flags = flags;
            this.tsFlags = tsFlags;
            this.implicitUses = implicitUses;
            this.implicitDefs = implicitDefs;
            this.opInfo = opInfo;
            this.deprecatedFeature = deprecatedFeature;
        }
    }
}
