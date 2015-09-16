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

import java.util.Iterator;
import java.util.List;

import static com.wrmsr.nativity.x86.Ref.Entry;

public class Instr
        implements Iterable<Byte>
{
    public final List<Entry> prefixes;
    public final Entry rexPrefix;
    // public final Entry vexPrefix;

    public final Entry entry;

    public final Byte sib;
    public final Byte modrm;

    public final Imm imm;

    public Instr(List<Entry> prefixes, Entry rexPrefix, Entry entry, Byte sib, Byte modrm, Imm imm)
    {
        this.prefixes = prefixes;
        this.rexPrefix = rexPrefix;
        this.entry = entry;
        this.sib = sib;
        this.modrm = modrm;
        this.imm = imm;
    }

    @Override
    public Iterator<Byte> iterator()
    {
        return null;
    }
}
