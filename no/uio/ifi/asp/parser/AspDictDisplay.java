package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AspDictDisplay extends AspAtom {
    ArrayList<AspStringLiteral> keys = new ArrayList<>();
    ArrayList<AspExpr> values = new ArrayList<>();

    AspDictDisplay(int n) {
        super(n);
    }

    static AspDictDisplay parse(Scanner s) {
        enterParser("dict display");
        AspDictDisplay add = new AspDictDisplay(s.curLineNum());

        skip(s, leftBraceToken);
        if (s.curToken().kind != rightBraceToken) {
            while (true) {
                add.keys.add(AspStringLiteral.parse(s));
                skip(s, colonToken);
                add.values.add(AspExpr.parse(s));

                if (s.curToken().kind != commaToken) break;
                skip(s, commaToken);
            }
        }
        skip(s, rightBraceToken);

        leaveParser("dict display");
        return add;
    }

    @Override
    void prettyPrint() {
        prettyWrite("{");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) prettyWrite(", ");
            keys.get(i).prettyPrint();
            prettyWrite(" : ");
            values.get(i).prettyPrint();
        }
        prettyWrite("}");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        HashMap<String, RuntimeValue> dict = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            dict.put(keys.get(i).eval(curScope).getStringValue("dict display operand", this), values.get(i).eval(curScope));
        }
        return new RuntimeDictValue(dict);
    }
}