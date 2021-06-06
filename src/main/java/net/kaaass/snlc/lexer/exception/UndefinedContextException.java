package net.kaaass.snlc.lexer.exception;

/**
 * 指定上下文不存在
 * @author kaaass
 */
public class UndefinedContextException extends LexGrammarException {
    public UndefinedContextException(String contextName) {
        super("上下文 '" + contextName + "' 不存在");
    }
}
