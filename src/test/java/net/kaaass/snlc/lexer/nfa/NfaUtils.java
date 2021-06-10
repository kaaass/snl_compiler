package net.kaaass.snlc.lexer.nfa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NfaUtils {

    public static NfaGraph createNfa(int n, List<List<Object>> edges, int endState) {
        var nfa = new NfaGraph();
        // node
        for (int i = 0; i < 6; i++) {
            nfa.addState(new NfaState());
        }
        // edge
        for (var edge : edges) {
            NfaEdge.edge((Character) edge.get(2))
                    .link(nfa.getStates().get((Integer) edge.get(0)),
                            nfa.getStates().get((Integer) edge.get(1)));
        }
        // ret
        nfa.setEntryEdge(NfaEdge.emptyTo(nfa.getStates().get(0)));
        var end = nfa.getStates().get(endState);
        end.setMatchedToken(233);
        nfa.setEndState(end);
        return nfa;
    }

    public static Set<NfaState> nfaStates(NfaGraph nfa, int...states) {
        var ret = new HashSet<NfaState>();
        for (var st : states) {
            ret.add(nfa.getStates().get(st));
        }
        return ret;
    }

    public static void printGraph(NfaGraph graph) {
        System.out.println("Start state: " + graph.getStartState().getId());
        if (graph.getEndState() != null) System.out.println("End state: " + graph.getEndState().getId());
        graph.getStates().forEach(state -> {
            var from = state.getId();
            state.getNextEdges().forEach(edge -> {
                var to = edge.getNextState().getId();
                System.out.printf("%d --%c--> %d\n", from, edge.getMatchChar(), to);
            });
        });
    }
}
