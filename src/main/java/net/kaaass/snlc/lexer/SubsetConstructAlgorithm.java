package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.dfa.DfaEdge;
import net.kaaass.snlc.lexer.dfa.DfaGraph;
import net.kaaass.snlc.lexer.dfa.DfaState;
import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 子集构造算法将 NFA 转为 DFA
 * @author kaaass
 */
public class SubsetConstructAlgorithm {

    /**
     * 子集构造算法核心
     */
    public static DfaGraph convert(NfaGraph nfa) {
        Set<NfaState> currentNfaSts;
        DfaState newState;
        var dfa = new DfaGraph();
        var queue = new ArrayDeque<DfaState>();
        var subsetMap = new HashMap<Set<NfaState>, DfaState>();
        // 获得字符集
        var charset = getCharset(nfa);
        // 初始化：添加初始状态
        currentNfaSts = getClosure(Set.of(nfa.getStartState()));
        newState = createState(dfa, subsetMap, currentNfaSts);
        dfa.setStartState(newState);
        queue.add(newState);
        // 遍历
        while (!queue.isEmpty()) {
            var state = queue.poll();
            for (var chr : charset) {
                currentNfaSts = getClosure(move(state.getNfaStates(), chr));
                if (currentNfaSts.isEmpty()) {
                    continue;
                }
                // 如果产生新状态
                if (!subsetMap.containsKey(currentNfaSts)) {
                    newState = createState(dfa, subsetMap, currentNfaSts);
                    queue.add(newState);
                } else {
                    newState = subsetMap.get(currentNfaSts);
                }
                // 创建转移
                DfaEdge.edge(chr).link(state, newState);
            }
        }
        return dfa;
    }

    private static DfaState createState(DfaGraph dfa, HashMap<Set<NfaState>, DfaState> subsetMap, Set<NfaState> cur) {
        var newState = new DfaState();
        // 创建状态
        subsetMap.put(cur, newState);
        newState.setNfaStates(cur);
        // 处理匹配 token
        var matchedTokens = cur.stream()
                .map(NfaState::getMatchedToken)
                .filter(id -> id != -1)
                .sorted()
                .collect(Collectors.toList());
        if (!matchedTokens.isEmpty()) {
            newState.setMatchedTokens(matchedTokens);
        }
        // 添加状态
        dfa.addState(newState);
        // 返回 ID
        return newState;
    }

    /**
     * 获得 NFA 中的所有字符集
     */
    public static Set<Character> getCharset(NfaGraph nfa) {
        return nfa.getStates().stream()
                .flatMap(state -> state.getNextEdges().stream())
                .filter(edge -> !edge.isEmpty())
                .map(NfaEdge::getMatchChar)
                .collect(Collectors.toSet());
    }

    /**
     * 获得空闭包。使用深度优先遍历
     */
    public static Set<NfaState> getClosure(Set<NfaState> states) {
        var stack = new Stack<NfaState>();
        var result = new HashSet<>(states);
        // 搜索所有元素
        stack.addAll(states);
        // DFS
        while (!stack.empty()) {
            var cur = stack.pop();
            // 所有空边可转移的状态
            cur.getNextEdges().stream()
                    .filter(NfaEdge::isEmpty)
                    .map(NfaEdge::getNextState)
                    .forEach(state -> {
                        // 如果为新状态，加入结果并入栈
                        if (result.add(state)) {
                            stack.add(state);
                        }
                    });
        }
        return result;
    }

    /**
     * 获得转移后状态集
     */
    public static Set<NfaState> move(Set<NfaState> states, char matchChar) {
        return states.stream()
                .flatMap(state -> state.getNextEdges().stream())
                .filter(edge -> edge.getMatchChar() == matchChar)
                .map(NfaEdge::getNextState)
                .collect(Collectors.toSet());
    }
}
