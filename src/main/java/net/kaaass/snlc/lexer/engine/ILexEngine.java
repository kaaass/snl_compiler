package net.kaaass.snlc.lexer.engine;

import net.kaaass.snlc.lexer.TokenResult;
import net.kaaass.snlc.lexer.exception.LexParseException;

/**
 * 执行词法分析的引擎
 * @author kaaass
 */
public interface ILexEngine<T> {

    /**
     * 读入一个 Token
     */
    TokenResult<T> readToken() throws LexParseException;
}
