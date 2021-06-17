package net.kaaass.snlc.lexer.dfa;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.GlushkovRegexTranslator;
import net.kaaass.snlc.lexer.LexGrammar;
import net.kaaass.snlc.lexer.SubsetConstructAlgorithm;
import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.nfa.NfaUtils;

import java.util.List;
import java.util.Set;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class DfaSimplifierTest extends TestCase {

    public void testSplitMatrix() {
        // int[][] mat, int[] group, Set<Integer> indexes
        var mat = new int[][]{
                {0, 1, 2},
                {0, 1, 3},
                {1, 0, 2},
                {1, 1, 3}
        };
        var group = new int[]{-1, 0, 1, 2, 2};
        var indexes = Set.of(0, 1);

        var result = DfaSimplifier.splitMatrix(mat, group, indexes);
        assertEquals(List.of(Set.of(0, 1)), result);

        indexes = Set.of(2, 3);
        result = DfaSimplifier.splitMatrix(mat, group, indexes);
        assertTrue(result.contains(Set.of(2)));
        assertTrue(result.contains(Set.of(3)));
    }

    public void testGroupMatrix() {
        var mat = new int[][]{
                {0, 1, 2},
                {0, 1, 3},
                {1, 0, 2},
                {1, 1, 3},
                {1, 1, 1}
        };
        var result = DfaSimplifier.groupMatrix(mat,
                List.of(Set.of(2, 3), Set.of(0, 1, 4)));
        assertTrue(result.contains(Set.of(2, 3)));
        assertTrue(result.contains(Set.of(4)));
        assertTrue(result.contains(Set.of(0, 1)));
    }

    public void testSimplify() {
        // (a+b*|ac*|abc)*
        var regex = or(
                concat(single('a').oneOrMany(), single('b').many()).group(0),
                or(concat(single('a'), single('c').many()).group(1),
                        string("abc").group(2))).many();
        var nfa = regex.toNfa();
        var dfa = SubsetConstructAlgorithm.convert(nfa);

        System.out.println("Before simplify: ");
        DfaUtils.printGraph(dfa);

        System.out.println("After simplify: ");
        var result = DfaSimplifier.run(dfa);
        DfaUtils.printGraph(result);
    }

    public void testSimplify2() {
        // (ab)+
        var regex = string("ab").oneOrMany();
        var nfa = regex.toNfa();
        var dfa = SubsetConstructAlgorithm.convert(nfa);

        System.out.println("Before simplify: ");
        DfaUtils.printGraph(dfa);

        System.out.println("After simplify: ");
        var result = DfaSimplifier.run(dfa);
        DfaUtils.printGraph(result);
    }

    public void testSimplify3() throws LexParseException {
        // (a+b*|ac*|abc)+
        var regex = or(
                concat(single('a').oneOrMany(), single('b').many()),
                or(concat(single('a'), single('c').many()),
                        string("abc"))).oneOrMany().group(0);

        System.out.println(regex);

        var nfa = new GlushkovRegexTranslator().translateRegex(regex);

        NfaUtils.printGraph(nfa);

        var dfa = SubsetConstructAlgorithm.convert(nfa);

        System.out.println("Before simplify: ");
        DfaUtils.printGraph(dfa);

        System.out.println("After simplify: ");
        var result = DfaSimplifier.run(dfa);
        DfaUtils.printGraph(result);

        var g = LexGrammar.<String>create();
        g.defineToken("<>", regex);
        System.out.println(g.compile().process("abc").readToken());;
    }
}