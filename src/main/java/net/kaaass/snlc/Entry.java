package net.kaaass.snlc;

import net.kaaass.snlc.lexer.exception.LexParseException;
import net.kaaass.snlc.lexer.snl.SnlLexerFactory;
import net.kaaass.snlc.parser.Parser;
import net.kaaass.snlc.parser.exception.TokenNotMatchException;
import net.kaaass.snlc.parser.exception.TreeNodeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Entry {
    public static void main(String[] args) {

        try {
            String code = Files.readString(Path.of("src/main/resources/example.snl"));

            var lexer = SnlLexerFactory.create();
            var engine = lexer.process(code);
            var tokens = engine.readAllTokens();

            System.out.println("----------------词法分析输出----------------");
            for (var token : tokens) {
                var type = token.getDefinition().getType();
                var indent = (type.toString().length() + 2) / 4;
                System.out.println(
                        "<" + type + ">" +
                                "\t".repeat(Math.max(0, 5 - indent)) +
                                token.getToken());
            }
            System.out.println("----------------词法分析完成----------------");
            System.out.println();
            System.out.println("----------------语法分析输出----------------");
            var parser = Parser.of(tokens);
            var ast = parser.getAst();
            ast.print();
            System.out.println("----------------语法分析完成----------------");
        } catch (TokenNotMatchException e) {
            System.out.println("语法分析异常");
            e.printStackTrace();
        } catch (LexParseException e) {
            System.out.println("词法分析异常");
            e.printStackTrace();
        } catch (TreeNodeException e) {
            System.out.println("语法书构建异常");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("文件不存在");
            e.printStackTrace();
        }
    }
}
