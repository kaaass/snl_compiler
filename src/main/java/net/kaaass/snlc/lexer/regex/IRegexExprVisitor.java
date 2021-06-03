package net.kaaass.snlc.lexer.regex;

/**
 * 正则表达式访问者接口
 * @author kaaass
 */
public interface IRegexExprVisitor<T> {

    T visit(ExprEmpty exprEmpty);

    T visit(ExprCharSet exprChar);

    T visit(ExprString exprString);

    T visit(ExprKleeneStar exprKleeneStar);

    T visit(ExprConcatenation exprConcatenation);

    T visit(ExprAlternation exprAlternation);
}
