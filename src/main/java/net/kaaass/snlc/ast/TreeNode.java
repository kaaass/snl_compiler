package net.kaaass.snlc.ast;

import lombok.Data;
import lombok.Getter;
import net.kaaass.snlc.ast.attr.BaseAttr;
import net.kaaass.snlc.ast.attr.ExprAttr;
import net.kaaass.snlc.ast.attr.ProcAttr;
import net.kaaass.snlc.ast.attr.VarKind;
import net.kaaass.snlc.lexer.snl.SnlLexeme;
import net.kaaass.snlc.parser.exception.TreeNodeException;

import java.util.ArrayList;
import java.util.List;

/**
 * node in Ast
 *
 * @author kevina
 */
@Data
@Getter
public class TreeNode {
    private List<TreeNode> child;
    private TreeNode parent = null;
    private NodeKind nodeK;
    private Kind kind;
    private Integer dept = 0;
    private BaseAttr attr;
    private List<String> name;

    public TreeNode() {
        child = new ArrayList<>();
        name = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "TreeNode{}";
    }

    public static TreeNode ofParent(TreeNode parent, NodeKind kind) {
        var node = new TreeNode();
        node.dept = parent.getDept() + 1;
        node.parent = parent;
        node.nodeK = kind;
        parent.getChild().add(node);

        return node;
    }

    public void addChild(TreeNode child) {
        this.child.add(child);
        this.updateChildDept();
    }

    public void updateChildDept() {
        for (var child: this.child) {
            child.dept = this.dept + 1;
            child.updateChildDept();
        }
    }

    public void print() throws TreeNodeException {
        System.out.println(this.printString());
    }

    public String printString() throws TreeNodeException {
        var builder = new StringBuilder();
        builder.append("    ".repeat(Math.max(0, this.dept)));
        builder.append(this.nodeK.toString());

        if (this.nodeK == NodeKind.ProK) {
            // pass
        } else if (this.nodeK == NodeKind.PheadK) {
            builder.append(" ");
            builder.append(this.name.get(0));
        } else if (this.nodeK == NodeKind.TypeK) {
            // pass
        } else if (this.nodeK == NodeKind.DecK) {
            builder.append(" ");
            if (this.attr != null && this.attr instanceof ProcAttr) {
                var attr = (ProcAttr) this.attr;
                builder.append(attr.getParamt());
                builder.append(" ");
                builder.append("param:");
            }
            builder.append(this.kind);
            for (var name: this.name) {
                builder.append(" ");
                builder.append(name);
            }
        } else if (this.nodeK == NodeKind.VarK) {
            // pass
        } else if (this.nodeK == NodeKind.ProcDecK) {
            builder.append(" ");
            builder.append(this.name.get(0));
        } else if (this.nodeK == NodeKind.StmLK) {
            // pass
        } else if (this.nodeK == NodeKind.StmtK) {
            builder.append(" ");
            builder.append(this.kind);
            if (this.kind == Kind.ReadK) {
                builder.append(" ");
                builder.append(this.name.get(0));
            }
        } else if (this.nodeK == NodeKind.ExpK) {
            var attr = (ExprAttr) this.attr;
            if (this.kind == Kind.OpK) {
                builder.append(" Op");

                if (attr.getOp() == SnlLexeme.PLUS) {
                    builder.append(" +");
                } else if (attr.getOp() == SnlLexeme.MINUS) {
                    builder.append(" -");
                } else if (attr.getOp() == SnlLexeme.TIMES) {
                    builder.append(" *");
                } else if (attr.getOp() == SnlLexeme.OVER) {
                    builder.append(" /");
                } else if (attr.getOp() == SnlLexeme.LT) {
                    builder.append(" <");
                } else if (attr.getOp() == SnlLexeme.EQ) {
                    builder.append(" =");
                } else {
                    throw new TreeNodeException();
                }
            } else if (this.kind == Kind.ConstK) {
                builder.append(" Const ");
                builder.append(this.name.get(0));
            } else if (attr != null && attr.getVarKind() == VarKind.IdV) {
                builder.append(" ");
                builder.append(this.name.get(0));
                builder.append(" IdV");
            } else {
                throw new TreeNodeException();
            }
        }
        builder.append("\n");

        for (var child: this.child) {
            builder.append(child.printString());
        }
        return builder.toString();
    }
}
