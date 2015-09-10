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

public class Hex
{
    public static String hexdump(byte b)
    {
        return String.format("%02X", b);
    }

    public static String hexdump(byte[] buffer, int offset, String sep)
    {
        StringBuffer dump = new StringBuffer();
        if ((buffer.length - offset) > 0) {
            dump.append(String.format("%02X", buffer[offset]));
            for (int i = offset + 1; i < buffer.length; i++) {
                dump.append(sep);
                dump.append(String.format("%02X", buffer[i]));
            }
        }
        return dump.toString();
    }

    public static String hexdump(byte[] buffer)
    {
        return hexdump(buffer, 0, "_");
    }
}
