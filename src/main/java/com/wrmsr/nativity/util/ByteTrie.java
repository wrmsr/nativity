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
package com.wrmsr.nativity.util;

import com.google.common.collect.Iterators;
import com.wrmsr.nativity.util.Hex;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ByteTrie<T>
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
