package net.kaaass.snlc.lexer.engine;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kaaass.snlc.lexer.*;
import net.kaaass.snlc.lexer.exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 词法匹配引擎基类
 *
 * @author kaaass
 */
@RequiredArgsConstructor
public abstract class BaseLexEngine<T> implements ILexEngine<T> {

    protected final Lexer<T> lexer;

    protected IRevertibleStream stream = null;

    protected LexContext<T> currentContext = null;

    protected Stack<LexContext<T>> contextStack = new Stack<>();

    /**
     * 初始化引擎以读入流
     */
    public void init(IRevertibleStream stream) {
        this.currentContext = lexer.getContexts().get(LexContext.DEFAULT);
        this.stream = stream;
        // 初始化上下文栈
        contextStack.clear();
        contextStack.push(this.currentContext);
        // 调用具体引擎初始化
        reset();
    }

    @Override
    public TokenResult<T> readToken() throws LexParseException {
        return readToken(this.currentContext);
    }

    /**
     * 重置所有匹配状态
     */
    public abstract void reset();

    protected abstract TokenResult<T> readToken(LexContext<T> context) throws LexParseException;

    /**
     * 从流读入
     */
    protected char read() {
        var chr = this.stream.read();
        // TODO count line
        return chr;
    }

    /**
     * 处理单次匹配结果
     */
    protected ActionResult<T> processMatchedToken(LexContext<T> context, int tokenId, int endStreamState) {
        var tokenInfo = context.getToken(tokenId);
        var action = tokenInfo.getMatchedAction();
        // 如果没有设置动作，直接接受
        if (action == null) {
            String matched;
            // LiteralTokenInfo 可以直接获得声明时使用的字面量
            if (tokenInfo instanceof LiteralTokenInfo<?>) {
                matched = ((LiteralTokenInfo<T>) tokenInfo).getLiteral();
            } else {
                matched = getMatchedString(endStreamState);
            }
            return ActionResult.accept(acceptToken(context,
                    tokenInfo.getType(),
                    matched));
        }
        // 否则调用 action
        // 准备上下文
        var ctx = new DefaultTokenContext(context, tokenInfo.getType(), endStreamState);
        // 调用
        action.accept(ctx);
        // 返回结果
        return ctx.getResult();
    }

    /**
     * 接受 Token
     */
    protected TokenResult<T> acceptToken(LexContext<T> context, T type, String content) {
        var token = new TokenResult<>(context.getToken(type));
        token.setToken(content);
        return token;
    }

    /**
     * 获得匹配 token 的字符串
     */
    protected String getMatchedString(int endStreamState) {
        return this.stream.readUtilState(endStreamState);
    }

    /**
     * 将新上下文入栈
     */
    protected void pushContext(String contextName) throws UndefinedContextException {
        var ctx = this.lexer.getContext(contextName);
        this.currentContext = ctx;
        this.contextStack.push(ctx);
    }

    /**
     * 弹出上下文栈
     */
    protected void popContext() throws EmptyContextStackException {
        if (this.contextStack.size() <= 1) {
            throw new EmptyContextStackException();
        }
        this.contextStack.pop();
        this.currentContext = this.contextStack.peek();
    }

    /**
     * Token 匹配上下文。匹配执行动作的参数。
     */
    @Getter
    @RequiredArgsConstructor
    public class DefaultTokenContext extends TokenInfo.TokenContext<T> {

        private final LexContext<T> context;
        private final T curType;
        private final int endStreamState;
        private final ActionResult<T> result = new ActionResult<>();

        @Override
        public void accept(T type, String content) {
            this.result.setType(ActionResultType.ACCEPT);
            this.result.setToken(acceptToken(context, type, content));
        }

        @Override
        public void reject() {
            this.result.setType(ActionResultType.REJECT);
        }

        @Override
        public TokenInfo<T> matchedType() {
            return this.context.getToken(this.curType);
        }

        @Override
        public String matchedString() {
            return getMatchedString(this.endStreamState);
        }

        @SneakyThrows
        @Override
        public void pushContext(String contextName) {
            try {
                BaseLexEngine.this.pushContext(contextName);
            } catch (UndefinedContextException e) {
                throw new ParseTimeGrammarCheckException(e);
            }
        }

        @SneakyThrows
        @Override
        public void popContext() {
            BaseLexEngine.this.popContext();
        }

        @Override
        public LexContext<T> currentContext() {
            return currentContext;
        }
    }

    /**
     * 动作调用执行结果类型
     */
    public enum ActionResultType {
        // 无操作
        NONE,
        // 接受
        ACCEPT,
        // 拒绝
        REJECT
    }

    /**
     * 动作调用执行结果
     */
    @Data
    public static class ActionResult<T> {

        private ActionResultType type = ActionResultType.NONE;

        private TokenResult<T> token = null;

        public static <T> ActionResult<T> accept(TokenResult<T> token) {
            var ret = new ActionResult<T>();
            ret.setType(ActionResultType.ACCEPT);
            ret.setToken(token);
            return ret;
        }
    }
}
