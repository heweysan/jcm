package pentomino.flow.gui;

import java.awt.CardLayout;
import java.awt.Container;

public class FlowLayout extends CardLayout{

	/**
	 * public JPanel contentPanel;
	 */
	private static final long serialVersionUID = 1L;


	
	public void show(Container parent, ImagePanel panel, int timeout, ImagePanel pageRedirect) {
		//System.out.println("SHOW VER 1 con time out");
		//System.out.println("timeout [" + timeout + "] pageRedirect [" + pageRedirect + "] panel.getName() [" + panel.getName() + "]");		
				
		panel.screenTimeOut = timeout;
		panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}
	
	public void show(Container parent, ImagePanel panel) {
		//System.out.println("SHOW VER 2 sin time out panel.getName() [" + panel.getName() + "]");
		

		super.show(parent,panel.getName());
		parent.setVisible(true);
	}	

}
