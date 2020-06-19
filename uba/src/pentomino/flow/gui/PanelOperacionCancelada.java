package pentomino.flow.gui;

import pentomino.config.Config;

public class PanelOperacionCancelada extends ImagePanel {


	private static final long serialVersionUID = 1L;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelOperacionCancelada(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
		
	}	



	@Override
	public void ContentPanel() {



	}

	@Override
	public void OnLoad() {
		System.out.println("OnUnload PanelOperacionCancelada");
		
	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelOperacionCancelada");
		Config.SetPersistence("BoardStatus", "Available");
		
	}



}
