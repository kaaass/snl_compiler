package net.kaaass.snlc.lexer.exception;

/**
 * 指定 Token 不存在
 * @author kaaass
 */
public class UndefinedTokenException extends LexGrammarException {

    public UndefinedTokenException() {
        super("无法获取不具名token");
    }

    public UndefinedTokenException(Object tokenEnum) {
        super("token '" + tokenEnum.toString() + "' 不存在");
    }
}
