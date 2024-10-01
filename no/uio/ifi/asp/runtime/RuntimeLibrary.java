// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.NoSuchElementException;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;


public class RuntimeLibrary extends RuntimeScope {
    private Scanner keyboard = new Scanner(System.in);

    public RuntimeLibrary() {

        // float
        assign("float", new RuntimeFunc("float") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    checkNumParams(actualParams, 1, "float", where);
                    return new RuntimeFloatValue(actualParams.get(0).getFloatValue("float operand", where));
                }});

        // input
        assign("input", new RuntimeFunc("input") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    checkNumParams(actualParams, 1, "input", where);
                    System.out.print(actualParams.get(0).toString());
                    System.out.flush();

                    try {
                        return new RuntimeStringValue(keyboard.nextLine());
                    } catch (NoSuchElementException e) {
                        RuntimeValue.runtimeError("Error while reading input", where);
                        return null;
                    }
                }});

        // int
        assign("int", new RuntimeFunc("int") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    checkNumParams(actualParams, 1, "int", where);
                    return new RuntimeIntValue(actualParams.get(0).getIntValue("int operand", where));
                }});
    
        // len
        assign("len", new RuntimeFunc("len") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    checkNumParams(actualParams, 1, "len", where);
                    return actualParams.get(0).evalLen(where);
                }});
        
        // print
        assign("print", new RuntimeFunc("print") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    
                    for (int i = 0; i < actualParams.size(); ++i) {
                        if (i > 0) System.out.print(" ");
                        System.out.print(actualParams.get(i).toString());
                    }
                    System.out.println();
                    return new RuntimeNoneValue();
                }});
        
        // range
        assign("range", new RuntimeFunc("range") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    checkNumParams(actualParams, 2, "range", where);

                    long start = actualParams.get(0).getIntValue("range operand", where);
                    long end = actualParams.get(1).getIntValue("range operand", where);

                    ArrayList<RuntimeValue> list = new ArrayList<>();
                    for (long i = start; i < end; i++) {
                        list.add(new RuntimeIntValue(i));
                    }

                    return new RuntimeListValue(list);
                }});
        
        // str
        assign("str", new RuntimeFunc("str") {
                @Override
                public RuntimeValue evalFuncCall(
                        ArrayList<RuntimeValue> actualParams, 
                        AspSyntax where) {
                    checkNumParams(actualParams, 1, "str", where);
                    return new RuntimeStringValue(actualParams.get(0).toString());
                }});
    }


    private void checkNumParams(ArrayList<RuntimeValue> actArgs, 
				                int nCorrect, String id, AspSyntax where) 
    {
	    if (actArgs.size() != nCorrect)
	        RuntimeValue.runtimeError("Wrong number of parameters to "+id+"!",where);
    }
}
