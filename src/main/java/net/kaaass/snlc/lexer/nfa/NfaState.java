package net.kaaass.snlc.lexer.nfa;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 非确定状态机状态，为状态图的一个节点，包含出边集合
 * @author kaaass
 */
@Getter
@Setter
@ToString(of = {"id"})
@EqualsAndHashCode(of = {"parent", "id"})
public class NfaState {

    private NfaGraph parent = null;

    @Setter(AccessLevel.PACKAGE)
    private int id = -1;

    /**
     * 状态出边
     */
    private List<NfaEdge> nextEdges = new ArrayList<>();

    public void addEdge(NfaEdge edge) {
        this.nextEdges.add(edge);
    }
}
