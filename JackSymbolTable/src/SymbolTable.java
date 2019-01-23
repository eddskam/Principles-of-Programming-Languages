import java.util.Hashtable;
import java.util.HashMap; //new
import java.util.Map; //new


public class SymbolTable {

/* Add fields as needed */
	private HashMap<String,Symbol> classSymbols;//for STATIC, FIELD
    private HashMap<String,Symbol> subroutineSymbols;//for ARG, VAR
    private HashMap<STKind,Integer> indices;

	
	public SymbolTable(){
		/* COMPLETE CODE */
		classSymbols = new HashMap<String, Symbol>();
        subroutineSymbols = new HashMap<String, Symbol>();

        indices = new HashMap<STKind, Integer>();
        indices.put(STKind.ARG,0);
        indices.put(STKind.FIELD,0);
        indices.put(STKind.STATIC,0);
        indices.put(STKind.VAR,0);
	}
	
	public void startSubroutine(){
		/* COMPLETE CODE */
		  subroutineSymbols.clear();
	        indices.put(STKind.VAR,0);
	        indices.put(STKind.ARG,0);
	}
	
	 /**
     * Defines a new identifier of a given name,type and kind
     * and assigns it a running index, STATIC and FIELD identifiers
     * have a class scope, while ARG and VAR identifiers have a subroutine scope
     * @param name
     * @param type
     * @param kind
     */
	
	public void define(String name, String type, STKind kind){
		/* COMPLETE CODE */
		   if (kind == STKind.ARG || kind == STKind.VAR){

	            int index = indices.get(kind);
	            Symbol symbol = new Symbol(type, kind,index);
	            indices.put(kind,index+1);
	            subroutineSymbols.put(name,symbol);

	        }else if(kind == STKind.STATIC || kind == STKind.FIELD){

	            int index = indices.get(kind);
	            Symbol symbol = new Symbol(type,kind,index);
	            indices.put(kind,index+1);
	            classSymbols.put(name,symbol);

	        }
		
	}
	
	public int varCount(STKind kind){
		/* COMPLETE CODE */
		//return indices.get(kind);
		return -1;
	}
	
	public STKind kindOf(String name){
		/* COMPLETE CODE */
		 Symbol symbol = lookUp(name);

	        if (symbol != null) return symbol.getKind();
	        else return null;
	}
	
	public String typeOf(String name){
		/* COMPLETE CODE */
		Symbol symbol = lookUp(name);
		if (symbol != null) return symbol.getType();

        return "";
//if(subroutineSymbols.containsKey(name)) return subroutineSymbols.get(name).getType();
//if(classSymbols.containsKey(name)) return classSymbols.get(name).getType();
//else return "";
	}
	
	
  
	
	public int indexOf(String name){
		/* COMPLETE CODE */
		Symbol symbol = lookUp(name);

        if (symbol != null) return symbol.getIndex();
		return -1;
	}
		
		
		/**
	     * check if target symbol is exist
	     * @param name
	     * @return
	     */
	    private Symbol lookUp(String name){

	        if (classSymbols.get(name) != null){
	            return classSymbols.get(name);
	        }else if (subroutineSymbols.get(name) != null){
	            return subroutineSymbols.get(name);
	        }else {
	            return null;
	        }
	        
	        
	}
}
