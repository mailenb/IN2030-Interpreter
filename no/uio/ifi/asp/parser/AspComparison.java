package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspComparison extends AspSyntax {
    ArrayList<AspTerm> terms = new ArrayList<>();
    ArrayList<AspCompOpr> operators = new ArrayList<>();
    
    AspComparison(int n) {
        super(n);
    }

    static AspComparison parse(Scanner s) {
        enterParser("comparison");
        AspComparison ac = new AspComparison(s.curLineNum());

        while (true) {
            ac.terms.add(AspTerm.parse(s));

            if (!s.isCompOpr()) break;
            ac.operators.add(AspCompOpr.parse(s));
        }

        leaveParser("comparison");
        return ac;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < terms.size(); i++) {
            if (i > 0) {
                prettyWrite(" ");
                operators.get(i-1).prettyPrint();
                prettyWrite(" ");
            }
            terms.get(i).prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
       
        RuntimeValue v = terms.get(0).eval(curScope);
        RuntimeValue comp = v;

        for (int i = 1; i < terms.size(); i++) {
            
            TokenKind k = operators.get(i-1).t.kind;
            switch(k) {
                case lessToken:
                    comp = v.evalLess(terms.get(i).eval(curScope), this); break;
                case greaterToken:
                    comp = v.evalGreater(terms.get(i).eval(curScope), this); break;
                case doubleEqualToken:
                    comp = v.evalEqual(terms.get(i).eval(curScope), this); break;
                case greaterEqualToken:
                    comp = v.evalGreaterEqual(terms.get(i).eval(curScope), this); break;
                case lessEqualToken:
                    comp = v.evalLessEqual(terms.get(i).eval(curScope), this); break;
                case notEqualToken:
                    comp = v.evalNotEqual(terms.get(i).eval(curScope), this); break;
                default:
                    Main.panic("Illegal comp operator: " + k + "!");
            }

            // Check if it gives False as this will determine the whole comparison
            if (!comp.getBoolValue("comp operand", this)) {
                return comp;
            }

            // Go to the next term
            v = terms.get(i).eval(curScope);
        }
        return comp;
    }
}