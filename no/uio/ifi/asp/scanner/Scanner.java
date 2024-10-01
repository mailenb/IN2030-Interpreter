// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.scanner;

import java.io.*;
import java.util.*;

import no.uio.ifi.asp.main.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class Scanner {
    private LineNumberReader sourceFile = null;
    private String curFileName;
    private ArrayList<Token> curLineTokens = new ArrayList<>();
    private Stack<Integer> indents = new Stack<>();
    private final int TABDIST = 4;


    public Scanner(String fileName) {
		curFileName = fileName;
		indents.push(0);

		try {
			sourceFile = new LineNumberReader(
					new InputStreamReader(
					new FileInputStream(fileName),
					"UTF-8"));
		} catch (IOException e) {
			scannerError("Cannot read " + fileName + "!");
		}
    }


    private void scannerError(String message) {
		String m = "Asp scanner error";
		if (curLineNum() > 0)
			m += " on line " + curLineNum();
		m += ": " + message;

		Main.error(m);
    }


    public Token curToken() {
		while (curLineTokens.isEmpty()) {
			readNextLine();
		}
		return curLineTokens.get(0);
    }


    public void readNextToken() {
		if (! curLineTokens.isEmpty())
			curLineTokens.remove(0);
    }


    private void readNextLine() {
		curLineTokens.clear();

		// Read the next line:
		String line = null;
		try {
			line = sourceFile.readLine();
            // Check if this is the last line
			if (line == null) {
                // Add the ending 'DEDENT'-symbol(s)
				for (int i : indents) {
					if (i > 0) {
						curLineTokens.add(new Token(dedentToken, curLineNum()));
					}
				}
				curLineTokens.add(new Token(eofToken, curLineNum()));
				sourceFile.close();
				sourceFile = null;
			} else {
				Main.log.noteSourceLine(curLineNum(), line);
			}
		} catch (IOException e) {
			sourceFile = null;
			scannerError("Unspecified I/O error!");
		}

		if (line != null) {
			// Ignore line if it only contains whitespace
			if (line.isBlank()) return;
			// Ignore line if it only contains a comment
			if (line.strip().charAt(0) == '#') return;
			
            // Converts all leading tabs to spaces
			line = expandLeadingTabs(line);

            // Adds the indentation tokens
			addIndentationTokens(line);

			// Finds and adds new tokens to curLineTokens
			makeTokens(line);
			
			// Terminate line:
			curLineTokens.add(new Token(newLineToken, curLineNum()));
		}

		// Logs the tokens
		for (Token t: curLineTokens) 
			Main.log.noteToken(t);
    }


    public int curLineNum() {
		return sourceFile!=null ? sourceFile.getLineNumber() : 0;
    }


    private int findIndent(String s) {
		int indent = 0;

		while (indent<s.length() && s.charAt(indent)==' ') indent++;
		return indent;
    }

    // Converts all leading tabs to spaces using the algorithm in figure 3.7
    private String expandLeadingTabs(String s) {
        int count = 0;
		String newString = "";
    
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                newString += " ";
                count++;
            }
            else if (c == '\t') {
                int toAdd = (TABDIST-(count%TABDIST));
                for (int j = 0; j < toAdd; j++ ) {
                    newString += " ";
                }
                count += toAdd;
            }
            else {
                newString += s.substring(i, s.length());
                break;
            }
        }
		return newString;
    }


    // Adds the correct amount of INDENT/DEDENT tokens based on the algorithm in figure 3.9
	private void addIndentationTokens(String line) {
		int n = findIndent(line);
		if (n > indents.peek()) {
			indents.push(n);
			curLineTokens.add(new Token(indentToken, curLineNum()));
		}

		while (n < indents.peek()) {
			indents.pop();
			curLineTokens.add(new Token(dedentToken, curLineNum()));
		}
		
		if (n != indents.peek()) {
			// throw IndentationError
			if (n < indents.peek()) {
				scannerError("IndentationError: Expected an indent block.");
			}
			else {
				scannerError("IndentationError: Unexpected indent.");
			}
		}
	}


    // Gos through the line and finds the correct tokens and adding them to curLineTokens
	private void makeTokens(String line) {
        String curWord = "";
        int i = 0;  // keeps track of where we are in the line: the index
        int len = line.length();

		while (i < len) {
            char c = line.charAt(i++); 
            curWord = "" + c;
            
            // Ignore everything after #
            if (c == '#') {                         
                break;
            }
            // Ignore whitespace
            else if (Character.isWhitespace(c)) {   
                continue;
            }
            // String literal
            else if (c == '"' || c == '\'') {       
				char quote = c;
                c = line.charAt(i++); curWord += c;

                while (c != quote) {
                    if (i >= len) scannerError("String literal not terminated!");   // Out of bounds
					c = line.charAt(i++);
                    curWord += c;
                }
				Token t = new Token(stringToken, curLineNum());
				t.stringLit = curWord.substring(1, curWord.length()-1);
				curLineTokens.add(t);
            }
            // Name or keyword
            else if (isLetterAZ(c) || c == '_') {   
                while (i < len) {
                    c = line.charAt(i);
                    if (!isLetterAZ(c) && !isDigit(c) && c != '_') {
                        break;
                    }
                    curWord += c; i++;
                }
                // Check if it's a keyword or not
                if (!isKeyword(curWord)) {
					Token t = new Token(nameToken, curLineNum());
					t.name = curWord;
					curLineTokens.add(t);
                }
            }
            // Integer or float literal
            else if (isDigit(c)) {                  
                boolean isFloat = false;
                if (c != '0' || (c == '0' && i < len && line.charAt(i) == '.')) {
                    while (i < len) {
                        c = line.charAt(i);
                        
                        if (!isDigit(c)) {
                            // Checks if it meets the requirements to be a float
                            if (c == '.' && !isFloat && i+1 < len && isDigit(line.charAt(i+1))) {
                                isFloat = true;
                            } else {
                                // the number is complete
                                break;
                            }
                        }
                        curWord += c; i++;
                    }
                }
				Token t;
				if (isFloat) {
					t = new Token(floatToken, curLineNum());
					t.floatLit = Float.parseFloat(curWord);
				}
                else {
					t = new Token(integerToken, curLineNum());
					t.integerLit = Integer.parseInt(curWord);
				}
				curLineTokens.add(t);
            }
            // Operators
            else if (isSingleOperator(c)) {
                continue;
            }
            else if (c == '=') {
                if (i < len && line.charAt(i) == '=') {
                    curWord += line.charAt(i++);
                    curLineTokens.add(new Token(doubleEqualToken, curLineNum()));
                }
                else {
                    curLineTokens.add(new Token(equalToken, curLineNum()));
                }
            }
            else if (c == '/') {
                if (i < len && line.charAt(i) == '/') {
                    curWord += line.charAt(i++);
                    curLineTokens.add(new Token(doubleSlashToken, curLineNum()));
                }
                else {
                    curLineTokens.add(new Token(slashToken, curLineNum()));
                }
            }
            else if (c == '>') {
                if (i < len && line.charAt(i) == '=') {
                    curWord += line.charAt(i++);
                    curLineTokens.add(new Token(greaterEqualToken, curLineNum()));
                }
                else {
                    curLineTokens.add(new Token(greaterToken, curLineNum()));
                }
            }
            else if (c == '<') {
                if (i < len && line.charAt(i) == '=') {
                    curWord += line.charAt(i++);
                    curLineTokens.add(new Token(lessEqualToken, curLineNum()));
                }
                else {
                    curLineTokens.add(new Token(lessToken, curLineNum()));
                }   
            }
            else if (c == '!' && i < len && line.charAt(i) == '=') {
                curWord += line.charAt(i++);
                curLineTokens.add(new Token(notEqualToken, curLineNum()));
            }
            // Delimiters:
            else if (isDelimiter(c)) {
                continue;
            }
            else {
                // The scanner couldn't match the given character
				scannerError("Illegal character: '" + c +"'!");
            }
        }
	}


    private boolean isLetterAZ(char c) {
		return ('A'<=c && c<='Z') || ('a'<=c && c<='z') || (c=='_');
    }


    private boolean isDigit(char c) {
		return '0'<=c && c<='9';
    }

    // Checks if the given string equals one of the asp keywords
	private boolean isKeyword(String s) {
        switch (s) {
            case "and":
                curLineTokens.add(new Token(andToken, curLineNum()));
                break;
            case "def":
                curLineTokens.add(new Token(defToken, curLineNum()));
                break;
            case "elif":
                curLineTokens.add(new Token(elifToken, curLineNum()));
                break;
            case "else":
                curLineTokens.add(new Token(elseToken, curLineNum()));
                break;
            case "False":
                curLineTokens.add(new Token(falseToken, curLineNum()));
                break;
            case "for":
                curLineTokens.add(new Token(forToken, curLineNum()));
                break;
            case "global":
                curLineTokens.add(new Token(globalToken, curLineNum()));
                break;
            case "if":
                curLineTokens.add(new Token(ifToken, curLineNum()));
                break;
            case "in":
                curLineTokens.add(new Token(inToken, curLineNum()));
                break;
            case "None":
                curLineTokens.add(new Token(noneToken, curLineNum()));
                break;
            case "not":
                curLineTokens.add(new Token(notToken, curLineNum()));
                break;
            case "or":
                curLineTokens.add(new Token(orToken, curLineNum()));
                break;
            case "pass":
                curLineTokens.add(new Token(passToken, curLineNum()));
                break;
            case "return":
                curLineTokens.add(new Token(returnToken, curLineNum()));
                break;
            case "True":
                curLineTokens.add(new Token(trueToken, curLineNum()));
                break;
            case "while":
                curLineTokens.add(new Token(whileToken, curLineNum()));
                break;
            default:
                return false;
        }
        return true;
    }

    // Checks if the given char equals one of the asp operators
	private boolean isSingleOperator(char c) {
        switch (c) {
            case '*':
				curLineTokens.add(new Token(astToken, curLineNum()));
                break;
            case '-':
				curLineTokens.add(new Token(minusToken, curLineNum()));
                break;
            case '%':
                curLineTokens.add(new Token(percentToken, curLineNum()));
                break;
            case '+':
                curLineTokens.add(new Token(plusToken, curLineNum()));
                break;
            default:
                return false;
        }
        return true;
    }

    // Checks if the given char equals one of the asp delimiters
	private boolean isDelimiter(char c) {
        switch (c) {
            case ':':
                curLineTokens.add(new Token(colonToken, curLineNum()));
                break;
            case ',':
				curLineTokens.add(new Token(commaToken, curLineNum()));
                break;
            case '{':
				curLineTokens.add(new Token(leftBraceToken, curLineNum()));
                break;
            case '[':
                curLineTokens.add(new Token(leftBracketToken, curLineNum()));
                break;
            case '(':
                curLineTokens.add(new Token(leftParToken, curLineNum()));
                break;
            case '}':
                curLineTokens.add(new Token(rightBraceToken, curLineNum()));
                break;
            case ']':
                curLineTokens.add(new Token(rightBracketToken, curLineNum()));
                break;
            case ')':
                curLineTokens.add(new Token(rightParToken, curLineNum()));
                break;
            case ';':
                curLineTokens.add(new Token(semicolonToken, curLineNum()));
                break;
            default:
                return false;
        }
        return true;
    }

	
    public boolean isCompOpr() {
		TokenKind k = curToken().kind;

        return (k == lessToken ||
                k == greaterToken ||
                k == doubleEqualToken ||
                k == greaterEqualToken || 
                k == lessEqualToken || 
                k == notEqualToken);
    }


    public boolean isFactorPrefix() {
		TokenKind k = curToken().kind;

		return (k == plusToken || k == minusToken);
    }


    public boolean isFactorOpr() {
		TokenKind k = curToken().kind;

		return (k == astToken ||
                k == slashToken ||
                k == percentToken ||
                k == doubleSlashToken);
    }
	

    public boolean isTermOpr() {
		TokenKind k = curToken().kind;

		return (k == plusToken || k == minusToken);
    }
    

    public boolean anyEqualToken() {
		for (Token t : curLineTokens) {
			if (t.kind == equalToken) return true;
			if (t.kind == semicolonToken) return false;
		}

		return false;
    }
}
