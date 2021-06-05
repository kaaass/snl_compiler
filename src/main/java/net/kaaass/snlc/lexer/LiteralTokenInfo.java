package net.kaaass.snlc.lexer;

import lombok.Getter;
import net.kaaass.snlc.lexer.regex.RegexExpression;

/**
 * 字面量 token 定义
 *
 * @author kaaass
 */
public class LiteralTokenInfo<T> extends TokenInfo<T> {

    @Getter
    private final String literal;

    public LiteralTokenInfo(T type, String literal) {
        super(type, RegexExpression.string(literal));
        this.literal = literal;
    }
}
