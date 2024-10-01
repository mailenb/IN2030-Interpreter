package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspSmallStmtList extends AspStmt {
    ArrayList<AspSmallStmt> stmts = new ArrayList<>();
    
    AspSmallStmtList(int n) {
        super(n);
    }

    static AspSmallStmtList parse(Scanner s) {
        enterParser("small stmt list");
        AspSmallStmtList assl = new AspSmallStmtList(s.curLineNum());
        
        while (true) {
            assl.stmts.add(AspSmallStmt.parse(s));

            if (s.curToken().kind == semicolonToken) {
                skip(s, semicolonToken);
                if (s.curToken().kind == newLineToken) {
                    break;
                }
            }
            else {
                break;
            }
        }
        skip(s, newLineToken);

        leaveParser("small stmt list");
        return assl;
    }

    @Override
    void prettyPrint() {
        int nPrinted = 0;
        for (AspSmallStmt ass : stmts) {
            if (nPrinted > 0) prettyWrite("; ");
            ass.prettyPrint();
            nPrinted++;
        }
        prettyWriteLn("");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for (AspSmallStmt ass : stmts) {
            ass.eval(curScope);
        }
        return null;
    }    
}