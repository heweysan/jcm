package pentomino.flow.gui;

public enum PinKey {
	_1("1"),
	_2("2"),
	_3("3"),
	_4("4"),
	_5("5"),
	_6("6"),
	_7("7"),
	_8("8"),
	_9("9"),
	_0("0"),
	_Cancel("Cancel"),
	_Ok("Ok");
	
	public final String label;
	 
    private PinKey(String label) {
        this.label = label;
    }
    
    public String getDigit() 
    { 
        return this.label; 
    } 
}




/*

PIPI POPO 2

public class PinKey {
	public static final PinKey _1   = new PinKey( "happy" );
    public static final PinKey _2 = new PinKey( "annoyed" );
    public static final PinKey _3   = new PinKey( "angry" );
    
    private String _mood;
    
    public String toString() {
        return _mood;
    }
    
    private PinKey( String mood ) {
        _mood = mood;
    }
}
*/