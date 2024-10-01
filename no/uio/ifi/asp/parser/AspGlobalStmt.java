package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspGlobalStmt extends AspSmallStmt {
    ArrayList<AspName> names = new ArrayList<>();
    
    AspGlobalStmt(int n) {
        super(n);
    }

    static AspGlobalStmt parse(Scanner s) {
        enterParser("AspGlobalStmt");
        AspGlobalStmt ags = new AspGlobalStmt(s.curLineNum());

        skip(s, globalToken);
        while(true) {
            ags.names.add(AspName.parse(s));
            if (s.curToken().kind != commaToken) break;
            skip(s, commaToken);
        }

        leaveParser("AspGlobalStmt");
        return ags;
    }

    @Override
    void prettyPrint() {
        int nPrinted = 0;
        prettyWrite("global ");
        for (AspName an : names) {
            if (nPrinted > 0) prettyWrite(", ");
            an.prettyPrint();
            nPrinted++;
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for (AspName an : names) curScope.registerGlobalName(an.t.name);
        return null;
    }
}