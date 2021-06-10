package net.kaaass.snlc.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kevin Axel
 */
@AllArgsConstructor
public class Array {
    @Getter
    private final int low;

    @Getter
    private final int top;

    @Getter
    private final String  type;
}
