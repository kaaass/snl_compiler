package net.kaaass.snlc.lexer.engine;

/**
 * 可回退字符流接口
 * @author kaaass
 */
public interface IRevertibleStream {

    char EOF = '\0';

    /**
     * 获得当前流状态
     */
    int getState();

    /**
     * 回退当前流状态。回退状态号必须大于等于上次接受号状态
     */
    void revert(int state);

    /**
     * 接受当前流小于该状态号的所有状态
     */
    void accept(int state);

    /**
     * 读入一个字符
     */
    char read();

    /**
     * 流是否完成读入
     */
    boolean isEof();

    default String readUtilState(int state) {
        StringBuilder sb = new StringBuilder();
        while (getState() != state) {
            sb.append(read());
        }
        return sb.toString();
    }
}
