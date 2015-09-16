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

import com.google.common.collect.Iterables;
import com.google.common.primitives.Bytes;
import com.wrmsr.nativity.util.ByteTrie;
import com.wrmsr.nativity.util.Hex;
import org.apache.commons.lang.ArrayUtils;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.wrmsr.nativity.x86.Ref.Util.toByteArray;
import static com.wrmsr.nativity.x86.Ref.Entry;
import static java.util.stream.Collectors.toList;

public class DisImpl
        implements Dis
{
    protected class Stepper
    {

    }

    @Override
    public Iterator<Instr> iterator()
    {
        return null;
    }

    @Override
    public boolean hasNext()
    {
        return false;
    }

    @Override
    public Dis next()
    {
        return null;
    }

    @Override
    public Instr get()
    {
        return null;
    }

    public static ByteTrie<Entry> buildTrie(Iterable<Entry> entries)
    {
        ByteTrie<Entry> trie = new ByteTrie<>();

        for (Entry entry : entries) {
            for (Ref.Syntax syntax : entry.getSyntaxes()) {
                byte[] bytes = toByteArray(entry.getBytes());
                if (entry.getPrefixByte() != null) {
                    bytes = ArrayUtils.addAll(new byte[] {entry.getPrefixByte()}, bytes);
                }
                if (entry.getSecondaryByte() != null) {
                    bytes = ArrayUtils.addAll(bytes, new byte[] {entry.getSecondaryByte()});
                }
                trie.add(bytes, entry);

                Ref.Operand zOperand = null;
                for (Ref.Operand operand : Iterables.concat(syntax.getDstOperands(), syntax.getSrcOperands())) {
                    if (operand.address == Ref.Operand.Address.Z) {
                        if (zOperand != null) {
                            throw new IllegalStateException();
                        }
                        zOperand = operand;
                        break;
                    }
                }
                if (zOperand == null) {
                    continue;
                }

                bytes = toByteArray(entry.getBytes());
                if ((bytes[bytes.length - 1] & (byte) 7) != (byte) 0) {
                    throw new IllegalStateException();
                }

                for (byte i = 1; i < 8; ++i) {
                    bytes = toByteArray(entry.getBytes());

                    bytes[bytes.length - 1] |= i;

                    if (entry.getPrefixByte() != null) {
                        bytes = ArrayUtils.addAll(new byte[] {entry.getPrefixByte()}, bytes);
                    }
                    if (entry.getSecondaryByte() != null) {
                        bytes = ArrayUtils.addAll(bytes, new byte[] {entry.getSecondaryByte()});
                    }
                    trie.add(bytes, entry);
                }
            }
        }

        return trie;
    }

    public static void dis(ByteTrie<Entry> trie, byte[] buf)
    {
        if (buf.length > 15) {
            // 2.3.11 AVX Instruction Length
            // The AVX instructions described in this document (including VEX and ignoring other prefixes) do not exceed 11 bytes in length, but may increase in the future. The maximum length of an Intel 64 and IA-32 instruction remains 15 bytes.
            throw new IllegalArgumentException("buf length must not exceed 15");
        }

        List<Entry> prefixes = newArrayList();
    }

    public static void run(ByteTrie<Entry> trie)
    {
        byte[] bytes;
        // = new byte[] {(byte) 0x55, (byte) 0x48, (byte) 0x89, (byte) 0xe5, (byte) 0x48, (byte) 0x8d, (byte) 0x3d, (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        //               (byte) 0xe8, (byte) 0x4e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0xc0, (byte) 0x5d, (byte) 0xc3, (byte) 0x66, (byte) 0x0f,
        //               (byte) 0x1f, (byte) 0x44, (byte) 0x00, (byte) 0x00, (byte) 0x66, (byte) 0x0f, (byte) 0x1f, (byte) 0x44, (byte) 0x00, (byte) 0x00};

        bytes = new byte[] {
                (byte) 0x64, (byte) 0x8b, (byte) 0x04, (byte) 0x25, (byte) 0xd4, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x64, (byte) 0x8b, (byte) 0x34, (byte) 0x25, (byte) 0xd0, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x85, (byte) 0xf6,
                (byte) 0x75, (byte) 0x2c,
                (byte) 0xb8, (byte) 0xba, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x0f, (byte) 0x05,
                (byte) 0x89, (byte) 0xc6,
                (byte) 0x64, (byte) 0x89, (byte) 0x04, (byte) 0x25, (byte) 0xd0, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x48, (byte) 0x63, (byte) 0xd7,
                (byte) 0x48, (byte) 0x63, (byte) 0xf6,
                (byte) 0x48, (byte) 0x63, (byte) 0xf8,
                (byte) 0xb8, (byte) 0xea, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x0f, (byte) 0x05,
                (byte) 0x48, (byte) 0x3d, (byte) 0x00, (byte) 0xf0, (byte) 0xff, (byte) 0xff,
                (byte) 0x77, (byte) 0x15,
                (byte) 0xf3, (byte) 0xc3,
                (byte) 0x90,
                (byte) 0x85, (byte) 0xc0,
                (byte) 0x7f, (byte) 0xe1,
                (byte) 0xa9, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x7f,
                (byte) 0x75, (byte) 0x17,
                (byte) 0x89, (byte) 0xf0,
                (byte) 0x0f, (byte) 0x1f, (byte) 0x00,
                (byte) 0xeb, (byte) 0xd3,
                (byte) 0x48, (byte) 0x8b, (byte) 0x15, (byte) 0x2f, (byte) 0xf7, (byte) 0x34, (byte) 0x00,
                (byte) 0xf7, (byte) 0xd8,
                (byte) 0x64, (byte) 0x89, (byte) 0x02,
                (byte) 0x83, (byte) 0xc8, (byte) 0xff,
                (byte) 0xc3,
                (byte) 0xf7, (byte) 0xd8,
                (byte) 0xeb, (byte) 0xbf,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90,
                (byte) 0x90
        };

        List<Byte> byteList = newArrayList(Bytes.asList(bytes));

        Entry.Mode mode = Entry.Mode.E;

        while (!byteList.isEmpty()) {
            byte[] b = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); ++i) {
                b[i] = byteList.get(i);
            }
            System.out.println(Hex.hexdump(b));
            System.out.println();

            List<Entry> keyEntries = newArrayList(trie.get(byteList.iterator()));
            Entry entry = null;

            if (keyEntries.size() > 1) {
                List<Entry> modeEntries = keyEntries.stream().filter(e -> e.mode == mode).collect(toList());
                if (modeEntries.size() == 1) {
                    entry = modeEntries.get(0);
                }
            }
            else {
                entry = keyEntries.get(0);
            }

            if (entry == null) {
                throw new IllegalStateException();
            }

            System.out.println(entry);
            System.out.println();

            int len = 1;
            int modRMCount = 0;
            List<Ref.Syntax> syntaxes = newArrayList(entry.getSyntaxes());
            Ref.Syntax syntax = syntaxes.get(syntaxes.size() - 1);
            System.out.println(syntax);
            Iterable<Ref.Operand> operands = Iterables.concat(syntax.getSrcOperands(), syntax.getDstOperands());
            for (Ref.Operand operand : operands) {
                switch (operand.address) {
                    case V:
                    case G:
                    case E:
                    case M:
                        ++modRMCount;
                        break;
                    case J:
                        len += 4;
                        break;
                    case Z:
                    case SC:
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
            len += modRMCount;

            for (int i = 0; i < len; ++i) {
                byteList.remove(0);
            }

            System.out.println("\n");
        }
    }
}
