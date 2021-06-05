package net.kaaass.snlc.lexer.exception;

/**
 * 解析时遇到未知字符
 * @author kaaass
 */
public class UnexpectedCharException extends LexParseException {

    public UnexpectedCharException(char chr) {
        super("解析时遇到未知字符：'" + chr + "'");
    }
}
