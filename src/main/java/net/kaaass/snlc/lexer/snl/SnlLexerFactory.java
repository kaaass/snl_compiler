package net.kaaass.snlc.lexer.snl;

import net.kaaass.snlc.lexer.LexGrammar;
import net.kaaass.snlc.lexer.Lexer;

import java.util.HashMap;
import java.util.Map;

import static net.kaaass.snlc.lexer.snl.SnlLexeme.*;
import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

/**
 * SNL 词法分析工厂类
 *
 * @author kaaass
 */
public class SnlLexerFactory {

    private static final Map<String, SnlLexeme> reservedWords = new HashMap<>() {{
        put("program", PROGRAM);
        put("type", TYPE);
        put("var", VAR);
        put("procedure", PROCEDURE);
        put("begin", BEGIN);
        put("end", END);
        put("array", ARRAY);
        put("of", OF);
        put("record", RECORD);
        put("if", IF);
        put("then", THEN);
        put("else", ELSE);
        put("fi", FI);
        put("while", WHILE);
        put("do", DO);
        put("endwh", ENDWH);
        put("read", READ);
        put("write", WRITE);
        put("return", RETURN);
        put("integer", INTEGER);
        put("char", CHAR);
    }};

    private static final Map<String, SnlLexeme> specialSymbols = new HashMap<>() {{
        put("+", PLUS);
        put("-", MINUS);
        put("*", TIMES);
        put("/", OVER);
        put("(", LPAREN);
        put(")", RPAREN);
        put(".", DOT);
        put("[", LMIDPAREN);
        put("]", RMIDPAREN);
        put(";", SEMI);
        put(":", COLON);
        put(",", COMMA);
        put("<", LT);
        put("=", EQ);
        put(":=", ASSIGN);
        put("..", UNDERANGE);
    }};

    private static <T> void addSymbolByMap(LexGrammar<T> grammar, Map<String, T> map) {
        map.forEach((literal, token) -> grammar.defineToken(token, literal));
    }

    public static Lexer<SnlLexeme> create() {
        var g = LexGrammar.<SnlLexeme>create();

        // 定义保留字
        addSymbolByMap(g, reservedWords);

        // 公共正则
        var alphabet = range('a', 'z');
        var digit = range('0', '9');
        var chr = or(alphabet, digit);

        // 整型常量
        g.defineToken(INTC, digit.oneOrMany());

        // 标识符
        g.defineToken(ID, concat(alphabet, chr.many()));

        // 字符常量
        g.defineToken(CHARC, concat(single('\''), concat(chr, single('\''))));

        // 定义特殊符号
        addSymbolByMap(g, specialSymbols);

        // TODO 定义注释
        g.defineToken(LANNOTATE, "{");
        g.defineToken(RANNOTATE, "}");

        // 定义空白
        g.defineToken(WHITESPACE, charset(' ', '\t', '\n'));

        return g.compile();
    }
}
