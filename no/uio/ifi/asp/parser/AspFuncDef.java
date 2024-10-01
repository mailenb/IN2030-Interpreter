package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspFuncDef extends AspCompoundStmt {
    public AspName name;
    public ArrayList<AspName> args = new ArrayList<>();
    public AspSuite body;
    
    public AspFuncDef(int n) {
        super(n);
    }

    public static AspFuncDef parse(Scanner s) {
        enterParser("func def");
        AspFuncDef afd = new AspFuncDef(s.curLineNum());

        skip(s, defToken);
        afd.name = AspName.parse(s);
        skip(s, leftParToken);

        if (s.curToken().kind != rightParToken) {
            while (true) {
                afd.args.add(AspName.parse(s));
                if (s.curToken().kind != commaToken) break;
                skip(s, commaToken);
            }
        }
        
        skip(s, rightParToken);
        skip(s, colonToken);
        afd.body = AspSuite.parse(s);

        leaveParser("func def");
        return afd;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("def ");
        name.prettyPrint();
        prettyWrite("(");

        int nPrinted = 0;
        for (AspName an : args) {
            if (nPrinted > 0) prettyWrite(", ");
            an.prettyPrint();
            nPrinted++;
        }

        prettyWrite("):");
        body.prettyPrint();
        prettyWriteLn("");
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        curScope.assign(name.t.name, new RuntimeFunc(this, curScope, name.t.name));
        trace("def " + name.t.name);
        return null;
    }
}