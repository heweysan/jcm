package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;

import pentomino.cashmanagement.CmQueue;
import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.DispenseStatus;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;

import javax.swing.SwingConstants;

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

		JButton btnAdminLogin = new JButton("ADMIN LOGIN");
		btnAdminLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminLogin);
			}
		});
		btnAdminLogin.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnAdminLogin.setBounds(1550, 11, 347, 122);
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
						PanelMenu.btnMenuRetiro.setEnabled(false);
					}else {
						Flow.panelMenu.setBackground("./images/Scr7RetiroAutorizado.png");				
						PanelMenu.btnMenuRetiro.setEnabled(true);	
					}

					Flow.redirect(Flow.panelMenu,5000,Flow.panelIdle);
				}
			}
		});

	}

	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelIdle");

		System.out.println("JCM1 INHIBIT DESHABILITAMOS ACEPTADOR");
		Flow.jcms[0].jcmMessage[3] = 0x01;
		Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);

		System.out.println("JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
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
				}  
			}
		}, 1000,60000);

		screenTimerNetwork = new Timer();
		screenTimerNetwork.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {				
				if(!netIsAvailable()) {
					Flow.redirect(Flow.panelErrorComunicate);
					screenTimerNetwork.cancel();
				}
			}
		}, 1000,60000);
	}


	private static boolean netIsAvailable() {    
		System.out.println("netIsAvailable");
		try {			
			Socket socket2 = new Socket();
			socket2.connect(new InetSocketAddress("11.50.0.7", 5672), 5000);			
			socket2.close();
			return true;
		} catch (UnknownHostException e) {		
		} catch (IOException e) {		
		}
		return false;
	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelIdle");
		screenTimerDispense.cancel();
		screenTimerNetwork.cancel();

	}
}
