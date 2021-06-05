package net.kaaass.snlc.lexer.exception;

/**
 * 解析词法错误
 * @author kaaass
 */
public class LexParseException extends LexException {
    public LexParseException() {
    }

    public LexParseException(String message) {
        super(message);
    }

    public LexParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LexParseException(Throwable cause) {
        super(cause);
    }
}
