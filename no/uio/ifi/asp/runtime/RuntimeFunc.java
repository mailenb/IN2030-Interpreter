package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspFuncDef;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFunc extends RuntimeValue {
    AspFuncDef def;         // reference to the definition in the syntax tree
    RuntimeScope defScope;  // reference to the scope it was declared in
    String name;            // the name of the function

    public RuntimeFunc (AspFuncDef d, RuntimeScope s, String n) {
        def = d;
        defScope = s;
        name = n;
    }

    public RuntimeFunc(String n) {
        name = n;
    }
    
    @Override
    public String typeName() {
        return "func";
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {        
        // Check if the func call has the right amount of parameters
        if (def.args.size() != actualParams.size()) {
            runtimeError("Wrong number of parameters calling " + name + "!", where);
        }

        // Creates the new scope
        RuntimeScope newScope = new RuntimeScope(defScope);
        // Initializes the parameters
        for (int i = 0; i < actualParams.size(); i++) {
            // Assigns the actual parameters to the formal
            newScope.assign(def.args.get(i).t.name, actualParams.get(i));
        }

        // Obtains any possible return call
        try {
            def.body.eval(newScope);
        } catch (RuntimeReturnValue rrv) {
            return rrv.value;
        }

        // Return None if the function has no return statement
        return new RuntimeNoneValue();
    }
}
