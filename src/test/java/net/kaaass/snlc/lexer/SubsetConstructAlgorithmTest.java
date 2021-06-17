package net.kaaass.snlc.lexer;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaState;
import net.kaaass.snlc.lexer.nfa.NfaUtils;

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

    public void testGetClosure() {
        // 0 -> 1 => 4
        // 0 -> 2 -> 3
        // 0 => 5
        var nfa = NfaUtils.createNfa(6, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(0, 5, 'a'),
                List.of(1, 4, 'b'),
                List.of(2, 3, NfaEdge.EMPTY_CHAR)
        ), 5);

        assertEquals(NfaUtils.nfaStates(nfa, 0, 1, 2, 3),
                SubsetConstructAlgorithm.getClosure(Set.of(nfa.getStartState())));

        // 0 <-> 1 => 3 -> 0
        // 0 -> 2 -> 4 => 3
        nfa = NfaUtils.createNfa(5, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(1, 0, NfaEdge.EMPTY_CHAR),
                List.of(1, 3, 'a'),
                List.of(2, 4, NfaEdge.EMPTY_CHAR),
                List.of(3, 0, NfaEdge.EMPTY_CHAR),
                List.of(4, 3, 'b')
        ), 4);

        assertEquals(NfaUtils.nfaStates(nfa, 0, 1, 2, 4),
                SubsetConstructAlgorithm.getClosure(Set.of(nfa.getStartState())));
    }

    public void testMove() {
        // 0 -> 1 => 2 => 3
        var nfa = NfaUtils.createNfa(4, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(1, 2, 'a'),
                List.of(2, 3, 'b')
        ), 3);

        assertEquals(NfaUtils.nfaStates(nfa, 2),
                SubsetConstructAlgorithm.move(NfaUtils.nfaStates(nfa, 0, 1), 'a'));
        assertEquals(NfaUtils.nfaStates(nfa),
                SubsetConstructAlgorithm.move(NfaUtils.nfaStates(nfa, 0, 1), 'b'));
        assertEquals(NfaUtils.nfaStates(nfa, 3),
                SubsetConstructAlgorithm.move(NfaUtils.nfaStates(nfa, 2), 'b'));

        // 0 <-> 1 => 3 -> 0
        // 0 -> 2 -> 4 => 3
        nfa = NfaUtils.createNfa(5, List.of(
                List.of(0, 1, NfaEdge.EMPTY_CHAR),
                List.of(0, 2, NfaEdge.EMPTY_CHAR),
                List.of(1, 0, NfaEdge.EMPTY_CHAR),
                List.of(1, 3, 'a'),
                List.of(2, 4, NfaEdge.EMPTY_CHAR),
                List.of(3, 0, NfaEdge.EMPTY_CHAR),
                List.of(4, 3, 'b')
        ), 4);

        assertEquals(NfaUtils.nfaStates(nfa, 3),
                SubsetConstructAlgorithm.move(NfaUtils.nfaStates(nfa, 0, 1, 2, 4), 'a'));
        assertEquals(NfaUtils.nfaStates(nfa, 3),
                SubsetConstructAlgorithm.move(NfaUtils.nfaStates(nfa, 0, 1, 2, 4), 'b'));
    }
}