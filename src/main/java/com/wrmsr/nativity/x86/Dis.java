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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.google.common.primitives.Bytes;
import com.wrmsr.nativity.util.Hex;
import org.apache.commons.lang.ArrayUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.wrmsr.nativity.x86.Ref.Entry;
import static com.wrmsr.nativity.x86.Ref.Operand;
import static com.wrmsr.nativity.x86.Ref.Syntax;
import static com.wrmsr.nativity.x86.Utils.toByteArray;
import static java.util.stream.Collectors.toList;

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
                        values = newArrayList();
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

    public static class Immediate
    {
        public final byte length;
        public final long value;

        public Immediate(byte length, long value)
        {
            this.length = length;
            this.value = value;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Immediate immediate = (Immediate) o;
            return Objects.equals(length, immediate.length) &&
                    Objects.equals(value, immediate.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(length, value);
        }

        @Override
        public String toString()
        {
            return "Immediate{" +
                    "length=" + length +
                    ", value=" + value +
                    '}';
        }
    }

    public static class Instruction implements Iterable<Byte>
    {
        public final List<Entry> prefixes;
        public final Entry rexPrefix;
        // public final Entry vexPrefix;

        public final Entry entry;

        public final Byte sib;
        public final Byte modrm;

        public final Immediate immediate;

        public Instruction(List<Entry> prefixes, Entry rexPrefix, Entry entry, Byte sib, Byte modrm, Immediate immediate)
        {
            this.prefixes = prefixes;
            this.rexPrefix = rexPrefix;
            this.entry = entry;
            this.sib = sib;
            this.modrm = modrm;
            this.immediate = immediate;
        }

        @Override
        public Iterator<Byte> iterator()
        {
            return null;
        }
    }

    public interface MultiPeekingIterator<E> extends PeekingIterator<E>
    {
        Iterable<E> peek(int size);
    }

    public static class MultiPeekingIteratorImpl<E> implements MultiPeekingIterator<E>
    {
        private final Iterator<E> iterator;
        private final Queue<E> peeked;

        public MultiPeekingIteratorImpl(Iterator<E> iterator)
        {
            this.iterator = iterator;
            peeked = new LinkedList<>();
        }

        @Override
        public Iterable<E> peek(int size)
        {
            while (peeked.size() < size) {
                peeked.add(iterator.next());
            }
            Iterator<E> peekedIterator = peeked.iterator();
            ImmutableList.Builder<E> builder = ImmutableList.builder();
            for (int i = 0; i < size; ++i) {
                peeked.add(peekedIterator.next());
            }
            return builder.build();
        }

        @Override
        public E peek()
        {
            if (peeked.isEmpty()) {
                peeked.add(iterator.next());
            }
            return peeked.element();
        }

        @Override
        public E next()
        {
            if (!peeked.isEmpty()) {
                return peeked.remove();
            }
            else {
                return iterator.next();
            }
        }

        @Override
        public void remove()
        {
            checkState(peeked.isEmpty(), "Can't remove after you've peeked at next");
            iterator.remove();
        }

        @Override
        public boolean hasNext()
        {
            return !peeked.isEmpty() || iterator.hasNext();
        }
    }

    public interface Disassembler extends Iterator<Disassembler>, Iterable<Instruction>, Supplier<Instruction>
    {
    }

    public static class DisassemblerImpl implements Disassembler
    {
        // Builder? Stepper?

        @Override
        public Iterator<Instruction> iterator()
        {
            return null;
        }

        @Override
        public boolean hasNext()
        {
            return false;
        }

        @Override
        public Disassembler next()
        {
            return null;
        }

        @Override
        public Instruction get()
        {
            return null;
        }
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

        bytes = new byte[]{
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
            List<Syntax> syntaxes = newArrayList(entry.getSyntaxes());
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

            System.out.println("\n");
        }
    }
}
