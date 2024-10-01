package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspArguments extends AspPrimarySuffix {
    ArrayList<AspExpr> expressions = new ArrayList<>();
    
    AspArguments(int n) {
        super(n);
    }

    static AspArguments parse(Scanner s) {
        enterParser("arguments");
        AspArguments aa = new AspArguments(s.curLineNum());

        skip(s, leftParToken);
        
        if (s.curToken().kind != rightParToken) {
            while (true) {
                aa.expressions.add(AspExpr.parse(s));
                if (s.curToken().kind != commaToken) break;
                skip(s, commaToken);
            }
        }
        
        skip(s, rightParToken);

        leaveParser("arguments");
        return aa;
    }

    @Override
    void prettyPrint() {
        prettyWrite("(");
        int nPrinted = 0;
        for (AspExpr ae : expressions) {
            if (nPrinted > 0) prettyWrite(", ");
            ae.prettyPrint();
            nPrinted++;
        }
        prettyWrite(")");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> args = new ArrayList<>();
        // Evaluates each expression and adds them to the list
        for (AspExpr ae : expressions) {
            args.add(ae.eval(curScope));
        }
        return new RuntimeListValue(args);
    }    
}