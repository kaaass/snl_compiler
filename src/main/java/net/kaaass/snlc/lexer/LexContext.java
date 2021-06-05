package net.kaaass.snlc.lexer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.dfa.DfaGraph;
import net.kaaass.snlc.lexer.dfa.DfaSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 词法分析上下文，包含对指定语法的真正分析
 * @author kaaass
 */
@Getter
@RequiredArgsConstructor
public class LexContext<T> {

    public final static String DEFAULT = "DEFAULT";

    private final String name;

    private final List<TokenInfo<T>> tokens = new ArrayList<>();

    private State state = null;

    public void addToken(TokenInfo<T> token) {
        token.setId(this.tokens.size());
        token.setParent(this);
        this.tokens.add(token);
    }

    /**
     * 编译匹配规则至 DFA 状态
     */
    public void compile() {
        // TODO
    }

    /**
     * 可持久化状态，记录了 DFS 信息
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class State {
        private final Map<Character, Integer> charMap;

        private final int[][] transMat;

        private final List<List<Integer>> tokenMat;

        public static State fromDfa(DfaGraph dfa) {
            var ret = DfaSerializer.serialize(dfa);
            return new State(ret.getCharMap(), ret.getTransMat(), ret.getTokenMat());
        }
    }
}
