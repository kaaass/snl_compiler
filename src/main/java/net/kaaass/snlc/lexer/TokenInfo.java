package net.kaaass.snlc.lexer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kaaass.snlc.lexer.regex.RegexExpression;

/**
 * 记录 token 定义
 *
 * @author kaaass
 */
@Data
@ToString(of = {"id", "type"})
@EqualsAndHashCode(of = {"parent", "id"})
public class TokenInfo<T> {

    public final static int DEAD = -1;

    private LexContext<T> parent = null;

    private int id = DEAD;

    /**
     * token 类型
     */
    private final T type;

    /**
     * 匹配 token 的正则表达式
     */
    private final RegexExpression regex;
}
