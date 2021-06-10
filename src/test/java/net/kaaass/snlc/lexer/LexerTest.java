package net.kaaass.snlc.lexer;

import junit.framework.TestCase;
import net.kaaass.snlc.lexer.dfa.DfaUtils;
import net.kaaass.snlc.lexer.exception.ContextStackNonEmptyException;
import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.exception.UndefinedContextException;
import net.kaaass.snlc.lexer.exception.UndefinedTokenException;

import java.util.ArrayList;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class LexerTest extends TestCase {

    enum Lang1 {
        WHITESPACE, ALPHABET, DIGIT, IF, AS
    }

    public void testBasic() throws LexParseException, UndefinedTokenException, UndefinedContextException {
        var g = LexGrammar.<Lang1>create();
        g.defineToken(Lang1.DIGIT, range('0', '1').oneOrMany());
        g.defineToken(Lang1.IF, "if");
        g.defineToken(Lang1.ALPHABET, or(range('a', 'z'), range('A', 'Z')).oneOrMany());
        g.defineToken(Lang1.AS, "as");
        g.defineToken(Lang1.WHITESPACE, charset(' ', '\n'));

        var lexer = g.compile();

        // Case 1
        var engine = lexer.process("a 0 if");
        var result = engine.readAllTokens();

        var expected = new ArrayList<TokenResult<Lang1>>();
        expected.add(new TokenResult<>(g.token(Lang1.ALPHABET), "a"));
        expected.add(new TokenResult<>(g.token(Lang1.WHITESPACE), " "));
        expected.add(new TokenResult<>(g.token(Lang1.DIGIT), "0"));
        expected.add(new TokenResult<>(g.token(Lang1.WHITESPACE), " "));
        expected.add(new TokenResult<>(g.token(Lang1.IF), "if"));

        DfaUtils.printGraph(lexer.getContext("DEFAULT").getState().getSource().get());

        assertEquals(expected, result);

        // Case 2: 多匹配情况下选最长
        engine = lexer.process("ifasasa");
        result = engine.readAllTokens();

        expected = new ArrayList<>();
        expected.add(new TokenResult<>(g.token(Lang1.ALPHABET), "ifasasa"));

        assertEquals(expected, result);

        // Case 3: 同长情况优先先定义的
        engine = lexer.process("if as");
        result = engine.readAllTokens();

        expected = new ArrayList<>();
        expected.add(new TokenResult<>(g.token(Lang1.IF), "if"));
        expected.add(new TokenResult<>(g.token(Lang1.WHITESPACE), " "));
        expected.add(new TokenResult<>(g.token(Lang1.ALPHABET), "as"));

        assertEquals(expected, result);
    }

    enum Lang2 {
        WHITESPACE,

        ID, STRING
    }

    public void testString() throws LexParseException, UndefinedTokenException {
        var g = LexGrammar.<Lang2>create();

        g.defineToken(Lang2.ID, or(range('a', 'z'), range('A', 'Z')).oneOrMany());

        // 字符串定义
        final StringBuilder sb = new StringBuilder();
        var string = g.defineContext("string");
        var rHexNumber = or(range('a', 'f'), range('0', '9')).oneOrMany();

        g.declareToken(Lang2.STRING);
        // 遇到字符串标志进入环境
        g.defineToken(single('\"'))
            .action(ctx -> {
                ctx.pushContext("string");
                // 清空字符串缓冲
                sb.setLength(0);
            });
        // 处理转义
        string.defineToken(string("\\n")).action(ctx -> sb.append('\n'));
        string.defineToken(string("\\\"")).action(ctx -> sb.append('\"'));
        string.defineToken(concat(string("\\u"), rHexNumber))
                .action(ctx -> {
                    var hex = ctx.matchedString().substring(2);
                    sb.append(Character.toChars(Integer.valueOf(hex, 16)));
                });
        // 字符串环境遇到退出标志
        string.defineToken(single('\"'))
                .action(ctx -> {
                    ctx.popContext();
                    // 产生字符串 token
                    ctx.accept(Lang2.STRING, sb.toString());
                });
        // 处理其他字符
        string.defineToken(single('\n')).action(ctx -> ctx.fail(() -> new LexParseException("单行字符串")));
        string.defineToken(anychar()).action(ctx -> sb.append(ctx.matchedString()));

        g.defineToken(Lang2.WHITESPACE, charset(' ', '\n')).ignore();

        var lexer = g.compile();

        // Case 1
        var engine = lexer.process("\"test\"");
        var result = engine.readAllTokens();

        var expected = new ArrayList<TokenResult<Lang2>>();
        expected.add(new TokenResult<>(g.token(Lang2.STRING), "test"));

        assertEquals(expected, result);

        // Case 2: 普通转义
        engine = lexer.process("\"te\\\"st\\n\"");
        result = engine.readAllTokens();

        expected = new ArrayList<>();
        expected.add(new TokenResult<>(g.token(Lang2.STRING), "te\"st\n"));

        assertEquals(expected, result);

        try {
            lexer.process("\"te\"st\"").readAllTokens();
            fail();
        } catch (LexParseException e) {
            assertTrue(e instanceof ContextStackNonEmptyException);
        }

        try {
            lexer.process("\"te\nst\"").readAllTokens();
            fail();
        } catch (LexParseException e) {
            e.printStackTrace();
        }

        // Case 3: Unicode 转义
        engine = lexer.process("\"test\\u0065\"");
        result = engine.readAllTokens();

        expected = new ArrayList<>();
        expected.add(new TokenResult<>(g.token(Lang2.STRING), "test\u0065"));

        assertEquals(expected, result);
    }

    public void testReject() throws LexParseException, UndefinedTokenException, UndefinedContextException {
        var g = LexGrammar.<Lang1>create();
        g.defineToken(Lang1.DIGIT, range('0', '1').oneOrMany());
        g.defineToken(Lang1.IF, "if").action(ctx -> ctx.reject());
        g.defineToken(Lang1.ALPHABET, or(range('a', 'z'), range('A', 'Z')).oneOrMany());
        g.defineToken(Lang1.AS, "as");
        g.defineToken(Lang1.WHITESPACE, charset(' ', '\n'));

        var lexer = g.compile();

        // 同长情况优先先定义的，此处拒绝了一次，所以变为 ALPHABET
        var engine = lexer.process("if as");
        var result = engine.readAllTokens();

        var expected = new ArrayList<>();
        expected.add(new TokenResult<>(g.token(Lang1.ALPHABET), "if"));
        expected.add(new TokenResult<>(g.token(Lang1.WHITESPACE), " "));
        expected.add(new TokenResult<>(g.token(Lang1.ALPHABET), "as"));

        assertEquals(expected, result);
    }
}