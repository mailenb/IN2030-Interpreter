package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeListValue extends RuntimeValue {
    public ArrayList<RuntimeValue> listValue;

    public RuntimeListValue(ArrayList<RuntimeValue> v) {
	    listValue = v;
    }

    @Override
    String typeName() {
	    return "list";
    }

    @Override 
    public String toString() {
        String s = "[";
        int nPrinted = 0;
        for (RuntimeValue v : listValue) {
            if (nPrinted++ > 0) s += ", ";
            s += v.showInfo();
        } 
        s += "]";
	    return s;
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
	    return !listValue.isEmpty();
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        runtimeError("Type error for ==", where);
	    return null;
    }

    public RuntimeValue evalLen(AspSyntax where) {
        return new RuntimeIntValue(listValue.size());
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            ArrayList<RuntimeValue> newList = new ArrayList<>();
            long n = v.getIntValue("* operand", where);
            for (int i = 0; i < n; i++) newList.addAll(listValue);
            return new RuntimeListValue(newList);
        }

        runtimeError("Type error for *", where);
	    return null;
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
	    return new RuntimeBoolValue(!getBoolValue("not operand", where));
    }

    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(true);
        }
        runtimeError("Type error for !=", where);
        return null;
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        // The index has to be an int
        if (v instanceof RuntimeIntValue) {
            long i = v.getIntValue("subscription '[...]' operand", where);
            // Check if the index is out of range
            if (i >= listValue.size()) {
                runtimeError("List index " + i + " out of range!", where);
            }
            return listValue.get((int) i);
        }
        runtimeError("A list index must be an integer!", where);
        return null;
    }

    
    @Override
    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
	    if (inx instanceof RuntimeIntValue) {
            long i = inx.getIntValue("assign operand", where);
            if (i >= listValue.size()) {
                runtimeError("List index " + i + " out of range!", where);
            }
            listValue.set((int) i, val);
        }
        else {
            runtimeError("Type error: index is not an integer!", where);
        }
    }
}
