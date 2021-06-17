package net.kaaass.snlc.lexer.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 解析词法错误
 * @author kaaass
 */
@Getter
@Setter
public class LexParseException extends LexException {

    private int line = -1;
    private int position = -1;

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

    @Override
    public String getMessage() {
        return String.format("[%d:%d]: %s", this.line, this.position, super.getMessage());
    }
}
