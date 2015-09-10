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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

public class Regs
{
    private Regs()
    {
    }

    protected static final ImmutableList.Builder<Reg> allRegsBuilder = ImmutableList.builder();
    protected static final ImmutableMap.Builder<String, Reg> regsByNameBuilder = ImmutableMap.builder();
    protected static final Map<Class<? extends Reg>, ImmutableList.Builder<Reg>> regsByClassBuilders = newHashMap();

    public static abstract class Reg<ParentReg extends Reg, ChildReg extends Reg>
    {
        public enum Type
        {
            GENERAL_PURPOSE,
            SEGMENT,
            FLAGS,
            INSTRUCTION_POINTER,
            CONTROL,
            MEMORY_MANAGEMENT,
            DEBUG,
            MEMORY_TYPE_RANGE,
            MACHINE_SPECIFIC,
            MACHINE_CHECK,
            PERFORMANCE_MONITORING_COUNTER,
            FPU,
            MMX,
            XMM,
            YMM,
            ZMM,
            DESCRIPTOR_TABLE,
        }

        public enum Flags
        {
            EXTENDED_ONLY,
            NOT_EXTENDED_ONLY,
            VIRTUAL,
            VARIABLE_WIDTH,
            SPECIAL,
            SSE_ONLY,
            VEX_ONLY,
            EVEX_ONLY,
        }

        public static final EnumSet<Flags> NO_FLAGS = EnumSet.noneOf(Flags.class);

        private final String[] names;
        private final Type type;
        private final EnumSet<Flags> flags;
        private final int number;
        private final ChildReg low;
        private final ChildReg high;
        private ParentReg parent;

        public String getName()
        {
            return names[0];
        }

        public EnumSet<Flags> getFlags()
        {
            return flags;
        }

        public Type getType()
        {
            return type;
        }

        public ChildReg getLow()
        {
            return low;
        }

        public ChildReg getHigh()
        {
            return high;
        }

        public ParentReg getParent()
        {
            return parent;
        }

        public void setParent(ParentReg parent)
        {
            if (this.parent != null) {
                throw new IllegalAccessError();
            }
            this.parent = parent;
        }

        public Reg getRoot()
        {
            return parent == null ? this : parent.getRoot();
        }

        public int getWidth()
        {
            throw new IllegalStateException();
        }

        protected Reg(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low, ChildReg high)
        {
            Preconditions.checkNotNull(names);
            Preconditions.checkNotNull(type);
            Preconditions.checkNotNull(flags);
            Preconditions.checkArgument(names.length > 0);
            this.names = names;
            this.type = type;
            this.flags = flags.clone();
            this.number = number;
            this.low = low;
            this.high = high;
            if (low != null) {
                low.setParent(this);
            }
            if (high != null) {
                high.setParent(this);
            }
            allRegsBuilder.add(this);
            for (String name : names) {
                regsByNameBuilder.put(name, this);
            }
            ImmutableList.Builder<Reg> regsByClassSetBuilder = regsByClassBuilders.get(getClass());
            if (regsByClassSetBuilder == null) {
                regsByClassBuilders.put(getClass(), regsByClassSetBuilder = ImmutableList.builder());
            }
            regsByClassSetBuilder.add(this);
        }

        public String toDetailedString()
        {
            return Objects.toStringHelper(this)
                    .add("names", Arrays.toString(names))
                    .add("type", type)
                    .add("flags", flags)
                    .add("number", number)
                    .add("low", low != null ? low.getName() : null)
                    .add("high", high != null ? high.getName() : null)
                    .add("parent", parent != null ? parent.getName() : null)
                    .toString();
        }

        @Override
        public String toString()
        {
            return getName();
        }
    }

    public static abstract class InvalidReg
            extends Reg<InvalidReg, InvalidReg>
    {

        private InvalidReg(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg low, InvalidReg high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public static abstract class Reg8<ParentReg extends Reg16>
            extends Reg<ParentReg, InvalidReg>
    {

        public Reg8(String[] names, Type type, EnumSet<Flags> flags, int number)
        {
            super(names, type, flags, number, null, null);
            flags.add(Flags.VIRTUAL);
        }

        @Override
        public int getWidth()
        {
            return 8;
        }
    }

    public static abstract class InvalidReg8
            extends Reg8<InvalidReg16>
    {

        private InvalidReg8(String[] names, Type type, EnumSet<Flags> flags, int number)
        {
            super(names, type, flags, number);
        }
    }

    public static class GPReg8
            extends Reg8<GPReg16>
    {

        public GPReg8(String[] names, EnumSet<Flags> flags, int number)
        {
            super(names, Type.GENERAL_PURPOSE, flags, number);
        }

        public GPReg8(String[] names, int number)
        {
            this(names, NO_FLAGS, number);
        }
    }

    public static abstract class Reg16<ParentReg extends Reg32, ChildReg extends Reg8>
            extends Reg<ParentReg, ChildReg>
    {

        public Reg16(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low, ChildReg high)
        {
            super(names, type, flags, number, low, high);
        }

        @Override
        public int getWidth()
        {
            return 16;
        }
    }

    public static abstract class InvalidReg16
            extends Reg16<InvalidReg32, InvalidReg8>
    {

        private InvalidReg16(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg8 low, InvalidReg8 high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public static class GPReg16
            extends Reg16<GPReg32, GPReg8>
    {

        public GPReg16(String[] names, EnumSet<Flags> flags, int number, GPReg8 low, GPReg8 high)
        {
            super(names, Type.GENERAL_PURPOSE, flags, number, low, high);
        }

        public GPReg16(String[] names, int number, GPReg8 low, GPReg8 high)
        {
            this(names, NO_FLAGS, number, low, high);
        }

        public GPReg16(String[] names, EnumSet<Flags> flags, int number, GPReg8 low)
        {
            this(names, flags, number, low, null);
        }

        public GPReg16(String[] names, int number, GPReg8 low)
        {
            this(names, number, low, null);
        }
    }

    public static class SegReg
            extends Reg16<InvalidReg32, InvalidReg8>
    {

        public SegReg(String[] names, int number)
        {
            super(names, Type.SEGMENT, NO_FLAGS, number, null, null);
        }
    }

    public static abstract class Reg32<ParentReg extends Reg64, ChildReg extends Reg16>
            extends Reg<ParentReg, ChildReg>
    {

        public Reg32(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low)
        {
            super(names, type, flags, number, low, null);
        }

        @Override
        public int getWidth()
        {
            return 32;
        }
    }

    public static abstract class InvalidReg32
            extends Reg32<InvalidReg64, InvalidReg16>
    {

        protected InvalidReg32(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg16 low)
        {
            super(names, type, flags, number, low);
        }
    }

    public static class GPReg32
            extends Reg32<GPReg64, GPReg16>
    {

        public GPReg32(String[] names, EnumSet<Flags> flags, int number, GPReg16 low)
        {
            super(names, Type.GENERAL_PURPOSE, flags, number, low);
        }

        public GPReg32(String[] names, int number, GPReg16 low)
        {
            this(names, NO_FLAGS, number, low);
        }
    }

    public static class EFlags
            extends Reg32<RFlags, InvalidReg16>
    {

        public EFlags(String[] names)
        {
            super(names, Type.FLAGS, NO_FLAGS, -1, null);
        }
    }

    public static class EIP
            extends Reg32<RIP, InvalidReg16>
    {

        public EIP(String[] names)
        {
            super(names, Type.INSTRUCTION_POINTER, NO_FLAGS, -1, null);
        }
    }

    public static abstract class Reg64<ParentReg extends Reg128, ChildReg extends Reg32>
            extends Reg<ParentReg, ChildReg>
    {

        public Reg64(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low)
        {
            super(names, type, flags, number, low, null);
            flags.add(Flags.EXTENDED_ONLY);
        }

        @Override
        public int getWidth()
        {
            return 64;
        }
    }

    public static abstract class InvalidReg64
            extends Reg64<InvalidReg128, InvalidReg32>
    {

        private InvalidReg64(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg32 low)
        {
            super(names, type, flags, number, low);
        }
    }

    public static class GPReg64
            extends Reg64<InvalidReg128, GPReg32>
    {

        public GPReg64(String[] names, EnumSet<Flags> flags, int number, GPReg32 low)
        {
            super(names, Type.GENERAL_PURPOSE, flags, number, low);
        }

        public GPReg64(String[] names, int number, GPReg32 low)
        {
            this(names, NO_FLAGS, number, low);
        }
    }

    // FIXME: aliasOf
    public static class MMXReg
            extends Reg64<InvalidReg128, InvalidReg32>
    {

        public MMXReg(String[] names, int number)
        {
            super(names, Type.MMX, NO_FLAGS, number, null);
        }
    }

    public static class RFlags
            extends Reg64<InvalidReg128, EFlags>
    {

        public RFlags(String[] names, EFlags low)
        {
            super(names, Type.FLAGS, NO_FLAGS, -1, low);
        }
    }

    public static class RIP
            extends Reg64<InvalidReg128, EIP>
    {

        public RIP(String[] names, EIP low)
        {
            super(names, Type.INSTRUCTION_POINTER, NO_FLAGS, -1, low);
        }
    }

    public static abstract class Reg80
            extends Reg<InvalidReg, InvalidReg>
    {

        protected Reg80(String[] names, Type type, EnumSet<Flags> flags, int number)
        {
            super(names, type, flags, number, null, null);
        }
    }

    public static class FPUReg
            extends Reg80
    {

        public FPUReg(String[] names, int number)
        {
            super(names, Type.FPU, NO_FLAGS, number);
        }
    }

    public static abstract class Reg128<ParentReg extends Reg256, ChildReg extends Reg64>
            extends Reg<ParentReg, ChildReg>
    {

        protected Reg128(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low, ChildReg high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public abstract static class InvalidReg128
            extends Reg128<InvalidReg256, InvalidReg64>
    {

        public InvalidReg128(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg64 low, InvalidReg64 high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public static class XMMReg
            extends Reg128<YMMReg, InvalidReg64>
    {

        public XMMReg(String[] names, EnumSet<Flags> flags, int number)
        {
            super(names, Type.XMM, flags, number, null, null);
        }

        public XMMReg(String[] names, int number)
        {
            this(names, NO_FLAGS, number);
        }
    }

    public static abstract class Reg256<ParentReg extends Reg512, ChildReg extends Reg128>
            extends Reg<ParentReg, ChildReg>
    {

        protected Reg256(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low, ChildReg high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public abstract static class InvalidReg256
            extends Reg256<InvalidReg512, InvalidReg128>
    {

        public InvalidReg256(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg128 low, InvalidReg128 high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public static class YMMReg
            extends Reg256<ZMMReg, XMMReg>
    {

        public YMMReg(String[] names, EnumSet<Flags> flags, int number, XMMReg low)
        {
            super(names, Type.YMM, flags, number, low, null);
        }

        public YMMReg(String[] names, int number, XMMReg low)
        {
            this(names, NO_FLAGS, number, low);
        }
    }

    public static abstract class Reg512<ChildReg extends Reg256>
            extends Reg<InvalidReg, ChildReg>
    {

        protected Reg512(String[] names, Type type, EnumSet<Flags> flags, int number, ChildReg low, ChildReg high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public abstract static class InvalidReg512
            extends Reg512<InvalidReg256>
    {

        public InvalidReg512(String[] names, Type type, EnumSet<Flags> flags, int number, InvalidReg256 low, InvalidReg256 high)
        {
            super(names, type, flags, number, low, high);
        }
    }

    public static class ZMMReg
            extends Reg512<YMMReg>
    {

        public ZMMReg(String[] names, int number, YMMReg low)
        {
            super(names, Type.ZMM, NO_FLAGS, number, low, null);
        }
    }

    public static abstract class RegV
            extends Reg<InvalidReg, InvalidReg>
    {

        public RegV(String[] names, Type type, EnumSet<Flags> flags, int number)
        {
            super(names, type, flags, number, null, null);
            flags.add(Flags.VARIABLE_WIDTH);
        }
    }

    public static class CReg
            extends RegV
    {

        public CReg(String[] names, EnumSet<Flags> flags, int number)
        {
            super(names, Type.CONTROL, flags, number);
        }

        public CReg(String[] names, int number)
        {
            this(names, NO_FLAGS, number);
        }
    }

    public static class DReg
            extends RegV
    {

        public DReg(String[] names, EnumSet<Flags> flags, int number)
        {
            super(names, Type.DEBUG, flags, number);
        }

        public DReg(String[] names, int number)
        {
            this(names, NO_FLAGS, number);
        }
    }

    public static final GPReg8 al = new GPReg8(new String[] {"al", "r0b"}, 0);
    public static final GPReg8 ah = new GPReg8(new String[] {"ah"}, EnumSet.of(Reg.Flags.NOT_EXTENDED_ONLY), 0);
    public static final GPReg8 cl = new GPReg8(new String[] {"cl", "r1b"}, 1);
    public static final GPReg8 ch = new GPReg8(new String[] {"ch"}, EnumSet.of(Reg.Flags.NOT_EXTENDED_ONLY), 1);
    public static final GPReg8 bl = new GPReg8(new String[] {"bl", "r2b"}, 2);
    public static final GPReg8 bh = new GPReg8(new String[] {"bh"}, EnumSet.of(Reg.Flags.NOT_EXTENDED_ONLY), 2);
    public static final GPReg8 dl = new GPReg8(new String[] {"dl", "r3b"}, 3);
    public static final GPReg8 dh = new GPReg8(new String[] {"dh"}, EnumSet.of(Reg.Flags.NOT_EXTENDED_ONLY), 3);
    public static final GPReg8 spl = new GPReg8(new String[] {"spl", "r4b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 4);
    public static final GPReg8 bpl = new GPReg8(new String[] {"bpl", "r5b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 5);
    public static final GPReg8 sil = new GPReg8(new String[] {"sil", "r6b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 6);
    public static final GPReg8 dil = new GPReg8(new String[] {"dil", "r7b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 7);

    public static final GPReg16 ax = new GPReg16(new String[] {"ax"}, 0, ah, al);
    public static final GPReg16 cx = new GPReg16(new String[] {"cx"}, 1, ch, cl);
    public static final GPReg16 bx = new GPReg16(new String[] {"bx"}, 2, bh, bl);
    public static final GPReg16 dx = new GPReg16(new String[] {"dx"}, 3, dh, dl);
    public static final GPReg16 sp = new GPReg16(new String[] {"sp"}, 4, spl);
    public static final GPReg16 bp = new GPReg16(new String[] {"bp"}, 5, bpl);
    public static final GPReg16 si = new GPReg16(new String[] {"si"}, 6, sil);
    public static final GPReg16 di = new GPReg16(new String[] {"di"}, 7, dil);

    public static final SegReg es = new SegReg(new String[] {"es"}, 0);
    public static final SegReg cs = new SegReg(new String[] {"cs"}, 1);
    public static final SegReg ss = new SegReg(new String[] {"ss"}, 2);
    public static final SegReg ds = new SegReg(new String[] {"ds"}, 3);
    public static final SegReg fs = new SegReg(new String[] {"fs"}, 4);
    public static final SegReg gs = new SegReg(new String[] {"gs"}, 5);

    public static final GPReg32 eax = new GPReg32(new String[] {"eax"}, 0, ax);
    public static final GPReg32 ecx = new GPReg32(new String[] {"ecx"}, 1, cx);
    public static final GPReg32 ebx = new GPReg32(new String[] {"ebx"}, 2, bx);
    public static final GPReg32 edx = new GPReg32(new String[] {"edx"}, 3, dx);
    public static final GPReg32 esp = new GPReg32(new String[] {"esp"}, 4, sp);
    public static final GPReg32 ebp = new GPReg32(new String[] {"ebp"}, 5, bp);
    public static final GPReg32 esi = new GPReg32(new String[] {"esi"}, 6, si);
    public static final GPReg32 edi = new GPReg32(new String[] {"edi"}, 7, di);

    public static final GPReg64 rax = new GPReg64(new String[] {"rax"}, 0, eax);
    public static final GPReg64 rcx = new GPReg64(new String[] {"rcx"}, 1, ecx);
    public static final GPReg64 rbx = new GPReg64(new String[] {"rbx"}, 2, ebx);
    public static final GPReg64 rdx = new GPReg64(new String[] {"rdx"}, 3, edx);
    public static final GPReg64 rsp = new GPReg64(new String[] {"rsp"}, 4, esp);
    public static final GPReg64 rbp = new GPReg64(new String[] {"rbp"}, 5, ebp);
    public static final GPReg64 rsi = new GPReg64(new String[] {"rsi"}, 6, esi);
    public static final GPReg64 rdi = new GPReg64(new String[] {"rdi"}, 7, edi);

    public static final GPReg8 r8b = new GPReg8(new String[] {"r8b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 8);
    public static final GPReg8 r9b = new GPReg8(new String[] {"r9b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 9);
    public static final GPReg8 r10b = new GPReg8(new String[] {"r10b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 10);
    public static final GPReg8 r11b = new GPReg8(new String[] {"r11b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 11);
    public static final GPReg8 r12b = new GPReg8(new String[] {"r12b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 12);
    public static final GPReg8 r13b = new GPReg8(new String[] {"r13b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 13);
    public static final GPReg8 r14b = new GPReg8(new String[] {"r14b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 14);
    public static final GPReg8 r15b = new GPReg8(new String[] {"r15b"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 15);

    public static final GPReg16 r8w = new GPReg16(new String[] {"r8w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 8, r8b);
    public static final GPReg16 r9w = new GPReg16(new String[] {"r9w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 9, r9b);
    public static final GPReg16 r10w = new GPReg16(new String[] {"r10w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 10, r10b);
    public static final GPReg16 r11w = new GPReg16(new String[] {"r11w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 11, r11b);
    public static final GPReg16 r12w = new GPReg16(new String[] {"r12w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 12, r12b);
    public static final GPReg16 r13w = new GPReg16(new String[] {"r13w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 13, r13b);
    public static final GPReg16 r14w = new GPReg16(new String[] {"r14w"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 14, r14b);
    public static final GPReg16 r15w = new GPReg16(new String[] {"r1fw"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 15, r15b);

    public static final GPReg32 r8d = new GPReg32(new String[] {"r8d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 8, r8w);
    public static final GPReg32 r9d = new GPReg32(new String[] {"r9d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 9, r9w);
    public static final GPReg32 r10d = new GPReg32(new String[] {"r10d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 10, r10w);
    public static final GPReg32 r11d = new GPReg32(new String[] {"r11d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 11, r11w);
    public static final GPReg32 r12d = new GPReg32(new String[] {"r12d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 12, r12w);
    public static final GPReg32 r13d = new GPReg32(new String[] {"r13d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 13, r13w);
    public static final GPReg32 r14d = new GPReg32(new String[] {"r14d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 14, r14w);
    public static final GPReg32 r15d = new GPReg32(new String[] {"r15d"}, EnumSet.of(Reg.Flags.EXTENDED_ONLY), 15, r15w);

    public static final GPReg64 r8 = new GPReg64(new String[] {"r8"}, 8, r8d);
    public static final GPReg64 r9 = new GPReg64(new String[] {"r9"}, 9, r9d);
    public static final GPReg64 r10 = new GPReg64(new String[] {"r10"}, 10, r10d);
    public static final GPReg64 r11 = new GPReg64(new String[] {"r11"}, 11, r11d);
    public static final GPReg64 r12 = new GPReg64(new String[] {"r12"}, 12, r12d);
    public static final GPReg64 r13 = new GPReg64(new String[] {"r13"}, 13, r13d);
    public static final GPReg64 r14 = new GPReg64(new String[] {"r14"}, 14, r14d);
    public static final GPReg64 r15 = new GPReg64(new String[] {"r15"}, 15, r15d);

    public static final EFlags eflags = new EFlags(new String[] {"eflags"});
    public static final RFlags rflags = new RFlags(new String[] {"rflags"}, eflags);

    public static final EIP eip = new EIP(new String[] {"eip"});
    public static final RIP rip = new RIP(new String[] {"rip"}, eip);

    public static final CReg cr0 = new CReg(new String[] {"cr0"}, 0);
    public static final CReg cr1 = new CReg(new String[] {"cr1"}, 1);
    public static final CReg cr2 = new CReg(new String[] {"cr2"}, 2);
    public static final CReg cr3 = new CReg(new String[] {"cr3"}, 3);
    public static final CReg cr4 = new CReg(new String[] {"cr4"}, 4);
    public static final CReg cr5 = new CReg(new String[] {"cr5"}, 5);
    public static final CReg cr6 = new CReg(new String[] {"cr6"}, 6);
    public static final CReg cr7 = new CReg(new String[] {"cr7"}, 7);
    public static final CReg cr8 = new CReg(new String[] {"cr8"}, 8);
    public static final CReg cr9 = new CReg(new String[] {"cr9"}, 9);
    public static final CReg cr10 = new CReg(new String[] {"cr10"}, 10);
    public static final CReg cr11 = new CReg(new String[] {"cr11"}, 11);
    public static final CReg cr12 = new CReg(new String[] {"cr12"}, 12);
    public static final CReg cr13 = new CReg(new String[] {"cr13"}, 13);
    public static final CReg cr14 = new CReg(new String[] {"cr14"}, 14);
    public static final CReg cr15 = new CReg(new String[] {"cr15"}, 15);

    public static final DReg dr0 = new DReg(new String[] {"dr0"}, 0);
    public static final DReg dr1 = new DReg(new String[] {"dr1"}, 1);
    public static final DReg dr2 = new DReg(new String[] {"dr2"}, 2);
    public static final DReg dr3 = new DReg(new String[] {"dr3"}, 3);
    public static final DReg dr4 = new DReg(new String[] {"dr4"}, 4);
    public static final DReg dr5 = new DReg(new String[] {"dr5"}, 5);
    public static final DReg dr6 = new DReg(new String[] {"dr6"}, 6);
    public static final DReg dr7 = new DReg(new String[] {"dr7"}, 7);
    public static final DReg dr8 = new DReg(new String[] {"dr8"}, 8);
    public static final DReg dr9 = new DReg(new String[] {"dr9"}, 9);
    public static final DReg dr10 = new DReg(new String[] {"dr10"}, 10);
    public static final DReg dr11 = new DReg(new String[] {"dr11"}, 11);
    public static final DReg dr12 = new DReg(new String[] {"dr12"}, 12);
    public static final DReg dr13 = new DReg(new String[] {"dr13"}, 13);
    public static final DReg dr14 = new DReg(new String[] {"dr14"}, 14);
    public static final DReg dr15 = new DReg(new String[] {"dr15"}, 15);

    public static final FPUReg st0 = new FPUReg(new String[] {"st0"}, 0);
    public static final FPUReg st1 = new FPUReg(new String[] {"st1"}, 1);
    public static final FPUReg st2 = new FPUReg(new String[] {"st2"}, 2);
    public static final FPUReg st3 = new FPUReg(new String[] {"st3"}, 3);
    public static final FPUReg st4 = new FPUReg(new String[] {"st4"}, 4);
    public static final FPUReg st5 = new FPUReg(new String[] {"st5"}, 5);
    public static final FPUReg st6 = new FPUReg(new String[] {"st6"}, 6);
    public static final FPUReg st7 = new FPUReg(new String[] {"st7"}, 7);

    public static final MMXReg mm0 = new MMXReg(new String[] {"mm0"}, 0);
    public static final MMXReg mm1 = new MMXReg(new String[] {"mm1"}, 1);
    public static final MMXReg mm2 = new MMXReg(new String[] {"mm2"}, 2);
    public static final MMXReg mm3 = new MMXReg(new String[] {"mm3"}, 3);
    public static final MMXReg mm4 = new MMXReg(new String[] {"mm4"}, 4);
    public static final MMXReg mm5 = new MMXReg(new String[] {"mm5"}, 5);
    public static final MMXReg mm6 = new MMXReg(new String[] {"mm6"}, 6);
    public static final MMXReg mm7 = new MMXReg(new String[] {"mm7"}, 7);

    public static final XMMReg xmm0 = new XMMReg(new String[] {"xmm0"}, 0);
    public static final XMMReg xmm1 = new XMMReg(new String[] {"xmm1"}, 1);
    public static final XMMReg xmm2 = new XMMReg(new String[] {"xmm2"}, 2);
    public static final XMMReg xmm3 = new XMMReg(new String[] {"xmm3"}, 3);
    public static final XMMReg xmm4 = new XMMReg(new String[] {"xmm4"}, 4);
    public static final XMMReg xmm5 = new XMMReg(new String[] {"xmm5"}, 5);
    public static final XMMReg xmm6 = new XMMReg(new String[] {"xmm6"}, 6);
    public static final XMMReg xmm7 = new XMMReg(new String[] {"xmm7"}, 7);
    public static final XMMReg xmm8 = new XMMReg(new String[] {"xmm8"}, EnumSet.of(Reg.Flags.VEX_ONLY), 8);
    public static final XMMReg xmm9 = new XMMReg(new String[] {"xmm9"}, EnumSet.of(Reg.Flags.VEX_ONLY), 9);
    public static final XMMReg xmm10 = new XMMReg(new String[] {"xmm10"}, EnumSet.of(Reg.Flags.VEX_ONLY), 10);
    public static final XMMReg xmm11 = new XMMReg(new String[] {"xmm11"}, EnumSet.of(Reg.Flags.VEX_ONLY), 11);
    public static final XMMReg xmm12 = new XMMReg(new String[] {"xmm12"}, EnumSet.of(Reg.Flags.VEX_ONLY), 12);
    public static final XMMReg xmm13 = new XMMReg(new String[] {"xmm13"}, EnumSet.of(Reg.Flags.VEX_ONLY), 13);
    public static final XMMReg xmm14 = new XMMReg(new String[] {"xmm14"}, EnumSet.of(Reg.Flags.VEX_ONLY), 14);
    public static final XMMReg xmm15 = new XMMReg(new String[] {"xmm15"}, EnumSet.of(Reg.Flags.VEX_ONLY), 15);
    public static final XMMReg xmm16 = new XMMReg(new String[] {"xmm16"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 16);
    public static final XMMReg xmm17 = new XMMReg(new String[] {"xmm17"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 17);
    public static final XMMReg xmm18 = new XMMReg(new String[] {"xmm18"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 18);
    public static final XMMReg xmm19 = new XMMReg(new String[] {"xmm19"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 19);
    public static final XMMReg xmm20 = new XMMReg(new String[] {"xmm20"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 20);
    public static final XMMReg xmm21 = new XMMReg(new String[] {"xmm21"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 21);
    public static final XMMReg xmm22 = new XMMReg(new String[] {"xmm22"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 22);
    public static final XMMReg xmm23 = new XMMReg(new String[] {"xmm23"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 23);
    public static final XMMReg xmm24 = new XMMReg(new String[] {"xmm24"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 24);
    public static final XMMReg xmm25 = new XMMReg(new String[] {"xmm25"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 25);
    public static final XMMReg xmm26 = new XMMReg(new String[] {"xmm26"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 26);
    public static final XMMReg xmm27 = new XMMReg(new String[] {"xmm27"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 27);
    public static final XMMReg xmm28 = new XMMReg(new String[] {"xmm28"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 28);
    public static final XMMReg xmm29 = new XMMReg(new String[] {"xmm29"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 29);
    public static final XMMReg xmm30 = new XMMReg(new String[] {"xmm30"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 30);
    public static final XMMReg xmm31 = new XMMReg(new String[] {"xmm31"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 31);

    public static final YMMReg ymm0 = new YMMReg(new String[] {"ymm0"}, 0, xmm0);
    public static final YMMReg ymm1 = new YMMReg(new String[] {"ymm1"}, 1, xmm1);
    public static final YMMReg ymm2 = new YMMReg(new String[] {"ymm2"}, 2, xmm2);
    public static final YMMReg ymm3 = new YMMReg(new String[] {"ymm3"}, 3, xmm3);
    public static final YMMReg ymm4 = new YMMReg(new String[] {"ymm4"}, 4, xmm4);
    public static final YMMReg ymm5 = new YMMReg(new String[] {"ymm5"}, 5, xmm5);
    public static final YMMReg ymm6 = new YMMReg(new String[] {"ymm6"}, 6, xmm6);
    public static final YMMReg ymm7 = new YMMReg(new String[] {"ymm7"}, 7, xmm7);
    public static final YMMReg ymm8 = new YMMReg(new String[] {"ymm8"}, 8, xmm8);
    public static final YMMReg ymm9 = new YMMReg(new String[] {"ymm9"}, 9, xmm9);
    public static final YMMReg ymm10 = new YMMReg(new String[] {"ymm10"}, 10, xmm10);
    public static final YMMReg ymm11 = new YMMReg(new String[] {"ymm11"}, 11, xmm11);
    public static final YMMReg ymm12 = new YMMReg(new String[] {"ymm12"}, 12, xmm12);
    public static final YMMReg ymm13 = new YMMReg(new String[] {"ymm13"}, 13, xmm13);
    public static final YMMReg ymm14 = new YMMReg(new String[] {"ymm14"}, 14, xmm14);
    public static final YMMReg ymm15 = new YMMReg(new String[] {"ymm15"}, 15, xmm15);
    public static final YMMReg ymm16 = new YMMReg(new String[] {"ymm16"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 16, xmm16);
    public static final YMMReg ymm17 = new YMMReg(new String[] {"ymm17"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 17, xmm17);
    public static final YMMReg ymm18 = new YMMReg(new String[] {"ymm18"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 18, xmm18);
    public static final YMMReg ymm19 = new YMMReg(new String[] {"ymm19"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 19, xmm19);
    public static final YMMReg ymm20 = new YMMReg(new String[] {"ymm20"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 20, xmm20);
    public static final YMMReg ymm21 = new YMMReg(new String[] {"ymm21"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 21, xmm21);
    public static final YMMReg ymm22 = new YMMReg(new String[] {"ymm22"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 22, xmm22);
    public static final YMMReg ymm23 = new YMMReg(new String[] {"ymm23"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 23, xmm23);
    public static final YMMReg ymm24 = new YMMReg(new String[] {"ymm24"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 24, xmm24);
    public static final YMMReg ymm25 = new YMMReg(new String[] {"ymm25"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 25, xmm25);
    public static final YMMReg ymm26 = new YMMReg(new String[] {"ymm26"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 26, xmm26);
    public static final YMMReg ymm27 = new YMMReg(new String[] {"ymm27"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 27, xmm27);
    public static final YMMReg ymm28 = new YMMReg(new String[] {"ymm28"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 28, xmm28);
    public static final YMMReg ymm29 = new YMMReg(new String[] {"ymm29"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 29, xmm29);
    public static final YMMReg ymm30 = new YMMReg(new String[] {"ymm30"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 30, xmm30);
    public static final YMMReg ymm31 = new YMMReg(new String[] {"ymm31"}, EnumSet.of(Reg.Flags.EVEX_ONLY), 31, xmm31);

    public static final ZMMReg zmm0 = new ZMMReg(new String[] {"zmm0"}, 0, ymm0);
    public static final ZMMReg zmm1 = new ZMMReg(new String[] {"zmm1"}, 1, ymm1);
    public static final ZMMReg zmm2 = new ZMMReg(new String[] {"zmm2"}, 2, ymm2);
    public static final ZMMReg zmm3 = new ZMMReg(new String[] {"zmm3"}, 3, ymm3);
    public static final ZMMReg zmm4 = new ZMMReg(new String[] {"zmm4"}, 4, ymm4);
    public static final ZMMReg zmm5 = new ZMMReg(new String[] {"zmm5"}, 5, ymm5);
    public static final ZMMReg zmm6 = new ZMMReg(new String[] {"zmm6"}, 6, ymm6);
    public static final ZMMReg zmm7 = new ZMMReg(new String[] {"zmm7"}, 7, ymm7);
    public static final ZMMReg zmm8 = new ZMMReg(new String[] {"zmm8"}, 8, ymm8);
    public static final ZMMReg zmm9 = new ZMMReg(new String[] {"zmm9"}, 9, ymm9);
    public static final ZMMReg zmm10 = new ZMMReg(new String[] {"zmm10"}, 10, ymm10);
    public static final ZMMReg zmm11 = new ZMMReg(new String[] {"zmm11"}, 11, ymm11);
    public static final ZMMReg zmm12 = new ZMMReg(new String[] {"zmm12"}, 12, ymm12);
    public static final ZMMReg zmm13 = new ZMMReg(new String[] {"zmm13"}, 13, ymm13);
    public static final ZMMReg zmm14 = new ZMMReg(new String[] {"zmm14"}, 14, ymm14);
    public static final ZMMReg zmm15 = new ZMMReg(new String[] {"zmm15"}, 15, ymm15);
    public static final ZMMReg zmm16 = new ZMMReg(new String[] {"zmm16"}, 16, ymm16);
    public static final ZMMReg zmm17 = new ZMMReg(new String[] {"zmm17"}, 17, ymm17);
    public static final ZMMReg zmm18 = new ZMMReg(new String[] {"zmm18"}, 18, ymm18);
    public static final ZMMReg zmm19 = new ZMMReg(new String[] {"zmm19"}, 19, ymm19);
    public static final ZMMReg zmm20 = new ZMMReg(new String[] {"zmm20"}, 20, ymm20);
    public static final ZMMReg zmm21 = new ZMMReg(new String[] {"zmm21"}, 21, ymm21);
    public static final ZMMReg zmm22 = new ZMMReg(new String[] {"zmm22"}, 22, ymm22);
    public static final ZMMReg zmm23 = new ZMMReg(new String[] {"zmm23"}, 23, ymm23);
    public static final ZMMReg zmm24 = new ZMMReg(new String[] {"zmm24"}, 24, ymm24);
    public static final ZMMReg zmm25 = new ZMMReg(new String[] {"zmm25"}, 25, ymm25);
    public static final ZMMReg zmm26 = new ZMMReg(new String[] {"zmm26"}, 26, ymm26);
    public static final ZMMReg zmm27 = new ZMMReg(new String[] {"zmm27"}, 27, ymm27);
    public static final ZMMReg zmm28 = new ZMMReg(new String[] {"zmm28"}, 28, ymm28);
    public static final ZMMReg zmm29 = new ZMMReg(new String[] {"zmm29"}, 29, ymm29);
    public static final ZMMReg zmm30 = new ZMMReg(new String[] {"zmm30"}, 30, ymm30);
    public static final ZMMReg zmm31 = new ZMMReg(new String[] {"zmm31"}, 31, ymm31);

    public static final ImmutableList<Reg> allRegs = allRegsBuilder.build();
    public static final ImmutableMap<String, Reg> regsByName = regsByNameBuilder.build();
    public static final ImmutableMap<Class<? extends Reg>, ImmutableList<Reg>> regsByClass =
            ImmutableMap.<Class<? extends Reg>, ImmutableList<Reg>>builder()
                    .putAll(regsByClassBuilders.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().build())))
                    .build();

    // aliasTarget
    // regsByClass
    // regClassesByWidth
    // gpRegsByOrdinal
    // interface Reg, abstract class AbstractReg implements Reg?
    // interface GPReg

    public static class RegClassMap
    {

        public final ImmutableMap<Class<? extends Reg>, Reg> regsByClass;

        public RegClassMap(ImmutableMap<Class<? extends Reg>, Reg> regsByClass)
        {
            this.regsByClass = regsByClass;
        }

        public RegClassMap(Iterable<Reg> regs)
        {
            ImmutableMap.Builder<Class<? extends Reg>, Reg> builder = ImmutableMap.builder();
            for (Reg reg : regs) {
                if (reg != null) {
                    builder.put(reg.getClass(), reg);
                }
            }
            this.regsByClass = builder.build();
        }

        public RegClassMap(Reg... regs)
        {
            this(Arrays.asList(regs));
        }

        public Reg get(Class<? extends Reg> klass)
        {
            return regsByClass.get(klass);
        }

        public RegClassMap update(Iterable<Reg> regs)
        {
            Set<Class<? extends Reg>> updates = Sets.newHashSet();
            ImmutableMap.Builder<Class<? extends Reg>, Reg> builder = ImmutableMap.builder();
            for (Reg reg : regs) {
                if (reg != null) {
                    builder.put(reg.getClass(), reg);
                    updates.add(reg.getClass());
                }
            }
            for (Map.Entry<Class<? extends Reg>, Reg> entry : regsByClass.entrySet()) {
                if (!updates.contains(entry.getKey())) {
                    builder.put(entry.getKey(), entry.getValue());
                }
            }
            return new RegClassMap(builder.build());
        }

        public RegClassMap update(Reg... regs)
        {
            return update(Arrays.asList(regs));
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("{");
            List<Map.Entry<Class<? extends Reg>, Reg>> entries = Lists.newArrayList(regsByClass.entrySet());
            for (int i = 0; i < entries.size(); ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                Map.Entry<Class<? extends Reg>, Reg> entry = entries.get(i);
                sb.append(entry.getKey().getSimpleName());
                sb.append("=");
                sb.append(entry.getValue());
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public static void main(String[] args)
            throws Exception
    {
        for (Reg reg : allRegs) {
            System.out.println(reg);
        }
    }
}
