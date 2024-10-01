package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspTerm extends AspSyntax {
    ArrayList<AspFactor> factors = new ArrayList<>();
    ArrayList<AspTermOpr> operators = new ArrayList<>();

    AspTerm(int n) {
        super(n);
    }

    static AspTerm parse(Scanner s) {
        enterParser("term");
        AspTerm at = new AspTerm(s.curLineNum());

        while (true) {
            at.factors.add(AspFactor.parse(s));
            if (!s.isTermOpr()) break;
            at.operators.add(AspTermOpr.parse(s));
        }

        leaveParser("term");
        return at;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < factors.size(); i++) {
            if (i > 0) {
                prettyWrite(" ");
                operators.get(i-1).prettyPrint();
                prettyWrite(" ");
            }
            factors.get(i).prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // Always (at least) one factor
        RuntimeValue v = factors.get(0).eval(curScope);

        for (int i = 1; i < factors.size(); ++i) {
            TokenKind k = operators.get(i-1).t.kind;
            switch (k) {
                case minusToken:
                    v = v.evalSubtract(factors.get(i).eval(curScope), this); break;
                case plusToken:
                    v = v.evalAdd(factors.get(i).eval(curScope), this); break;
                default:
                    Main.panic("Illegal term operator: " + k + "!");
            }
        }
        return v;
    }    
}