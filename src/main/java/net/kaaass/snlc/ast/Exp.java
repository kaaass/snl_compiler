package net.kaaass.snlc.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KevinAxel
 */
@AllArgsConstructor
public class Exp {
    @Getter
    private final String var;

    @Getter
    private final String val;
}
