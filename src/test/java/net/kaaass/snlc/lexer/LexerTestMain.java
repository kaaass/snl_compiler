package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.exception.LexParseException;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class LexerTestMain {

    enum Lang1 {
        WHITESPACE, ALPHABET, DIGIT, IF, AS
    }

    public static void testLang1() {
        var grammar = LexGrammar.<Lang1>create();
        grammar.defineToken(Lang1.ALPHABET, or(range('a', 'b'), range('A', 'B')).oneOrMany());
        grammar.defineToken(Lang1.DIGIT, range('0', '1').oneOrMany());
        grammar.defineToken(Lang1.IF, "if");
        grammar.defineToken(Lang1.AS, "as");
        grammar.defineToken(Lang1.WHITESPACE, charset(' ', '\n'));

        var lexer = grammar.compile();

        System.out.println("'a 0 if':");

        try {
            var engine = lexer.process("a 0 if");
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }

        System.out.println("\n'  abaA if 0 as 1101 BB':");

        try {
            var engine = lexer.process("  abaA if 0 as 1101 BB");
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }

        System.out.println("\n'a#':");

        try {
            var engine = lexer.process("a#");
            engine.readAllTokens().forEach(System.out::println);
        } catch (LexParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testLang1();
    }
}