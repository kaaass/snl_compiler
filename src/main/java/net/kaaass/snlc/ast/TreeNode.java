package net.kaaass.snlc.ast;

import lombok.*;
import net.kaaass.snlc.ast.attr.BaseAttr;

import java.util.List;

/**
 * node in Ast
 *
 * @author kevina
 */
@Data
@Getter
@NoArgsConstructor
public class TreeNode {
    private List<TreeNode> child;
    private TreeNode parent = null;
    private NodeKind nodeK;
    private Kind kind;
    private Integer dept = 0;
    private BaseAttr attr;
    private String typeName;

    public static TreeNode ofParent(TreeNode parent, NodeKind kind) {
        var node = new TreeNode();
        node.dept = parent.getDept() + 1;
        node.parent = parent;
        node.nodeK = kind;
        parent.getChild().add(node);

        return node;
    }

    /**
     * 标识符个数
     */
    private Integer idnum;

    /**
     * 标识符名字
     */
    private List<String> name;

    public void print(int dept) {
        // todo print tree
    }
}
