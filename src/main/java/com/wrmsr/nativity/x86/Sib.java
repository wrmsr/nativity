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

import java.util.Arrays;

import static com.wrmsr.nativity.x86.Regs.*;

public class Sib
{
    private Sib()
    {
    }

    public abstract static class Base
    {
    }

    public static class RegBase
            extends Base
    {

        public final Reg reg;

        public RegBase(Reg reg)
        {
            this.reg = reg;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("reg", reg)
                    .toString();
        }
    }

    public static class DispRegBase
            extends Base
    {

        public final Reg reg;
        public final int disp;

        public DispRegBase(Reg reg, int disp)
        {
            this.reg = reg;
            this.disp = disp;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("reg", reg)
                    .add("disp", disp)
                    .toString();
        }
    }

    public static class ModBitsConditionalBase
            extends Base
    {

        public final Base[] children;

        public ModBitsConditionalBase(Base... children)
        {
            Preconditions.checkArgument(children.length == 3);
            this.children = children;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("children", Arrays.toString(children))
                    .toString();
        }
    }

    public static class ScaledIndex
    {

        public final Reg index;
        public final int scale;

        public ScaledIndex(Reg index, int scale)
        {
            this.index = index;
            this.scale = scale;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("index", index)
                    .add("scale", scale)
                    .toString();
        }
    }

    public static final Base[] bases32NoRex = new Base[] {
            new RegBase(eax),
            new RegBase(ecx),
            new RegBase(edx),
            new RegBase(ebx),
            new RegBase(esp),
            new ModBitsConditionalBase(
                    new DispRegBase(null, 32),
                    new DispRegBase(ebp, 8),
                    new DispRegBase(ebp, 32)
            ),
            new RegBase(esi),
            new RegBase(edi),
    };

    public static final Base[] bases32RexB = new Base[] {
            new RegBase(r8d),
            new RegBase(r9d),
            new RegBase(r10d),
            new RegBase(r11d),
            new RegBase(r12d),
            new ModBitsConditionalBase(
                    new DispRegBase(null, 32),
                    new DispRegBase(r13d, 8),
                    new DispRegBase(r13d, 32)
            ),
            new RegBase(r14d),
            new RegBase(r15d),
    };

    public static final Base[] bases64NoRex = new Base[] {
            new RegBase(rax),
            new RegBase(rcx),
            new RegBase(rdx),
            new RegBase(rbx),
            new RegBase(rsp),
            new ModBitsConditionalBase(
                    new DispRegBase(null, 32),
                    new DispRegBase(rbp, 8),
                    new DispRegBase(rbp, 32)
            ),
            new RegBase(rsi),
            new RegBase(rdi),
    };

    public static final Base[] bases64RexB = new Base[] {
            new RegBase(r8),
            new RegBase(r9),
            new RegBase(r10),
            new RegBase(r11),
            new RegBase(r12),
            new ModBitsConditionalBase(
                    new DispRegBase(null, 32),
                    new DispRegBase(r13, 8),
                    new DispRegBase(r13, 32)
            ),
            new RegBase(r14),
            new RegBase(r15),
    };

    public static final ScaledIndex[][] scaledIndexes32NoRex = new ScaledIndex[][] {
            new ScaledIndex[] {
                    new ScaledIndex(eax, 0),
                    new ScaledIndex(ecx, 0),
                    new ScaledIndex(edx, 0),
                    new ScaledIndex(ebx, 0),
                    null,
                    new ScaledIndex(ebp, 0),
                    new ScaledIndex(esi, 0),
                    new ScaledIndex(edi, 0),
            },
            new ScaledIndex[] {
                    new ScaledIndex(eax, 2),
                    new ScaledIndex(ecx, 2),
                    new ScaledIndex(edx, 2),
                    new ScaledIndex(ebx, 2),
                    null,
                    new ScaledIndex(ebp, 2),
                    new ScaledIndex(esi, 2),
                    new ScaledIndex(edi, 2),
            },
            new ScaledIndex[] {
                    new ScaledIndex(eax, 4),
                    new ScaledIndex(ecx, 4),
                    new ScaledIndex(edx, 4),
                    new ScaledIndex(ebx, 4),
                    null,
                    new ScaledIndex(ebp, 4),
                    new ScaledIndex(esi, 4),
                    new ScaledIndex(edi, 4),
            },
            new ScaledIndex[] {
                    new ScaledIndex(eax, 8),
                    new ScaledIndex(ecx, 8),
                    new ScaledIndex(edx, 8),
                    new ScaledIndex(ebx, 8),
                    null,
                    new ScaledIndex(ebp, 8),
                    new ScaledIndex(esi, 8),
                    new ScaledIndex(edi, 8),
            },
    };

    public static final ScaledIndex[][] scaledIndexes32RexX = new ScaledIndex[][] {
            new ScaledIndex[] {
                    new ScaledIndex(r8d, 0),
                    new ScaledIndex(r9d, 0),
                    new ScaledIndex(r10d, 0),
                    new ScaledIndex(r11d, 0),
                    new ScaledIndex(r12d, 0),
                    new ScaledIndex(r13d, 0),
                    new ScaledIndex(r14d, 0),
                    new ScaledIndex(r15d, 0),
            },
            new ScaledIndex[] {
                    new ScaledIndex(r8d, 2),
                    new ScaledIndex(r9d, 2),
                    new ScaledIndex(r10d, 2),
                    new ScaledIndex(r11d, 2),
                    new ScaledIndex(r12d, 2),
                    new ScaledIndex(r13d, 2),
                    new ScaledIndex(r14d, 2),
                    new ScaledIndex(r15d, 2),
            },
            new ScaledIndex[] {
                    new ScaledIndex(r8d, 4),
                    new ScaledIndex(r9d, 4),
                    new ScaledIndex(r10d, 4),
                    new ScaledIndex(r11d, 4),
                    new ScaledIndex(r12d, 4),
                    new ScaledIndex(r13d, 4),
                    new ScaledIndex(r14d, 4),
                    new ScaledIndex(r15d, 4),
            },
            new ScaledIndex[] {
                    new ScaledIndex(r8d, 8),
                    new ScaledIndex(r9d, 8),
                    new ScaledIndex(r10d, 8),
                    new ScaledIndex(r11d, 8),
                    new ScaledIndex(r12d, 8),
                    new ScaledIndex(r13d, 8),
                    new ScaledIndex(r14d, 8),
                    new ScaledIndex(r15d, 8),
            },
    };

    public static final ScaledIndex[][] scaledIndexes64NoRex = new ScaledIndex[][] {
            new ScaledIndex[] {
                    new ScaledIndex(rax, 0),
                    new ScaledIndex(rcx, 0),
                    new ScaledIndex(rdx, 0),
                    new ScaledIndex(rbx, 0),
                    null,
                    new ScaledIndex(rbp, 0),
                    new ScaledIndex(rsi, 0),
                    new ScaledIndex(rdi, 0),
            },
            new ScaledIndex[] {
                    new ScaledIndex(rax, 2),
                    new ScaledIndex(rcx, 2),
                    new ScaledIndex(rdx, 2),
                    new ScaledIndex(rbx, 2),
                    null,
                    new ScaledIndex(rbp, 2),
                    new ScaledIndex(rsi, 2),
                    new ScaledIndex(rdi, 2),
            },
            new ScaledIndex[] {
                    new ScaledIndex(rax, 4),
                    new ScaledIndex(rcx, 4),
                    new ScaledIndex(rdx, 4),
                    new ScaledIndex(rbx, 4),
                    null,
                    new ScaledIndex(rbp, 4),
                    new ScaledIndex(rsi, 4),
                    new ScaledIndex(rdi, 4),
            },
            new ScaledIndex[] {
                    new ScaledIndex(rax, 8),
                    new ScaledIndex(rcx, 8),
                    new ScaledIndex(rdx, 8),
                    new ScaledIndex(rbx, 8),
                    null,
                    new ScaledIndex(rbp, 8),
                    new ScaledIndex(rsi, 8),
                    new ScaledIndex(rdi, 8),
            },
    };

    public static final ScaledIndex[][] scaledIndexes64RexX = new ScaledIndex[][] {
            new ScaledIndex[] {
                    new ScaledIndex(r8, 0),
                    new ScaledIndex(r9, 0),
                    new ScaledIndex(r10, 0),
                    new ScaledIndex(r11, 0),
                    new ScaledIndex(r12, 0),
                    new ScaledIndex(r13, 0),
                    new ScaledIndex(r14, 0),
                    new ScaledIndex(r15, 0),
            },
            new ScaledIndex[] {
                    new ScaledIndex(r8, 2),
                    new ScaledIndex(r9, 2),
                    new ScaledIndex(r10, 2),
                    new ScaledIndex(r11, 2),
                    new ScaledIndex(r12, 2),
                    new ScaledIndex(r13, 2),
                    new ScaledIndex(r14, 2),
                    new ScaledIndex(r15, 2),
            },
            new ScaledIndex[] {
                    new ScaledIndex(r8, 4),
                    new ScaledIndex(r9, 4),
                    new ScaledIndex(r10, 4),
                    new ScaledIndex(r11, 4),
                    new ScaledIndex(r12, 4),
                    new ScaledIndex(r13, 4),
                    new ScaledIndex(r14, 4),
                    new ScaledIndex(r15, 4),
            },
            new ScaledIndex[] {
                    new ScaledIndex(r8, 8),
                    new ScaledIndex(r9, 8),
                    new ScaledIndex(r10, 8),
                    new ScaledIndex(r11, 8),
                    new ScaledIndex(r12, 8),
                    new ScaledIndex(r13, 8),
                    new ScaledIndex(r14, 8),
                    new ScaledIndex(r15, 8),
            },
    };

    public static class Entry
    {

        public final Base base;
        public final ScaledIndex scaledIndex;

        public Entry(Base base, ScaledIndex scaledIndex)
        {
            this.base = base;
            this.scaledIndex = scaledIndex;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                    .add("base", base)
                    .add("scaledIndex", scaledIndex)
                    .toString();
        }
    }

    protected static Entry[] buildTable(Base[] baseTable, ScaledIndex[][] scaledIndexTable)
    {
        Entry[] table = new Entry[256];
        for (int i = 0; i < 256; ++i) {
            int ss = (i & 0b11000000) >> 6;
            int index = (i & 0b111000) >> 3;
            int base = i & 0b111;
            Entry entry = new Entry(
                    baseTable[base],
                    scaledIndexTable[ss][index]
            );
            table[i] = entry;
        }
        return table;
    }

    public static final Entry[][] entries3264 = new Entry[][] {
            buildTable(bases32NoRex, scaledIndexes32NoRex),
            buildTable(bases32NoRex, scaledIndexes32RexX),
            buildTable(bases32RexB, scaledIndexes32NoRex),
            buildTable(bases32RexB, scaledIndexes32RexX),
            buildTable(bases64NoRex, scaledIndexes64NoRex),
            buildTable(bases64NoRex, scaledIndexes64RexX),
            buildTable(bases64RexB, scaledIndexes64NoRex),
            buildTable(bases64RexB, scaledIndexes64RexX),
    };

    public static Entry[] getEntries3264(boolean _64, boolean rexB, boolean rexX)
    {
        int idx = (_64 ? 4 : 0) + (rexB ? 2 : 0) + (rexX ? 1 : 0);
        return entries3264[idx];
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
