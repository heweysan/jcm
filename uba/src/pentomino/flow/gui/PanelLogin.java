package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;

public class PanelLogin extends JPanel implements PinpadListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JPanel contentPanel= new JPanel();
	public JButton btnMenuRetiro;
	public JButton btnMenuDeposito;
	public final static JLabel lblLoginUser = new JLabel("");
	public final static JLabel lblLoginPassword = new JLabel("");
	public final static JLabel lblLoginOpcion = new JLabel(".");
	public final static JLabel lblLoginMensaje = new JLabel("");
	private Image img;
	
	public PanelLogin() {
		
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		
		contentPanel.add(new DebugButtons().getPanel());
		
		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(257, 625, 496, 87);
		contentPanel.add(lblLoginUser);
		contentPanel.add(new DebugButtons().getPanel());
		
		
		lblLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginPassword.setForeground(Color.WHITE);
		lblLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginPassword.setBounds(257, 793, 496, 87);
		contentPanel.add(lblLoginPassword);

		contentPanel.add(new DebugButtons().getPanel());
		
		
		lblLoginMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginMensaje.setForeground(Color.WHITE);
		lblLoginMensaje.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblLoginMensaje.setBounds(91, 79, 837, 70);
		contentPanel.add(lblLoginMensaje);
		
		lblLoginOpcion.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginOpcion.setForeground(Color.WHITE);
		lblLoginOpcion.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginOpcion.setBounds(230, 520, 87, 87);   //Este es login sin password
		contentPanel.add(lblLoginOpcion);
		contentPanel.add(new DebugButtons().getPanel());
		
			
		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);
		
		contentPanel.add(panelPinpad.getPanel());
		
	}
	
	
	public JPanel getPanel() {
		return contentPanel;
	}


	
	public void pinKeyReceived(PinpadEvent event) {
		
		PinKey digito = event.key();
		
		//Flow.panelLoginHolder.screenTimer.cancel();
		Flow.panelLoginHolder.screenTimerReset(7000,"");

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
				System.out.println("loginUser");
				
				if(CurrentUser.currentOperation == jcmOperation.Deposit) {
					System.out.println("loginUser deposit");
					//No ha ingresado su user
					if(CurrentUser.loginUser.length() <= 0) {
						return;
					}

					System.out.println("Validando usuario....");
					//Validamos el usuario
					CMUserVO user = Transactions.ValidaUsuario(CurrentUser.loginUser);
					System.out.println("loginUser success[" +  user.success +"] success [" + user.isValid + "]");


					if(user.success && user.isValid) {
						System.out.println("loginUser deposit success y isvalid");
						//Si es deposito ya lo dejamos pasar
						CurrentUser.pinpadMode = PinpadMode.None;											
					

						RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
								,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid TRUE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
						
						CurrentUser.totalAmountInserted = 0;
						
						PanelDeposito.lblMontoDepositado.setText("");
						
						Flow.depositBillsCounter.x20 = 0;
						Flow.depositBillsCounter.x50 = 0;
						Flow.depositBillsCounter.x100 = 0;
						Flow.depositBillsCounter.x200 = 0;
						Flow.depositBillsCounter.x500 = 0;
						Flow.depositBillsCounter.x1000 = 0;
						
						if(JcmGlobalData.isDebug) {
							CurrentUser.totalAmountInserted = 3720;
							PanelDeposito.lblMontoDepositado.setText("$3,720");
						}
						
						
						
						Flow.redirect(Flow.panelDepositoHolder);
						
												
						Transactions.BorraCashInOPs(Config.GetDirective("AtmId", ""));
						
						
					}
					else {						
						if(!user.success) {
							System.out.println("loginUser deposit success NO");
							//Si es deposito ya lo dejamos pasar
							CurrentUser.pinpadMode = PinpadMode.None;
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid FALSE (Se deja depositar)",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							
							CurrentUser.totalAmountInserted = 0;
							Flow.redirect(Flow.panelDepositoHolder);
							Transactions.BorraCashInOPs(Config.GetDirective("AtmId", "")); 
						}
						else {	
							if(++CurrentUser.loginAttempts >= 2) {
								//Intentos superados
								CurrentUser.loginUser = "";
								CurrentUser.cleanPinpadData();
								Flow.redirect(Flow.panelOperacionCanceladaHolder,5000,"panelIdle");
							}
							else {
										
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
										,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid FALSE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
								
								CurrentUser.loginUser = "";
								CurrentUser.loginPassword = "";
								lblLoginUser.setText("");
								lblLoginPassword.setText("");
								CurrentUser.asteriscos = "";
								CurrentUser.pinpadMode = PinpadMode.loginUser;
								Flow.panelLoginHolder.setBackground("./images/Scr7UsuarioIncorrecto.png");
							}
							return;
						}
					}
				}
				else {
					System.out.println("loginUser retiro");
					lblLoginOpcion.setBounds(230, 675, 87, 87);   //Este es password 
					CurrentUser.pinpadMode = PinpadMode.loginPassword;
				}
				break;
			case loginPassword:

				System.out.println("loginPassword");
				//No ha ingresado su user o pwd
				if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
					CurrentUser.pinpadMode = PinpadMode.loginUser;						
					return;
				}

				System.out.println("Validando usuario....");
				//Validamos el usuario
				CMUserVO user = Transactions.ValidaUsuario(CurrentUser.loginUser);

				System.out.println("loginPassword success[" +  user.success +"] success [" + user.isValid + "]");

				if(user.success && user.isValid) {
					System.out.println("loginPassword success y isvalid");
					switch(CurrentUser.currentOperation) {						
					case Dispense:
						//Si puede retirar lo dejamos pasar
						if(user.allowWithdrawals) {
							//El unico que dejamos pasar para dispensado
							System.out.println("loginPassword success y isvalid y dispense y allowsWithdrawals");
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid TRUE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							CurrentUser.pinpadMode = PinpadMode.retiroToken;
							Flow.panelTokenHolder.setBackground("./images/Scr7ConfirmaToken.png");
							Flow.redirect(Flow.panelTokenHolder);
						}
						else {
							System.out.println("loginPassword success y isvalid y dispense y NO allowsWithdrawals");
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid FALSE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.asteriscos = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;

							if(++CurrentUser.loginAttempts >= 2) {
								Flow.redirect(Flow.panelOperacionCanceladaHolder,5000,"panleIdle");								
							}
							else {	
								lblLoginMensaje.setText("¡Oh no! No tienes permisos para hacer retiros.");
								Flow.panelLoginHolder.setBackground("./images/Scr7DatosIncorrectos.png");								
							}
						}
						break;
					default:
						System.out.println("Validando usuario.... 4");
						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.loginUser = "";
						CurrentUser.loginPassword = "";
						CurrentUser.asteriscos = "";
						CurrentUser.pinpadMode = PinpadMode.loginUser;							
						Flow.panelLoginHolder.setBackground("./images/Scr7DatosIncorrectos.png");
						break;
					}
				}
				else {						
					if(!user.success) {
						System.out.println("loginPassword success NO");

						switch(CurrentUser.currentOperation) {

						case Deposit:
							System.out.println("Validando usuario.... 6");
							//Si es deposito ya lo dejamos pasar
							CurrentUser.pinpadMode = PinpadMode.None;
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid EXCEPTION",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							CurrentUser.totalAmountInserted = 0;
							Flow.redirect(Flow.panelDepositoHolder);
							Transactions.BorraCashInOPs(Config.GetDirective("AtmId", "")); 
							break;
						case Dispense:								
							System.out.println("Validando usuario.... 7");
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid EXCEPTION",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							CurrentUser.asteriscos = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							CurrentUser.loginAttempts++;							
							this.setBackground("./images/Scr7DatosIncorrectos.png");							
							break;
						default:
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							CurrentUser.asteriscos = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							this.setBackground("./images/Scr7DatosIncorrectos.png");							
							break;
						}							
					}
					else {

						System.out.println("loginPassword success isValid NO");
						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.loginUser = "";
						CurrentUser.loginPassword = "";
						CurrentUser.asteriscos = "";
						if(++CurrentUser.loginAttempts >= 2) {
							Flow.redirect(Flow.panelOperacionCanceladaHolder,5000,"panelIdle");
							
						}else {

							CurrentUser.pinpadMode = PinpadMode.loginUser;		
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", CurrentUser.loginUser, "","","",""
									,Config.GetDirective("FullAtmId", "Financial") ,"VALIDAUSUARIO IsValid FALSE",AccountType.None, TransactionType.ControlMessage, "","",0,"");
							Flow.panelLoginHolder.setBackground("./images/Scr7DatosIncorrectos.png");
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

}



