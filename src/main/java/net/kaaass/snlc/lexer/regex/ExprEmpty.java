package net.kaaass.snlc.lexer.regex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空正则表达式
 * @author kaaass
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExprEmpty extends RegexExpression {
    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
