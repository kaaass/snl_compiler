package net.kaaass.snlc.lexer.dfa;

import java.util.*;

public class DfaUtils {

    public static DfaGraph createDfa(int n, List<List<Object>> edges) {
        var dfa = new DfaGraph();
        // node
        for (int i = 0; i < 6; i++) {
            dfa.addState(new DfaState());
        }
        // edge
        for (var edge : edges) {
            DfaEdge.edge((Character) edge.get(2))
                    .link(dfa.getStates().get((Integer) edge.get(0)),
                            dfa.getStates().get((Integer) edge.get(1)));
        }
        // ret
        dfa.setStartState(dfa.getStates().get(0));
        return dfa;
    }

    public static Set<DfaState> dfaStates(DfaGraph dfa, int...states) {
        var ret = new HashSet<DfaState>();
        for (var st : states) {
            ret.add(dfa.getStates().get(st));
        }
        return ret;
    }

    public static void printGraph(DfaGraph graph) {
        System.out.println("Start state: " + graph.getStartState().getId());
        graph.getStates().forEach(from -> {
            var toMapChars = new HashMap<DfaState, Set<Character>>();
            from.getNextEdges().forEach(edge -> {
                var to = edge.getNextState();
                var chars = toMapChars.computeIfAbsent(to, dfaState -> new HashSet<>());
                chars.add(edge.getMatchChar());
            });
            toMapChars.forEach((to, chars) -> {
                var sorted = new ArrayList<>(chars);
                sorted.sort(Character::compareTo);
                System.out.printf("%s --%s--> %s\n", from, sorted, to);
            });
        });
    }
}
