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
package com.wrmsr.nativity.llvm;

import com.wrmsr.nativity.llvm.parser.llvmLexer;
import com.wrmsr.nativity.llvm.parser.llvmParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.testng.annotations.Test;

public class llvmTest
{
    @Test
    public void testLlvmLexer()
            throws Throwable
    {
        String src = "true";
        llvmLexer lexer = new llvmLexer(new ANTLRInputStream());

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        llvmParser parser = new llvmParser(tokenStream);

        // lexer.removeErrorListeners();
        // parser.removeErrorListeners();

        ParserRuleContext tree;
        // first, try parsing with potentially faster SLL mode
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
    }
}
