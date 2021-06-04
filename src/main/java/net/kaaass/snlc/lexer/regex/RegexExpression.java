package net.kaaass.snlc.lexer.regex;

import net.kaaass.snlc.lexer.ThompsonRegexTranslator;
import net.kaaass.snlc.lexer.nfa.NfaGraph;

/**
 * 正则表达式节点基类
 *
 * @author kaaass
 */
public abstract class RegexExpression {

    /**
     * 接受访问者访问
     */
    public abstract <T> T accept(IRegexExprVisitor<T> visitor);

    /**
     * 匹配单个字符
     */
    public static RegexExpression single(char chr) {
        return new ExprCharSet(chr, chr);
    }

    /**
     * 匹配字符区间
     */
    public static RegexExpression range(char start, char end) {
        return new ExprCharSet(start, end);
    }

    /**
     * 匹配字符串
     */
    public static RegexExpression string(String literal) {
        return new ExprString(literal);
    }

    /**
     * 顺序匹配表达式
     */
    public static RegexExpression concat(RegexExpression left, RegexExpression right) {
        return new ExprConcatenation(left, right);
    }

    /**
     * 匹配其中一个表达式
     */
    public static RegexExpression or(RegexExpression left, RegexExpression right) {
        return new ExprAlternation(left, right);
    }

    /**
     * 匹配重复 0 至多次
     */
    public RegexExpression many() {
        return new ExprKleeneStar(this);
    }

    /**
     * 匹配重复 1 至多次
     */
    public RegexExpression oneOrMany() {
        return concat(this, this.many());
    }

    /**
     * 匹配 0 或 1 次
     */
    public RegexExpression zeroOrOne() {
        return or(this, new ExprEmpty());
    }

    public NfaGraph toNfa() {
        return accept(ThompsonRegexTranslator.INSTANCE);
    }
}
