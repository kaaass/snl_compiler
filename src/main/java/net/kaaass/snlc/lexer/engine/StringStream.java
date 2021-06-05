package net.kaaass.snlc.lexer.engine;

import lombok.Data;

/**
 * 创建字符串读入流
 * @author kaaass
 */
@Data
public class StringStream implements IRevertibleStream {

    private final String data;

    private int pos = 0;

    @Override
    public int getState() {
        return this.pos;
    }

    @Override
    public void revert(int state) {
        this.pos = state;
    }

    @Override
    public void accept(int state) {
        // 无需操作
    }

    @Override
    public char read() {
        if (isEof()) {
            return EOF;
        }
        return this.data.charAt(this.pos++);
    }

    @Override
    public boolean isEof() {
        return this.pos == this.data.length();
    }
}
