package pentomino.flow.gui.admin;

import pentomino.flow.gui.helpers.ImagePanel;

public class PanelAdminIniciando extends ImagePanel {

	
	
	public PanelAdminIniciando(String img, String name, long _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void ContentPanel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnLoad() {
		
			//System.out.println("OnUnload PanelAdminIniciando");
		
	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelAdminIniciando");
		
	}

}
