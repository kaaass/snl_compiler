package net.kaaass.snlc.lexer.engine;

import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.LexContext;
import net.kaaass.snlc.lexer.Lexer;
import net.kaaass.snlc.lexer.TokenResult;
import net.kaaass.snlc.lexer.exception.LexParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法匹配引擎基类
 * @author kaaass
 */
@RequiredArgsConstructor
public abstract class BaseLexEngine<T> implements ILexEngine<T> {

    protected final Lexer<T> lexer;

    protected IRevertibleStream stream = null;

    protected LexContext<T> currentContext = null;

    /**
     * 初始化引擎以读入流
     */
    public void init(IRevertibleStream stream) {
        this.currentContext = lexer.getContexts().get(LexContext.DEFAULT);
        this.stream = stream;
        reset();
    }

    @Override
    public TokenResult<T> readToken() throws LexParseException {
        return readToken(this.currentContext);
    }

    public List<TokenResult<T>> readAllTokens() throws LexParseException {
        var ret = new ArrayList<TokenResult<T>>();
        TokenResult<T> token;
        while ((token = readToken()) != null) {
            ret.add(token);
        }
        return ret;
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
}
