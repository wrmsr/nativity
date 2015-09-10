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

import static com.wrmsr.nativity.x86.Regs.*;

public class ModRm
{
    private ModRm()
    {
    }

    public static abstract class Addr
    {
    }

    public static class RegAddr
            extends Addr
    {

        public final Reg reg;
        public final Reg addReg;
        public final int disp;

        public RegAddr(Reg reg, Reg addReg, int disp)
        {
            this.reg = reg;
            this.addReg = addReg;
            this.disp = disp;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("reg", reg)
                    .add("addReg", addReg)
                    .add("disp", disp)
                    .toString();
        }
    }

    public static class SIBAddr
            extends Addr
    {

        public final int disp;

        public SIBAddr(int disp)
        {
            this.disp = disp;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("disp", disp)
                    .toString();
        }
    }

    public static class RegsAddr
            extends Addr
    {

        public final RegClassMap regClassMap;

        public RegsAddr(RegClassMap regClassMap)
        {
            this.regClassMap = regClassMap;
        }

        public RegsAddr(Iterable<Reg> regs)
        {
            this(new RegClassMap(regs));
        }

        public RegsAddr(Reg... regs)
        {
            this(new RegClassMap(regs));
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("regClassMap", regClassMap)
                    .toString();
        }
    }

    public static final RegClassMap[] regs32NoRex = new RegClassMap[] {
            new RegClassMap(al, ax, eax, rax, mm0, xmm0, es, cr0, dr0),
            new RegClassMap(cl, cx, ecx, rcx, mm1, xmm1, cs, null, dr1),
            new RegClassMap(dl, dx, edx, rdx, mm2, xmm2, ss, cr2, dr2),
            new RegClassMap(bl, bx, ebx, rbx, mm3, xmm3, ds, cr3, dr3),
            new RegClassMap(ah, sp, esp, rsp, mm4, xmm4, fs, cr4, dr4),
            new RegClassMap(ch, bp, ebp, rbp, mm5, xmm5, gs, null, dr5),
            new RegClassMap(dh, si, esi, rsi, mm6, xmm6, null, null, dr6),
            new RegClassMap(bh, di, edi, rdi, mm7, xmm7, null, null, dr7),
    };

    public static final RegClassMap[] regs32RexR = new RegClassMap[] {
            new RegClassMap(r8b, r8w, r8d, r8, mm0, xmm8, es, cr8, null),
            new RegClassMap(r9b, r9w, r9d, r9, mm1, xmm9, cs, null, null),
            new RegClassMap(r10b, r10w, r10d, r10, mm2, xmm10, ss, null, null),
            new RegClassMap(r11b, r11w, r11d, r11, mm3, xmm11, ds, null, null),
            new RegClassMap(r12b, r12w, r12d, r12, mm4, xmm12, fs, null, null),
            new RegClassMap(r13b, r13w, r13d, r13, mm5, xmm13, gs, null, null),
            new RegClassMap(r14b, r14w, r14d, r14, mm6, xmm14, null, null, null),
            new RegClassMap(r15b, r15w, r15d, r15, mm7, xmm15, null, null, null),
    };

    public static final RegClassMap[] regs64NoRex = regs32NoRex;
    public static final RegClassMap[] regs64RexR = regs32RexR;

    public static final Addr[][] addrs32NoRex = new Addr[][] {
            new Addr[] {
                    new RegAddr(eax, null, 0),
                    new RegAddr(edx, null, 0),
                    new RegAddr(ecx, null, 0),
                    new RegAddr(ebx, null, 0),
                    new SIBAddr(0),
                    new RegAddr(eip, null, 32),
                    new RegAddr(esi, null, 0),
                    new RegAddr(edi, null, 0),
            },
            new Addr[] {
                    new RegAddr(eax, null, 8),
                    new RegAddr(edx, null, 8),
                    new RegAddr(ecx, null, 8),
                    new RegAddr(ebx, null, 8),
                    new SIBAddr(8),
                    new RegAddr(ebp, null, 8),
                    new RegAddr(esi, null, 8),
                    new RegAddr(edi, null, 8),
            },
            new Addr[] {
                    new RegAddr(eax, null, 32),
                    new RegAddr(edx, null, 32),
                    new RegAddr(ecx, null, 32),
                    new RegAddr(ebx, null, 32),
                    new SIBAddr(32),
                    new RegAddr(ebp, null, 32),
                    new RegAddr(esi, null, 32),
                    new RegAddr(edi, null, 32),
            },
            new Addr[] {
                    new RegsAddr(al, ax, eax, rax, st0, mm0, xmm0),
                    new RegsAddr(cl, cx, ecx, rcx, st1, mm1, xmm1),
                    new RegsAddr(dl, dx, edx, rdx, st2, mm2, xmm2),
                    new RegsAddr(bl, bx, ebx, rbx, st3, mm3, xmm3),
                    new RegsAddr(ah, sp, esp, rsp, st4, mm4, xmm4),
                    new RegsAddr(ch, bp, ebp, rbp, st5, mm5, xmm5),
                    new RegsAddr(dh, si, esi, rsi, st6, mm6, xmm6),
                    new RegsAddr(bh, di, edi, rdi, st7, mm7, xmm7),
            },
    };

    public static final Addr[][] addrs64NoRex = new Addr[][] {
            new Addr[] {
                    new RegAddr(rax, null, 0),
                    new RegAddr(rdx, null, 0),
                    new RegAddr(rcx, null, 0),
                    new RegAddr(rbx, null, 0),
                    new SIBAddr(0),
                    new RegAddr(rip, null, 32),
                    new RegAddr(rsi, null, 0),
                    new RegAddr(rdi, null, 0),
            },
            new Addr[] {
                    new RegAddr(rax, null, 8),
                    new RegAddr(rdx, null, 8),
                    new RegAddr(rcx, null, 8),
                    new RegAddr(rbx, null, 8),
                    new SIBAddr(8),
                    new RegAddr(rbp, null, 8),
                    new RegAddr(rsi, null, 8),
                    new RegAddr(rdi, null, 8),
            },
            new Addr[] {
                    new RegAddr(rax, null, 32),
                    new RegAddr(rdx, null, 32),
                    new RegAddr(rcx, null, 32),
                    new RegAddr(rbx, null, 32),
                    new SIBAddr(32),
                    new RegAddr(rbp, null, 32),
                    new RegAddr(rsi, null, 32),
                    new RegAddr(rdi, null, 32),
            },
            addrs32NoRex[3],
    };

    public static final Addr[][] addrs32RexB = new Addr[][] {
            new Addr[] {
                    new RegAddr(r8d, null, 0),
                    new RegAddr(r9d, null, 0),
                    new RegAddr(r10d, null, 0),
                    new RegAddr(r11d, null, 0),
                    new SIBAddr(0),
                    new RegAddr(eip, null, 32),
                    new RegAddr(r14d, null, 0),
                    new RegAddr(r15d, null, 0),
            },
            new Addr[] {
                    new RegAddr(r8d, null, 8),
                    new RegAddr(r9d, null, 8),
                    new RegAddr(r10d, null, 8),
                    new RegAddr(r11d, null, 8),
                    new SIBAddr(8),
                    new RegAddr(r13d, null, 8),
                    new RegAddr(r14d, null, 8),
                    new RegAddr(r15d, null, 8),
            },
            new Addr[] {
                    new RegAddr(r8d, null, 32),
                    new RegAddr(r9d, null, 32),
                    new RegAddr(r10d, null, 32),
                    new RegAddr(r11d, null, 32),
                    new SIBAddr(32),
                    new RegAddr(r13d, null, 32),
                    new RegAddr(r14d, null, 32),
                    new RegAddr(r15d, null, 32),
            },
            new Addr[] {
                    new RegsAddr(r8b, r8w, r8d, r8, st0, mm0, xmm8),
                    new RegsAddr(r9b, r9w, r9d, r9, st1, mm1, xmm9),
                    new RegsAddr(r10b, r10w, r10d, r10, st2, mm2, xmm10),
                    new RegsAddr(r11b, r11w, r11d, r11, st3, mm3, xmm11),
                    new RegsAddr(r12b, r12w, r12d, r12, st4, mm4, xmm12),
                    new RegsAddr(r13b, r13w, r13d, r13, st5, mm5, xmm13),
                    new RegsAddr(r14b, r14w, r14d, r14, st6, mm6, xmm14),
                    new RegsAddr(r15b, r15w, r15d, r15, st7, mm7, xmm15),
            },
    };

    public static final Addr[][] addrs64RexB = new Addr[][] {
            new Addr[] {
                    new RegAddr(r8, null, 0),
                    new RegAddr(r9, null, 0),
                    new RegAddr(r10, null, 0),
                    new RegAddr(r11, null, 0),
                    new SIBAddr(0),
                    new RegAddr(rip, null, 32),
                    new RegAddr(r14, null, 0),
                    new RegAddr(r15, null, 0),
            },
            new Addr[] {
                    new RegAddr(r8, null, 8),
                    new RegAddr(r9, null, 8),
                    new RegAddr(r10, null, 8),
                    new RegAddr(r11, null, 8),
                    new SIBAddr(8),
                    new RegAddr(r13, null, 8),
                    new RegAddr(r14, null, 8),
                    new RegAddr(r15, null, 8),
            },
            new Addr[] {
                    new RegAddr(r8, null, 32),
                    new RegAddr(r9, null, 32),
                    new RegAddr(r10, null, 32),
                    new RegAddr(r11, null, 32),
                    new SIBAddr(32),
                    new RegAddr(r13, null, 32),
                    new RegAddr(r14, null, 32),
                    new RegAddr(r15, null, 32),
            },
            addrs32RexB[3],
    };

    public static final RegClassMap[] regs16 = new RegClassMap[] {
            new RegClassMap(al, ax, eax, mm0, xmm0, es, cr0, dr0),
            new RegClassMap(cl, cx, ecx, mm1, xmm1, cs, null, dr1),
            new RegClassMap(dl, dx, edx, mm2, xmm2, ss, cr2, dr2),
            new RegClassMap(bl, bx, ebx, mm3, xmm3, ds, cr3, dr3),
            new RegClassMap(ah, sp, esp, mm4, xmm4, fs, cr4, dr4),
            new RegClassMap(ch, bp, ebp, mm5, xmm5, gs, null, dr5),
            new RegClassMap(dh, si, esi, mm6, xmm6, null, null, dr6),
            new RegClassMap(bh, di, edi, mm7, xmm7, null, null, dr7),
    };

    public static final Addr[][] addrs16 = new Addr[][] {
            new Addr[] {
                    new RegAddr(bx, si, 0),
                    new RegAddr(bx, di, 0),
                    new RegAddr(bp, si, 0),
                    new RegAddr(bp, di, 0),
                    new RegAddr(si, null, 0),
                    new RegAddr(di, null, 0),
                    new RegAddr(null, null, 16),
                    new RegAddr(bx, null, 0),
            },
            new Addr[] {
                    new RegAddr(bx, si, 8),
                    new RegAddr(bx, di, 8),
                    new RegAddr(bp, si, 8),
                    new RegAddr(bp, di, 8),
                    new RegAddr(si, null, 8),
                    new RegAddr(di, null, 8),
                    new RegAddr(bp, null, 8),
                    new RegAddr(bx, null, 8),
            },
            new Addr[] {
                    new RegAddr(bx, si, 16),
                    new RegAddr(bx, di, 16),
                    new RegAddr(bp, si, 16),
                    new RegAddr(bp, di, 16),
                    new RegAddr(si, null, 16),
                    new RegAddr(di, null, 16),
                    new RegAddr(bp, null, 16),
                    new RegAddr(bx, null, 16),
            },
            addrs32NoRex[3],
    };

    public static class Entry
    {

        public final RegClassMap reg;
        public final Addr addr;

        public Entry(RegClassMap reg, Addr addr)
        {
            this.reg = reg;
            this.addr = addr;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("reg", reg)
                    .add("addr", addr)
                    .toString();
        }
    }

    protected static Entry[] buildTable(RegClassMap[] regTable, Addr[][] addrTable)
    {
        Entry[] table = new Entry[256];
        for (int i = 0; i < 256; ++i) {
            int mod = (i & 0b11000000) >> 6;
            int rm = (i & 0b111000) >> 3;
            int reg = i & 0b111;
            Entry entry = new Entry(
                    regTable[reg],
                    addrTable[mod][rm]
            );
            table[i] = entry;
        }
        return table;
    }

    public static final Entry[][] entries3264 = new Entry[][] {
            buildTable(regs32NoRex, addrs32NoRex),
            buildTable(regs32NoRex, addrs32RexB),
            buildTable(regs32RexR, addrs32NoRex),
            buildTable(regs32RexR, addrs32RexB),
            buildTable(regs64NoRex, addrs64NoRex),
            buildTable(regs64NoRex, addrs64RexB),
            buildTable(regs64RexR, addrs64NoRex),
            buildTable(regs64RexR, addrs64RexB),
    };

    public static Entry[] getEntries3264(boolean _64, boolean rexR, boolean rexB)
    {
        int idx = (_64 ? 4 : 0) + (rexR ? 2 : 0) + (rexB ? 1 : 0);
        return entries3264[idx];
    }

    public static final Entry[] entries16 = buildTable(regs16, addrs16);

    public static Entry[] getEntries3264()
    {
        return entries16;
    }

    public static void main(String[] args)
            throws Exception
    {
        Entry[] entries = getEntries3264(true, true, true);
        for (int i = 0; i < 256; ++i) {
            System.out.println(String.format("%x : %s", i, entries[i]));
        }
    }
}
