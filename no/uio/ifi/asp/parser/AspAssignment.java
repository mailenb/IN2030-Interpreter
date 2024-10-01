package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspAssignment extends AspSmallStmt {
    AspName name;
    AspExpr expr;
    ArrayList<AspSubscription> subscriptions = new ArrayList<>();
    
    AspAssignment(int n) {
        super(n);
    }

    static AspAssignment parse(Scanner s) {
        enterParser("assignment");
        AspAssignment aa = new AspAssignment(s.curLineNum());

        aa.name = AspName.parse(s);
        
        while (true) {
            if (s.curToken().kind == equalToken) break;
            aa.subscriptions.add(AspSubscription.parse(s));
        }

        skip(s, equalToken);
        aa.expr = AspExpr.parse(s);

        leaveParser("assignment");
        return aa;
    }

    @Override
    void prettyPrint() {
        name.prettyPrint();
        for (AspSubscription as : subscriptions) {
            as.prettyPrint();
        }
        prettyWrite(" = ");
        expr.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue value = expr.eval(curScope);
        
        // Simple assignment
        if (subscriptions.isEmpty()) {  
            curScope.assign(name.t.name, value);
            trace(name.t.name + " = " + value.showInfo());
        }
        // Assignment with subscription
        else {
            RuntimeValue variable = name.eval(curScope);
            for (int i = 0; i < subscriptions.size()-1; i++) {
                variable = variable.evalSubscription(subscriptions.get(i).eval(curScope), this);
            }
            
            RuntimeValue index = subscriptions.get(subscriptions.size()-1).eval(curScope);

            variable.evalAssignElem(index, value, this);
            trace(name.t.name + "[" + index.showInfo() + "]" + " = " + value.showInfo());
        } 
        return null;
    }
}