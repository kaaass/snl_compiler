package net.kaaass.snlc.lexer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.dfa.DfaGraph;
import net.kaaass.snlc.lexer.dfa.DfaSerializer;
import net.kaaass.snlc.lexer.exception.UndefinedTokenException;
import net.kaaass.snlc.lexer.regex.RegexExpression;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词法分析上下文，包含对指定语法的真正分析结构
 *
 * @author kaaass
 */
@Getter
@RequiredArgsConstructor
public class LexContext<T> {

    public final static String DEFAULT = "DEFAULT";

    private final String name;

    private final List<TokenInfo<T>> tokens = new ArrayList<>();

    private final Map<T, TokenInfo<T>> tokenMap = new HashMap<>();

    private State state = null;

    public void addToken(TokenInfo<T> token) {
        // 非声明则生成 ID
        if (!token.isDeclaration()) {
            token.setId(this.tokens.size());
            this.tokens.add(token);
        }
        token.setParent(this);
        this.tokenMap.put(token.getType(), token);
    }

    public TokenInfo<T> getToken(int id) {
        return this.tokens.get(id);
    }

    public TokenInfo<T> getToken(T type) throws UndefinedTokenException {
        if (type == null) {
            throw new UndefinedTokenException();
        }
        var ret = this.tokenMap.get(type);
        if (ret == null) {
            throw new UndefinedTokenException(type);
        }
        return ret;
    }

    public boolean isDefaultContext() {
        return DEFAULT.equals(this.name);
    }

    /**
     * 编译匹配规则至 DFA 状态
     */
    public void compile() {
        if (tokens.isEmpty()) {
            return;
        }
        // 第一步：组合正则
        RegexExpression regex = null;
        for (var token : tokens) {
            var cur = token.getRegex();
            // 设置匹配组
            cur.group(token.getId());
            // 组合正则
            if (regex == null) {
                regex = cur;
            } else {
                regex = RegexExpression.or(regex, cur);
            }
        }
        // 第二步：转换 NFA
        var nfa = regex.toNfa();
        // 第三步：转换 DFA
        var dfa = SubsetConstructAlgorithm.convert(nfa);
        this.state = State.fromDfa(dfa);
    }

    /**
     * 可持久化状态，记录了 DFS 信息
     */
    @Data
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class State implements Serializable {

        private static final long serialVersionUID = 2039325900482461802L;

        public final Map<Character, Integer> charMap;

        public final int[][] transMat;

        public final List<List<Integer>> tokenMat;

        public final int startState;

        private transient WeakReference<DfaGraph> source = null;

        public static State fromDfa(DfaGraph dfa) {
            var serializer = DfaSerializer.serialize(dfa);
            var ret = new State(serializer.getCharMap(),
                    serializer.getTransMat(),
                    serializer.getTokenMat(),
                    dfa.getStartState().getId());
            ret.source = new WeakReference<>(dfa);
            return ret;
        }
    }
}
