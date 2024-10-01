package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspReturnStmt extends AspSmallStmt {
    AspExpr expr;
    
    AspReturnStmt(int n) {
        super(n);
    }

    static AspReturnStmt parse(Scanner s) {
        enterParser("return stmt");
        AspReturnStmt ars = new AspReturnStmt(s.curLineNum());

        skip(s, returnToken);
        ars.expr = AspExpr.parse(s);

        leaveParser("return stmt");
        return ars;
    }

    @Override
    void prettyPrint() {
        prettyWrite("return ");
        expr.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // Evaluates the return value
        RuntimeValue v = expr.eval(curScope);    
        trace("return " + v.showInfo());

        // Stops the function from running by throwing an exception
        throw new RuntimeReturnValue(v, lineNum); 
    }
}