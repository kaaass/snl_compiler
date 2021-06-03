package net.kaaass.snlc.lexer.regex;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 字符串正则表达式
 * @author kaaass
 */
@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprString extends RegexExpression {

    private final String stringLiteral;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
