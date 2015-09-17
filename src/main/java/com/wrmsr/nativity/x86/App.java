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

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.wrmsr.nativity.util.ByteTrie;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public class App
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args)
            throws Exception
    {
        logger.info("hi");

        Document doc;
        try (InputStream is = App.class.getClassLoader().getResourceAsStream("x86reference.xml")) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
        }

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        List<Ref.Entry> entries = Lists.newArrayList();
        Ref.Parsing.parseRoot(doc, entries);
        ByteTrie<Ref.Entry> trie = DisImpl.buildTrie(entries);

        System.out.println(trie.toDetailedString());
        System.out.println();
        System.out.println();

        // Dis.run(trie);

        Ordering<Pair<Ref.Operand.Type, Ref.Operand.Address>> ord = Ordering.from((o1, o2) -> {
            int c = ObjectUtils.compare(o1.getLeft(), o2.getLeft());
            if (c == 0) {
                c = ObjectUtils.compare(o1.getRight(), o2.getRight());
            }
            return c;
        });

        Set<Pair<Ref.Operand.Type, Ref.Operand.Address>> set = Sets.newHashSet();
        for (Ref.Entry entry : entries) {
            for (Ref.Syntax syntax : entry.getSyntaxes()) {
                for (Ref.Operand operand : syntax.getOperands()) {
                    set.add(new ImmutablePair<>(operand.type, operand.address));
                }
            }
        }
        for (Pair<Ref.Operand.Type, Ref.Operand.Address> pair : ord.sortedCopy(set)) {
            System.out.println(pair);
        }
        System.out.println("\n");

        DisImpl.run(trie);
    }
}
