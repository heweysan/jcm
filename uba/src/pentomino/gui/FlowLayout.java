package pentomino.gui;

import java.awt.CardLayout;
import java.awt.Container;

public class FlowLayout extends CardLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void show(Container parent, ImagePanel panel, int timeout, String pageRedirect) {
		System.out.println("timeout [" + timeout + "]");		
		System.out.println("pageRedirect [" + pageRedirect + "]");		
		System.out.println("panel.getName() [" + panel.getName() + "]");
		
		panel.screenTimeOut = timeout;
		panel.panelRedirect = pageRedirect;
		super.show(parent,panel.getName());
		
	}

}
