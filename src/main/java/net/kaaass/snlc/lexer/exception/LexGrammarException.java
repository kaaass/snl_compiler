package net.kaaass.snlc.lexer.exception;

/**
 * 词法定义错误
 * @author kaaass
 */
public class LexGrammarException extends LexException {
    public LexGrammarException() {
    }

    public LexGrammarException(String message) {
        super(message);
    }

    public LexGrammarException(String message, Throwable cause) {
        super(message, cause);
    }

    public LexGrammarException(Throwable cause) {
        super(cause);
    }
}
