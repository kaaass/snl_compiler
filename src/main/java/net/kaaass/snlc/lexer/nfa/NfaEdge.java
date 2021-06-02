package net.kaaass.snlc.lexer.nfa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * NFA 状态机边，表示状态转移
 *
 * @author kaaass
 */
@Data
public class NfaEdge {

    @Setter(AccessLevel.PACKAGE)
    private char matchChar;

    @Setter(AccessLevel.PACKAGE)
    private NfaState nextState;
}
