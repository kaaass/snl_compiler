package net.kaaass.snlc.parser;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kaaass.snlc.ast.ExpType;
import net.kaaass.snlc.ast.Kind;
import net.kaaass.snlc.ast.NodeKind;
import net.kaaass.snlc.ast.TreeNode;
import net.kaaass.snlc.ast.attr.*;
import net.kaaass.snlc.lexer.TokenResult;
import net.kaaass.snlc.lexer.snl.SnlLexeme;
import net.kaaass.snlc.parser.exception.TokenNotMatchException;

import java.util.List;
import java.util.Optional;

/**
 * Parser for SNL
 *
 * @author Kevin Axel
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser {

    private final Tokens tokens;

    public static Parser of(List<TokenResult<SnlLexeme>> tokenList) {
        return new Parser(new Tokens(tokenList));
    }

    public TreeNode getAst() throws TokenNotMatchException {
        var root = Program();
        if (tokens.pos == tokens.tokenList.size())
            return root;
        else {
            throw new TokenNotMatchException();
        }
    }

    public TreeNode Program() throws TokenNotMatchException {
        var root = new TreeNode();
        root.setNodeK(NodeKind.ProK);

        ProgramHead(root);
        DeclarePart(root);
        ProgramBody(root);
        tokens.match(SnlLexeme.DOT);
        return root;
    }

    public void ProgramHead(TreeNode parent) throws TokenNotMatchException {
        var cur = TreeNode.ofParent(parent, NodeKind.PheadK);

        tokens.match(SnlLexeme.PROGRAM);
        ProgramName(cur);
    }

    public void ProgramName(TreeNode parent) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);

        parent.getName().add(name.getToken());
    }

    public void DeclarePart(TreeNode parent) throws TokenNotMatchException {
        TypeDecpart(parent);
        VarDecpart(parent);
        ProcDecpart(parent);
    }

    public void TypeDecpart(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.VAR || tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.TYPE) {
            var cur = TreeNode.ofParent(parent, NodeKind.TypeK);

            TypeDec(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void TypeDec(TreeNode parent) throws TokenNotMatchException {
        var cur = TreeNode.ofParent(parent, NodeKind.DecK);

        tokens.match(SnlLexeme.TYPE);
        TypeDecList(cur);
    }

    public void TypeDecList(TreeNode parent) throws TokenNotMatchException {
        TypeId(parent);
        tokens.match(SnlLexeme.EQ);
        TypeDef(parent, false);
        tokens.match(SnlLexeme.SEMI);
        TypeDecMore(parent.getParent());
    }

    public void TypeDecMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.VAR || tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.ID) {
            var cur = TreeNode.ofParent(parent, NodeKind.DecK);

            TypeDecList(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void TypeId(TreeNode parent) throws TokenNotMatchException {
        var typeid = tokens.match(SnlLexeme.ID);
        parent.getName().add(typeid.getToken());
    }

    public void TypeDef(TreeNode parent, boolean proc) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR) {
            parent.setKind(BaseType());
            if (proc)
                parent.setAttr(new ProcAttr(Paramt.ValParamType));
        } else if (tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD) {
            if (proc)
                parent.setAttr(new ProcAttr(Paramt.VarParamType));
            StructureType(parent);
        } else if (tokens.current() == SnlLexeme.ID) {
            tokens.match(SnlLexeme.ID);
            parent.setKind(Kind.IdK);
            if (proc)
                parent.setAttr(new ProcAttr(Paramt.VarParamType));
        } else {
            throw new TokenNotMatchException();
        }
    }

    public Kind BaseType() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.INTEGER) {
            tokens.match(SnlLexeme.INTEGER);

            return Kind.IntegerK;
        } else if (tokens.current() == SnlLexeme.CHAR) {
            tokens.match(SnlLexeme.CHAR);

            return Kind.CharK;
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void StructureType(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ARRAY) {
            ArrayType(parent);
        } else if (tokens.current() == SnlLexeme.RECORD) {
            RecType(parent);
        } else {
            throw new TokenNotMatchException();
        }

    }

    public void ArrayType(TreeNode parent) throws TokenNotMatchException {
        tokens.match(SnlLexeme.ARRAY);
        parent.setKind(Kind.ArrayK);
        var attr = new ArrayAttr();
        parent.setAttr(attr);
        var low = Low();
        var top = Top();
        tokens.match(SnlLexeme.OF);
        attr.setLow(low);
        attr.setTop(top);
        attr.setChildType(BaseType());
    }

    public Integer Low() throws TokenNotMatchException {
        var low = tokens.match(SnlLexeme.INTC);
        return Integer.valueOf(low.getToken());
    }

    public Integer Top() throws TokenNotMatchException {
        var top = tokens.match(SnlLexeme.INTC);
        return Integer.valueOf(top.getToken());
    }

    public void RecType(TreeNode parent) throws TokenNotMatchException {
        tokens.match(SnlLexeme.RECORD);
        parent.setKind(Kind.RecordK);
        FieldDecList(parent);
        tokens.match(SnlLexeme.END);
    }

    public void FieldDecList(TreeNode parent) throws TokenNotMatchException {
        var cur = TreeNode.ofParent(parent, NodeKind.DecK);

        if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR) {
            cur.setKind(BaseType());
            IdList(cur);
            tokens.match(SnlLexeme.SEMI);
            FieldDecMore(cur);
        } else if (tokens.current() == SnlLexeme.ARRAY) {
            ArrayType(cur);
            IdList(cur);
            tokens.match(SnlLexeme.SEMI);
            FieldDecMore(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void FieldDecMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.END) {
            // pass
        } else if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY) {
            FieldDecList(parent.getParent());
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void IdList(TreeNode parent) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);
        parent.getName().add(name.getToken());
        IdMore(parent);
    }

    public void IdMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.SEMI) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            IdList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void VarDecpart(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.VAR) {
            var cur = TreeNode.ofParent(parent, NodeKind.VarK);

            VarDec(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void VarDec(TreeNode parent) throws TokenNotMatchException {
        tokens.match(SnlLexeme.VAR);
        var cur = TreeNode.ofParent(parent, NodeKind.DecK);

        VarDecList(cur);
    }

    public void VarDecList(TreeNode parent) throws TokenNotMatchException {
        TypeDef(parent, false);
        VarIdList(parent);
        tokens.match(SnlLexeme.SEMI);
        VarDecMore(parent);
    }

    public void VarDecMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PROCEDURE || tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD || tokens.current() == SnlLexeme.ID) {
            VarDecList(parent.getParent());
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void VarIdList(TreeNode parent) throws TokenNotMatchException {
        var token = tokens.match(SnlLexeme.ID);
        parent.getName().add(token.getToken());
        VarIdMore(parent);
    }

    public void VarIdMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.SEMI) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            VarIdList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcDecpart(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.PROCEDURE) {
            var cur = TreeNode.ofParent(parent, NodeKind.ProcDecK);

            ProcDec(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcDec(TreeNode parent) throws TokenNotMatchException {
        tokens.match(SnlLexeme.PROCEDURE);
        ProcName(parent);
        tokens.match(SnlLexeme.LPAREN);
        ParamList(parent);
        tokens.match(SnlLexeme.RPAREN);
        tokens.match(SnlLexeme.SEMI);
        ProcDecPart(parent);
        ProcBody(parent);
        ProcDecMore(parent);
    }

    public void ProcDecMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.BEGIN) {
            // pass
        } else if (tokens.current() == SnlLexeme.PROCEDURE) {
            var cur = TreeNode.ofParent(parent.getParent(), NodeKind.ProcDecK);
            ProcDec(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcName(TreeNode parent) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);
        parent.getName().add(name.getToken());
    }

    public void ParamList(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD || tokens.current() == SnlLexeme.ID || tokens.current() == SnlLexeme.VAR) {
            ParamDecList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ParamDecList(TreeNode parent) throws TokenNotMatchException {
        Param(parent);
        ParamMore(parent);
    }

    public void ParamMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.SEMI) {
            tokens.match(SnlLexeme.SEMI);
            ParamDecList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Param(TreeNode parent) throws TokenNotMatchException {

        if (tokens.current() == SnlLexeme.INTEGER || tokens.current() == SnlLexeme.CHAR || tokens.current() == SnlLexeme.ARRAY || tokens.current() == SnlLexeme.RECORD || tokens.current() == SnlLexeme.ID ) {
            var cur = TreeNode.ofParent(parent, NodeKind.DecK);
            TypeDef(cur, true);
            FormList(cur);
        } else if (tokens.current() == SnlLexeme.VAR) {
            var cur = TreeNode.ofParent(parent, NodeKind.VarK);
            tokens.match(SnlLexeme.VAR);
            TypeDef(cur, true);
            FormList(cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void FormList(TreeNode parent) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);
        parent.getName().add(name.getToken());
        FidMore(parent);
    }

    public void FidMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            FormList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ProcDecPart(TreeNode parent) throws TokenNotMatchException {
        DeclarePart(parent);
    }

    public void ProcBody(TreeNode parent) throws TokenNotMatchException {
        ProgramBody(parent);
    }

    public void ProgramBody(TreeNode parent) throws TokenNotMatchException {
        tokens.match(SnlLexeme.BEGIN);
        var cur = TreeNode.ofParent(parent, NodeKind.StmLK);
        StmList(cur);
        tokens.match(SnlLexeme.END);
    }

    public void StmList(TreeNode parent) throws TokenNotMatchException {
        Stm(parent);
        StmMore(parent);
    }

    public void StmMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.ENDWH) {
            // pass
        } else if (tokens.current() == SnlLexeme.SEMI) {
            tokens.match(SnlLexeme.SEMI);
            StmList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Stm(TreeNode parent) throws TokenNotMatchException {
        var cur = TreeNode.ofParent(parent, NodeKind.StmtK);
        if (tokens.current() == SnlLexeme.IF) {
            ConditionalStm(cur);
        } else if (tokens.current() == SnlLexeme.WHILE) {
            LoopStm(cur);
        } else if (tokens.current() == SnlLexeme.READ) {
            InputStm(cur);
        } else if (tokens.current() == SnlLexeme.WRITE) {
            OutputStm(cur);
        } else if (tokens.current() == SnlLexeme.RETURN) {
            ReturnStm(cur);
        } else if (tokens.current() == SnlLexeme.ID) {
            var name = tokens.match(SnlLexeme.ID);
            AssCall(name.getToken(), cur);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void AssCall(String name, TreeNode parent) throws TokenNotMatchException {
        var expAttr = new ExprAttr(SnlLexeme.ASSIGN, null, ExpType.Void);
        parent.setAttr(expAttr);

        var exp1 = TreeNode.ofParent(parent, NodeKind.ExpK);
        exp1.setAttr(new ExprAttr(SnlLexeme.ID, VarKind.IdV, ExpType.Integer));
        exp1.getName().add(name);

        if (tokens.current() == SnlLexeme.ASSIGN || tokens.current() == SnlLexeme.DOT || tokens.current() == SnlLexeme.LMIDPAREN) {
            parent.setKind(Kind.AssignK);
            var exp2 = AssignmentRest(exp1);
            parent.addChild(exp2);
        } else if (tokens.current() == SnlLexeme.LPAREN) {
            parent.setKind(Kind.CallK);
            CallStmRest(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public TreeNode AssignmentRest(TreeNode exp1) throws TokenNotMatchException {
        VariMore(exp1);
        tokens.match(SnlLexeme.ASSIGN);
        return Exp();

    }

    public void ConditionalStm(TreeNode parent) throws TokenNotMatchException {
        parent.setKind(Kind.IfK);
        tokens.match(SnlLexeme.IF);
        RelExp(parent);
        tokens.match(SnlLexeme.THEN);
        StmList(parent);
        tokens.match(SnlLexeme.ELSE);
        StmList(parent);
        tokens.match(SnlLexeme.FI);
    }

    public void LoopStm(TreeNode parent) throws TokenNotMatchException {
        parent.setKind(Kind.WhileK);
        tokens.match(SnlLexeme.WHILE);
        RelExp(parent);
        tokens.match(SnlLexeme.DO);
        StmList(parent);
        tokens.match(SnlLexeme.ENDWH);
    }

    public void InputStm(TreeNode parent) throws TokenNotMatchException {
        parent.setKind(Kind.ReadK);
        tokens.match(SnlLexeme.READ);
        tokens.match(SnlLexeme.LPAREN);
        Invar(parent);
        tokens.match(SnlLexeme.RPAREN);
    }

    public void Invar(TreeNode parent) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);
        parent.getName().add(name.getToken());
    }

    public void OutputStm(TreeNode parent) throws TokenNotMatchException {
        parent.setKind(Kind.WriteK);
        tokens.match(SnlLexeme.WRITE);
        tokens.match(SnlLexeme.LPAREN);
        var exp = Exp();
        exp.setNodeK(NodeKind.ExpK);
        parent.addChild(exp);
        tokens.match(SnlLexeme.RPAREN);
    }

    public void ReturnStm(TreeNode parent) throws TokenNotMatchException {
        parent.setKind(Kind.ReturnK);
        tokens.match(SnlLexeme.RETURN);
    }

    public void CallStmRest(TreeNode parent) throws TokenNotMatchException {
        tokens.match(SnlLexeme.LPAREN);
        ActParamList(parent);
        tokens.match(SnlLexeme.RPAREN);
    }

    public void ActParamList(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.LPAREN || tokens.current() == SnlLexeme.INTC || tokens.current() == SnlLexeme.ID) {
            var exp = Exp();
            exp.setNodeK(NodeKind.ExpK);
            parent.addChild(exp);
            ActParamMore(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void ActParamMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RPAREN) {
            // pass
        } else if (tokens.current() == SnlLexeme.COMMA) {
            tokens.match(SnlLexeme.COMMA);
            ActParamList(parent);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void RelExp(TreeNode parent) throws TokenNotMatchException {
        var exp1 = Exp();
        exp1.setNodeK(NodeKind.ExpK);
        var exp2 = OtherRelE();
        exp2.getChild().add(0, exp1);

        parent.addChild(exp2);
    }

    public TreeNode OtherRelE() throws TokenNotMatchException {
        var cmpNode = CmpOp();

        var exp2 = Exp();
        cmpNode.addChild(exp2);

        return cmpNode;
    }

    public TreeNode Exp() throws TokenNotMatchException {
        var exp1 = Term();
        var exp2 = OtherTerm();

        if (exp2.isEmpty()) {
            return exp1;
        } else {
            var ret = exp2.get();
            ret.getChild().add(0, exp1);

            return ret;
        }
    }

    public Optional<TreeNode> OtherTerm() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.RMIDPAREN || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            return Optional.empty();
        } else if (tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS) {

            var exp = new TreeNode();
            exp.setNodeK(NodeKind.ExpK);

            AddOp(exp);

            var exp2 = Exp();
            exp.addChild(exp2);

            return Optional.of(exp);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public TreeNode Term() throws TokenNotMatchException {
        var exp1 = Factor();
        var exp2 = OtherFactor();

        if (exp2.isEmpty()) {
            return exp1;
        } else {
            var ret = exp2.get();
            ret.getChild().add(0, exp1);
            return ret;
        }
    }

    public Optional<TreeNode> OtherFactor() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS || tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.RMIDPAREN || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            return Optional.empty();
        } else if (tokens.current() == SnlLexeme.TIMES || tokens.current() == SnlLexeme.OVER) {
            var exp = new TreeNode();
            exp.setNodeK(NodeKind.ExpK);
            MultOp(exp);
            var exp2 = Term();

            exp.addChild(exp2);
            return Optional.of(exp);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public TreeNode Factor() throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.LPAREN) {
            tokens.match(SnlLexeme.LPAREN);
            var exp = Exp();
            tokens.match(SnlLexeme.RPAREN);

            return exp;
        } else if (tokens.current() == SnlLexeme.INTC) {
            var val = tokens.match(SnlLexeme.INTC);

            var exp = new TreeNode();
            exp.setNodeK(NodeKind.ExpK);
            exp.setKind(Kind.ConstK);
            exp.getName().add(val.getToken());

            return exp;
        } else if (tokens.current() == SnlLexeme.ID) {
            var exp = new TreeNode();
            exp.setNodeK(NodeKind.ExpK);
            exp.setAttr(new ExprAttr(SnlLexeme.ID, VarKind.IdV, ExpType.Integer));
            Variable(exp);
            return exp;
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void Variable(TreeNode exp) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);
        exp.setKind(Kind.IdEK);
        exp.getName().add(name.getToken());
        VariMore(exp);
    }

    public void VariMore(TreeNode exp) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.RMIDPAREN || tokens.current() == SnlLexeme.ASSIGN || tokens.current() == SnlLexeme.TIMES || tokens.current() == SnlLexeme.OVER || tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS || tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            // pass
        } else if (tokens.current() == SnlLexeme.LMIDPAREN) {
            tokens.match(SnlLexeme.LMIDPAREN);
            var arrayMem = Exp();
            arrayMem.setNodeK(NodeKind.ExpK);
            arrayMem.setAttr(new ExprAttr(SnlLexeme.ARRAY, VarKind.ArrayMembV, ExpType.Void));
            tokens.match(SnlLexeme.RMIDPAREN);
        } else if (tokens.current() == SnlLexeme.DOT) {
            var field = TreeNode.ofParent(exp, NodeKind.ExpK);
            field.setAttr(new ExprAttr(SnlLexeme.RECORD, VarKind.FiledMembV, ExpType.Void));
            tokens.match(SnlLexeme.DOT);
            FieldVar(field);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void FieldVar(TreeNode parent) throws TokenNotMatchException {
        var name = tokens.match(SnlLexeme.ID);
        parent.getName().add(name.getToken());
        FieldVarMore(parent);
    }

    public void FieldVarMore(TreeNode parent) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.ASSIGN || tokens.current() == SnlLexeme.TIMES || tokens.current() == SnlLexeme.OVER || tokens.current() == SnlLexeme.PLUS || tokens.current() == SnlLexeme.MINUS || tokens.current() == SnlLexeme.LT || tokens.current() == SnlLexeme.EQ || tokens.current() == SnlLexeme.THEN || tokens.current() == SnlLexeme.ELSE || tokens.current() == SnlLexeme.FI || tokens.current() == SnlLexeme.DO || tokens.current() == SnlLexeme.ENDWH || tokens.current() == SnlLexeme.RPAREN || tokens.current() == SnlLexeme.END || tokens.current() == SnlLexeme.SEMI || tokens.current() == SnlLexeme.COMMA) {
            // pass
        } else if (tokens.current() == SnlLexeme.LMIDPAREN) {
            tokens.match(SnlLexeme.LMIDPAREN);
            var arrayMem = Exp();
            parent.addChild(arrayMem);
            arrayMem.setAttr(new ExprAttr(SnlLexeme.ARRAY, VarKind.ArrayMembV, ExpType.Void));
            tokens.match(SnlLexeme.RMIDPAREN);
        } else {
            throw new TokenNotMatchException();
        }
    }

    public TreeNode CmpOp() throws TokenNotMatchException {
        var cmpNode = new TreeNode();
        cmpNode.setNodeK(NodeKind.ExpK);
        cmpNode.setKind(Kind.OpK);
        if (tokens.current() == SnlLexeme.LT) {
            tokens.match(SnlLexeme.LT);
            cmpNode.setAttr(new ExprAttr(SnlLexeme.LT, null, ExpType.Boolean));
            return cmpNode;
        } else if (tokens.current() == SnlLexeme.EQ) {
            tokens.match(SnlLexeme.EQ);
            cmpNode.setAttr(new ExprAttr(SnlLexeme.EQ, null, ExpType.Boolean));
            return cmpNode;
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void AddOp(TreeNode exp) throws TokenNotMatchException {
        exp.setKind(Kind.OpK);
        if (tokens.current() == SnlLexeme.PLUS) {
            tokens.match(SnlLexeme.PLUS);
            exp.setAttr(new ExprAttr(SnlLexeme.PLUS, null, ExpType.Integer));
        } else if (tokens.current() == SnlLexeme.MINUS) {
            tokens.match(SnlLexeme.MINUS);
            exp.setAttr(new ExprAttr(SnlLexeme.MINUS, null, ExpType.Integer));
        } else {
            throw new TokenNotMatchException();
        }
    }

    public void MultOp(TreeNode exp) throws TokenNotMatchException {
        if (tokens.current() == SnlLexeme.TIMES) {
            tokens.match(SnlLexeme.TIMES);
            exp.setKind(Kind.OpK);
            exp.setAttr(new ExprAttr(SnlLexeme.TIMES, null, ExpType.Integer));
        } else if (tokens.current() == SnlLexeme.OVER) {
            tokens.match(SnlLexeme.OVER);
            exp.setKind(Kind.OpK);
            exp.setAttr(new ExprAttr(SnlLexeme.OVER, null, ExpType.Integer));
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

        public TokenResult<SnlLexeme> match(SnlLexeme type) throws TokenNotMatchException {
            if (current() == type) {
                var token = currentToken();
                pos += 1;
                return token;
            } else {
                throw new TokenNotMatchException();
            }
        }
    }
}
