package net.kaaass.snlc.ast;

/**
 * 标志节点类型
 * @author Kevin Axel
 */
public enum Kind {
    // 声明类型
    ArrayK, CharK, IntegerK, RecordK, IdK,

    // 语句类型
    IfK, WhileK, AssignK, ReadK, WriteK, CallK, ReturnK,

    // 表达式类型
    OpK, ConstK, IdEK
}
