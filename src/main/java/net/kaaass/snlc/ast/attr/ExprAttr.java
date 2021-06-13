package net.kaaass.snlc.ast.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kaaass.snlc.ast.ExpType;
import net.kaaass.snlc.lexer.snl.SnlLexeme;

@Getter
@Setter
@AllArgsConstructor
public class ExprAttr extends BaseAttr{
    private SnlLexeme op;
    private VarKind varKind;
    private ExpType type;
}

