package net.kaaass.snlc.lexer.regex;

import lombok.*;

/**
 * 连接正则表达式
 * @author kaaass
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprConcatenation extends RegexExpression {

    private final RegexExpression leftRegex;

    private final RegexExpression rightRegex;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String friendlyString() {
        return String.format("%s %s", leftRegex.friendlyString(), rightRegex.friendlyString());
    }
}
