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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface Exec
{
    void exec(String path, String[] params, Map<String, String> env)
            throws IOException;

    default void exec(String path, String[] params)
            throws IOException
    {
        exec(path, params, null);
    }

    abstract class AbstractExec
            implements Exec
    {
        public static String[] convertEnv(Map<String, String> env)
        {
            if (env == null) {
                return null;
            }
            ArrayList<String> ret = Lists.newArrayList(Iterables.transform(
                    env.entrySet(), entry -> String.format("%s=%s", entry.getKey(), entry.getValue())));
            return ret.toArray(new String[ret.size()]);
        }
    }

    class ProcessBuilderExec
            extends AbstractExec
    {

        public static final Method environment;

        static {
            try {
                environment = ProcessBuilder.class.getDeclaredMethod("environment", String[].class);
                environment.setAccessible(true);
            }
            catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void exec(String path, String[] params, Map<String, String> env)
                throws IOException
        {
            ProcessBuilder pb = new ProcessBuilder();
            String[] envArr = convertEnv(env);
            try {
                environment.invoke(pb, new Object[] {envArr});
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
            List<String> command = Lists.newArrayList(path);
            command.addAll(Arrays.asList(params));
            pb.command(command);
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            int ret;
            try {
                ret = process.waitFor();
            }
            catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.exit(ret);
        }
    }
}
