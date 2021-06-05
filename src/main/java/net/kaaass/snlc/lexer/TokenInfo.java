package net.kaaass.snlc.lexer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.kaaass.snlc.lexer.regex.RegexExpression;

/**
 * 记录 token 定义
 *
 * @author kaaass
 */
@Data
@EqualsAndHashCode(of = {"parent", "id"})
public class TokenInfo<T> {

    private LexContext<T> parent = null;

    private int id = -1;

    /**
     * token 类型
     */
    private final T type;

    /**
     * 匹配 token 的正则表达式
     */
    private final RegexExpression regex;
}
