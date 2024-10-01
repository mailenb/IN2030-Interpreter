package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspWhileStmt extends AspCompoundStmt {
    AspExpr test;
    AspSuite body;

    AspWhileStmt(int n) {
        super(n);
    }

    static AspWhileStmt parse(Scanner s) {
        enterParser("while stmt");
        AspWhileStmt aws = new AspWhileStmt(s.curLineNum());

        skip(s, whileToken);            // check if we have read 'while'
        aws.test = AspExpr.parse(s);    // parse the expr
        skip(s, colonToken);            // check that we have read :
        aws.body = AspSuite.parse(s);   // parse the suite

        leaveParser("while stmt");
        return aws;
    }

    void prettyPrint() {
        prettyWrite("while ");
        test.prettyPrint();
        prettyWrite(": ");
        body.prettyPrint();
    }

    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        while (true) {
            RuntimeValue t = test.eval(curScope);
            
            // If <expr> is false the while loop is done
            if (! t.getBoolValue("while loop test", this)) break;
            
            trace("while True: ...");
            body.eval(curScope);
        }

        trace("while False:");
        // It doesn't matter what value we return, as it will not be used
        return null;    
    }
}
