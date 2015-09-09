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
package com.wrmsr.nativity;

import com.google.common.collect.Lists;
import jnr.ffi.provider.jffi.NativeLibrary;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Native
{
    private Native()
    {
    }

    public static final int PROT_READ = 0x1;
    public static final int PROT_WRITE = 0x2;
    public static final int PROT_EXEC = 0x4;

    public static List<String> getPropertyPaths(String propName)
    {
        String value = System.getProperty(propName);
        if (value != null) {
            String[] paths = value.split(File.pathSeparator);
            return Lists.newArrayList(Arrays.asList(paths));
        }
        return Collections.emptyList();
    }

    public static List<String> USER_LIBRARY_PATH = Lists.newArrayList();

    static {
        USER_LIBRARY_PATH.addAll(getPropertyPaths("jnr.ffi.library.path"));
        USER_LIBRARY_PATH.addAll(getPropertyPaths("jaffl.library.path"));
        // Add JNA paths for compatibility
        USER_LIBRARY_PATH.addAll(getPropertyPaths("jna.library.path"));
        USER_LIBRARY_PATH.addAll(getPropertyPaths("java.library.path"));
    }

    public static final Constructor<NativeLibrary> newNativeLibrary;
    public static final Method getSymbolAddress;

    static {
        try {
            newNativeLibrary = NativeLibrary.class.getDeclaredConstructor(Collection.class, Collection.class);
            newNativeLibrary.setAccessible(true);

            getSymbolAddress = NativeLibrary.class.getDeclaredMethod("getSymbolAddress", String.class);
            getSymbolAddress.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static NativeLibrary newNativeLibrary(Collection<String> libraryNames, Collection<String> searchPaths)
    {
        try {
            return newNativeLibrary.newInstance(libraryNames, searchPaths);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static NativeLibrary newNativeLibrary(Collection<String> libraryNames)
    {
        return newNativeLibrary(libraryNames, USER_LIBRARY_PATH);
    }

    public static long getSymbolAddress(NativeLibrary library, String name)
    {
        try {
            return (long) getSymbolAddress.invoke(library, name);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
