package net.kaaass.snlc.lexer.regex;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.TreeSet;

/**
 * 字符集正则表达式
 * @author kaaass
 */
@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprCharSet extends RegexExpression {

    private final Set<Character> charSet;

    public ExprCharSet(char start, char end) {
        this.charSet = new TreeSet<>();
        for (char cur = start; cur <= end; cur++) {
            this.charSet.add(cur);
        }
    }

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
