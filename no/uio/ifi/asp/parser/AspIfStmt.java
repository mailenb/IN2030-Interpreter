package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspIfStmt extends AspCompoundStmt {
    ArrayList<AspExpr> tests = new ArrayList<>();
    ArrayList<AspSuite> bodies = new ArrayList<>();

    AspIfStmt(int n) {
        super(n);
    }

    static AspIfStmt parse(Scanner s) {
        enterParser("if stmt");
        AspIfStmt ais = new AspIfStmt(s.curLineNum());

        skip(s, ifToken);

        while (true) {
            ais.tests.add(AspExpr.parse(s));
            skip(s, colonToken);
            ais.bodies.add(AspSuite.parse(s));
            if (s.curToken().kind != elifToken) break;
            skip(s, elifToken);
        }
        
        if (s.curToken().kind == elseToken) {
            skip(s, elseToken);
            skip(s, colonToken);
            ais.bodies.add(AspSuite.parse(s));
        }

        leaveParser("if stmt");
        return ais;
    }

    @Override
    void prettyPrint() {
        prettyWrite("if ");
        
        for (int i = 0; i < tests.size(); i++) {
            if (i > 0) prettyWrite("elif ");
            tests.get(i).prettyPrint();
            prettyWrite(": ");
            bodies.get(i).prettyPrint();
        }

        if (bodies.size() > tests.size()) {
            prettyWrite("else: ");
            bodies.get(bodies.size()-1).prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // Evaluates all <exp> till it find the one that is true
        for (int i = 0; i < tests.size(); i++) {
            AspExpr curTest = tests.get(i);
            
            // Finding the right if test
            if (curTest.eval(curScope).getBoolValue("if stmt test", this)) {
                trace("if True alt #" + (i+1) + ": ...");
                // Evaluates the corresponding body
                bodies.get(i).eval(curScope);
                return null;
            }
        }

        // If no <expr> was true, and it has an else-branch, evaluate it
        if (bodies.size() > tests.size()) {
            trace("else: ...");
            bodies.get(bodies.size()-1).eval(curScope);
        }

        return null;
    }    
}
