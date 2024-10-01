// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.beans.Expression;
import java.util.ArrayList;

public class AspExpr extends AspSyntax {
    ArrayList<AspAndTest> andTests = new ArrayList<>();

    AspExpr(int n) {
	    super(n);
    }


    public static AspExpr parse(Scanner s) {
	    enterParser("expr");
        AspExpr ae = new AspExpr(s.curLineNum());

        while (true) {
            ae.andTests.add(AspAndTest.parse(s));
            if (s.curToken().kind != orToken) break;
            skip(s, orToken);
        }
    
        leaveParser("expr");
        return ae;
    }


    @Override
    public void prettyPrint() {
        int nPrinted = 0;
        for (AspAndTest aat : andTests) {
            if (nPrinted > 0) prettyWrite(" or ");
            aat.prettyPrint(); 
            nPrinted++;
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = andTests.get(0).eval(curScope);
        for (int i = 1; i < andTests.size(); ++i) {
            // Returns true at the first true value it finds
            if (v.getBoolValue("or operand", this)) return v;   
            v = andTests.get(i).eval(curScope);
        }
        return v;
    }
}
