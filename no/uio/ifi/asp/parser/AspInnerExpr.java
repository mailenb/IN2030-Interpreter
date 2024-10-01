package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspInnerExpr extends AspAtom {
    AspExpr inside;

    AspInnerExpr(int n) {
        super(n);
    }

    static AspInnerExpr parse(Scanner s) {
        enterParser("inner expr");
        AspInnerExpr aie = new AspInnerExpr(s.curLineNum());

        skip(s, leftParToken);
        aie.inside = AspExpr.parse(s);
        skip(s, rightParToken);

        leaveParser("inner expr");
        return aie;
    }

    @Override
    void prettyPrint() {
        prettyWrite("(");
        inside.prettyPrint();
        prettyWrite(")");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return inside.eval(curScope);
    }    
}