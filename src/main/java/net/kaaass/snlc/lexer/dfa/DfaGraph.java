package net.kaaass.snlc.lexer.dfa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 确定状态机状态图，包含状态列表、开始状态
 */
@Getter
@Setter
@ToString
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
        state.setParent(this);
        this.states.add(state);
    }

    /**
     * 获得 DFA 中的所有字符集
     */
    public Set<Character> getCharset() {
        return this.states.stream()
                .flatMap(state -> state.getNextEdges().stream())
                .map(DfaEdge::getMatchChar)
                .collect(Collectors.toSet());
    }
}
