package net.kaaass.snlc.lexer;

import net.kaaass.snlc.lexer.nfa.NfaGraph;

import static net.kaaass.snlc.lexer.regex.RegexExpression.*;

public class ThompsonRegexTranslatorTestMain {

    public static void printGraph(NfaGraph graph) {
        System.out.println("Start state: " + graph.getStartState().getId());
        System.out.println("End state: " + graph.getEndState().getId());
        graph.getStates().forEach(state -> {
            var from = state.getId();
            state.getNextEdges().forEach(edge -> {
                var to = edge.getNextState().getId();
                System.out.printf("%d --%c--> %d\n", from, edge.getMatchChar(), to);
            });
        });
    }

    public static void main(String[] args) {
        var alphabet = or(single('a'), single('A'));
        var number = single('0');
        var identifier = concat(alphabet, or(alphabet, number).many());

        var translator = new ThompsonRegexTranslator();

        System.out.println("alphabet:");
        printGraph(alphabet.accept(translator));
        System.out.println("number:");
        printGraph(number.many().accept(translator));
        System.out.println("identifier:");
        printGraph(identifier.accept(translator));
        System.out.println("'else':");
        printGraph(string("else").accept(translator));
        System.out.println("ab:");
        printGraph(concat(single('a'), single('b')).accept(translator));
    }
}