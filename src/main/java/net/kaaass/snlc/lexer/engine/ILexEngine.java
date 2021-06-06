package net.kaaass.snlc.lexer.engine;

import net.kaaass.snlc.lexer.TokenResult;
import net.kaaass.snlc.lexer.exception.EofParseException;
import net.kaaass.snlc.lexer.exception.LexParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行词法分析的引擎
 * @author kaaass
 */
public interface ILexEngine<T> {

    /**
     * 读入一个 Token
     * @return 可能返回 null
     * @throws LexParseException 若输入流结束将返回 EofParseException
     */
    TokenResult<T> readToken() throws LexParseException;

    /**
     * 读入所有 Token 直至流结束
     */
    default List<TokenResult<T>> readAllTokens() throws LexParseException {
        var ret = new ArrayList<TokenResult<T>>();
        TokenResult<T> token;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                if ((token = readToken()) != null) {
                    ret.add(token);
                }
            }
        } catch (EofParseException ignore) {
        }
        return ret;
    }
}
