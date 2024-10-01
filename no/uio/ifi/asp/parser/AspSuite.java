package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspSuite extends AspSyntax {
    ArrayList<AspStmt> stmts = new ArrayList<>();
    AspSmallStmtList stmtList;
    
    public AspSuite(int n) {
        super(n);
    }

    public static AspSuite parse(Scanner s) {
        enterParser("suite");
        AspSuite as = new AspSuite(s.curLineNum());

        if (s.curToken().kind == newLineToken) {
            skip(s, newLineToken);
            skip(s, indentToken);
            while (true) {
                as.stmts.add(AspStmt.parse(s));
                if (s.curToken().kind == dedentToken) break;
            }
            skip(s, dedentToken);
        }
        else {
            as.stmtList = AspSmallStmtList.parse(s);
        }

        leaveParser("suite");
        return as;
    }

    @Override
    public void prettyPrint() {
        if (!stmts.isEmpty()) {
            prettyWriteLn("");
            prettyIndent();
            for (AspStmt ass : stmts) {
                ass.prettyPrint();
            }
            prettyDedent();
        }
        else {
            stmtList.prettyPrint();
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        if (!stmts.isEmpty()) {
            for (AspStmt ass : stmts) {
                ass.eval(curScope);
            }
        } else {
            stmtList.eval(curScope);
        }
        return null;
    }    
}
