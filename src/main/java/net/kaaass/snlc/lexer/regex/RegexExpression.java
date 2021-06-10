package net.kaaass.snlc.lexer.regex;

import lombok.Getter;
import net.kaaass.snlc.lexer.ThompsonRegexTranslator;
import net.kaaass.snlc.lexer.nfa.NfaGraph;

import java.util.HashSet;
import java.util.Set;

/**
 * 正则表达式节点基类
 *
 * @author kaaass
 */
public abstract class RegexExpression {

    @Getter
    private int groupId = -1;

    /**
     * 接受访问者访问
     */
    public abstract <T> T accept(IRegexExprVisitor<T> visitor);

    /**
     * 设置匹配组
     */
    public RegexExpression group(int groupId) {
        this.groupId = groupId;
        return this;
    }

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
     * 匹配任何字符
     */
    public static RegexExpression anychar() {
        return range(Character.MIN_VALUE, '\u007F');
    }

    /**
     * 匹配字符集
     */
    public static RegexExpression charset(Set<Character> charSet) {
        return new ExprCharSet(charSet);
    }

    /**
     * 匹配字符集
     */
    public static RegexExpression charset(char ...chars) {
        var set = new HashSet<Character>();
        for (char chr : chars) {
            set.add(chr);
        }
        return new ExprCharSet(set);
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

    public abstract String friendlyString();

    @Override
    public String toString() {
        return String.format("Regex[ %s ]", friendlyString());
    }
}
