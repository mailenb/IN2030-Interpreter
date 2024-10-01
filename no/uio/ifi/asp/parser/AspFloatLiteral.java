package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFloatLiteral extends AspAtom {
    Token t;

    AspFloatLiteral(int n) {
        super(n);
    }

    static AspFloatLiteral parse(Scanner s) {
        enterParser("float literal");
        AspFloatLiteral afl = new AspFloatLiteral(s.curLineNum());

        test(s, floatToken);
        afl.t = s.curToken();
        s.readNextToken();
  
        leaveParser("float literal");
        return afl;
    }

    @Override
    void prettyPrint() {
        prettyWrite(t.floatLit+"");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeFloatValue(t.floatLit);
    }    
}