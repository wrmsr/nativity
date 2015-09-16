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

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.xml.XmlEscapers;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.wrmsr.nativity.util.Maps.buildMapToList;

public class Util
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
