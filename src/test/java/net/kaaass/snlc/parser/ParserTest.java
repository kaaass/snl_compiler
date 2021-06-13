package net.kaaass.snlc.parser;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.Lexer;
import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.snl.SnlLexeme;
import net.kaaass.snlc.lexer.snl.SnlLexerFactory;
import net.kaaass.snlc.parser.exception.TokenNotMatchException;
import net.kaaass.snlc.parser.exception.TreeNodeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ParserTest extends TestCase {

    public static final Lexer<SnlLexeme> lexer = SnlLexerFactory.create();

    public void testAst() throws LexParseException, TokenNotMatchException, TreeNodeException, IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        var codePath = Objects.requireNonNull(classLoader.getResource("example.snl")).getPath();
        var astPath = Objects.requireNonNull(classLoader.getResource("ast.txt")).getPath();
        String code = Files.readString(Path.of(codePath));
        String astRes = Files.readString(Path.of(astPath));

        var engine = lexer.process(code);
        var parser = Parser.of(engine.readAllTokens());
        var ast = parser.getAst();
        ast.print();

        assertEquals(astRes, ast.printString());
    }


}
