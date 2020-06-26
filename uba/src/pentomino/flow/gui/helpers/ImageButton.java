package pentomino.flow.gui.helpers;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class ImageButton extends JButton {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageButton(String img) {
		
		super(new ImageIcon(img));
		
		setOpaque(false);				
		setContentAreaFilled(false);
		setBorderPainted(false);
	}

}
