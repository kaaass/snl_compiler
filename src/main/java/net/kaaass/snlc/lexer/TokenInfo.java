package net.kaaass.snlc.lexer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kaaass.snlc.lexer.engine.BaseLexEngine;
import net.kaaass.snlc.lexer.exception.EmptyContextStackException;
import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.exception.UndefinedContextException;
import net.kaaass.snlc.lexer.regex.RegexExpression;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

    /**
     * 匹配后执行的动作
     */
    private Consumer<TokenContext<T>> matchedAction = null;

    /**
     * 设置匹配后执行的动作。若不设置则默认接受。
     */
    public void action(Consumer<TokenContext<T>> consumer) {
        this.matchedAction = consumer;
    }

    /**
     * 忽略匹配到的 Token
     */
    public void ignore() {
        action(ctx -> {});
    }

    /**
     * Token 是否仅是声明
     */
    public boolean isDeclaration() {
        return this.regex == null;
    }

    /**
     * Token 匹配上下文。匹配执行动作的参数。
     */
    public abstract static class TokenContext<R> {

        /**
         * 匹配当前 Token
         */
        public void accept() {
            accept(matchedType().type, matchedString());
        }

        /**
         * 匹配指定 Token
         * @param type Token 类型
         * @param content 内容
         */
        public abstract void accept(R type, String content);

        /**
         * 解决指定 Token
         */
        public abstract void reject();

        /**
         * 已匹配的类型
         */
        public abstract TokenInfo<R> matchedType();

        /**
         * 已匹配的内容
         */
        public abstract String matchedString();

        /**
         * 将新上下文入栈
         * @param contextName 上下文名称
         */
        public abstract void pushContext(String contextName);

        /**
         * 弹出上下文栈
         */
        public abstract void popContext();

        /**
         * 当前上下文
         */
        public abstract LexContext<R> currentContext();

        /**
         * 匹配失败。该方法应视为会造成处理函数立刻退出。
         * @param exceptionSupplier 提供匹配异常
         */
        public abstract void fail(Supplier<? extends LexParseException> exceptionSupplier);
    }
}
