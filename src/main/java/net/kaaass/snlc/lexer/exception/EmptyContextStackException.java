package net.kaaass.snlc.lexer.exception;

/**
 * 上下文栈为空
 * @author kaaass
 */
public class EmptyContextStackException extends LexParseException {

    public EmptyContextStackException() {
        super("上下文栈为空");
    }
}
