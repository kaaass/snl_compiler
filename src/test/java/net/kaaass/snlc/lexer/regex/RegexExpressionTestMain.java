package net.kaaass.snlc.lexer.regex;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;
import static net.kaaass.snlc.lexer.regex.RegexExpression.or;

public class RegexExpressionTestMain {
    public static void main(String[] args) {
        var alphabet = or(range('a', 'z'), range('A', 'Z'));
        System.out.println(alphabet);

        var number = range('0', '9');
        System.out.println(number);

        var identifier = concat(alphabet, or(alphabet, number).many());
        System.out.println(identifier);
    }
}