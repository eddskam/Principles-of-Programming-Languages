import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Hashtable;


public class CompilationEngine {
	
	JackTokenizer tokenizer = null;
	XMLWriter xmlWriter = null;
	SymbolTable symbolTable = null;
	STKind kind;
	String typeVar;
	KeyWord key;
	String name;
	String termIdentifier;
	
	
	
	public CompilationEngine(String filename, File file){
	    tokenizer = new JackTokenizer(file);
	    xmlWriter = new XMLWriter(filename);
	    symbolTable = new SymbolTable();
	}
	
	public void close(){
		try{
			xmlWriter.close();
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	private void symbolInfo(String use, String identifier){
		xmlWriter.writeCode(use+" "+identifier+ " type:"+symbolTable.typeOf(identifier)+" kind:"+
				symbolTable.kindOf(identifier)+ " index:"+symbolTable.indexOf(identifier)+'\n');
	}
	
	/* REPLACE METHODS BELOW WITH YOUR CODE FROM PROJECT 2 AND THEN EDIT FOR PROJECT 3.
	 *  USE symbolInfo TO PRINT OUT INFO FOR DEFINING OR USING VARIABLES IN SYMBOL TABLE*/
	
	public void compileClass(){
		xmlWriter.writeCode("<class>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<keyword> " + tokenizer.identifier() +" </keyword> \n");
		tokenizer.advance();
		xmlWriter.writeCode("<identifier> "+tokenizer.identifier() + " </identifier>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		while(tokenizer.keyWord()==KeyWord.STATIC || tokenizer.keyWord()==KeyWord.FIELD) {
			this.compileClassVarDec();
		}
		while(tokenizer.keyWord()==KeyWord.CONSTRUCTOR || tokenizer.keyWord()==KeyWord.FUNCTION 
		||tokenizer.keyWord()==KeyWord.METHOD || tokenizer.keyWord()==KeyWord.VOID 
		||tokenizer.tokenType()==TokenType.IDENTIFIER) {
			this.compileSubroutineDec();
		}
		xmlWriter.writeCode("<symbol> } </symbol> \n");
		tokenizer.advance();
		xmlWriter.writeCode("</class>\n");
		close();
	}
	public void compileClassVarDec(){
		// COMPLETE THIS CODE
		xmlWriter.writeCode("<classVarDec>\n");
		if (tokenizer.keyWord()==KeyWord.STATIC) {
			xmlWriter.writeCode("<keyword> static </keyword> \n");
			kind =STKind.STATIC;
			key = tokenizer.keyWord();
			tokenizer.advance();
		}else {
			xmlWriter.writeCode("<keyword> field </keyword> \n");
			key = tokenizer.keyWord();
			kind =STKind.FIELD;
			tokenizer.advance();
		}
		this.compileType();
		symbolTable.define(tokenizer.identifier(), typeVar, kind);
		String x = tokenizer.identifier();
		xmlWriter.writeCode("Define "+x+" type: "+symbolTable.typeOf(x)+" kind: "+symbolTable.kindOf(x)+ " index: "+symbolTable.indexOf(x)+"\n");
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() +" </identifier> \n");
		tokenizer.advance();
		this.compileVarNameList();
		xmlWriter.writeCode("<symbol> ; </symbol> \n");
		tokenizer.advance();
		xmlWriter.writeCode("</classVarDec>\n");
	}
	
	public void compileType(){
			// COMPLETE THIS CODE
		String isType = tokenizer.identifier();
		switch (isType) {
		case "int":
			xmlWriter.writeCode("<keyword> int </keyword> \n");
			typeVar = "int";
			tokenizer.advance();
			break;
		case "char":
			xmlWriter.writeCode("<keyword> char </keyword>\n");
			typeVar = "char";
			tokenizer.advance();
			break;
		case "boolean":
			xmlWriter.writeCode("<keyword> boolean </keyword>\n");
			typeVar = "boolean";
			tokenizer.advance();
			break;
		default:
			xmlWriter.writeCode("<identifier> "+tokenizer.identifier()+ " </identifier>\n");
			typeVar = tokenizer.identifier();
			tokenizer.advance();
			break;
		}
	}
	
	public void compileVarNameList(){
		// COMPLETE THIS CODE
		while(tokenizer.symbol()==',') {
			tokenizer.advance();
			symbolTable.define(tokenizer.identifier(), typeVar, kind);
			xmlWriter.writeCode("<symbol> , </symbol> \n");
			this.symbolInfo("Define", tokenizer.identifier());
			xmlWriter.writeCode("<identifier> "+tokenizer.identifier()+ " </identifier>\n");
			tokenizer.advance();
	}
	}
	
	public void compileSubroutineDec(){
		// COMPLETE THIS CODE
		symbolTable.startSubroutine();
		xmlWriter.writeCode("<subroutineDec>\n");
		if(tokenizer.identifier().equals("constructor")) {
			xmlWriter.writeCode("<keyword> constructor </keyword>\n");
			tokenizer.advance();
		}
		if(tokenizer.identifier().equals("function")) {
			xmlWriter.writeCode("<keyword> function </keyword>\n");
			tokenizer.advance();
		}
		if(tokenizer.identifier().equals("method")) {
			xmlWriter.writeCode("<keyword> method </keyword>\n");
			symbolTable.define("this", typeVar, STKind.ARG);
			tokenizer.advance();
		}
		if(tokenizer.identifier().equals("void")) {
			xmlWriter.writeCode("<keyword> void </keyword>\n");
			tokenizer.advance();
		}
		else {
			this.compileType();
		}
		String x;
		if(tokenizer.identifier()=="bouncingDirection")  x = "fish";
		xmlWriter.writeCode("<identifier> "+tokenizer.identifier()+ " </identifier>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> ( </symbol>\n");
		tokenizer.advance();
		this.compileParameterList();
		xmlWriter.writeCode("<symbol> ) </symbol>\n");
		tokenizer.advance();
		this.compileSubroutineBody();
		xmlWriter.writeCode("</subroutineDec> \n");
	}

	public void compileParameterList(){
		xmlWriter.writeCode("<parameterList>\n");
		// COMPLETE THIS CODE
	       //check if there is parameterList, if next token is ')' than go back
		if(tokenizer.symbol()!= ')') {
			this.compileType();
			if(key == KeyWord.VAR) kind = STKind.VAR;
			else kind = STKind.ARG;
			symbolTable.define(tokenizer.identifier(), typeVar, kind);
			this.symbolInfo("Define", tokenizer.identifier());
			xmlWriter.writeCode("<identifier> "+tokenizer.identifier()+" </identifier>\n");
			tokenizer.advance();
			while(tokenizer.symbol()==',') {
				tokenizer.advance();
				xmlWriter.writeCode("<symbol> , </symbol> \n");
				this.compileType();
				if(key == KeyWord.VAR) kind = STKind.VAR;
				else kind = STKind.ARG;
				symbolTable.define(tokenizer.identifier(), typeVar, kind);
				this.symbolInfo("Define", tokenizer.identifier());
				xmlWriter.writeCode("<identifier> "+tokenizer.identifier()+ " </identifier>\n");
				tokenizer.advance();
			}
		}
		xmlWriter.writeCode("</parameterList>\n");
	}
	public void compileSubroutineBody(){
		xmlWriter.writeCode("<subroutineBody>\n");
		// COMPLETE THIS CODE
		 
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		while(tokenizer.keyWord()==KeyWord.VAR) {
			this.compileVarDec();
		}
		this.compileStatements();
		xmlWriter.writeCode("<symbol> } </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</subroutineBody>\n");
		

	}
	
	public void compileVarDec(){
		// COMPLETE THIS CODE
		//determine if there is a varDec

       
		xmlWriter.writeCode("<varDec> \n");
		xmlWriter.writeCode("<keyword> var </keyword>\n");
		kind = STKind.VAR;
		tokenizer.advance();
		this.compileType();
		symbolTable.define(tokenizer.identifier(), typeVar, kind);
		this.symbolInfo("Define", tokenizer.identifier());
		xmlWriter.writeCode("<identifier> " + tokenizer.identifier() +" </identifier> \n");
		tokenizer.advance();
		this.compileVarNameList();
		xmlWriter.writeCode("<symbol> ; </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</varDec> \n");

	}
	
	public void compileStatements(){
		xmlWriter.writeCode("<statements>\n");
		// COMPLETE THIS CODE
		 //determine whether there is a statement next can be a '}'
		while(tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord()==KeyWord.LET||tokenizer.keyWord()==KeyWord.IF||tokenizer.keyWord()==KeyWord.WHILE
				|| tokenizer.keyWord()==KeyWord.DO ||tokenizer.keyWord()==KeyWord.RETURN)) {
		if (tokenizer.keyWord()== KeyWord.LET) {
			this.compileLetStatement();
		}else if (tokenizer.keyWord()==KeyWord.IF) {
			this.compileIfStatement();
		}else if (tokenizer.keyWord()==KeyWord.WHILE) {
			this.compileWhileStatement();
		}else if(tokenizer.keyWord()==KeyWord.DO) {
			this.compileDoStatement();
		}else if(tokenizer.keyWord()==KeyWord.RETURN) {
			this.compileReturnStatement();
		}
		}
		
		xmlWriter.writeCode("</statements>\n");
		
	}
	
	public void compileLetStatement(){
		xmlWriter.writeCode("<letStatement>\n");
		// COMPLETE THIS CODE
		xmlWriter.writeCode("<keyword> let </keyword> \n");
		tokenizer.advance();
		xmlWriter.writeCode("<identifier> "+tokenizer.identifier() +" </identifier>\n");
		this.symbolInfo("Use", tokenizer.identifier());
		tokenizer.advance();
		if (tokenizer.symbol()=='[') {
			xmlWriter.writeCode("<symbol> [ </symbol> \n");
			tokenizer.advance();
			this.compileExpression();
			xmlWriter.writeCode("<symbol> ] </symbol> \n");
			tokenizer.advance();
		}
		xmlWriter.writeCode("<symbol> = </symbol> \n");
		tokenizer.advance();
		this.compileExpression();
		xmlWriter.writeCode("<symbol> ; </symbol>\n"); 
		tokenizer.advance();
		xmlWriter.writeCode("</letStatement>\n");
	}
	public void compileIfStatement(){
		xmlWriter.writeCode("<ifStatement>\n");
		xmlWriter.writeCode("<keyword> if </keyword>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> ( </symbol>\n");
		tokenizer.advance();
		this.compileExpression();
		xmlWriter.writeCode("<symbol> ) </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		this.compileStatements();
		xmlWriter.writeCode("<symbol> } </symbol>\n");
		tokenizer.advance();

	        //check if there is 'else'
		if(tokenizer.keyWord()==KeyWord.ELSE) {
			xmlWriter.writeCode("<keyword> else </keyword>\n");
			tokenizer.advance();
			xmlWriter.writeCode("<symbol> { </symbol>\n");
			tokenizer.advance();
			this.compileStatements();
			xmlWriter.writeCode("<symbol> } </symbol>\n");
			tokenizer.advance();
		}
		xmlWriter.writeCode("</ifStatement>\n");
	}
	
	public void compileWhileStatement(){
		xmlWriter.writeCode("<whileStatement>\n");
		xmlWriter.writeCode("<keyword> while </keyword>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> ( </symbol>\n");
		tokenizer.advance();
		this.compileExpression();
		xmlWriter.writeCode("<symbol> ) </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("<symbol> { </symbol>\n");
		tokenizer.advance();
		this.compileStatements();
		xmlWriter.writeCode("<symbol> } </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</whileStatement>\n");
	}
	
	public void compileDoStatement(){
		xmlWriter.writeCode("<doStatement>\n");
		xmlWriter.writeCode("<keyword> do </keyword>\n");
		tokenizer.advance();
		this.compileSubroutineCall();
		xmlWriter.writeCode("<symbol> ; </symbol>\n");
		tokenizer.advance();
		xmlWriter.writeCode("</doStatement>\n");
	}
	
	public void compileReturnStatement(){
		xmlWriter.writeCode("<returnStatement>\n");
		// COMPLETE THIS CODE
		 xmlWriter.writeCode("<keyword> return </keyword>\n");
	        
	        //check if there is any expression
	        tokenizer.advance();
	        //no expression
	        if(tokenizer.symbol()==';'&& tokenizer.tokenType()==TokenType.SYMBOL) {
				xmlWriter.writeCode("<symbol> ; </symbol>\n");
				tokenizer.advance();
			}else {
				this.compileExpression();
				xmlWriter.writeCode("<symbol> ; </symbol>\n");
				tokenizer.advance();
			}
			xmlWriter.writeCode("</returnStatement>\n");
		}
	
	public void compileExpression(){
		xmlWriter.writeCode("<expression>\n");
		// COMPLETE THIS CODE
		//term
		this.compileTerm();
		while( tokenizer.symbol()=='+'|| tokenizer.symbol()=='-'||tokenizer.symbol()=='*'
				||tokenizer.symbol()=='/'||tokenizer.symbol()=='&'||tokenizer.symbol()=='|'
				||tokenizer.symbol()=='<'|| tokenizer.symbol()=='>'|| tokenizer.symbol()=='=') {
			if(tokenizer.symbol()=='<') {
				xmlWriter.writeCode("<symbol> &lt; </symbol> \n");
				tokenizer.advance();
				this.compileTerm();
			}else if(tokenizer.symbol()=='>'){
				xmlWriter.writeCode("<symbol> &gt; </symbol> \n");
				tokenizer.advance();
				this.compileTerm();
			}else if(tokenizer.symbol()=='&') {
				xmlWriter.writeCode("<symbol> &amp; </symbol> \n");
				tokenizer.advance();
				this.compileTerm();
			}
			else {
			xmlWriter.writeCode("<symbol> "+ tokenizer.symbol()+" </symbol>\n");
			tokenizer.advance();
			this.compileTerm();
			}
		}
		xmlWriter.writeCode("</expression>\n");
		}
	
	
	public void compileTerm(){
		xmlWriter.writeCode("<term>\n");
		if(tokenizer.tokenType()==TokenType.INT_CONST) {
			xmlWriter.writeCode("<integerConstant> "+tokenizer.identifier()+" </integerConstant>\n");
			tokenizer.advance();
		}else if(tokenizer.tokenType()==TokenType.STRING_CONST) {
			xmlWriter.writeCode("<stringConstant> "+tokenizer.identifier()+" </stringConstant>\n");
			tokenizer.advance();
		}else if(tokenizer.keyWord()==KeyWord.TRUE ||tokenizer.keyWord()==KeyWord.FALSE||
				tokenizer.keyWord()==KeyWord.NULL|| tokenizer.keyWord()==KeyWord.THIS) {
			xmlWriter.writeCode("<keyword> "+ tokenizer.identifier()+" </keyword>\n");
			tokenizer.advance();
		}else if(tokenizer.tokenType()==TokenType.SYMBOL && tokenizer.symbol()=='-'||tokenizer.tokenType()==TokenType.SYMBOL && tokenizer.symbol()=='~') {
			xmlWriter.writeCode("<symbol> "+ tokenizer.symbol() + " </symbol>\n");
			tokenizer.advance();
			this.compileTerm();
		}else if(tokenizer.tokenType()==TokenType.SYMBOL&& tokenizer.symbol()=='(') {
			xmlWriter.writeCode("<symbol> ( </symbol> \n");
			tokenizer.advance();
			this.compileExpression();
			xmlWriter.writeCode("<symbol> ) </symbol> \n");
			tokenizer.advance();
		}else if(tokenizer.tokenType()==TokenType.IDENTIFIER) {
			xmlWriter.writeCode("<identifier> "+ tokenizer.identifier() + " </identifier>\n");
			if (symbolTable.indexOf(tokenizer.identifier())!=-1) {
				this.symbolInfo("Use",tokenizer.identifier());
			}
			tokenizer.advance();
			if(tokenizer.symbol()=='('||tokenizer.symbol()=='.') {
				this.compileSubroutineCall();
			}else if (tokenizer.symbol()=='[') {
				xmlWriter.writeCode("<symbol> [ </symbol>\n");
				tokenizer.advance();
				this.compileExpression();
				xmlWriter.writeCode("<symbol> ] </symbol>\n");
				tokenizer.advance();
			}
		}
		xmlWriter.writeCode("</term>\n");
	}
	
	public void compileSubroutineCall(){
		// COMPLETE THIS CODE
		if(tokenizer.tokenType()==TokenType.IDENTIFIER) {
			xmlWriter.writeCode("<identifier> "+ tokenizer.identifier() + " </identifier>\n");
			if (symbolTable.indexOf(tokenizer.identifier())!=-1) {
				this.symbolInfo("Use",tokenizer.identifier());
			}
			tokenizer.advance();
			}
		if(tokenizer.symbol()=='(') { 
			xmlWriter.writeCode("<symbol> ( </symbol> \n");
			tokenizer.advance();
			this.compileExpressionList();
			xmlWriter.writeCode("<symbol> ) </symbol> \n");
			tokenizer.advance();
		}else {
			xmlWriter.writeCode("<symbol> . </symbol> \n");
			tokenizer.advance();
			xmlWriter.writeCode("<identifier> "+ tokenizer.identifier() + " </identifier>\n");
			tokenizer.advance();
			xmlWriter.writeCode("<symbol> ( </symbol> \n");
			tokenizer.advance();
			this.compileExpressionList();
			xmlWriter.writeCode("<symbol> ) </symbol> \n");
			tokenizer.advance();
		}
	}
	public void compileExpressionList(){
		xmlWriter.writeCode("<expressionList>\n");
		TokenType x = tokenizer.tokenType();
		if(x == TokenType.INT_CONST || x==TokenType.STRING_CONST || x==TokenType.KEYWORD || x==TokenType.IDENTIFIER
		|| tokenizer.symbol()=='(' || tokenizer.symbol()=='-'|| tokenizer.symbol()=='~') {
			this.compileExpression();
			while(tokenizer.symbol()==',') {
				xmlWriter.writeCode("<symbol> , </symbol> \n");
				tokenizer.advance();
				this.compileExpression();
			}
		}
		xmlWriter.writeCode("</expressionList>\n");
	}

	
    /**
     * throw an exception to report errors
     * @param val
     */
    private void error(String val){
        //throw new IllegalStateException("Expected token missing : " + val + " Current token:" + tokenizer.tokenType());
    }

   
    
}
	