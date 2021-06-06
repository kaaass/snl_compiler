package net.kaaass.snlc.lexer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.exception.UndefinedTokenException;
import net.kaaass.snlc.lexer.regex.RegexExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析语法定义，即词法分析器的建造者
 * @author kaaass
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LexGrammar<T> {

    /**
     * 构造的词法上下文
     */
    private final LexContext<T> context;

    /**
     * 子上下文
     */
    private final List<LexContext<T>> subContext = new ArrayList<>();

    /**
     * 定义字面量 token
     * @param type Token 类型
     * @param literal 字面量
     */
    public TokenInfo<T> defineToken(T type, @NonNull String literal) {
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
     * 定义不具名正则匹配 token
     * @param regex 正则
     */
    public TokenInfo<T> defineToken(RegexExpression regex) {
        return defineToken(null, regex);
    }

    /**
     * 声明 token，但不进行任何定义
     */
    public TokenInfo<T> declareToken(T type) {
        return defineToken(type, (RegexExpression) null);
    }

    /**
     * 通过 Token 类型找 Token
     */
    public TokenInfo<T> token(T type) throws UndefinedTokenException {
        return this.context.getToken(type);
    }

    /**
     * 定义匹配上下文
     * @param contextName 上下文名称
     */
    public LexGrammar<T> defineContext(String contextName) {
        if (!this.context.isDefaultContext()) {
            throw new UnsupportedOperationException("不支持嵌套子环境");
        }
        var ctx = new LexContext<T>(contextName);
        this.subContext.add(ctx);
        return new LexGrammar<>(ctx);
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
