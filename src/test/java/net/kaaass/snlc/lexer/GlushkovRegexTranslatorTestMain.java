package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.nfa.NfaUtils;
import net.kaaass.snlc.lexer.regex.RegexExpression;

import java.util.HashMap;
import java.util.List;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class GlushkovRegexTranslatorTestMain {

    public static void testUFuncVisitor() {
        // (a(ab)*)* | (ba)*
        var regex = or(concat(single('a'), string("ab").many()).many(), string("ba").many());
        var visitor = new GlushkovRegexTranslator.UFuncVisitor();
        regex.accept(visitor);
        System.out.println(visitor.getMapping());
    }

    public static void testPSetVisitor() {
        // (a(ab)*)* | (ba)*
        var regex = or(concat(single('a'), string("ab").many()).many(), string("ba").many());
        var cache = new HashMap<RegexExpression, List<GlushkovRegexTranslator.Letter>>();
        var uFunc = new GlushkovRegexTranslator.UFuncVisitor();
        var visitor = new GlushkovRegexTranslator.PSetVisitor(uFunc, cache);
        regex.accept(visitor);
        System.out.println("testPSetVisitor:");
        System.out.println(regex.accept(visitor));
    }

    public static void testDSetVisitor() {
        // (a(ab)*)* | (ba)*
        var regex = or(concat(single('a'), string("ab").many()).many(), string("ba").many());
        var cache = new HashMap<RegexExpression, List<GlushkovRegexTranslator.Letter>>();
        var uFunc = new GlushkovRegexTranslator.UFuncVisitor();
        var visitor = new GlushkovRegexTranslator.DSetVisitor(uFunc, cache);
        regex.accept(visitor);
        System.out.println("testDSetVisitor:");
        System.out.println(regex.accept(visitor));
    }

    public static void testFPairSetVisitor() {
        // (a(ab)*)* | (ba)*
        var regex = or(concat(single('a'), string("ab").many()).many(), string("ba").many());
        var cache = new HashMap<RegexExpression, List<GlushkovRegexTranslator.Letter>>();
        var uFunc = new GlushkovRegexTranslator.UFuncVisitor();
        var pSet = new GlushkovRegexTranslator.PSetVisitor(uFunc, cache);
        var dSet = new GlushkovRegexTranslator.DSetVisitor(uFunc, cache);
        var visitor = new GlushkovRegexTranslator.FPairSetVisitor(pSet, dSet, cache);
        regex.accept(visitor);
        System.out.println("testFPairSetVisitor:");
        System.out.println(regex.accept(visitor));
    }

    public static void testTranslateRegex() {
        // (a(ab)*)* | (ba)*
        var regex = or(concat(single('a'), string("ab").many()).many(), string("ba").many());
        // 转换
        var translator = new GlushkovRegexTranslator();
        var result = translator.translateRegex(regex);

        System.out.println("testTranslateRegex:");
        NfaUtils.printGraph(result);
    }

    public static void main(String[] args) {
        testUFuncVisitor();
        testPSetVisitor();
        testDSetVisitor();
        testFPairSetVisitor();
        testTranslateRegex();
    }
}