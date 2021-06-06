package net.kaaass.snlc.lexer.exception;

/**
 * 上下文栈在流结束时非空
 * @author kaaass
 */
public class ContextStackNonEmptyException extends LexParseException {

    public ContextStackNonEmptyException() {
        super("上下文栈在流结束时非空");
    }
}
