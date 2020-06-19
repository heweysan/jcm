package pentomino.flow.gui;

import java.util.Timer;
import java.util.TimerTask;

import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;

public class PanelOos  extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelOos(String img,String name, int _timeout, ImagePanel _redirect) {
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
		System.out.println("OnLoad PanelOos");
		
		Timer screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {			
				if(Config.GetDirective("ForceOos", "false").equalsIgnoreCase("false")) {
					System.out.println("Saliendo de ForceOoS");
					RaspiAgent.WriteToJournal("Financial",0,0, "",CurrentUser.loginUser, "Saliendo de ForceOoS", AccountType.None, TransactionType.Administrative);
					Flow.redirect(Flow.panelIdle);
					screenTimerDispense.cancel();
				}        	
			}
		}, 1000,30000);
		

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelOos");
		
	}
	
}
