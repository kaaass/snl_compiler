package net.kaaass.snlc.lexer.regex;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 或正则表达式
 * @author kaaass
 */
@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprAlternation extends RegexExpression {

    private final RegexExpression leftRegex;

    private final RegexExpression rightRegex;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
