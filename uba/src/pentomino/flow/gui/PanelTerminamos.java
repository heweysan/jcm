package pentomino.flow.gui;

import pentomino.config.Config;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;

public class PanelTerminamos extends ImagePanel {

	/**
	 * @wbp.parser.constructor
	 */
	public PanelTerminamos(String img,String name, int _timeout, ImagePanel _redirect) {
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
		System.out.println("OnLoad [PanelTerminamos]");
		Config.SetPersistence("BoardStatus", "Available");
		screenTimerReset(5000,Flow.panelIdle);

	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload [PanelTerminamos]");
		Config.SetPersistence("BoardStatus", "Available");

	}

}
