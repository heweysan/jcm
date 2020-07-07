package pentomino.flow.gui.helpers;

import java.awt.CardLayout;
import java.awt.Container;

public class FlowLayout extends CardLayout{

	/**
	 * public JPanel contentPanel;
	 */
	private static final long serialVersionUID = 1L;


	
	public void show(Container parent, ImagePanel panel, int timeout, ImagePanel pageRedirect) {

		panel.screenTimeOut = timeout;
		panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}
	
	public void show(Container parent, ImagePanel panel, long timeout, ImagePanel pageRedirect) {

		panel.screenTimeOut = timeout;
		panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}
	
	public void show(Container parent, ImagePanel panel) {
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}	

}
