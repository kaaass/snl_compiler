package net.kaaass.snlc.lexer.exception;

/**
 * 解析时词法定义检查
 * @author kaaass
 */
public class ParseTimeGrammarCheckException extends LexParseException {

    public ParseTimeGrammarCheckException(LexGrammarException exception) {
        super("解析时词法定义检查失败", exception);
    }
}
