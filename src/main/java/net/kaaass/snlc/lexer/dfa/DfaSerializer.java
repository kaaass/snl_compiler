package net.kaaass.snlc.lexer.dfa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * 序列化 DFA
 * @author kaaass
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DfaSerializer {

    private Map<Character, Integer> charMap = null;

    private int[][] transMat = null;

    private final DfaGraph dfa;

    private void run() {
        runCharMap();
        runTransMat();
    }

    private void runCharMap() {
        var charset = new ArrayList<>(this.dfa.getCharset());
        charMap = new HashMap<>();
        for (int i = 0; i < charset.size(); i++) {
            charMap.put(charset.get(i), i);
        }
    }

    private void runTransMat() {
        var states = this.dfa.getStates();
        var n = states.size();
        var m = this.charMap.size();
        this.transMat = new int[n][m];
        // 遍历状态
        for (int i = 0; i < n; i++) {
            var state = states.get(i);
            Arrays.fill(this.transMat[i], -1);
            // 遍历边
            for (var edge : state.getNextEdges()) {
                int chr = this.charMap.get(edge.getMatchChar());
                this.transMat[i][chr] = edge.getNextState().getId();
            }
        }
    }

    public static DfaSerializer serialize(DfaGraph dfa) {
        var ret = new DfaSerializer(dfa);
        ret.run();
        return ret;
    }
}
