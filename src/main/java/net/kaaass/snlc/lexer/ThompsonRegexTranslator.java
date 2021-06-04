package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.nfa.NfaEdge;
import net.kaaass.snlc.lexer.nfa.NfaGraph;
import net.kaaass.snlc.lexer.nfa.NfaState;
import net.kaaass.snlc.lexer.regex.*;

/**
 * 使用 Thompson 构造法将正则表达式转为 NFA。对部分构造方式进行了优化。
 * @author kaaass
 */
public class ThompsonRegexTranslator implements IRegexExprVisitor<NfaGraph> {

    public final static ThompsonRegexTranslator INSTANCE = new ThompsonRegexTranslator();

    /**
     * 转换空式
     */
    @Override
    public NfaGraph visit(ExprEmpty exprEmpty) {
        var result = new NfaGraph();
        var state = new NfaState();
        var edge = NfaEdge.empty();

        edge.setNextState(state);
        result.addState(state);

        result.setEndState(state);
        result.setEntryEdge(edge);

        return null;
    }

    /**
     * 转换字符集匹配
     */
    @Override
    public NfaGraph visit(ExprCharSet exprChar) {
        var result = new NfaGraph();
        var startState = new NfaState();
        var endState = new NfaState();

        // 每个符号生成一个匹配边
        exprChar.getCharSet()
                .stream()
                .map(NfaEdge::new)
                .forEach(matchEdge -> matchEdge.link(startState, endState));

        // 生成开始边
        var entryEdge = NfaEdge.empty();
        entryEdge.setNextState(startState);

        // 建图
        result.addState(startState);
        result.addState(endState);
        result.setEntryEdge(entryEdge);
        result.setEndState(endState);

        return result;
    }

    /**
     * 转换字符串匹配
     */
    @Override
    public NfaGraph visit(ExprString exprString) {
        var result = new NfaGraph();

        if (exprString.getStringLiteral().isEmpty()) {
            return visit(new ExprEmpty());
        }

        // 每个字符产生一条边、一个状态
        NfaEdge entryEdge = null;
        NfaEdge curEdge;
        NfaState curState = null;
        for (var chr : exprString.getStringLiteral().toCharArray()) {
            // 边
            curEdge = new NfaEdge(chr);
            if (curState != null) {
                curState.addEdge(curEdge);
            }
            if (entryEdge == null) {
                entryEdge = curEdge;
            }
            // 状态
            curState = new NfaState();
            curEdge.setNextState(curState);
            // 添加状态到图
            result.addState(curState);
        }

        // 建图
        result.setEntryEdge(entryEdge);
        result.setEndState(curState);

        return result;
    }

    /**
     * 转换 * 匹配
     */
    @Override
    public NfaGraph visit(ExprKleeneStar exprKleeneStar) {
        var result = new NfaGraph();
        var inner = exprKleeneStar.getInnerRegex().accept(this);
        var entryEdge = NfaEdge.empty();
        var endState = new NfaState();

        endState.addEdge(inner.getEntryEdge());
        NfaEdge.empty().link(inner.getEndState(), endState);
        entryEdge.setNextState(endState);

        // 建图
        result.annexSubGraph(inner);
        result.addState(endState);
        result.setEntryEdge(entryEdge);
        result.setEndState(endState);

        return result;
    }

    /**
     * 转换正则表达式连接
     */
    @Override
    public NfaGraph visit(ExprConcatenation exprConcatenation) {
        var result = new NfaGraph();
        var left = exprConcatenation.getLeftRegex().accept(this);
        var right = exprConcatenation.getRightRegex().accept(this);

        // 连接两图节点
        left.getEndState().addEdge(right.getEntryEdge());

        // 建图
        result.annexSubGraph(left);
        result.annexSubGraph(right);
        result.setEntryEdge(left.getEntryEdge());
        result.setEndState(right.getEndState());

        return result;
    }

    /**
     * 转换正则表达式选择
     */
    @Override
    public NfaGraph visit(ExprAlternation exprAlternation) {
        var result = new NfaGraph();
        var left = exprAlternation.getLeftRegex().accept(this);
        var right = exprAlternation.getRightRegex().accept(this);

        var startState = new NfaState();
        var endState = new NfaState();

        // 连接开始节点
        startState.addEdge(left.getEntryEdge());
        startState.addEdge(right.getEntryEdge());

        // 连接结束节点
        NfaEdge.empty().link(left.getEndState(), endState);
        NfaEdge.empty().link(right.getEndState(), endState);

        // 建图
        result.addState(startState);
        result.annexSubGraph(left);
        result.annexSubGraph(right);
        result.addState(endState);

        result.setEntryEdge(NfaEdge.emptyTo(startState));
        result.setEndState(endState);

        return result;
    }
}
