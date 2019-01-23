
public class Symbol {
	
   // public static enum String {STATIC, FIELD, ARG, VAR, NONE};

    private String type;
    private STKind kind;
    private int index;

    public Symbol(String type, STKind kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public STKind getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

   /** @Override
    public String toString() {
        return "Symbol{" +
                "type='" + type + '\'' +
                ", kind=" + kind +
                ", index=" + index +
                '}';
    }**/

}


