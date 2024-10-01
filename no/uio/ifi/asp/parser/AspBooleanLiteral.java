package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspBooleanLiteral extends AspAtom {
    Token bool;

    AspBooleanLiteral(int n) {
        super(n);
    }

    static AspBooleanLiteral parse(Scanner s) {
        enterParser("boolean literal");
        AspBooleanLiteral abl = new AspBooleanLiteral(s.curLineNum());

        test(s, trueToken, falseToken);
        abl.bool = s.curToken();
        s.readNextToken();

        leaveParser("boolean literal");
        return abl;
    }

    @Override
    void prettyPrint() {
        prettyWrite(bool.toString());
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeBoolValue(Boolean.parseBoolean(bool.toString()));
    }    
}