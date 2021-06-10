package net.kaaass.snlc.lexer.dfa;

import lombok.Getter;

import java.util.*;

/**
 * 序列化 DFA
 *
 * @author kaaass
 */
@Getter
public class DfaSerializer {

    public static final int CHAR_MAP = 0x01;

    private static final int TRANS_ONLY = 0x02;

    public static final int TRANS = TRANS_ONLY | CHAR_MAP;

    public static final int TOKEN_MAT = 0x04;

    public static final int ALL = 0xff;

    private Map<Character, Integer> charMap = null;

    private int[][] transMat = null;

    private List<List<Integer>> tokenMat = null;

    private final DfaGraph dfa;

    private final int needed;

    public DfaSerializer(DfaGraph dfa, int needed) {
        this.dfa = dfa;
        this.needed = needed;
    }

    private void run() {
        if ((this.needed & CHAR_MAP) == CHAR_MAP) {
            runCharMap();
        }
        if ((this.needed & TRANS) == TRANS) {
            runTransMat();
        }
        if ((this.needed & TOKEN_MAT) == TOKEN_MAT) {
            runTokenMat();
        }
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
            Arrays.fill(this.transMat[i], DfaState.DEAD);
            // 遍历边
            for (var edge : state.getNextEdges()) {
                int chr = this.charMap.get(edge.getMatchChar());
                this.transMat[i][chr] = edge.getNextState().getId();
            }
        }
    }

    private void runTokenMat() {
        this.tokenMat = new ArrayList<>();
        for (var state : this.dfa.getStates()) {
            this.tokenMat.add(state.getMatchedTokens());
        }
    }

    public static DfaSerializer on(DfaGraph dfa) {
        return on(dfa, ALL);
    }

    public static DfaSerializer on(DfaGraph dfa, int needed) {
        var ret = new DfaSerializer(dfa, needed);
        ret.run();
        return ret;
    }
}
