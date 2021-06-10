package net.kaaass.snlc.lexer.regex;

import lombok.*;

/**
 * 字符串正则表达式
 * @author kaaass
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprString extends RegexExpression {

    private final String stringLiteral;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String friendlyString() {
        return this.stringLiteral;
    }
}
