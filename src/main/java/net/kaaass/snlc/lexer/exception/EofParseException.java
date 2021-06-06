package net.kaaass.snlc.lexer.exception;

/**
 * 解析流结束
 * @author kaaass
 */
public class EofParseException extends LexParseException {

    public EofParseException() {
        super("解析流结束");
    }
}
