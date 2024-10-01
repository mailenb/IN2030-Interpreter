package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspName extends AspAtom {
    public Token t;

    AspName(int n) {
        super(n);
    }

    static AspName parse(Scanner s) {
        enterParser("name");
        AspName an = new AspName(s.curLineNum());

        test(s, nameToken);
        an.t = s.curToken();
        s.readNextToken();

        leaveParser("name");
        return an;
    }

    @Override
    void prettyPrint() {
        prettyWrite(t.name);
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return curScope.find(t.name, this);
    }    
}