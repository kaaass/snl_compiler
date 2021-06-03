package net.kaaass.snlc.lexer.nfa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * NFA 状态机状态，为状态图的一个节点，包含
 *
 * @author kaaass
 */
@Data
public class NfaState {

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
