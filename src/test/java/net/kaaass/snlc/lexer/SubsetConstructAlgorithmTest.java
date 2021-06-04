package net.kaaass.snlc.lexer;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class SubsetConstructAlgorithmTest extends TestCase {

    public void testGetCharset() {
        var alphabets = or(range('a', 'z'), range('A', 'Z')).many();
        var nfa = alphabets.accept(ThompsonRegexTranslator.INSTANCE);
        var result = SubsetConstructAlgorithm.getCharset(nfa);
        for (char cur = 'a'; cur <= 'z'; cur++) {
            assertTrue(result.contains(cur));
        }
        for (char cur = 'A'; cur <= 'Z'; cur++) {
            assertTrue(result.contains(cur));
        }
        //
        alphabets = or(string("if"), string("else").oneOrMany()).many();
        nfa = alphabets.accept(ThompsonRegexTranslator.INSTANCE);
        result = SubsetConstructAlgorithm.getCharset(nfa);
        assertEquals(Arrays.asList('e', 'f', 'i', 'l', 's'),
                result.stream().sorted().collect(Collectors.toList()));
    }

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
        nfa.setEndState(nfa.getStates().get(endState));
        return nfa;
    }

    public static Set<NfaState> nfaStates(NfaGraph nfa, int...states) {
        var ret = new HashSet<NfaState>();
        for (var st : states) {
            ret.add(nfa.getStates().get(st));
        }
        return ret;
    }

    public void testGetClosure() {
        // 0 -> 1 => 4
        // 0 -> 2 -> 3
        // 0 => 5
        var nfa = createNfa(6, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(0, 5, 'a'),
                List.of(1, 4, 'b'),
                List.of(2, 3, NfaEdge.EMPTY_CHAR)
        ), 5);

        assertEquals(nfaStates(nfa, 0, 1, 2, 3),
                SubsetConstructAlgorithm.getClosure(Set.of(nfa.getStartState())));

        // 0 <-> 1 => 3 -> 0
        // 0 -> 2 -> 4 => 3
        nfa = createNfa(5, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(1, 0, NfaEdge.EMPTY_CHAR),
                List.of(1, 3, 'a'),
                List.of(2, 4, NfaEdge.EMPTY_CHAR),
                List.of(3, 0, NfaEdge.EMPTY_CHAR),
                List.of(4, 3, 'b')
        ), 4);

        assertEquals(nfaStates(nfa, 0, 1, 2, 4),
                SubsetConstructAlgorithm.getClosure(Set.of(nfa.getStartState())));
    }

    public void testMove() {
        // 0 -> 1 => 2 => 3
        var nfa = createNfa(4, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(1, 2, 'a'),
                List.of(2, 3, 'b')
        ), 3);

        assertEquals(nfaStates(nfa, 2),
                SubsetConstructAlgorithm.move(nfaStates(nfa, 0, 1), 'a'));
        assertEquals(nfaStates(nfa),
                SubsetConstructAlgorithm.move(nfaStates(nfa, 0, 1), 'b'));
        assertEquals(nfaStates(nfa, 3),
                SubsetConstructAlgorithm.move(nfaStates(nfa, 2), 'b'));

        // 0 <-> 1 => 3 -> 0
        // 0 -> 2 -> 4 => 3
        nfa = createNfa(5, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(1, 0, NfaEdge.EMPTY_CHAR),
                List.of(1, 3, 'a'),
                List.of(2, 4, NfaEdge.EMPTY_CHAR),
                List.of(3, 0, NfaEdge.EMPTY_CHAR),
                List.of(4, 3, 'b')
        ), 4);

        assertEquals(nfaStates(nfa, 3),
                SubsetConstructAlgorithm.move(nfaStates(nfa, 0, 1, 2, 4), 'a'));
        assertEquals(nfaStates(nfa, 3),
                SubsetConstructAlgorithm.move(nfaStates(nfa, 0, 1, 2, 4), 'b'));
    }
}