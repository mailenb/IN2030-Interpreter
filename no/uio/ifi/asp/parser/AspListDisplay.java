package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspListDisplay extends AspAtom {
    ArrayList<AspExpr> expressions = new ArrayList<>();

    AspListDisplay(int n) {
        super(n);
    }

    static AspListDisplay parse(Scanner s) {
        enterParser("list display");
        AspListDisplay ald = new AspListDisplay(s.curLineNum());

        skip(s, leftBracketToken);

        if (s.curToken().kind != rightBracketToken) {
            while (true) {
                ald.expressions.add(AspExpr.parse(s));
                if (s.curToken().kind != commaToken) break;
                skip(s, commaToken);
            }
        }
        
        skip(s, rightBracketToken);

        leaveParser("list display");
        return ald;
    }

    @Override
    void prettyPrint() {
        int nPrinted = 0;
        prettyWrite("[");
        for (AspExpr ae : expressions) {
            if (nPrinted > 0) prettyWrite(", ");
            ae.prettyPrint();
            nPrinted++;
        }
        prettyWrite("]");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> list = new ArrayList<>();
        for (AspExpr ae : expressions)
            list.add(ae.eval(curScope));
        return new RuntimeListValue(list);
    }    
}