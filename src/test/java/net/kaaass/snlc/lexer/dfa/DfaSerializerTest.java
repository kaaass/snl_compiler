package net.kaaass.snlc.lexer.dfa;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DfaSerializerTest extends TestCase {

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

    public void testSerialize() {
        // 0 -> 1 -> 2
        // 0 -> 2
        // 1 -> 0
        List<List<Object>> edges = List.of(
                List.of(0, 1, 'a'),
                List.of(1, 2, 'b'),
                List.of(0, 2, 'c'),
                List.of(1, 0, 'a')
        );
        var dfa = createDfa(3, edges);
        var result = DfaSerializer.serialize(dfa);

        var C = result.getCharMap();
        assertEquals(Set.of('a', 'b', 'c'), C.keySet());
        var T = result.getTransMat();
        for (var edge : edges) {
            assertEquals(edge.get(1), T[(int) edge.get(0)][C.get(edge.get(2))]);
        }
    }
}