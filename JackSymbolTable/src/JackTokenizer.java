import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;


public class JackTokenizer {

	
	private PushbackReader reader;
	private boolean hasMoreTokens;
	private char ch;
	private TokenType tokenType;
	private KeyWord keyWord;
	private char symbol;
	private String identifier;


	public JackTokenizer(File src) {
		hasMoreTokens = true;

		try {
			reader = new PushbackReader(new FileReader(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}

	public boolean hasMoreTokens() {
		return hasMoreTokens;
	}
	
	public TokenType tokenType(){
		return tokenType;
	}
	
	public KeyWord keyWord(){
		return keyWord;
	}
	
	public char symbol(){
		return symbol;
	}
	
	public String identifier(){
		return identifier;
	}
	
	public int intVal(){
		try{
			return Integer.parseInt(identifier);
		}catch (NumberFormatException e){
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}
	
	// Return string without the enclosing quotes.
	public String stringVal(){
		return identifier;
	}
	
	private void getNextChar(){
		try{
			int data = reader.read();
			if (data == -1)
				hasMoreTokens = false;
			else
				ch = (char)data;
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void unread(){
		try{
			reader.unread(ch);
		} catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
		

	public void advance() {
		// Complete to advance over next token, setting values in the fields
		//use getnextChar() here. Read a character (ch) and it returns a letter
		//if(hasMoreTokens()) 
		identifier ="";
		this.getNextChar();
		char[] symbolRegister = new char [] {'{','}','(',')','[',']','.',',',';','+','-','*','&','|','<','>','=','-','~'};
		State state = State.START;
		while(hasMoreTokens) {
			switch (state) {
			case START:
				if(Character.isDigit(ch)) {
					identifier += ch;
					state = State.NUMBER;
				}else if (ch == '"'){
					state = State.STRING;
				}else if(Character.isLetter(ch)) {
					identifier += ch;
					state = State.IN_ID;
				}else if(ch =='/') {
					state = State.SLASH;
				}for(int i =0;i<=18;i++) {
					if(ch == symbolRegister[i]) {
						symbol = ch;
						tokenType=TokenType.SYMBOL;
						return;
					}
				}
				break;
			case NUMBER:
				if(!(Character.isDigit(ch))) {
					this.unread();
					tokenType = TokenType.INT_CONST;	
					return;
				}else {
					identifier += ch;
					state = State.NUMBER;
				}
				break;
			case STRING:
				if(ch=='"') {
					tokenType = TokenType.STRING_CONST;
					return;
				}else {
					identifier += ch;
					state = State.STRING;
				}
				break;
			case IN_ID:
				if(Character.isDigit(ch) || Character.isLetter(ch) || ch =='_') {
					identifier += ch;
					state = State.IN_ID;
				}else {
					this.unread();
					state = State.ID;
				}
				break;
			case ID:
						switch(identifier) {
						case "class":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord = KeyWord.CLASS;
							return;
						case "method":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord = KeyWord.METHOD;
							return;
						case "function":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.FUNCTION;
							return;
						case "constructor":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.CONSTRUCTOR;
							return;
						case "int":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.INT;
							return;
						case "boolean":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.BOOLEAN;
							return;
						case "char":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.CHAR;
							return;
						case "void":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.VOID;
							return;
						case "var":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.VAR;
							return;
						case "static":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.STATIC;
							return;
						case "field":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.FIELD;
							return;
						case "let":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.LET;
							return;
						case "do":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.DO;
							return;
						case "if":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.IF;
							return;
						case "else":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.ELSE;
							return;
						case "while":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.WHILE;
							return;
						case "return":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.RETURN;
							return;
						case "true":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.TRUE;
							return;
						case "false":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.FALSE;
							return;
						case "null":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.NULL;
							return;
						case "this":
							this.unread();
							tokenType = TokenType.KEYWORD;
							keyWord=KeyWord.THIS;
							return;
						default:
							this.unread();
							tokenType = TokenType.IDENTIFIER;
							break;
						}
				return;
			case SLASH:
				if(ch == '*' ) {
					state=State.IN_BLOCK;	
				}else if (ch =='/'){
					state=State.LINE_COMMENT;
				}else {
					this.unread();
					symbol = '/';
					tokenType = TokenType.SYMBOL;
					return;
				}
			break;
			case LINE_COMMENT:
				if(ch != '\n') {
					state = State.LINE_COMMENT;
				}else {
					state=State.START;
				}
			break;
			case IN_BLOCK:
				
				if (ch != '*') {
					state=State.IN_BLOCK;
				}else if(ch =='*') {
					state = State.STAR_SLASH;
				}
				break;
			case STAR_SLASH:
				if (ch =='/') {
					state=State.START;
				}else {
					state=State.IN_BLOCK;
				}
				break;
			default:
				break;
			}
			this.getNextChar();
		}
		
	}
	
}
