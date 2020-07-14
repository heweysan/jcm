package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.common.AccountType;
import pentomino.common.BusinessEvent;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.PinKey;
import pentomino.flow.gui.PinpadEvent;
import pentomino.flow.gui.PinpadListener;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.flow.gui.helpers.PanelPinpad;
import pentomino.jcmagent.AccountClient;
import pentomino.jcmagent.BEA;
import pentomino.jcmagent.RaspiAgent;


public class PanelAdminLoginTienda extends ImagePanel implements PinpadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JButton btnMenuRetiro;
	public JButton btnMenuDeposito;
	public static JLabel lblAdminLoginUser = new JLabel(".");
	public static JLabel lblAdminLoginPassword = new JLabel(".");
	public static JLabel lblLoginOpcion = new JLabel(".");
	public static JLabel lblLoginRow1 = new JLabel("");

	private static String dailyPass = "";

	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminLoginTienda(String img, String name, long _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}
	
	
	public PanelAdminLoginTienda(ImageIcon img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}	


	
	@Override
	public void ContentPanel() {


		lblAdminLoginUser.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblAdminLoginUser.setForeground(Color.WHITE);
		lblAdminLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblAdminLoginUser.setBounds(262, 707, 500, 80);
		add(lblAdminLoginUser);


		lblAdminLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblAdminLoginPassword.setForeground(Color.WHITE);
		lblAdminLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblAdminLoginPassword.setBounds(250, 650, 500, 80);
		add(lblAdminLoginPassword);


		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblLoginRow1.setBounds(10, 193, 918, 70);
		add(lblLoginRow1);

		lblLoginOpcion.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginOpcion.setForeground(Color.WHITE);
		lblLoginOpcion.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginOpcion.setBounds(230, 570, 87, 87);   //Este es login sin password
		add(lblLoginOpcion);


		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);

		add(panelPinpad.getPanel());
		
		JLabel lblNewLabel = new JLabel(".");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblNewLabel.setBounds(80, 129, 48, 36);
		add(lblNewLabel);
		
		
		
		
	}

	public void pinKeyReceived(PinpadEvent event) {

		PinKey digito = event.key();
	
		screenTimerReset(TimeUnit.SECONDS.toMillis(10),Flow.panelOperacionCancelada);

		switch(digito)
		{

		case _Cancel:			       	
			CurrentUser.cleanPinpadData();
			lblAdminLoginUser.setText("");
			lblAdminLoginPassword.setText("");			
			CurrentUser.loginPasswordMasked = "";							
			Flow.redirect(Flow.panelOperacionCancelada,TimeUnit.SECONDS.toMillis(3), Flow.panelIdle);
			break;
		case _Ok:

			switch(CurrentUser.pinpadMode) {
			case loginUser:					
								
				lblAdminLoginUser.setVisible(false);
				lblAdminLoginPassword.setVisible(true);
				lblAdminLoginPassword.setText("");
				Flow.panelAdminLoginTienda.setBackground(Flow.bgAdminPassword);				
				CurrentUser.pinpadMode = PinpadMode.loginPassword;

				break;
			case loginPassword:				
				//No ha ingresado su user o pwd
				if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
					CurrentUser.pinpadMode = PinpadMode.loginUser;						
					return;
				}
				
				CurrentUser.loginAttempts++;

				//System.out.println("Validando usuario tienda [" + CurrentUser.loginUser + "] [" + CurrentUser.loginPassword + "]");
				//Validamos el usuario
				if(CurrentUser.loginUser.equalsIgnoreCase("0000")) {
					System.out.println("Nos vamos por dailyPassword usuario tienda");
					
					CalculateDailyPass();

					if(CurrentUser.loginPassword.equalsIgnoreCase(dailyPass)) {

						RaspiAgent.WriteToJournal("ADMIN", 0,0, "", "", CurrentUser.loginUser, "LOGIN STORE FALLBACK OK","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"LOGIN FALLBACK OK",AccountType.None, TransactionType.ControlMessage, "","",0,"");

						BEA.BusinessEvent(BusinessEvent.AdministrativeOperationStarted, true, true, "");
						Flow.redirect(Flow.panelAdminMenu,5000, Flow.panelIdle);
					}
					else {
						RaspiAgent.WriteToJournal("ADMIN", 0,0, "", "", CurrentUser.loginUser, "LOGIN STORE FALLBACK Fail","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"Usuario inválido",AccountType.None, TransactionType.ControlMessage, "","",0,"");
						
						if(CurrentUser.loginAttempts >= 2) {
							CurrentUser.loginAttempts = 0;
							Flow.redirect(Flow.panelOperacionCancelada,3000,Flow.panelIdle);
						}
						else {												
							PanelAdminError.lblSubMensaje.setText("Usuario inválido");
							Flow.redirect(Flow.panelAdminUsuarioInvalido,5000, Flow.panelIdle);
						}
					}
				}
				else{				
					
					//Cualquier resultado falso es cadena vacia.
					AccountClient myA = new AccountClient();
					String adminMenuOptions = myA.LoginAdminAccess(CurrentUser.loginUser, CurrentUser.loginPassword);
					
					System.out.println("PanelAdminLoginTienda adminMenuOptions [" + adminMenuOptions + "]");
					
					if (adminMenuOptions.equalsIgnoreCase("0")) {						
						
						RaspiAgent.WriteToJournal("ADMIN", 0,0, "", "", CurrentUser.loginUser, "LOGIN STORE FAIL","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"LOGIN STORE FAIL",AccountType.None, TransactionType.ControlMessage, "","",0,"");
						
						if(CurrentUser.loginAttempts >= 2) {
							CurrentUser.loginAttempts = 0;
							Flow.redirect(Flow.panelOperacionCancelada,3000,Flow.panelIdle);
						}
						else {
																			
							lblAdminLoginUser.setText("");
							lblAdminLoginPassword.setText("");
							CurrentUser.loginPasswordMasked = "";
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							
							PanelAdminError.lblSubMensaje.setText("Usuario inválido.");
							Flow.redirect(Flow.panelAdminUsuarioInvalido,5000, Flow.panelIdle);
						}
						
					}
					else {						
						Flow.timerBoveda();
						CurrentUser.currentOperation = jcmOperation.AdminLogin;
						Flow.redirect(Flow.panelAdminLogin);
					}

				}
				break;
			default:
				break;

			}

			break;
		default:

			switch(CurrentUser.pinpadMode) {
			case None:
				break;
			case loginUser:
				if (CurrentUser.loginUser.length() > 7)				
					return;
				CurrentUser.loginUser = CurrentUser.loginUser + digito.getDigit();
				lblAdminLoginUser.setText(CurrentUser.loginUser);			
				break;
			case loginPassword:
				if (CurrentUser.loginPassword.length() > 7)
					return;
				CurrentUser.loginPassword += digito.getDigit();
				CurrentUser.loginPasswordMasked += "*";
				lblAdminLoginPassword.setText(CurrentUser.loginPasswordMasked);
				break;	
			default:
				break;				
			}	
			break;
		}

	}

	
	private static void CalculateDailyPass() {
		
		dailyPass = "";

		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		Date hoy = new Date();
		String MMddyyyy = dateFormat.format(hoy);
		System.out.println(MMddyyyy);

		int M = Integer.parseInt(MMddyyyy.charAt(0) + "");
		int m = Integer.parseInt(MMddyyyy.charAt(1) + "");
		int D = Integer.parseInt(MMddyyyy.charAt(2) + "");
		int d = Integer.parseInt(MMddyyyy.charAt(3) + "");
		int y1 = Integer.parseInt(MMddyyyy.charAt(4) + "");
		int y2 = Integer.parseInt(MMddyyyy.charAt(5) + "");
		int y3 = Integer.parseInt(MMddyyyy.charAt(6) + "");
		int y4 = Integer.parseInt(MMddyyyy.charAt(7) + "");


		if (d % 2 == 0) {
			m = (m + y4) % 10;
			D = (D + y3) % 10;
			M = (M + y2) % 10;
			d = (d + y1) % 10;
			dailyPass = String.format("%s%s%s%s", m, D, M, d);
		} else {
			D = (D + y1) % 10;
			m = (m + y2) % 10;
			d = (d + y3) % 10;
			M = (M + y4) % 10;
			dailyPass = String.format("%s%s%s%s", D, m, d, M);
		}
	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelAdminLoginTienda]");
		lblAdminLoginUser.setVisible(true);
		lblAdminLoginPassword.setVisible(false);
		Flow.panelAdminLoginTienda.setBackground(Flow.bgAdminUsuario);
		
		lblAdminLoginUser.setBounds(250, 650, 500, 80);
		lblLoginOpcion.setBounds(230, 540, 87, 87); 
		lblAdminLoginUser.setText("");
		lblAdminLoginPassword.setText("");
		CurrentUser.pinpadMode = PinpadMode.loginUser;
		CurrentUser.cleanPinpadData();
		CurrentUser.currentOperation = jcmOperation.StoreLogin;
		
	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload [PanelAdminLoginTienda]");
		lblAdminLoginUser.setVisible(true);
		lblAdminLoginPassword.setVisible(false);
		Flow.panelAdminLoginTienda.setBackground(Flow.bgAdminUsuario);
		
	}
}