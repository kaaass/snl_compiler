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

        System.out.println("\nsimple1:");
        try {
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }
    }

    public static void comment1() {
        String code = "{read to variable} end\n";

        var engine = lexer.process(code);

        System.out.println("\ncomment1:");
        try {
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }
    }

    public static void comment2() {
        String code = "program p\n" +
                "\ttype t := integer;\n" +
                "\tvar t v1;\n" +
                "\tchar v2;\n" +
                "begin\n" +
                "\t{read to variable}\n" +
                "\tread(v1);\n" +
                "\t{increase by 10}\n" +
                "\tv1:=v1+10;\n" +
                "\t{print v1}\n" +
                "\twrite(v1)\n" +
                "end\n";

        var engine = lexer.process(code);

        System.out.println("\ncomment2:");
        try {
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        simple1();

        comment1();
        comment2();
    }
}