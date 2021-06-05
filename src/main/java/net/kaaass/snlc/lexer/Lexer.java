package net.kaaass.snlc.lexer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kaaass.snlc.lexer.engine.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 词法分析器
 *
 * @author kaaass
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Lexer<T> {

    @Getter
    private final Map<String, LexContext<T>> contexts;

    @Setter
    private BaseLexEngine<T> engine = null;

    /**
     * 对词法进行预处理
     */
    private void preprocess() {
        // 编译所有上下文
        contexts.values().forEach(LexContext::compile);
        // 创建引擎
        this.engine = new BasicEngine<>(this);
    }

    /**
     * 返回输入流对应的解析引擎
     */
    public BaseLexEngine<T> process(IRevertibleStream stream) {
        // 初始化引擎
        this.engine.init(stream);
        return this.engine;
    }

    /**
     * 返回字符串对应的解析引擎
     */
    public BaseLexEngine<T> process(String input) {
        return process(new StringStream(input));
    }

    public static <T> Lexer<T> of(LexGrammar<T> grammar) {
        var contexts = new HashMap<String, LexContext<T>>();
        var ret = new Lexer<>(contexts);
        // 添加当前 Context
        var context = grammar.getContext();
        contexts.put(context.getName(), context);
        ret.preprocess();
        return ret;
    }
}
