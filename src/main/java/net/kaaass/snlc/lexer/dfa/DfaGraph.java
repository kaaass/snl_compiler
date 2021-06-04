package net.kaaass.snlc.lexer.dfa;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 确定状态机状态图，包含状态列表、开始状态
 */
@Data
public class DfaGraph {

    /**
     * 状态表
     */
    private final List<DfaState> states = new ArrayList<>();

    /**
     * 开始状态
     */
    private DfaState startState = null;

    /**
     * 增加状态
     */
    public void addState(DfaState state) {
        state.setId(this.states.size());
        this.states.add(state);
    }
}
