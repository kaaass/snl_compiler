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
    private NfaState endState = null;

    /**
     * 增加状态
     */
    public void addState(NfaState state) {
        state.setId(this.states.size());
        this.states.add(state);
    }

    /**
     * 占有子图的状态节点
     */
    public void annexSubGraph(NfaGraph sub) {
        // 添加节点
        sub.getStates().forEach(this::addState);
        // 清空子图
        sub.getStates().clear();
    }

    /**
     * 图是否正规化
     */
    public boolean isNormalised() {
        return this.entryEdge.isEmpty();
    }

    /**
     * 正规化图。正规化后的图入边为空边。
     */
    public void normalise() {
        if (isNormalised()) {
            return;
        }
        var startState = new NfaState();
        startState.addEdge(this.entryEdge);
        addState(startState);
        this.entryEdge = NfaEdge.emptyTo(startState);
    }

    /**
     * 获得开始状态。只有正规化的图才有开始状态。
     */
    public NfaState getStartState() {
        normalise();
        return this.entryEdge.getNextState();
    }
}
