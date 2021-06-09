package net.kaaass.snlc.lexer.engine;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kaaass.snlc.lexer.*;
import net.kaaass.snlc.lexer.exception.*;

import java.util.Stack;
import java.util.function.Supplier;

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

    protected int line = -1;

    protected int position = -1;

    /**
     * 初始化引擎以读入流
     */
    public void init(IRevertibleStream stream) {
        this.currentContext = lexer.getContexts().get(LexContext.DEFAULT);
        this.stream = stream;
        // 初始化上下文栈
        this.contextStack.clear();
        this.contextStack.push(this.currentContext);
        // 初始化读取位置
        this.line = 1;
        this.position = 0;
        // 调用具体引擎初始化
        reset();
    }

    @Override
    public TokenResult<T> readToken() throws LexParseException {
        TokenResult<T> result;
        // 匹配
        try {
            result = readToken(this.currentContext);
        } catch (LexParseException e) {
            e.setLine(this.line);
            e.setPosition(this.position);
            throw e;
        }
        // 结束解析时上下文栈非空
        if (this.stream.isEof() && this.contextStack.size() > 1) {
            throw new ContextStackNonEmptyException();
        }
        return result;
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
        // 统计行
        if (chr == '\n') {
            this.line++;
            this.position = 0;
        } else {
            this.position++;
        }
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
                    tokenInfo,
                    matched));
        }
        // 否则调用 action
        // 准备上下文
        var ctx = new DefaultTokenContext(tokenInfo.getType(), endStreamState);
        // 调用
        action.accept(ctx);
        // 返回结果
        return ctx.getResult();
    }

    /**
     * 接受 Token
     */
    protected TokenResult<T> acceptToken(LexContext<T> context, TokenInfo<T> type, String content) {
        var token = new TokenResult<>(type);
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

        private final T curType;
        private final int endStreamState;
        private final ActionResult<T> result = new ActionResult<>();

        @SneakyThrows
        @Override
        public void accept(T type, String content) {
            var tokenInfo = BaseLexEngine.this.currentContext.getToken(type);
            this.result.setType(ActionResultType.ACCEPT);
            this.result.setToken(acceptToken(BaseLexEngine.this.currentContext, tokenInfo, content));
        }

        @Override
        public void reject() {
            this.result.setType(ActionResultType.REJECT);
        }

        @SneakyThrows
        @Override
        public TokenInfo<T> matchedType() {
            return BaseLexEngine.this.currentContext.getToken(this.curType);
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

        @SneakyThrows
        @Override
        public void fail(Supplier<? extends LexParseException> exceptionSupplier) {
            throw exceptionSupplier.get();
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
