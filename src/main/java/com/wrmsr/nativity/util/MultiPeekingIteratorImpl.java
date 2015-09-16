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

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkState;

public class MultiPeekingIteratorImpl<E> implements MultiPeekingIterator<E>
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
