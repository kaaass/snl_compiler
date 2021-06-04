package net.kaaass.snlc.lexer.dfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 确定状态机边，表示状态转移
 * @author kaaass
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DfaEdge {

    private final char matchChar;

    private DfaState nextState = null;

    /**
     * 将边建在状态之间
     */
    public void link(DfaState from, DfaState to) {
        from.addEdge(this);
        this.nextState = to;
    }

    public static DfaEdge edgeTo(char matchChar, DfaState to) {
        return new DfaEdge(matchChar, to);
    }
}
