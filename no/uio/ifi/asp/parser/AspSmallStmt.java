package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

abstract class AspSmallStmt extends AspSyntax {
    
    AspSmallStmt(int n) {
        super(n);
    }

    static AspSmallStmt parse(Scanner s) {
        enterParser("small stmt");
        AspSmallStmt ass = null;

        /* assignment - name
                Assignment og expr kan begge starter med name
                - men bare assignment kan ha '=' --> Scanner.anyEqualToken()
            expr stmt - expr - .. | name
            global stmt - global-token
            pass stmt - pass-token
            return stmt - return-token
        */

        if (s.anyEqualToken()) {
            ass = AspAssignment.parse(s);
        }
        else {
            switch (s.curToken().kind) {
                case globalToken:
                    ass = AspGlobalStmt.parse(s);
                    break;
                case passToken:
                    ass = AspPassStmt.parse(s);
                    break;
                case returnToken:
                    ass = AspReturnStmt.parse(s);
                    break;
                default:
                    ass = AspExprStmt.parse(s);
                    break;
            }
        } 

        leaveParser("small stmt");
        return ass;
    }
}