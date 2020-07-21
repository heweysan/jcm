package pentomino.flow.gui;

import java.util.Timer;
import java.util.TimerTask;

import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.jcmagent.RaspiAgent;
import pentomino.flow.gui.helpers.ImageButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
		
		ImageButton btnAdminLogin = new ImageButton("./images/BTN7_ADMIN.png");
		btnAdminLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JcmGlobalData.isAdmin = true;
				CurrentUser.pinpadMode = PinpadMode.loginUser;
				CurrentUser.loginAttempts = 0;
				
				Flow.redirect(Flow.panelAdminIniciando);
			}
		});
		btnAdminLogin.setBounds(1710, 45, 162, 162);
		add(btnAdminLogin);
	}	


	@Override
	public void ContentPanel() {

		
	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelOos");
		Config.SetPersistence("BoardStatus", "Available");
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
		//System.out.println("OnUnload PanelOos");
		
	}
}
