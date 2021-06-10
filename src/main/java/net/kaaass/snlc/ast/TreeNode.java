package net.kaaass.snlc.ast;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * node in Ast
 *
 * @author kevina
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeNode {
    private List<TreeNode> child;
    private TreeNode sibling;
    private Integer lineno;
    private NodeKind nodeK;
    private Kind kind;

    /**
     * 标识符个数
     */
    private Integer idnum;

    /**
     * 标识符名字
     */
    private List<String> name;


    public static TreeNode ofDecK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.DecK;
        return node;
    }

    public static TreeNode ofExpK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.ExpK;
        return node;
    }

    public static TreeNode ofProK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.ProK;
        return node;
    }

    public static TreeNode ofProcDecK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.ProcDecK;
        return node;
    }

    public static TreeNode ofStmLK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.StmLK;
        return node;
    }

    public static TreeNode ofStmtK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.StmtK;
        return node;
    }

    public static TreeNode ofTypeK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.TypeK;
        return node;
    }

    public static TreeNode ofVarK() {
        var node = new TreeNode();
        node.nodeK = NodeKind.VarK;
        return node;
    }

    public void print(int dept) {
        // todo print tree
    }
}
