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

import com.wrmsr.nativity.llvm.parser.LlvmBaseVisitor;
import com.wrmsr.nativity.llvm.parser.LlvmLexer;
import com.wrmsr.nativity.llvm.parser.LlvmParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.testng.annotations.Test;

import static java.lang.String.format;

public class LlvmTest
{
    public static class ParsingException
            extends RuntimeException
    {
        private final int line;
        private final int charPositionInLine;

        public ParsingException(String message, RecognitionException cause, int line, int charPositionInLine)
        {
            super(message, cause);

            this.line = line;
            this.charPositionInLine = charPositionInLine;
        }

        public ParsingException(String message)
        {
            this(message, null, 1, 0);
        }

        public int getLineNumber()
        {
            return line;
        }

        public int getColumnNumber()
        {
            return charPositionInLine + 1;
        }

        public String getErrorMessage()
        {
            return super.getMessage();
        }

        @Override
        public String getMessage()
        {
            return format("line %s:%s: %s", getLineNumber(), getColumnNumber(), getErrorMessage());
        }
    }

    private static final BaseErrorListener ERROR_LISTENER = new BaseErrorListener()
    {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e)
        {
            throw new ParsingException(message, e, line, charPositionInLine);
        }
    };

    @Test
    public void testLlvmLexer()
            throws Throwable
    {
        String src = "ftrue";
        LlvmLexer lexer = new LlvmLexer(new ANTLRInputStream(src));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LlvmParser parser = new LlvmParser(tokenStream);

        lexer.removeErrorListeners();
        lexer.addErrorListener(ERROR_LISTENER);

        parser.removeErrorListeners();
        parser.addErrorListener(ERROR_LISTENER);

        ParserRuleContext tree;
        try {
            // first, try parsing with potentially faster SLL mode
            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
            tree = parser.statement();
        }
        catch (ParseCancellationException ex) {
            // if we fail, parse with LL mode
            tokenStream.reset(); // rewind input stream
            parser.reset();

            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            tree = parser.statement();
        }
        new AstBuilder().visit(tree);
    }

    private static class AstBuilder
            extends
            LlvmBaseVisitor<Node>
    {

    }

    private static abstract class Node
    {
    }
}
