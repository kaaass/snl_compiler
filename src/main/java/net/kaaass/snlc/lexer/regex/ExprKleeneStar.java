package net.kaaass.snlc.lexer.regex;

import lombok.*;

/**
 * Kleene * 正则表达式
 * @author kaaass
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprKleeneStar extends RegexExpression {

    private final RegexExpression innerRegex;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String friendlyString() {
        var inner = innerRegex.friendlyString();
        return String.format(inner.length() > 1 ? "(%s)*": "%s*", inner);
    }

    @Override
    public RegexExpression deepCopy() {
        return new ExprKleeneStar(this.innerRegex.deepCopy());
    }
}
