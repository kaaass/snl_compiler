package net.kaaass.snlc.lexer.engine;

import net.kaaass.snlc.lexer.LexContext;
import net.kaaass.snlc.lexer.Lexer;
import net.kaaass.snlc.lexer.TokenResult;
import net.kaaass.snlc.lexer.dfa.DfaState;
import net.kaaass.snlc.lexer.exception.EofParseException;
import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.exception.UnexpectedCharException;

import java.util.Stack;

/**
 * 栈式匹配引擎。允许追溯旧匹配信息
 *
 * @author kaaass
 */
public class StackedEngine<T> extends BaseLexEngine<T> {

    private final Stack<MatchedInfo> matchedStack = new Stack<>();

    public StackedEngine(Lexer<T> lexer) {
        super(lexer);
    }

    @Override
    public void reset() {
        // 无需操作
    }

    @Override
    protected TokenResult<T> readToken(LexContext<T> context) throws LexParseException {
        int initStreamState = this.stream.getState();
        matchedStack.clear();
        // 匹配
        var dfa = context.getState();
        int state = dfa.startState;
        char chr;
        boolean eofFlag = false;
        while (true) {
            chr = read();
            // 流结束
            if (chr == IRevertibleStream.EOF) {
                eofFlag = true;
                break;
            }
            // 获取字符号
            var chrId = dfa.charMap.get(chr);
            if (chrId == null) {
                break;
            }
            // 转移状态
            state = dfa.transMat[state][chrId];
            if (state == DfaState.DEAD) {
                break;
            }
            // 检查匹配
            var matched = dfa.tokenMat.get(state);
            if (matched != null && !matched.isEmpty()) {
                for (int i = matched.size() - 1; i >= 0; i--) {
                    matchedStack.push(createMatchedInfo(matched.get(i)));
                }
            }
        }
        // 是否有匹配
        while (!matchedStack.empty()) {
            // 回退流到初始状态，用于获取匹配内容
            this.stream.revert(initStreamState);
            // 产生 token
            var matchedInfo = matchedStack.peek();
            var result = processMatchedToken(context, matchedInfo);
            switch (result.getType()) {
                case ACCEPT:
                    // 接受 Token，确认之前匹配信息后返回
                    matchedInfo.accept();
                    return result.getToken();
                case REJECT:
                    // 拒绝 Token，弹栈
                    matchedStack.pop();
                    continue;
                case NONE:
                default:
                    // 不进行操作，确认之前匹配信息
                    matchedInfo.accept();
                    return null;
            }
        }
        // 如果是 eof 报错
        if (eofFlag) {
            throw new EofParseException();
        }
        throw new UnexpectedCharException(chr);
    }
}
