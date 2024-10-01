package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeStringValue extends RuntimeValue {
    String strValue;

    public RuntimeStringValue(String v) {
	    strValue = v;
    }

    @Override
    String typeName() {
	    return "String";
    }

    @Override
    public String showInfo() {
        if (strValue.indexOf('\'') >= 0)
            return '"' + strValue + '"';
        else
            return "'" + strValue + "'";
    }

    @Override 
    public String toString() {
	    return strValue;
    }

    @Override
    public String getStringValue(String what, AspSyntax where) {
	    return strValue;
    }

    @Override
    public long getIntValue(String what, AspSyntax where) {
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            runtimeError("String " + strValue + " is not a legal int", where);
            return 0;
        }
    }

    @Override
    public double getFloatValue(String what, AspSyntax where) {
        try {
            return Double.parseDouble(strValue);
        } catch (NumberFormatException e) {
            runtimeError("String " + strValue + " is not a legal float", where);
            return 0;
        }
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
	    return strValue != "";
    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeStringValue(strValue + v.getStringValue("+ operand", where));
        }
        
        runtimeError("Type error for +", where);
        return null;
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(strValue.equals(v.getStringValue("== operand", where)));
        }
        else if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }

        runtimeError("Type error for ==", where);
        return null;
    }

    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(strValue.compareTo(v.getStringValue("> operand", where)) > 0);
        }
        
        runtimeError("Type error for >", where);
        return null;
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(strValue.compareTo(v.getStringValue(">= operand", where)) >= 0);
        }
        
        runtimeError("Type error for >=", where);
        return null;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) {
        return new RuntimeIntValue(strValue.length());
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(strValue.compareTo(v.getStringValue("< operand", where)) < 0);
        }
        
        runtimeError("Type error for <", where);
        return null;
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(strValue.compareTo(v.getStringValue("<= operand", where)) <= 0);
        }
        
        runtimeError("Type error for <=", where);
        return null;
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            return new RuntimeStringValue(strValue.repeat((int) v.getIntValue("* operand", where)));
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
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(!strValue.equals(v.getStringValue("!= operand", where)));
        }
        else if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(true);
        }

        runtimeError("Type error for !=", where);
        return null;
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntValue) {
            long i = v.getIntValue("subscription '[...]' operand", where);
            if (i >= strValue.length()) {
                runtimeError("String index " + i + " out of range!", where);
            }
            return new RuntimeStringValue("" + strValue.charAt((int) i));
        }

        runtimeError("A string index must be an integer!", where);
        return null;
    }
}