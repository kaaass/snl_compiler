package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.dfa.DfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaEdge;

import java.util.List;

import static net.kaaass.snlc.lexer.SubsetConstructAlgorithmTest.createNfa;
import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class SubsetConstructAlgorithmTestMain {

    public static void printGraph(DfaGraph graph) {
        System.out.println("Start state: " + graph.getStartState().getId());
        graph.getStates().forEach(state -> {
            var from = state;
            state.getNextEdges().forEach(edge -> {
                var to = edge.getNextState();
                System.out.printf("%s --%c--> %s\n", from, edge.getMatchChar(), to);
            });
        });
    }

    public static void main(String[] args) {
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

        printGraph(SubsetConstructAlgorithm.convert(nfa));

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

        printGraph(SubsetConstructAlgorithm.convert(nfa));

        // 0 -> 1 => 3 => 4 -> 0
        // 0 -> 2 => 4
        nfa = createNfa(5, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(1, 3, 'a'),
                List.of(2, 4, 'a'),
                List.of(3, 4, 'b'),
                List.of(4, 0, NfaEdge.EMPTY_CHAR)
        ), 4);

        printGraph(SubsetConstructAlgorithm.convert(nfa));

        // a+b*|ac*|abc
        var regex = or(
                concat(single('a').oneOrMany(), single('b').many()).group(0),
                or(concat(single('a'), single('c').many()).group(1),
                        string("abc").group(2)));

        printGraph(SubsetConstructAlgorithm.convert(regex.toNfa()));
    }
}