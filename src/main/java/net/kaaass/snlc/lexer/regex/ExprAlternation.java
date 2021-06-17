package net.kaaass.snlc.lexer.regex;

import lombok.*;

/**
 * 或正则表达式
 * @author kaaass
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprAlternation extends RegexExpression {

    private final RegexExpression leftRegex;

    private final RegexExpression rightRegex;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String friendlyString() {
        return String.format("(%s | %s)", leftRegex.friendlyString(), rightRegex.friendlyString());
    }

    @Override
    public RegexExpression deepCopy() {
        return new ExprAlternation(this.leftRegex.deepCopy(), this.rightRegex.deepCopy());
    }
}
