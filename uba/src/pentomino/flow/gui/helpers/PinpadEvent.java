package pentomino.flow.gui.helpers;
import java.util.EventObject;

public class PinpadEvent extends EventObject {
	
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	//private PinKey _mood;
	private PinKey _key;
	    
	//public PinpadEvent( Object source, PinKey mood) {
	public PinpadEvent( Object source, PinKey key) {
		super( source );
	       _key = key;
	    }
	
		/*
	    public PinKey mood() {
	        return _mood;
	    }
		 */
	    public PinKey key() {
	        return _key;
	    }
		
}
