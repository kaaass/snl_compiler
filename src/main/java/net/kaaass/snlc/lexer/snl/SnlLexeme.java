package net.kaaass.snlc.lexer.snl;

/**
 * SNL Token 种类
 *
 * @author kaaass
 */
public enum SnlLexeme {

    // 保留字
    PROGRAM, PROCEDURE, TYPE, VAR, IF,
    THEN, ELSE, FI, WHILE, DO,
    ENDWH, BEGIN, END, READ, WRITE,
    ARRAY, OF, RECORD, RETURN,

    // 类型
    INTEGER, CHAR,

    // 多字符单词符号
    ID, INTC, CHARC,

    // 特殊符号
    ASSIGN, EQ, LT, PLUS, MINUS,
    TIMES, OVER, LPAREN, RPAREN, DOT,
    COLON, SEMI, COMMA, LMIDPAREN, RMIDPAREN,
    UNDERANGE,

    // 注释
    LCOMMENT, RCOMMENT, COMMENT,

    // 空白
    WHITESPACE,

}
