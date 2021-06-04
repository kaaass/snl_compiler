package net.kaaass.snlc.lexer.nfa;

import lombok.*;

/**
 * 非确定状态机边，表示状态转移
 * @author kaaass
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NfaEdge {

    public final static char EMPTY_CHAR = '\0';

    private final char matchChar;

    private NfaState nextState = null;

    /**
     * 将边建在状态之间
     */
    public void link(NfaState from, NfaState to) {
        from.addEdge(this);
        this.nextState = to;
    }

    /**
     * 是否为空边
     */
    public boolean isEmpty() {
        return this.matchChar == EMPTY_CHAR;
    }

    public static NfaEdge edge(char matchChar) {
        return new NfaEdge(matchChar);
    }

    public static NfaEdge empty() {
        return new NfaEdge(EMPTY_CHAR);
    }

    public static NfaEdge emptyTo(NfaState to) {
        return new NfaEdge(EMPTY_CHAR, to);
    }
}
