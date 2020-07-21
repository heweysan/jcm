package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.common.AccountType;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.DebugButtons;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.flow.gui.helpers.PanelPinpad;
import pentomino.flow.gui.helpers.PinKey;
import pentomino.flow.gui.helpers.PinpadEvent;
import pentomino.flow.gui.helpers.PinpadListener;
import pentomino.jcmagent.RaspiAgent;

public class PanelAdminLoginTienda extends ImagePanel implements PinpadListener {

	private static final long serialVersionUID = 1L;
	public JButton btnMenuRetiro;
	public JButton btnMenuDeposito;
	public final static JLabel lblLoginUser = new JLabel("");
	public final static JLabel lblLoginPassword = new JLabel("");
	public final static JLabel lblLoginOpcion = new JLabel(".");
	public final static JLabel lblLoginMensaje = new JLabel("");


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminLoginTienda(String img,String name, int _timeout, ImagePanel _redirect) {
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


		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(257, 640, 496, 87);
		add(lblLoginUser);
		add(new DebugButtons().getPanel());


		lblLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginPassword.setForeground(Color.WHITE);
		lblLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginPassword.setBounds(257, 800, 496, 87);
		add(lblLoginPassword);

		add(new DebugButtons().getPanel());
		lblLoginMensaje.setBackground(Color.LIGHT_GRAY);


		lblLoginMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginMensaje.setForeground(Color.WHITE);
		lblLoginMensaje.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblLoginMensaje.setBounds(10, 300, 947, 70);
		add(lblLoginMensaje);

		lblLoginOpcion.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginOpcion.setForeground(Color.WHITE);
		lblLoginOpcion.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginOpcion.setBounds(230, 520, 87, 87);   //Este es login sin password
		add(lblLoginOpcion);
		add(new DebugButtons().getPanel());


		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);

		add(panelPinpad.getPanel());



	}




	public void pinKeyReceived(PinpadEvent event) {

		PinKey digito = event.key();		

		screenTimerReset(TimeUnit.SECONDS.toMillis(10),Flow.panelOperacionCancelada);

		switch(digito)
		{

		case _Cancel:

			System.out.println("PanelLogin cancel");        	
			CurrentUser.cleanPinpadData();
			lblLoginUser.setText("");
			lblLoginPassword.setText("");			
			CurrentUser.loginUserMasked = "";	
			CurrentUser.loginPasswordMasked = "";							
			Flow.redirect(Flow.panelOperacionCancelada,TimeUnit.SECONDS.toMillis(3), Flow.panelIdle);
			break;
		case _Ok:

			switch(CurrentUser.pinpadMode) {
			case loginUser:					
				
				System.out.println("admin loginUser tienda");
				//No ha ingresado su user
				if(CurrentUser.loginUser.length() <= 0) {
					return;
				}
				
				lblLoginOpcion.setBounds(230, 675, 87, 87);   //Este es password 
				CurrentUser.pinpadMode = PinpadMode.loginPassword;
				
				break;			

			case loginPassword:

				System.out.println("loginPassword Tienda");
				//No ha ingresado su user o pwd
				if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
					CurrentUser.pinpadMode = PinpadMode.loginUser;						
					return;
				}

				System.out.println("Validando usuario admin Tienda....");
				//Validamos el usuario
				CMUserVO user = Transactions.ValidaUsuarioPassword(CurrentUser.loginUser,CurrentUser.loginPassword);

				System.out.println("loginPassword success[" +  user.success +"] success [" + user.isValid + "]");

				if(user.success && user.isValid) {
					System.out.println("loginPassword success y isvalid");
					
						//Si puede retirar lo dejamos pasar
					//TODO: RITCHIE: Aqui en lugar de allowWithdrawals es el allow de entrr a modo admin
						if(user.allowWithdrawals) {
							//El unico que dejamos pasar para dispensado
							System.out.println("loginPassword success y isvalid y dispense y allowsWithdrawals");
							RaspiAgent.WriteToJournal("ADMIN", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO TIENDA ADMIN IsValid TRUE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							
							Flow.redirect(Flow.panelAdminLogin);
						}
						else {
							System.out.println("loginPassword success y isvalid y dispense y NO allowsWithdrawals");
							RaspiAgent.WriteToJournal("ADMIN", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO TIENDA ADMIN IsValid FALSE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.loginPasswordMasked = "";
							CurrentUser.loginUserMasked = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;

							if(++CurrentUser.loginAttempts >= 2) {
								Flow.redirect(Flow.panelOperacionCancelada,TimeUnit.SECONDS.toMillis(3),Flow.panelIdle);								
							}
							else {	
								
								Flow.panelAdminLoginTienda.setBackground("./images/Scr7DatosIncorrectos.png");								
							}
						}
						break;
					
				}
				else {						
					if(!user.success) {
						System.out.println("loginPassword success NO");

													
							
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid EXCEPTION",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							CurrentUser.loginUserMasked = "";
							CurrentUser.loginPasswordMasked = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							CurrentUser.loginAttempts++;				
							lblLoginOpcion.setBounds(230, 430, 87, 87); 
							this.setBackground("./images/Scr7DatosIncorrectos.png");							
											
					}
					else {

						System.out.println("loginPassword success isValid NO");
						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.loginUser = "";
						CurrentUser.loginPassword = "";
						CurrentUser.loginUserMasked = "";
						CurrentUser.loginPasswordMasked = "";
						if(++CurrentUser.loginAttempts >= 2) {
							Flow.redirect(Flow.panelOperacionCancelada,TimeUnit.SECONDS.toMillis(3),Flow.panelIdle);
							
						}else {
							lblLoginOpcion.setBounds(230, 430, 87, 87); 
							CurrentUser.pinpadMode = PinpadMode.loginUser;		
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid FALSE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							Flow.panelAdminLoginTienda.setBackground("./images/Scr7DatosIncorrectos.png");
						}
						return;
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
				CurrentUser.loginUser += digito.getDigit();
				CurrentUser.loginUserMasked += "*";
				lblLoginUser.setText(CurrentUser.loginUserMasked);			
				break;
			case loginPassword:
				if (CurrentUser.loginPassword.length() > 7)
					return;
				CurrentUser.loginPassword += digito.getDigit();
				CurrentUser.loginPasswordMasked += "*";
				lblLoginPassword.setText(CurrentUser.loginPasswordMasked);
				break;	
			default:
				break;				
			}	
			break;
		}

	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelLogin]");
		CurrentUser.pinpadMode = PinpadMode.loginUser;
		CurrentUser.loginUser = "";
		CurrentUser.loginUserMasked = "";
		CurrentUser.loginPassword = "";
		CurrentUser.loginPasswordMasked = "";
		CurrentUser.loginAttempts = 0;
		lblLoginUser.setText("");
		lblLoginPassword.setText("");
		lblLoginMensaje.setText("");		
		lblLoginUser.setLocation(257, 540);				
		lblLoginOpcion.setBounds(230, 430, 87, 87); 

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload [PanelLogin]");
		lblLoginUser.setText("");
		lblLoginPassword.setText("");
		lblLoginMensaje.setText("");
	}
}



