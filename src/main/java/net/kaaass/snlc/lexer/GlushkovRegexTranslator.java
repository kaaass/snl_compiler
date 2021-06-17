package net.kaaass.snlc.lexer;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaState;
import net.kaaass.snlc.lexer.regex.*;

import java.util.*;

/**
 * 使用 Glushkov 构造法将正则表达式转为 NFA
 * Glushkov 构造法将不会创建空转换，且状态数等于字符数。
 * 基于简单实现的考虑，仅有正则表达式组转换会考虑根节点的组信息
 *
 * @author kaaass
 */
public class GlushkovRegexTranslator {

    Map<RegexExpression, List<Letter>> cache = new HashMap<>();
    UFuncVisitor uFuncVisitor = new UFuncVisitor();
    PSetVisitor pSetVisitor = new PSetVisitor(uFuncVisitor, cache);
    DSetVisitor dSetVisitor = new DSetVisitor(uFuncVisitor, cache);
    FPairSetVisitor fPairSetVisitor = new FPairSetVisitor(pSetVisitor, dSetVisitor, cache);

    /**
     * 翻译若干正则表达式（使用或关系组合）
     * 会考虑每个正则根节点的组信息，其他忽略
     * 结果开始状态将使用空边组合，并且不会产生终结节点
     */
    public NfaGraph translateRegexes(List<RegexExpression> regexes) {
        var nfa = new NfaGraph();
        var stState = new NfaState();
        nfa.addState(stState);
        nfa.setEntryEdge(NfaEdge.emptyTo(stState));
        // 依次转换
        for (var regex : regexes) {
            var subNfa = translateRegex(regex);
            // 合并子图
            NfaEdge.empty().link(stState, subNfa.getStartState());
            nfa.annexSubGraph(subNfa);
        }
        return nfa;
    }

    /**
     * 翻译单个正则表达式
     * 会考虑正则根节点的组信息，其他忽略
     * 结果开始状态将使用空边组合，并且不会产生终结节点
     */
    public NfaGraph translateRegex(RegexExpression regex) {
        // 清理缓存
        this.cache.clear();
        this.uFuncVisitor.clear();
        this.pSetVisitor.clear();
        this.dSetVisitor.clear();
        // 计算 P、D、F
        var pSet = regex.accept(pSetVisitor);
        var dSet = regex.accept(dSetVisitor);
        var fPairSet = regex.accept(fPairSetVisitor);
        // 创建节点
        var states = new HashMap<Letter, NfaState>();
        var startState = new NfaState();
        var nfa = new NfaGraph();
        nfa.addState(startState);
        for (var letters : this.cache.values()) {
            for (var letter : letters) {
                var state = new NfaState();
                nfa.addState(state);
                states.put(letter, state);
            }
        }
        // 创建转移边
        for (var pair : fPairSet) {
            var from = pair.getFrom();
            var to = pair.getTo();
            // from -(to)-> to
            to.link(states.get(from), states.get(to));
        }
        // 设置结束状态组
        for (var endLetter : dSet) {
            var end = states.get(endLetter);
            end.setMatchedToken(regex.getGroupId());
        }
        // 创建开始边
        for (var startLetter : pSet) {
            // 0 -(st)-> st
            startLetter.link(startState, states.get(startLetter));
        }
        nfa.setEntryEdge(NfaEdge.emptyTo(startState));
        return nfa;
    }

    /**
     * 计算 Λ-函数，即是否可能为空
     */
    @Getter
    public static class UFuncVisitor implements IRegexExprVisitor<Boolean> {

        @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
        private final Map<RegexExpression, Boolean> mapping = new HashMap<>();

        @Override
        public Boolean visit(ExprEmpty exprEmpty) {
            // 空符号为真
            return mapping.computeIfAbsent(exprEmpty, exp -> true);
        }

        @Override
        public Boolean visit(ExprCharSet exprChar) {
            // 字符为假，或也为假
            return mapping.computeIfAbsent(exprChar, exp -> false);
        }

        @Override
        public Boolean visit(ExprString exprString) {
            // 字符为假，并也为假
            return mapping.computeIfAbsent(exprString, exp -> false);
        }

        @Override
        public Boolean visit(ExprKleeneStar exprKleeneStar) {
            return mapping.computeIfAbsent(exprKleeneStar, exp -> true);
        }

        @Override
        public Boolean visit(ExprConcatenation exprConcatenation) {
            if (mapping.containsKey(exprConcatenation)) {
                return mapping.get(exprConcatenation);
            }
            var lf = exprConcatenation.getLeftRegex().accept(this);
            var rt = exprConcatenation.getRightRegex().accept(this);
            var ret = lf && rt;
            mapping.put(exprConcatenation, ret);
            return ret;
        }

        @Override
        public Boolean visit(ExprAlternation exprAlternation) {
            if (mapping.containsKey(exprAlternation)) {
                return mapping.get(exprAlternation);
            }
            var lf = exprAlternation.getLeftRegex().accept(this);
            var rt = exprAlternation.getRightRegex().accept(this);
            var ret = lf || rt;
            mapping.put(exprAlternation, ret);
            return ret;
        }

        public void clear() {
            this.mapping.clear();
        }
    }

    /**
     * 标志单词
     */
    @Data
    public static class Letter {

        private ExprCharSet charSet = null;

        private ExprString exprString = null;

        private int stringPos = -1;

        Letter(ExprCharSet charSet) {
            this.charSet = charSet;
        }

        Letter(ExprString exprString, int stringPos) {
            this.exprString = exprString;
            this.stringPos = stringPos;
        }

        public static Letter createToCache(Map<RegexExpression, List<Letter>> cache,
                                           ExprCharSet charSet) {
            // 字符集的 Letter 缓存只有一个元素
            return cache.computeIfAbsent(charSet, e -> List.of(new Letter(charSet)))
                    .get(0);
        }

        public static Letter createToCache(Map<RegexExpression, List<Letter>> cache,
                                           ExprString exprString, int stringPos) {
            // 字符集的 Letter 缓存只有一个元素
            return cache.computeIfAbsent(exprString, e -> {
                var letters = new ArrayList<Letter>();
                var literal = exprString.getStringLiteral();
                for (int i = 0; i < literal.length(); i++) {
                    letters.add(new Letter(exprString, i));
                }
                return letters;
            }).get(stringPos);
        }

        public void link(NfaState from, NfaState to) {
            if (isCharSetLetter()) {
                // 给每个字符建边
                for (var chr : this.charSet.getCharSet()) {
                    NfaEdge.edge(chr).link(from, to);
                }
            } else {
                // 给指定位置字符建边
                var chr = this.exprString.getStringLiteral().charAt(this.stringPos);
                NfaEdge.edge(chr).link(from, to);
            }
        }

        public boolean isCharSetLetter() {
            return this.charSet != null;
        }

        @Override
        public String toString() {
            if (isCharSetLetter()) {
                return this.charSet.friendlyString();
            } else {
                return String.format("%c(\"%s\"[%d])",
                        this.exprString.getStringLiteral().charAt(this.stringPos),
                        this.exprString.getStringLiteral(), this.stringPos);
            }
        }
    }

    /**
     * 表示字母转移
     */
    @Data
    public static class LetterPair {
        private final Letter from;
        private final Letter to;

        @Override
        public String toString() {
            return String.format("Pair < %s --> %s >", from, to);
        }
    }

    /**
     * 计算开始状态集合 P 集合
     */
    @RequiredArgsConstructor
    public static class PSetVisitor implements IRegexExprVisitor<Set<Letter>> {

        private final UFuncVisitor uFuncVisitor;

        @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
        private final Map<RegexExpression, List<Letter>> letterMap;

        @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
        private final Map<RegexExpression, Set<Letter>> mapping = new HashMap<>();

        @Override
        public Set<Letter> visit(ExprEmpty exprEmpty) {
            return mapping.computeIfAbsent(exprEmpty, exp -> Set.of());
        }

        @Override
        public Set<Letter> visit(ExprCharSet exprChar) {
            return mapping.computeIfAbsent(exprChar, exp -> {
                var letter = Letter.createToCache(letterMap, exprChar);
                return Set.of(letter);
            });
        }

        @Override
        public Set<Letter> visit(ExprString exprString) {
            return mapping.computeIfAbsent(exprString, exp -> {
                var letter =
                        Letter.createToCache(letterMap, exprString, 0);
                return Set.of(letter);
            });
        }

        @Override
        public Set<Letter> visit(ExprKleeneStar exprKleeneStar) {
            if (mapping.containsKey(exprKleeneStar)) {
                return mapping.get(exprKleeneStar);
            }
            var inner = exprKleeneStar.getInnerRegex().accept(this);
            mapping.put(exprKleeneStar, inner);
            return inner;
        }

        @Override
        public Set<Letter> visit(ExprConcatenation exprConcatenation) {
            if (mapping.containsKey(exprConcatenation)) {
                return mapping.get(exprConcatenation);
            }
            var lf = exprConcatenation.getLeftRegex().accept(this);
            Set<Letter> ret = lf;
            // 若左可能为空
            if (exprConcatenation.getLeftRegex().accept(uFuncVisitor)) {
                var rt = exprConcatenation.getRightRegex().accept(this);
                ret = new HashSet<>();
                ret.addAll(lf);
                ret.addAll(rt);
            }
            mapping.put(exprConcatenation, ret);
            return ret;
        }

        @Override
        public Set<Letter> visit(ExprAlternation exprAlternation) {
            if (mapping.containsKey(exprAlternation)) {
                return mapping.get(exprAlternation);
            }
            var lf = exprAlternation.getLeftRegex().accept(this);
            var rt = exprAlternation.getRightRegex().accept(this);
            var ret = new HashSet<Letter>();
            ret.addAll(lf);
            ret.addAll(rt);
            mapping.put(exprAlternation, ret);
            return ret;
        }

        public void clear() {
            this.mapping.clear();
        }
    }

    /**
     * 计算结束状态集合 D 集合
     */
    @RequiredArgsConstructor
    public static class DSetVisitor implements IRegexExprVisitor<Set<Letter>> {

        private final UFuncVisitor uFuncVisitor;

        @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
        private final Map<RegexExpression, List<Letter>> letterMap;

        @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
        private final Map<RegexExpression, Set<Letter>> mapping = new HashMap<>();

        @Override
        public Set<Letter> visit(ExprEmpty exprEmpty) {
            return mapping.computeIfAbsent(exprEmpty, exp -> Set.of());
        }

        @Override
        public Set<Letter> visit(ExprCharSet exprChar) {
            return mapping.computeIfAbsent(exprChar, exp -> {
                var letter = Letter.createToCache(letterMap, exprChar);
                return Set.of(letter);
            });
        }

        @Override
        public Set<Letter> visit(ExprString exprString) {
            return mapping.computeIfAbsent(exprString, exp -> {
                var letter =
                        Letter.createToCache(letterMap,
                                exprString, exprString.getStringLiteral().length() - 1);
                return Set.of(letter);
            });
        }

        @Override
        public Set<Letter> visit(ExprKleeneStar exprKleeneStar) {
            if (mapping.containsKey(exprKleeneStar)) {
                return mapping.get(exprKleeneStar);
            }
            var inner = exprKleeneStar.getInnerRegex().accept(this);
            mapping.put(exprKleeneStar, inner);
            return inner;
        }

        @Override
        public Set<Letter> visit(ExprConcatenation exprConcatenation) {
            if (mapping.containsKey(exprConcatenation)) {
                return mapping.get(exprConcatenation);
            }
            var rt = exprConcatenation.getRightRegex().accept(this);
            Set<Letter> ret = rt;
            // 若右可能为空
            if (exprConcatenation.getRightRegex().accept(uFuncVisitor)) {
                var lf = exprConcatenation.getLeftRegex().accept(this);
                ret = new HashSet<>();
                ret.addAll(lf);
                ret.addAll(rt);
            }
            mapping.put(exprConcatenation, ret);
            return ret;
        }

        @Override
        public Set<Letter> visit(ExprAlternation exprAlternation) {
            if (mapping.containsKey(exprAlternation)) {
                return mapping.get(exprAlternation);
            }
            var lf = exprAlternation.getLeftRegex().accept(this);
            var rt = exprAlternation.getRightRegex().accept(this);
            var ret = new HashSet<Letter>();
            ret.addAll(lf);
            ret.addAll(rt);
            mapping.put(exprAlternation, ret);
            return ret;
        }

        public void clear() {
            this.mapping.clear();
        }
    }

    /**
     * 计算状态转移集合 F
     */
    @RequiredArgsConstructor
    public static class FPairSetVisitor implements IRegexExprVisitor<Set<LetterPair>> {

        private final PSetVisitor pSetVisitor;

        private final DSetVisitor dSetVisitor;

        @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
        private final Map<RegexExpression, List<Letter>> letterMap;

        @Override
        public Set<LetterPair> visit(ExprEmpty exprEmpty) {
            return Set.of();
        }

        @Override
        public Set<LetterPair> visit(ExprCharSet exprChar) {
            return Set.of();
        }

        @Override
        public Set<LetterPair> visit(ExprString exprString) {
            // 创建字母对象
            Letter.createToCache(letterMap, exprString, 0);
            // 在每两字母之间创建转移
            var letters = letterMap.get(exprString);
            var ret = new HashSet<LetterPair>();
            for (int i = 0; i < letters.size() - 1; i++) {
                ret.add(new LetterPair(letters.get(i), letters.get(i + 1)));
            }
            return ret;
        }

        @Override
        public Set<LetterPair> visit(ExprKleeneStar exprKleeneStar) {
            var inner = exprKleeneStar.getInnerRegex().accept(this);
            var starts = exprKleeneStar.accept(dSetVisitor);
            var ends = exprKleeneStar.accept(pSetVisitor);
            var ret = new HashSet<>(inner);
            // 增加 D -> P
            starts.forEach(from -> {
                ends.forEach(to -> {
                    ret.add(new LetterPair(from, to));
                });
            });
            return ret;
        }

        @Override
        public Set<LetterPair> visit(ExprConcatenation exprConcatenation) {
            // 并求关系并集 + D->P
            var lf = exprConcatenation.getLeftRegex().accept(this);
            var rt = exprConcatenation.getRightRegex().accept(this);
            var starts = exprConcatenation.getLeftRegex().accept(dSetVisitor);
            var ends = exprConcatenation.getRightRegex().accept(pSetVisitor);
            var ret = new HashSet<>(lf);
            ret.addAll(rt);
            // 增加 D -> P
            starts.forEach(from -> {
                ends.forEach(to -> {
                    ret.add(new LetterPair(from, to));
                });
            });
            return ret;
        }

        @Override
        public Set<LetterPair> visit(ExprAlternation exprAlternation) {
            // 或直接求关系并集
            var lf = exprAlternation.getLeftRegex().accept(this);
            var rt = exprAlternation.getRightRegex().accept(this);
            var ret = new HashSet<>(lf);
            ret.addAll(rt);
            return ret;
        }
    }
}