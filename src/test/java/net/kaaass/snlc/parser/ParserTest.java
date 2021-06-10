package net.kaaass.snlc.parser;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.Lexer;
import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.snl.SnlLexeme;
import net.kaaass.snlc.lexer.snl.SnlLexerFactory;
import net.kaaass.snlc.parser.exception.TokenNotMatchException;

public class ParserTest extends TestCase {

    public static final Lexer<SnlLexeme> lexer = SnlLexerFactory.create();

    public void testHello() throws LexParseException, TokenNotMatchException {
        String code = "program p\n" +
                "\ttype t = integer;\n" +
                "\tvar t v1;\n" +
                "\tchar v2;\n" +
                "begin\n" +
                "\tread(v1);\n" +
                "\tv1:=v1+10;\n" +
                "\twrite(v1)\n" +
                "end\n";

        var engine = lexer.process(code);
        var parser = Parser.of(engine.readAllTokens());
        assertNull(parser.getAst());
    }
}
