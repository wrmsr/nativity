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

import java.util.Objects;

public class Imm
{
    public final byte length;
    public final long value;

    public Imm(byte length, long value)
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
        Imm imm = (Imm) o;
        return Objects.equals(length, imm.length) &&
                Objects.equals(value, imm.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(length, value);
    }

    @Override
    public String toString()
    {
        return "Imm{" +
                "length=" + length +
                ", value=" + value +
                '}';
    }
}
