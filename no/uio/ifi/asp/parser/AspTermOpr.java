package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspTermOpr extends AspSyntax {
    Token t;

    AspTermOpr(int n) {
        super(n);
    }

    static AspTermOpr parse(Scanner s) {
        enterParser("term opr");
        AspTermOpr ato = new AspTermOpr(s.curLineNum());

        test(s, plusToken, minusToken);
        ato.t = s.curToken();
        s.readNextToken();

        leaveParser("term opr");
        return ato;
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