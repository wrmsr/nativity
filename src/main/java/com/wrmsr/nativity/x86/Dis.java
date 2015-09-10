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
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.wrmsr.nativity.util.Hex;
import org.apache.commons.lang.ArrayUtils;

import java.util.Iterator;
import java.util.List;

import static com.wrmsr.nativity.x86.Ref.Entry;
import static com.wrmsr.nativity.x86.Ref.Operand;
import static com.wrmsr.nativity.x86.Ref.Syntax;
import static com.wrmsr.nativity.x86.Utils.toByteArray;

public class Dis
{
    public static class ByteTrie<T>
    {
        public static class Node<T>
        {
            private List<T> values;

            private Node<T>[] children;

            public Node()
            {
            }

            public void add(byte[] key, int keyOfs, T value)
            {
                if (key.length == keyOfs) {
                    if (values == null) {
                        values = Lists.newArrayList();
                    }
                    values.add(value);
                }
                else {
                    int idx = key[keyOfs] & 0xFF;
                    if (children == null) {
                        children = new Node[256];
                    }
                    Node<T> child = children[idx];
                    if (child == null) {
                        child = children[idx] = new Node<>();
                    }
                    child.add(key, keyOfs + 1, value);
                }
            }

            public void toString(StringBuilder sb, int indent)
            {
                if (values != null) {
                    for (T value : values) {
                        for (int j = 0; j < indent; ++j) {
                            sb.append("   ");
                        }
                        sb.append(value.toString());
                        sb.append("\n");
                    }
                }
                if (children != null) {
                    for (int i = 0; i < children.length; ++i) {
                        Node child = children[i];
                        if (child == null) {
                            continue;
                        }
                        for (int j = 0; j < indent; ++j) {
                            sb.append("   ");
                        }
                        sb.append(Hex.hexdump((byte) i));
                        sb.append("\n");
                        child.toString(sb, indent + 1);
                    }
                }
            }

            public Iterator<T> get(Iterator<Byte> key)
            {
                Iterator<T> valuesIterator = values != null ? values.iterator() : Iterators.emptyIterator();
                Iterator<T> childIterator = Iterators.emptyIterator();
                if (children != null && key.hasNext()) {
                    Node<T> child = children[key.next() & 0xFF];
                    if (child != null) {
                        childIterator = child.get(key);
                    }
                }
                return Iterators.concat(valuesIterator, childIterator);
            }
        }

        private final Node<T> root;

        public ByteTrie()
        {
            root = new Node<>();
        }

        public void add(byte[] key, T value)
        {
            root.add(key, 0, value);
        }

        public String toDetailedString()
        {
            StringBuilder sb = new StringBuilder();
            root.toString(sb, 0);
            return sb.toString();
        }

        public Iterator<T> get(Iterator<Byte> key)
        {
            return root.get(key);
        }
    }

    public static ByteTrie<Entry> buildTrie(Iterable<Entry> entries)
    {
        ByteTrie<Entry> trie = new ByteTrie<>();

        for (Entry entry : entries) {
            for (Syntax syntax : entry.getSyntaxes()) {
                byte[] bytes = toByteArray(entry.getBytes());
                if (entry.getPrefixByte() != null) {
                    bytes = ArrayUtils.addAll(new byte[] {entry.getPrefixByte()}, bytes);
                }
                if (entry.getSecondaryByte() != null) {
                    bytes = ArrayUtils.addAll(bytes, new byte[] {entry.getSecondaryByte()});
                }
                trie.add(bytes, entry);

                Operand zOperand = null;
                for (Operand operand : Iterables.concat(syntax.getDstOperands(), syntax.getSrcOperands())) {
                    if (operand.address == Operand.Address.Z) {
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

    public static class Disassembler
    {

        protected final List<Entry> prefixes = Lists.newArrayList();
    }

    public static void run(ByteTrie<Entry> trie)
    {
        byte[] bytes = new byte[] {(byte) 0x55, (byte) 0x48, (byte) 0x89, (byte) 0xe5, (byte) 0x48, (byte) 0x8d, (byte) 0x3d, (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                                   (byte) 0xe8, (byte) 0x4e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x31, (byte) 0xc0, (byte) 0x5d, (byte) 0xc3, (byte) 0x66, (byte) 0x0f,
                                   (byte) 0x1f, (byte) 0x44, (byte) 0x00, (byte) 0x00, (byte) 0x66, (byte) 0x0f, (byte) 0x1f, (byte) 0x44, (byte) 0x00, (byte) 0x00};
        List<Byte> byteList = Lists.newArrayList(Bytes.asList(bytes));

        while (!byteList.isEmpty()) {
            byte[] b = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); ++i) {
                b[i] = byteList.get(i);
            }
            System.out.println(Hex.hexdump(b));
            System.out.println();

            List<Entry> keyEntries = Lists.newArrayList(trie.get(byteList.iterator()));
            for (Entry entry : keyEntries) {
                System.out.println(entry);
            }
            System.out.println();

            Entry entry = keyEntries.get(keyEntries.size() - 1);
            int len = 1;
            int modRMCount = 0;
            List<Syntax> syntaxes = Lists.newArrayList(entry.getSyntaxes());
            Syntax syntax = syntaxes.get(syntaxes.size() - 1);
            System.out.println(syntax);
            Iterable<Operand> operands = Iterables.concat(syntax.getSrcOperands(), syntax.getDstOperands());
            for (Operand operand : operands) {
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

            System.out.println("\n\n\n");
        }
    }
}
