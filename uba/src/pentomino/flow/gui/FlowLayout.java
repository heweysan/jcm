package pentomino.flow.gui;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JPanel;

public class FlowLayout extends CardLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void show(Container parent, ImagePanel panel, int timeout, String pageRedirect) {
		System.out.println("timeout [" + timeout + "] pageRedirect [" + pageRedirect + "] panel.getName() [" + panel.getName() + "]");		
				
		panel.screenTimeOut = timeout;
		panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());		
	}

	public void show(Container parent, JPanel panel, int timeout, String pageRedirect) {
		System.out.println("timeout [" + timeout + "] pageRedirect [" + pageRedirect + "] panel.getName() [" + panel.getName() + "]");		
				
		//panel.screenTimeOut = timeout;
		//panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());		
	}
	
	public void show(Container parent, ImagePanel panel) {
		System.out.println("panel.getName() [" + panel.getName() + "]");
		
		super.show(parent,panel.getName());		
	}
}
