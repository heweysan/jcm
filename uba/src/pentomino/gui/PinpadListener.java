package pentomino.gui;

import javax.swing.JPanel;

public interface PinpadListener 
	{
	    
		public void pinKeyReceived( PinpadEvent event );
	    
	    public JPanel getPanel();
	}