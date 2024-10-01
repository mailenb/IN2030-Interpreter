package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspIntegerLiteral extends AspAtom {
    Token t;

    AspIntegerLiteral(int n) {
        super(n);
    }

    static AspIntegerLiteral parse(Scanner s) {
        enterParser("integer literal");
        AspIntegerLiteral ail = new AspIntegerLiteral(s.curLineNum());

        test(s, integerToken);
        ail.t = s.curToken();
        s.readNextToken();

        leaveParser("integer literal");
        return ail;
    }

    @Override
    void prettyPrint() {
        prettyWrite(t.integerLit+"");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeIntValue(t.integerLit);
    }    
}