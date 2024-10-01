package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspForStmt extends AspCompoundStmt {
    AspName name;
    AspExpr expr;
    AspSuite body;

    AspForStmt(int n) {
        super(n);
    }

    static AspForStmt parse(Scanner s) {
        enterParser("for stmt");
        AspForStmt afs = new AspForStmt(s.curLineNum());

        skip(s, forToken);
        afs.name = AspName.parse(s);
        skip(s, inToken);
        afs.expr = AspExpr.parse(s);
        skip(s, colonToken);
        afs.body = AspSuite.parse(s);

        leaveParser("for stmt");
        return afs;
    }

    @Override
    void prettyPrint() {
        prettyWrite("for ");
        name.prettyPrint();
        prettyWrite(" in ");
        expr.prettyPrint();
        prettyWrite(": ");
        body.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue list = expr.eval(curScope);

        // Check that the list is actually of type list
        if (list instanceof RuntimeListValue) {
            // Calculate the list size
            long size = list.evalLen(this).getIntValue("for loop", this);

            // Goes through each element in the list, assigns them to the scope and evaluates the suite
            for (int i = 0; i < size; i++) {
                RuntimeValue value = list.evalSubscription(new RuntimeIntValue(i), this);
                curScope.assign(name.t.name, value);
                trace("for #" + (i+1) + ": " + name.t.name + " = " + value);
                body.eval(curScope);
            }
        }
        return null;
    }    
}