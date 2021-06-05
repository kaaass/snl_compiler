package net.kaaass.snlc.lexer.engine;

import junit.framework.TestCase;

public class StringStreamTest extends TestCase {

    public void testRevert() {
        var ss = new StringStream("0123456789");

        assertEquals('0', ss.read());
        assertEquals('1', ss.read());

        var state = ss.getState();

        assertEquals('2', ss.read());
        assertEquals('3', ss.read());

        ss.revert(state);

        assertEquals('2', ss.read());
    }

}