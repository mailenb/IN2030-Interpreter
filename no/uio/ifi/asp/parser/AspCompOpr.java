package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspCompOpr extends AspSyntax {
    Token t;
    
    AspCompOpr(int n) {
        super(n);
    }

    static AspCompOpr parse(Scanner s) {
        enterParser("comp opr");
        AspCompOpr aco = new AspCompOpr(s.curLineNum());
        
        if (!s.isCompOpr()) {
            parserError("Expected comp opr but found " + s.curToken().kind + "!", s.curLineNum());
        }

        aco.t = s.curToken();
        s.readNextToken();
        
        leaveParser("comp opr");
        return aco;
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