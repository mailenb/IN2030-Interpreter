package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactorOpr extends AspSyntax {
    Token t;

    AspFactorOpr(int n) {
        super(n);
    }

    static AspFactorOpr parse(Scanner s) {
        enterParser("factor opr");
        AspFactorOpr afo = new AspFactorOpr(s.curLineNum());

        if (!s.isFactorOpr()) {
            parserError("Expected factor opr but found " + s.curToken().kind + "!", s.curLineNum());
        }

        afo.t = s.curToken();
        s.readNextToken();

        leaveParser("factor opr");
        return afo;
    }

    @Override
    void prettyPrint() {
        prettyWrite(t.toString());
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return null;
    }    
}
