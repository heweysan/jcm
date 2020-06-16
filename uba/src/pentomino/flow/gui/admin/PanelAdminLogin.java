package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.common.AccountType;
import pentomino.common.BusinessEvent;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.PanelPinpad;
import pentomino.flow.gui.PinKey;
import pentomino.flow.gui.PinpadEvent;
import pentomino.flow.gui.PinpadListener;
import pentomino.jcmagent.AccountClient;
import pentomino.jcmagent.BEA;
import pentomino.jcmagent.RaspiAgent;


public class PanelAdminLogin extends JPanel implements PinpadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JPanel contentPanel= new JPanel();
	public JButton btnMenuRetiro;
	public JButton btnMenuDeposito;
	public static JLabel lblLoginUser = new JLabel("");
	public static JLabel lblLoginPassword = new JLabel("");
	public static JLabel lblLoginOpcion = new JLabel(".");
	final JLabel lblLoginRow1 = new JLabel("Ingrese su clave de usuario");
	private Image img;

	private static String dailyPass = "";

	public PanelAdminLogin() {

		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	

		contentPanel.add(new DebugButtons().getPanel());

		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(350, 545, 496, 87);
		contentPanel.add(lblLoginUser);
		contentPanel.add(new DebugButtons().getPanel());


		lblLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginPassword.setForeground(Color.WHITE);
		lblLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginPassword.setBounds(424, 786, 496, 87);
		contentPanel.add(lblLoginPassword);

		contentPanel.add(new DebugButtons().getPanel());


		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginRow1.setBounds(91, 91, 837, 70);
		contentPanel.add(lblLoginRow1);

		lblLoginOpcion.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginOpcion.setForeground(Color.WHITE);
		lblLoginOpcion.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginOpcion.setBounds(230, 520, 87, 87);   //Este es login sin password
		contentPanel.add(lblLoginOpcion);
		contentPanel.add(new DebugButtons().getPanel());


		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);

		contentPanel.add(panelPinpad.getPanel());
		
		/*
		 * @param businessEvent
		 * @param inSession def(false)
		 * @param newSession def(false)
		 * @param attributes def("")
		*/
		BEA.BusinessEvent(BusinessEvent.SessionStart, true, true, "");
		
		BEA.BusinessEvent(BusinessEvent.SessionEnd, true, false, "");
		
		
	}



	public JPanel getPanel() {
		return contentPanel;
	}

	public void pinKeyReceived(PinpadEvent event) {

		PinKey digito = event.key();

		//Flow.panelLoginHolder.screenTimer.cancel();
		Flow.panelAdminLoginHolder.screenTimerReset(700000,"");

		switch(digito)
		{

		case _Cancel:

			System.out.println("Es cancel Papawh");        	
			CurrentUser.cleanPinpadData();
			lblLoginUser.setText("");
			lblLoginPassword.setText("");			
			CurrentUser.asteriscos = "";							
			Flow.redirect(Flow.panelOperacionCanceladaHolder,5000, "panelIdle");
			break;
		case _Ok:

			switch(CurrentUser.pinpadMode) {
			case loginUser:					
				System.out.println("admin loginUser");					
				lblLoginOpcion.setBounds(230, 675, 87, 87);   //Este es password 
				CurrentUser.pinpadMode = PinpadMode.loginPassword;

				break;
			case loginPassword:

				System.out.println("admin loginPassword");
				//No ha ingresado su user o pwd
				if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
					CurrentUser.pinpadMode = PinpadMode.loginUser;						
					return;
				}

				System.out.println("Validando usuario [" + CurrentUser.loginUser + "]");
				//Validamos el usuario
				if(CurrentUser.loginUser.equalsIgnoreCase("0000")) {
					System.out.println("Nos vamos por dailyPassword");
					//DmdM + 2017 if non --- mDMd + 7102 if par   3630
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

					if(CurrentUser.loginPassword.equalsIgnoreCase(dailyPass)) {

						RaspiAgent.WriteToJournal("ADMIN", 0,0, "", "", CurrentUser.loginUser, "LOGIN FALLBACK OK","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"OGIN FALLBACK OK",AccountType.None, TransactionType.ControlMessage, "","",0,"");

						Flow.redirect(Flow.panelAdminMenuHolder,5000, "panelIdle");
					}
					else {

						RaspiAgent.WriteToJournal("ADMIN", 0,0, "", "", CurrentUser.loginUser, "LOGIN FALLBACK Fail","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"Usuario inválido",AccountType.None, TransactionType.ControlMessage, "","",0,"");

						PanelAdminError.lblSubMensaje.setText("Usuario inválido");
						Flow.redirect(Flow.panelAdminErrorHolder,5000, "panelAdminLogin");
					}
				}
				else{

					//Cualquier resultado falso es cadena vacia.
					AccountClient myA = new AccountClient();
					String adminMenuOptions = myA.LoginAdminAccess(CurrentUser.loginUser, CurrentUser.loginPassword);
					
					if (adminMenuOptions.equalsIgnoreCase("0")) {

						RaspiAgent.WriteToJournal("ADMIN", 0,0, "", "", CurrentUser.loginUser, "LOGIN FAIL","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"LOGIN FAIL",AccountType.None, TransactionType.ControlMessage, "","",0,"");

						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.asteriscos = "";
						CurrentUser.loginUser = "";
						CurrentUser.loginPassword = "";
						CurrentUser.pinpadMode = PinpadMode.loginUser;
						
						PanelAdminError.lblSubMensaje.setText("Usuario inválido.");
						Flow.redirect(Flow.panelAdminErrorHolder,5000, "panelAdminLogin");
					}
					else
						Flow.redirect(Flow.panelAdminMenuHolder);

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
				lblLoginUser.setText(CurrentUser.loginUser);			
				break;
			case loginPassword:
				if (CurrentUser.loginPassword.length() > 7)
					return;
				CurrentUser.loginPassword += digito.getDigit();
				CurrentUser.asteriscos += "*";
				lblLoginPassword.setText(CurrentUser.asteriscos);
				break;	
			default:
				break;				
			}	
			break;
		}

	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	// Metodo donde le pasaremos la dirección de la imagen a cargar.
	public void setBackground(String imagePath) {

		// Construimos la imagen y se la asignamos al atributo background.
		this.setOpaque(false);
		this.img = new ImageIcon(imagePath).getImage();
		repaint();
	}

	public static void resetFormData() {


	}

}