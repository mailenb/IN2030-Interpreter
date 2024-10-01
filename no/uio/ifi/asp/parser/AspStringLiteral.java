package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspStringLiteral extends AspAtom {
    Token t;

    AspStringLiteral(int n) {
        super(n);
    }

    static AspStringLiteral parse(Scanner s) {
        enterParser("string literal");
        AspStringLiteral asl = new AspStringLiteral(s.curLineNum());

        test(s, stringToken);
        asl.t = s.curToken();
        s.readNextToken();
  
        leaveParser("string literal");
        return asl;
    }

    @Override
    void prettyPrint() {
        prettyWrite("'" + t.stringLit + "'");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeStringValue(t.stringLit);
    }    
}