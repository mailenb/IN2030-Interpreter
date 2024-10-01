package no.uio.ifi.asp.runtime;

import java.util.HashMap;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeDictValue extends RuntimeValue {
    HashMap<String, RuntimeValue> dictValue;

    public RuntimeDictValue(HashMap<String, RuntimeValue> v) {
	    dictValue = v;
    }

    @Override
    String typeName() {
	    return "dict";
    }

    @Override 
    public String toString() {
        String s = "{";

        int nPrinted = 0;
        for (String k : dictValue.keySet()) {
            s += "'" + k + "': " + dictValue.get(k).showInfo();
            if (nPrinted++ < dictValue.size()-1) {
                s += ", ";
            }
        }
        
        s += "}";
	    return s;
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
	    return !dictValue.isEmpty();
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
        return new RuntimeIntValue(dictValue.size());
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
        if (v instanceof RuntimeStringValue) {
            String s = v.getStringValue("subscription '[...]' operand", where);
            if (!dictValue.containsKey(s)) {
                runtimeError("Dictionary key '" + s + "' undefined!", where);
            }
            return dictValue.get(s);
        }

        runtimeError("A dictionary key must be a text string!", where);
        return null;
    }

    @Override
    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
	    if (inx instanceof RuntimeStringValue) {            
            dictValue.put(inx.getStringValue("assign operand", where), val);
        }
        else {
            runtimeError("Type error: index is not a text string!", where);
        }
    }
}