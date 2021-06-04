package net.kaaass.snlc.lexer.dfa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 确定状态机状态，为状态图的一个节点，包含出边集合
 * @author kaaass
 */
@Data
public class DfaState {

    @Setter(AccessLevel.PACKAGE)
    private int id = -1;

    /**
     * 状态出边
     */
    private List<DfaEdge> nextEdges = new ArrayList<>();

    public void addEdge(DfaEdge edge) {
        this.nextEdges.add(edge);
    }
}
