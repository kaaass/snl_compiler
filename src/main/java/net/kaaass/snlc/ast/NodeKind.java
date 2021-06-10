package net.kaaass.snlc.ast;

/**
 * 语法树节点类型
 * @author Kevin Axel
 */
public enum NodeKind {
    // 标志节点类型
    // 根标志节点
    ProK,

    // 程序头标志节点
    PheadK,

    // 类型声明标志节点
    TypeK,

    // 变量声明标志节点
    VarK,

    // 函数声明标志节点
    ProcDecK,

    // 语句序列标志节点
    StmLK,

    // 具体节点
    // 声明节点
    DecK,

    // 语句节点
    StmtK,

    // 表达式节点
    ExpK
}
