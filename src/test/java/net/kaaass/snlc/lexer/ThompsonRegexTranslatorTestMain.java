package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.nfa.NfaUtils;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class ThompsonRegexTranslatorTestMain {

    public static void main(String[] args) {
        var alphabet = or(single('a'), single('A'));
        var number = single('0');
        var identifier = concat(alphabet, or(alphabet, number).many());

        var translator = new ThompsonRegexTranslator();

        System.out.println("alphabet:");
        NfaUtils.printGraph(alphabet.accept(translator));
        System.out.println("number:");
        NfaUtils.printGraph(number.many().accept(translator));
        System.out.println("identifier:");
        NfaUtils.printGraph(identifier.accept(translator));
        System.out.println("'else':");
        NfaUtils.printGraph(string("else").accept(translator));
        System.out.println("ab:");
        NfaUtils.printGraph(concat(single('a'), single('b')).accept(translator));
    }
}