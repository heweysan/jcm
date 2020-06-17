package pentomino.flow.gui;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JPanel;

public class FlowLayout extends CardLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void show(Container parent, ImagePanelOld panel, int timeout, String pageRedirect) {
		System.out.println("timeout [" + timeout + "] pageRedirect [" + pageRedirect + "] panel.getName() [" + panel.getName() + "]");		
				
		panel.screenTimeOut = timeout;
		panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}

	public void show(Container parent, JPanel panel, int timeout, String pageRedirect) {
		System.out.println("timeout [" + timeout + "] pageRedirect [" + pageRedirect + "] panel.getName() [" + panel.getName() + "]");		
				
		//panel.screenTimeOut = timeout;
		//panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}
	
	public void show(Container parent, JPanel panel) {
		System.out.println("panel.getName() [" + panel.getName() + "]");		
				
		//panel.screenTimeOut = timeout;
		//panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		parent.setVisible(true);
	}
	
	public void show(Container parent, ImagePanelOld panel) {
		System.out.println("panel.getName() [" + panel.getName() + "]");
		
		super.show(parent,panel.getName());	
		parent.setVisible(true);
	}
}
