package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspPrimary extends AspSyntax {
    AspAtom atom;
    ArrayList<AspPrimarySuffix> pSuffixes = new ArrayList<>();
    
    AspPrimary(int n) {
        super(n);
    }

    static AspPrimary parse(Scanner s) {
        enterParser("primary");
        AspPrimary ap = new AspPrimary(s.curLineNum());

        ap.atom = AspAtom.parse(s);
        while (s.curToken().kind == leftParToken || s.curToken().kind == leftBracketToken) {
            ap.pSuffixes.add(AspPrimarySuffix.parse(s));        
        }

        leaveParser("primary");
        return ap;
    }

    @Override
    void prettyPrint() {
        atom.prettyPrint();
        for (AspPrimarySuffix aps : pSuffixes) {
            aps.prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = atom.eval(curScope);
        
        for (AspPrimarySuffix aps : pSuffixes) {
            if (aps instanceof AspSubscription) {
                v = v.evalSubscription(aps.eval(curScope), this) ;
            }
            // It is a function call
            else if (aps instanceof AspArguments) {
                // Gets the list of arguments
                RuntimeListValue args = (RuntimeListValue) aps.eval(curScope);
                
                trace("Call function " + v.toString() + " with params " + args.toString());
                
                // Calls the function
                v = v.evalFuncCall(args.listValue, this);
                trace("Expression statement produced " + v.showInfo());
            }
            else {
                Main.panic("Illegal primary suffix: " + aps.getClass() + "!");
            }
        }
        return v;
    }    
}