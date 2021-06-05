package net.kaaass.snlc.lexer.exception;

/**
 * 词法错误
 * @author kaaass
 */
public class LexException extends Exception {
    public LexException() {
    }

    public LexException(String message) {
        super(message);
    }

    public LexException(String message, Throwable cause) {
        super(message, cause);
    }

    public LexException(Throwable cause) {
        super(cause);
    }
}
