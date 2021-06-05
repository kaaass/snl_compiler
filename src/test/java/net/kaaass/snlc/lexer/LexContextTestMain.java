package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.dfa.DfaUtils;

import java.util.Objects;
import java.util.Set;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class LexContextTestMain {

    enum Lang {
        WHITESPACE, ALPHABET, DIGIT, IF, AS
    }

    public static void testCompile() {
        var grammar = LexGrammar.<Lang>create();
        grammar.defineToken(Lang.WHITESPACE, charset(' ', '\n'));
        grammar.defineToken(Lang.ALPHABET, or(range('a', 'b'), range('A', 'B')));
        grammar.defineToken(Lang.DIGIT, range('0', '1'));
        grammar.defineToken(Lang.IF, "if");
        grammar.defineToken(Lang.AS, "as");
        var context = grammar.getContext();
        context.compile();

        DfaUtils.printGraph(Objects.requireNonNull(context.getState().getSource().get()));
        System.out.println(context.getState());
    }

    public static void main(String[] args) {
        testCompile();
    }
}