package net.kaaass.snlc.lexer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.lexer.engine.ILexEngine;
import net.kaaass.snlc.lexer.engine.IRevertibleStream;

import java.util.HashMap;
import java.util.Map;

/**
 * 词法分析器
 *
 * @author kaaass
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Lexer<T> {

    private final Map<String, LexContext<T>> contexts;

    /**
     * 对词法进行预处理
     */
    private void preprocess() {
        // 编译所有上下文
        contexts.values().forEach(LexContext::compile);
    }

    /**
     * 返回输入流对应的解析引擎
     */
    public ILexEngine<T> process(IRevertibleStream stream) {
        // TODO
        return null;
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
