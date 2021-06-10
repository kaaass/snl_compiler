package net.kaaass.snlc.parser;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.ast.TreeNode;
import net.kaaass.snlc.lexer.TokenResult;
import net.kaaass.snlc.lexer.snl.SnlLexeme;
import net.kaaass.snlc.parser.exception.TokenNotMatchException;

import java.util.List;

/**
 * Parser for SNL
 *
 * @author Kevin Axel
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser {

    private final Tokens tokens;

    static Parser of(List<TokenResult<SnlLexeme>> tokenList) {
        return new Parser(new Tokens(tokenList));
    }

    public TreeNode getAst() throws TokenNotMatchException {
        Program();
        if (tokens.pos == tokens.tokenList.size())
            return null;
        else {
            throw new TokenNotMatchException();
        }
    }

    public void Program() throws TokenNotMatchException {
        ProgramHead();
        DeclarePart();
        ProgramBody();
    }

    public void ProgramHead() throws TokenNotMatchException {
        tokens.match(SnlLexeme.PROGRAM);
        ProgramName();
    }

    public void ProgramName() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
    }

    public void DeclarePart() throws TokenNotMatchException {
        TypeDecpart();
        VarDecpart();
        ProcDecpart();
    }

    public void TypeDecpart() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.VAR || tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.TYPE) {
            tokens.match(SnlLexeme.TYPE);
            TypeDecList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void TypeDec() throws TokenNotMatchException {
        tokens.match(SnlLexeme.TYPE);
        TypeDecList();
    }

    public void TypeDecList() throws TokenNotMatchException {
        TypeId();
        tokens.match(SnlLexeme.EQ);
        TypeDef();
        tokens.match(SnlLexeme.SEMI);
        TypeDecMore();
    }

    public void TypeDecMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.VAR || tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.ID) {
            TypeDecList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void TypeId() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
    }

    public void TypeDef() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR) {
            BaseType();
        } else if (tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD) {
            StructureType();
        } else if (tokens.current() == SnlLexeme.ID) {
            tokens.match(SnlLexeme.ID);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void BaseType() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.INTEGER) {
            tokens.match(SnlLexeme.INTEGER);
        } else if (tokens.current() == SnlLexeme.CHAR) {
            tokens.match(SnlLexeme.CHAR);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void StructureType() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ARRAY) {
            tokens.match(SnlLexeme.ARRAY);
        } else if (tokens.current() == SnlLexeme.RECORD) {
            tokens.match(SnlLexeme.RECORD);
        } else {
            throw new TokenNotMatchException();
        }

    }

    public void ArrayType() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ARRAY);
        Low();
        Top();
        tokens.match(SnlLexeme.OF);
        BaseType();
    }

    public void Low() throws TokenNotMatchException {
        tokens.match(SnlLexeme.INTC);
    }

    public void Top() throws TokenNotMatchException {
        tokens.match(SnlLexeme.INTC);
    }

    public void RecType() throws TokenNotMatchException {
        tokens.match(SnlLexeme.RECORD);
        FieldDecList();
        tokens.match(SnlLexeme.END);
    }

    public void FieldDecList() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR) {
            BaseType();
            IdList();
            tokens.match(SnlLexeme.SEMI);
            FieldDecMore();
        } else if (tokens.current() == SnlLexeme.ARRAY) {
            ArrayType();
            IdList();
            tokens.match(SnlLexeme.SEMI);
            FieldDecMore();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void FieldDecMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.END) {
            // pass
        } else if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY) {
            FieldDecList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void IdList() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
        IdMore();
    }

    public void IdMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.SEMI) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            IdList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void VarDecpart() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.VAR) {
            VarDec();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void VarDec() throws TokenNotMatchException {
        tokens.match(SnlLexeme.VAR);
        VarDecList();
    }

    public void VarDecList() throws TokenNotMatchException {
        TypeDef();
        VarIdList();
        tokens.match(SnlLexeme.SEMI);
        VarDecMore();
    }

    public void VarDecMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD || tokens.current() == SnlLexeme.ID) {
            VarDecList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void VarIdList() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
        VarIdMore();
    }

    public void VarIdMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.SEMI) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            VarIdList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcDecpart() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.PROCEDURE) {
            ProcDec();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcDec() throws TokenNotMatchException {
        tokens.match(SnlLexeme.PROCEDURE);
        ProgramName();
        tokens.match(SnlLexeme.LPAREN);
        ParamList();
        tokens.match(SnlLexeme.RPAREN);
        tokens.match(SnlLexeme.SEMI);
        ProcDecPart();
        ProcBody();
        ProcDecPart();
    }

    public void ProcDecMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.PROCEDURE) {
            ProcDec();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcName() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
    }

    public void ParamList() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD || tokens.current() == SnlLexeme.ID || tokens.current() == SnlLexeme.VAR) {
            ParamDecList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ParamDecList() throws TokenNotMatchException {
        Param();
        ParamMore();
    }

    public void ParamMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.SEMI) {
            tokens.match(SnlLexeme.SEMI);
            ParamDecList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Param() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD || tokens.current() == SnlLexeme.ID ) {
            TypeDef();
            FromList();
        } else if (tokens.current() == SnlLexeme.VAR) {
            tokens.match(SnlLexeme.VAR);
            TypeDef();
            FromList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void FromList() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
        FidMore();
    }

    public void FidMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            FromList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcDecPart() throws TokenNotMatchException {
        DeclarePart();
    }

    public void ProcBody() throws TokenNotMatchException {
        ProgramBody();
    }

    public void ProgramBody() throws TokenNotMatchException {
        tokens.match(SnlLexeme.BEGIN);
        StmList();
        tokens.match(SnlLexeme.END);
    }

    public void StmList() throws TokenNotMatchException {
        Stm();
        StmMore();
    }

    public void StmMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.ENDWH) {
            // pass
        } else if (tokens.current() == SnlLexeme.SEMI) {
            tokens.match(SnlLexeme.SEMI);
            StmList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Stm() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.IF) {
            ConditionalStm();
        } else if (tokens.current() == SnlLexeme.WHILE) {
            LoopStm();
        } else if (tokens.current() == SnlLexeme.READ) {
            InputStm();
        } else if (tokens.current() == SnlLexeme.WRITE) {
            OutputStm();
        } else if (tokens.current() == SnlLexeme.RETURN) {
            ReturnStm();
        } else if (tokens.current() == SnlLexeme.ID) {
            tokens.match(SnlLexeme.ID);
            AssCall();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void AssCall() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ASSIGN || tokens.current() == SnlLexeme.DOT || tokens.current() == SnlLexeme.LMIDPAREN) {
            AssignmentRest();
        } else if (tokens.current() == SnlLexeme.LPAREN) {
            CallStmRest();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void AssignmentRest() throws TokenNotMatchException {
        VariMore();
        tokens.match(SnlLexeme.ASSIGN);
        Exp();
    }

    public void ConditionalStm() throws TokenNotMatchException {
        tokens.match(SnlLexeme.IF);
        RelExp();
        tokens.match(SnlLexeme.THEN);
        StmList();
        tokens.match(SnlLexeme.ELSE);
        StmList();
        tokens.match(SnlLexeme.FI);
    }

    public void LoopStm() throws TokenNotMatchException {
        tokens.match(SnlLexeme.WHILE);
        RelExp();
        tokens.match(SnlLexeme.DO);
        StmList();
        tokens.match(SnlLexeme.ENDWH);
    }

    public void InputStm() throws TokenNotMatchException {
        tokens.match(SnlLexeme.READ);
        tokens.match(SnlLexeme.LPAREN);
        Invar();
        tokens.match(SnlLexeme.RPAREN);
    }

    public void Invar() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
    }

    public void OutputStm() throws TokenNotMatchException {
        tokens.match(SnlLexeme.WRITE);
        tokens.match(SnlLexeme.LPAREN);
        Exp();
        tokens.match(SnlLexeme.RPAREN);
    }

    public void ReturnStm() throws TokenNotMatchException {
        tokens.match(SnlLexeme.RETURN);
    }

    public void CallStmRest() throws TokenNotMatchException {
        tokens.match(SnlLexeme.LPAREN);
        ActParamList();
        tokens.match(SnlLexeme.RPAREN);
    }

    public void ActParamList() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.LPAREN || tokens.current() == SnlLexeme.INTC || tokens.current() == SnlLexeme.ID) {
            Exp();
            ActParamMore();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ActParamMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            ActParamList();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void RelExp() throws TokenNotMatchException {
        Exp();
        OtherRelE();
    }

    public void OtherRelE() throws TokenNotMatchException {
        CmpOp();
        Exp();
    }

    public void Exp() throws TokenNotMatchException {
        Term();
        OtherTerm();
    }

    public void OtherTerm() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.RMIDPAREN || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            // pass
        } else if (tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS) {
            AddOp();
            Exp();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Term() throws TokenNotMatchException {
        Factor();
        OtherFactor();
    }

    public void OtherFactor() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS || tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.RMIDPAREN || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            // pass
        } else if (tokens.current() == SnlLexeme.TIMES || tokens.current() == SnlLexeme.OVER) {
            MultOp();
            Term();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Factor() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.LPAREN) {
            tokens.match(SnlLexeme.LPAREN);
            Exp();
            tokens.match(SnlLexeme.RPAREN);
        } else if (tokens.current() == SnlLexeme.INTC) {
            tokens.match(SnlLexeme.INTC);
        } else if (tokens.current() == SnlLexeme.ID) {
            Variable();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Variable() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
        VariMore();
    }

    public void VariMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RMIDPAREN || tokens.current() == SnlLexeme.ASSIGN || tokens.current() == SnlLexeme.TIMES || tokens.current() == SnlLexeme.OVER || tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS || tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            // pass
        } else if (tokens.current() == SnlLexeme.LMIDPAREN) {
            tokens.match(SnlLexeme.LMIDPAREN);
            Exp();
            tokens.match(SnlLexeme.RMIDPAREN);
        } else if (tokens.current() == SnlLexeme.DOT) {
            tokens.match(SnlLexeme.DOT);
            FieldVar();
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void FieldVar() throws TokenNotMatchException {
        tokens.match(SnlLexeme.ID);
        FieldVarMore();
    }

    public void FieldVarMore() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ASSIGN || tokens.current() == SnlLexeme.TIMES || tokens.current() == SnlLexeme.OVER || tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS || tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            // pass
        } else if (tokens.current() == SnlLexeme.LMIDPAREN) {
            tokens.match(SnlLexeme.LMIDPAREN);
            Exp();
            tokens.match(SnlLexeme.RMIDPAREN);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void CmpOp() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.LT) {
            tokens.match(SnlLexeme.LT);
        } else if (tokens.current() == SnlLexeme.EQ) {
            tokens.match(SnlLexeme.EQ);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void AddOp() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PLUS) {
            tokens.match(SnlLexeme.PLUS);
        } else if (tokens.current() == SnlLexeme.MINUS) {
            tokens.match(SnlLexeme.MINUS);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void MultOp() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.TIMES) {
            tokens.match(SnlLexeme.TIMES);
        } else if (tokens.current() == SnlLexeme.OVER) {
            tokens.match(SnlLexeme.OVER);
        } else {
            throw new TokenNotMatchException();
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Tokens {
        private final List<TokenResult<SnlLexeme>> tokenList;

        private int pos = 0;

        public TokenResult<SnlLexeme> currentToken() {
            return this.tokenList.get(pos);
        }

        public SnlLexeme current() {
            return currentToken().getDefinition().getType();
        }

        public SnlLexeme nextToken() {
            return this.tokenList.get(pos + 1).getDefinition().getType();
        }

        public TokenResult<SnlLexeme> match(SnlLexeme type) throws TokenNotMatchException {
            if (current() == type) {
                var token = currentToken();
                pos += 1;
                return token;
            } else {
                throw new TokenNotMatchException();
            }
        }

        public void step() {
            pos += 1;
        }
    }
}
