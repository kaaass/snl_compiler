package net.kaaass.snlc.lexer.regex;

import lombok.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * 字符集正则表达式
 * @author kaaass
 */
@Getter
@Setter
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

    @Override
    public String friendlyString() {
        var ret = new StringBuilder("[");
        charSet.forEach(ret::append);
        ret.append(']');
        return ret.toString();
    }
}
