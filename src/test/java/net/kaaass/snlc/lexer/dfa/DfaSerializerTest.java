package net.kaaass.snlc.lexer.dfa;

import junit.framework.TestCase;

import java.util.List;
import java.util.Set;

public class DfaSerializerTest extends TestCase {

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
        var dfa = DfaUtils.createDfa(3, edges);
        var result = DfaSerializer.on(dfa);

        var C = result.getCharMap();
        assertEquals(Set.of('a', 'b', 'c'), C.keySet());
        var T = result.getTransMat();
        for (var edge : edges) {
            assertEquals(edge.get(1), T[(int) edge.get(0)][C.get(edge.get(2))]);
        }
    }

    public void testNeeded() {
        List<List<Object>> edges = List.of(
                List.of(0, 1, 'a'),
                List.of(1, 2, 'b'),
                List.of(0, 2, 'c'),
                List.of(1, 0, 'a')
        );
        var dfa = DfaUtils.createDfa(3, edges);

        var result = DfaSerializer.on(dfa, DfaSerializer.CHAR_MAP);
        assertNotNull(result.getCharMap());
        assertNull(result.getTransMat());
        assertNull(result.getTokenMat());

        result = DfaSerializer.on(dfa, DfaSerializer.TOKEN_MAT);
        assertNull(result.getCharMap());
        assertNull(result.getTransMat());
        assertNotNull(result.getTokenMat());

        result = DfaSerializer.on(dfa, DfaSerializer.TRANS);
        assertNotNull(result.getCharMap());
        assertNotNull(result.getTransMat());
        assertNull(result.getTokenMat());
    }
}