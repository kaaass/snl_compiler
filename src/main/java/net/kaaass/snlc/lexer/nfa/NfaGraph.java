package net.kaaass.snlc.lexer.nfa;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * NFA 状态机状态图，包含状态列表、进入边、最终节点
 *
 * @author kaaass
 */
@Data
public class NfaGraph {

    /**
     * 状态表
     */
    private final List<NfaState> states = new ArrayList<>();

    /**
     * 进入边
     */
    private NfaEdge entryEdge = null;

    /**
     * 最终状态
     */
    private NfaState lastState = null;

    /**
     * 增加状态
     */
    public void addState(NfaState state) {
        state.setId(this.states.size());
        this.states.add(state);
    }
}
