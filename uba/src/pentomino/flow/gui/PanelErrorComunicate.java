package pentomino.flow.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;

import pentomino.common.JcmGlobalData;
import pentomino.common.NetUtils;
import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImageButton;
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
		
		JButton btnAdminLogin = new ImageButton("./images/BTN7_ADMIN.png");
		btnAdminLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JcmGlobalData.isAdmin = true;
				CurrentUser.pinpadMode = PinpadMode.loginUser;
				CurrentUser.loginAttempts = 0;
				
				Flow.redirect(Flow.panelAdminIniciando);
			}
		});
		btnAdminLogin.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnAdminLogin.setBounds(1710, 45, 162, 162);
		add(btnAdminLogin);
		
		JButton btnIdle = new JButton("");
		btnIdle.setBounds(0, 0, 1920, 1080);		
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnIdle.setOpaque(false);
		btnIdle.setContentAreaFilled(false);
		btnIdle.setBorderPainted(false);		
		add(btnIdle);

		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {		
				CurrentUser.currentOperation = jcmOperation.Deposit;
					Flow.redirect(Flow.panelMenuSinFondo,5000,Flow.panelErrorComunicate);
			}
		});
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
