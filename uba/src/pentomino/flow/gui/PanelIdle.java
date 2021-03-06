package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.CmQueue;
import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.NetUtils;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.DispenseStatus;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImageButton;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.jcmagent.RaspiAgent;

public class PanelIdle  extends ImagePanel {


	/**
	 * @wbp.parser.constructor
	 */

	public PanelIdle(String img,String name, int _timeout, ImagePanel _redirect) {
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


	public static JLabel lblAtmId = new JLabel("-----");
	public static JLabel lblPanelError = new JLabel("");

	Timer screenTimerDispense = new Timer();
	Timer screenTimerNetwork = new Timer();

	@Override
	public void ContentPanel() {

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
		lblAtmId.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblAtmId.setHorizontalAlignment(SwingConstants.RIGHT);

		lblAtmId.setForeground(Color.WHITE);
		lblAtmId.setBounds(1452, 950, 407, 47);
		add(lblAtmId);

		JButton btnIdle = new JButton("");
		btnIdle.setBounds(0, 0, 1920, 1080);		
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnIdle.setOpaque(false);
		btnIdle.setContentAreaFilled(false);
		btnIdle.setBorderPainted(false);		
		add(btnIdle);

		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {						


				//Checamos que tenga algo de dinero.
				if(Flow.jcms[0].billCounters.Cass1Available == 0 && Flow.jcms[0].billCounters.Cass2Available == 0 && Flow.jcms[1].billCounters.Cass1Available == 0 && Flow.jcms[1].billCounters.Cass2Available == 0){
					System.out.println("No hay dinero en los caseteros para dispensar");
					CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
					Flow.redirect(Flow.panelMenuSinFondo,5000,Flow.panelIdle);					
					return;
				}

				if(JcmGlobalData.getMaxRecyclableCash() == 0 ) {
					Flow.redirect(Flow.panelMenuSinFondo,5000,Flow.panelIdle);
				}
				else {
					if(CmQueue.queueList.isEmpty()) {
						Flow.panelMenu.setBackground("./images/Scr7SinRetiroAutorizado.png");
						PanelMenu.btnMenuRetiro.setIcon(new ImageIcon("./images/BTN7RetiroOff.png"));
						PanelMenu.btnMenuRetiro.setEnabled(false);
					}else {
						Flow.panelMenu.setBackground("./images/Scr7RetiroAutorizado.png");	
						PanelMenu.btnMenuRetiro.setIcon(new ImageIcon("./images/BTN7Retiro.png"));			
						PanelMenu.btnMenuRetiro.setEnabled(true);	
					}

					Flow.redirect(Flow.panelMenu,5000,Flow.panelIdle);
				}


			}
		});

	}

	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelIdle]");

		JcmGlobalData.isAdmin = false;

		System.out.println("JCM1/JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
		Flow.jcms[0].jcmMessage[3] = 0x01;
		Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
		
		Flow.jcms[1].jcmMessage[3] = 0x01;
		Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);

		if(Flow.jcms[0].billCounters.Cass1Available == 0 && Flow.jcms[0].billCounters.Cass2Available == 0 && Flow.jcms[1].billCounters.Cass1Available == 0 && Flow.jcms[1].billCounters.Cass2Available == 0){
			System.out.println("No hay dinero en los caseteros para dispensar ponemos pantalla de sin billetes");
			Flow.panelIdle.setBackground("./images/Scr7SinEfectivo.png");			
		}
		else {
			Flow.panelIdle.setBackground("./images/Scr7Inicio.png");
		}



		screenTimerDispense = new Timer();
		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {			
				if(Config.GetDirective("ForceOos", "false").equalsIgnoreCase("true")) {
					System.out.println("Entrando a ForceOoS");
					RaspiAgent.WriteToJournal("Financial",0,0, "",CurrentUser.loginUser, "Entrando a ForceOoS[true]", AccountType.None, TransactionType.Administrative);
					Flow.redirect(Flow.panelOos);
					screenTimerDispense.cancel();
					return;
				}
			}
		}, 1000,60000);

		screenTimerNetwork = new Timer();
		screenTimerNetwork.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {				
				if(!NetUtils.netIsAvailable()) {
					if(!JcmGlobalData.isAdmin) {
						System.out.println("PanelIdle panelErrorComunicate");
						Flow.redirect(Flow.panelErrorComunicate);
						screenTimerNetwork.cancel();
					}
				}
			}		

		}, 1000,TimeUnit.MINUTES.toMillis(1));
	}


	@Override
	public void OnUnload() {
		System.out.println("OnUnload [PanelIdle]");
		screenTimerDispense.cancel();
		screenTimerNetwork.cancel();

	}
}
