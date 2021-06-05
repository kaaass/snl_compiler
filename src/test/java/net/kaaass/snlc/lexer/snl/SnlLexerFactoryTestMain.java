package net.kaaass.snlc.lexer.snl;

import net.kaaass.snlc.lexer.Lexer;
import net.kaaass.snlc.lexer.exception.LexParseException;

public class SnlLexerFactoryTestMain {

    public static final Lexer<SnlLexeme> lexer = SnlLexerFactory.create();

    public static void simple1() {
        String code = "program p\n" +
                "\ttype t := integer;\n" +
                "\tvar t v1;\n" +
                "\tchar v2;\n" +
                "begin\n" +
                "\tread(v1);\n" +
                "\tv1:=v1+10;\n" +
                "\twrite(v1)\n" +
                "end\n";

        var engine = lexer.process(code);

        try {
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        simple1();
    }
}