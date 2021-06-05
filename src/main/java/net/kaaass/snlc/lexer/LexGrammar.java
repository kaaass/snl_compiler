package net.kaaass.snlc.lexer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.regex.RegexExpression;

/**
 * 词法分析语法定义，即词法分析器的建造者
 * @author kaaass
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LexGrammar<T> {

    private final LexContext<T> context;

    /**
     * 定义字面量 token
     * @param type Token 类型
     * @param literal 字面量
     */
    public TokenInfo<T> defineToken(T type, String literal) {
        var token = new LiteralTokenInfo<>(type, literal);
        context.addToken(token);
        return token;
    }

    /**
     * 定义正则匹配 token
     * @param type Token 类型
     * @param regex 正则
     */
    public TokenInfo<T> defineToken(T type, RegexExpression regex) {
        var token = new TokenInfo<>(type, regex);
        context.addToken(token);
        return token;
    }

    /**
     * 从词法创建词法分析器
     */
    public Lexer<T> compile() {
        return Lexer.of(this);
    }

    public static <T> LexGrammar<T> create() {
        return new LexGrammar<>(new LexContext<>(LexContext.DEFAULT));
    }
}
