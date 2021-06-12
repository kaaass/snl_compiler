package net.kaaass.snlc.lexer.regex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 空正则表达式
 * @author kaaass
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ExprEmpty extends RegexExpression {
    @Override
    public <T> T accept(IRegexExprVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String friendlyString() {
        return "ε";
    }

    @Override
    public RegexExpression deepCopy() {
        return new ExprEmpty();
    }
}
