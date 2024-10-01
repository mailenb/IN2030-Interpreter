package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactorPrefix extends AspSyntax {
    Token t;
    
    AspFactorPrefix(int n) {
        super(n);
    }

    static AspFactorPrefix parse(Scanner s) {
        enterParser("factor prefix");
        AspFactorPrefix afp = new AspFactorPrefix(s.curLineNum());

        test(s, plusToken, minusToken);
        afp.t = s.curToken();
        s.readNextToken();

        leaveParser("factor prefix");
        return afp;
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