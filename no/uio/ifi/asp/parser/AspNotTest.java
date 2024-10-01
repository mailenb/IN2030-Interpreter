package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspNotTest extends AspSyntax {
    boolean hasNot = false;
    AspComparison ac;
    
    AspNotTest(int n) {
        super(n);
    }

    static AspNotTest parse(Scanner s) {
        enterParser("not test");
        AspNotTest ant = new AspNotTest(s.curLineNum());

        if (s.curToken().kind == notToken) {
            ant.hasNot = true;
            skip(s, notToken);
        }
        ant.ac = AspComparison.parse(s);

        leaveParser("not test");
        return ant;
    }

    @Override
    void prettyPrint() {
        if (hasNot) prettyWrite("not ");
        ac.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = ac.eval(curScope);
        if (hasNot) v = v.evalNot(this); // Invert the value to the logical opposite
        return v;
    }
}