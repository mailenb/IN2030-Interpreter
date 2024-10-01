package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspFactor extends AspSyntax {
    ArrayList<AspFactorPrefix> prefixes = new ArrayList<>();
    ArrayList<AspPrimary> primaries = new ArrayList<>();
    ArrayList<AspFactorOpr> operators = new ArrayList<>();
    
    AspFactor(int n) {
        super(n);
    }

    static AspFactor parse(Scanner s) {
        enterParser("factor");
        AspFactor af = new AspFactor(s.curLineNum());

        while(true) {
            if (s.isFactorPrefix()) {
                af.prefixes.add(AspFactorPrefix.parse(s));
            }
            else {
                af.prefixes.add(null);
            }

            af.primaries.add(AspPrimary.parse(s));

            if (!s.isFactorOpr()) break;
            af.operators.add(AspFactorOpr.parse(s));
        }

        leaveParser("factor");
        return af;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < prefixes.size(); i++) {
            if (i > 0) {
                prettyWrite(" ");
                operators.get(i-1).prettyPrint();
                prettyWrite(" ");
            }
            if (prefixes.get(i) != null) {
                prefixes.get(i).prettyPrint();
            }
            primaries.get(i).prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = primaries.get(0).eval(curScope);
        
        for (int i = 0; i < prefixes.size(); i++) {
            if (prefixes.get(i) != null) {
                TokenKind k = prefixes.get(i).t.kind;
                switch(k) {
                    case plusToken:
                        v = v.evalPositive(this); break;
                    case minusToken:
                        v = v.evalNegate(this); break;
                    default:
                        Main.panic("Illegal factor prefix: " + k + "!");
                }
            }

            if (i < operators.size()) {
                TokenKind k = operators.get(i).t.kind;
                switch(k) {
                    case astToken:
                        v = v.evalMultiply(primaries.get(i+1).eval(curScope), this);
                        break;
                    case slashToken:
                        v = v.evalDivide(primaries.get(i+1).eval(curScope), this);
                        break;
                    case percentToken:
                        v = v.evalModulo(primaries.get(i+1).eval(curScope), this);
                        break;
                    case doubleSlashToken:
                        v = v.evalIntDivide(primaries.get(i+1).eval(curScope), this);
                        break;
                    default:
                        Main.panic("Illegal factor operator: " + k + "!");
                }
            }
        }

        return v;
    }    
}