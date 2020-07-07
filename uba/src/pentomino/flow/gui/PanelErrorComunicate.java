package pentomino.flow.gui;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import pentomino.common.NetUtils;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;

public class PanelErrorComunicate  extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelErrorComunicate(String img,String name, int _timeout, ImagePanel _redirect) {
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
		System.out.println("OnLoad PanelErrorComunicate");
		Timer screenTimerNetwork = new Timer();
		screenTimerNetwork.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {				
				if(NetUtils.netIsAvailable()) {
					Flow.redirect(Flow.panelIdle);
					screenTimerNetwork.cancel();
				}
			}
		}, TimeUnit.MINUTES.toMillis(1),TimeUnit.MINUTES.toMillis(1));
		
	}



	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelErrorComunicate");
		
	}
}
