package net.kaaass.snlc.lexer.regex;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Kleene * 正则表达式
 * @author kaaass
 */
@Data
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ExprKleeneStar extends RegexExpression {

    private final RegexExpression innerRegex;

    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}