package pentomino.gui;

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
	final JLabel lblLoginRow1 = new JLabel("");
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
		
		
		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginRow1.setBounds(89, 70, 837, 70);
		contentPanel.add(lblLoginRow1);
		
			
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
					if(CurrentUser.getLoginUser().length() <= 0) {						
						//CurrentUser.pinpadMode = PinpadMode.loginUser;						
						return;
					}

					System.out.println("Validando usuario....");
					//Validamos el usuario
					CMUserVO user = Transactions.ValidaUsuario(CurrentUser.getLoginUser());
					System.out.println("loginUser success[" +  user.success +"] success [" + user.isValid + "]");


					if(user.success && user.isValid) {
						System.out.println("loginUser deposit success y isvalid");
						//Si es deposito ya lo dejamos pasar
						CurrentUser.pinpadMode = PinpadMode.None;
						RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
						Flow.montoDepositado = 0;
						Flow.redirect(Flow.panelDepositoHolder);
						Transactions.BorraCashInOPs(Config.GetDirective("AtmId", "")); 								
					}
					else {						
						if(!user.success) {
							System.out.println("loginUser deposit success NO");
							//Si es deposito ya lo dejamos pasar
							CurrentUser.pinpadMode = PinpadMode.None;
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
							Flow.montoDepositado = 0;
							Flow.redirect(Flow.panelDepositoHolder);
							Transactions.BorraCashInOPs(Config.GetDirective("AtmId", "")); //"IXXGS0020 CI01GL0001
						}
						else {	
							if(++CurrentUser.loginAttempts >= 2) {
								//Intentos superados
								CurrentUser.setLoginUser("");
								CurrentUser.cleanPinpadData();
								Flow.redirect(Flow.panelOperacionCanceladaHolder,5000,"panelIdle");
							}
							else {
								CurrentUser.setLoginUser("");
								CurrentUser.loginPassword = "";
								lblLoginUser.setText("");
								lblLoginPassword.setText("");
								CurrentUser.asteriscos = "";
								CurrentUser.pinpadMode = PinpadMode.loginUser;		
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
								Flow.panelLoginHolder.setBackground("./images/Scr7UsuarioIncorrecto.png");
							}
							return;
						}
					}
				}
				else {
					System.out.println("loginUser retiro");
					CurrentUser.pinpadMode = PinpadMode.loginPassword;
				}
				break;
			case loginPassword:

				System.out.println("loginPassword");
				//No ha ingresado su user o pwd
				if(CurrentUser.getLoginUser().length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
					CurrentUser.pinpadMode = PinpadMode.loginUser;						
					return;
				}

				System.out.println("Validando usuario....");
				//Validamos el usuario
				CMUserVO user = Transactions.ValidaUsuario(CurrentUser.getLoginUser());

				System.out.println("loginPassword success[" +  user.success +"] success [" + user.isValid + "]");

				if(user.success && user.isValid) {
					System.out.println("loginPassword success y isvalid");
					switch(CurrentUser.currentOperation) {						
					case Dispense:
						//Si puede retirar lo dejamos pasar
						if(user.allowWithdrawals) {
							//El unico que dejamos pasar para dispensado
							System.out.println("loginPassword success y isvalid y dispense y allowsWithdrawals");
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
							CurrentUser.pinpadMode = PinpadMode.retiroToken;
							Flow.panelTokenHolder.setBackground("./images/Scr7ConfirmaToken.png");
							Flow.redirect(Flow.panelTokenHolder);
						}
						else {
							System.out.println("loginPassword success y isvalid y dispense y NO allowsWithdrawals");
							CurrentUser.setLoginUser("");
							CurrentUser.loginPassword = "";
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.asteriscos = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;

							if(++CurrentUser.loginAttempts >= 2) {
								Flow.redirect(Flow.panelOperacionCanceladaHolder,5000,"panleIdle");								
							}
							else {	
								lblLoginRow1.setText("¡Oh no! No tienes permisos para hacer retiros.");
								Flow.panelLoginHolder.setBackground("./images/Scr7DatosIncorrectos.png");
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
							}
						}
						break;
					default:
						System.out.println("Validando usuario.... 4");
						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.setLoginUser("");
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
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
							Flow.montoDepositado = 0;
							Flow.redirect(Flow.panelDepositoHolder);
							Transactions.BorraCashInOPs(Config.GetDirective("AtmId", "")); 
							break;
						case Dispense:								
							System.out.println("Validando usuario.... 7");
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.setLoginUser("");
							CurrentUser.loginPassword = "";
							CurrentUser.asteriscos = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							CurrentUser.loginAttempts++;
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
							this.setBackground("./images/Scr7DatosIncorrectos.png");
							//Flow.panelLoginHolder.setBackground("./images/Scr7DatosIncorrectos.png");
							break;
						default:
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.setLoginUser("");
							CurrentUser.loginPassword = "";
							CurrentUser.asteriscos = "";
							CurrentUser.pinpadMode = PinpadMode.loginUser;
							this.setBackground("./images/Scr7DatosIncorrectos.png");
							//Flow.panelLoginHolder.setBackground("./images/Scr7DatosIncorrectos.png");
							break;
						}							
					}
					else {

						System.out.println("loginPassword success isValid NO");
						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.setLoginUser("");
						CurrentUser.loginPassword = "";
						CurrentUser.asteriscos = "";
						if(++CurrentUser.loginAttempts >= 2) {
							Flow.redirect(Flow.panelOperacionCanceladaHolder,5000,"panelIdle");
							
						}else {

							CurrentUser.pinpadMode = PinpadMode.loginUser;		
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
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
			if (CurrentUser.getLoginUser().length() > 7)				
				return;
			CurrentUser.setLoginUser(CurrentUser.getLoginUser() + digito.getDigit());
			lblLoginUser.setText(CurrentUser.getLoginUser());			
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


/*
case retiroToken:
	
	
	System.out.println("token["  + token + "] valdiation[" + CurrentUser.tokenConfirmacion + "]");
	if(token.equalsIgnoreCase(CurrentUser.tokenConfirmacion)) {

		if(!validateDispense()) {
			System.out.println("No se puede dispensar en este momento.");							
			lblPanelError.setText("No se puede dispensar en este momento.");
			Flow.cl.show(Flow.panelContainer, panelError,10000,"panelIdle");							
		}
		else {

			CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
			cmWithdrawalVo.atmId = atmId;
			cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
			cmWithdrawalVo.password = CurrentUser.loginPassword;
			cmWithdrawalVo.reference = CurrentUser.referencia;
			cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
			cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
			cmWithdrawalVo.amount = JcmGlobalData.montoDispensar;


			System.out.println("ConfirmaRetiro");
			if(!Transactions.ConfirmaRetiro(cmWithdrawalVo)) {
				System.out.println("Usuario sin permiso para dispensar!");
				lblPanelError.setText("Lo sentimos, no se pude procesar su petición");
				Flow.cl.show(Flow.panelContainer, panelError,10000,"panelIdle");
			}
			else {

				//TODO: HEWEY AQUI SE QUITA EL ELEMENTO DEL QUEUE DE RETIROS
				//Quitamos el retiro del queue
				CmQueue.queueList.removeFirst();


				if(isDebug) {													
					Timer screenTimer = new Timer();
					screenTimer.schedule(new TimerTask() {
						@Override
						public void run() {				                
							Flow.cl.show(Flow.panelContainer, "panelTerminamos");

							Ptr.print("SI",new HashMap<String,String>());
							Timer screenTimer2 = new Timer();
							screenTimer2.schedule(new TimerTask() {
								@Override
								public void run() {				                
									//Revisamos si hay retiros listos
									Flow.cl.show(Flow.panelContainer,"panelIdle");											
									screenTimer.cancel();
									screenTimer2.cancel();
								}
							}, 3000);

						}
					}, 3000);
				}
				else {


					//Preparamos el retiro
					token = "";
					CurrentUser.pinpadMode = PinpadMode.None;

					switch(dispenseStatus) {
					case Complete:
						lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.montoDispensar);
						Flow.cl.show(Flow.panelContainer, "panelRetiraBilletes");
						break;
					case Partial:
						lblRetiraBilletesMontoDispensarParcial.setText("$" + JcmGlobalData.montoDispensar);
						Flow.cl.show(Flow.panelContainer, "panelRetiroParcial");
						break;
					default:
						break;
					}

					System.out.println("ConfirmaRetiro");

					dispense();


					Timer screenTimerDispense = new Timer();
					screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							if(jcm1cass1Dispensed && jcm1cass2Dispensed && jcm2cass1Dispensed && jcm2cass2Dispensed) {
								Flow.cl.show(Flow.panelContainer, "panelTerminamos");
								RaspiAgent.Broadcast(DeviceEvent.AFD_DispenseOk, "" + JcmGlobalData.montoDispensar);
								RaspiAgent.WriteToJournal("Withdrawal", montoRetiro,0, "","", "Withdrawal DispenseOk", AccountType.Other, TransactionType.Withdrawal);
								CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
								cmWithdrawalVo.atmId = atmId;
								cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
								cmWithdrawalVo.password = CurrentUser.loginPassword;
								cmWithdrawalVo.reference = CurrentUser.referencia;
								cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
								cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();

								System.out.println("ConfirmaRetiro");

								Ptr.printDispense(montoRetiro,CurrentUser.getLoginUser());
								Timer screenTimer2 = new Timer();
								screenTimer2.schedule(new TimerTask() {
									@Override
									public void run() {
										//Revisamos si hay retiros listos

										Flow.cl.show(Flow.panelContainer,"panelIdle");

										screenTimerDispense.cancel();
										screenTimer2.cancel();
									}
								}, 1000);
							}
						}
					}, 1000,2000);
				}
			}

		}


	}
	else {

		CurrentUser.tokenConfirmacion = "";						
		lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);

		if( ++CurrentUser.tokenAttempts >= 2) {
			//Intentos superados
			CurrentUser.cleanPinpadData();																				
			Flow.cl.show(Flow.panelContainer, panelOperacionCancelada, 5000, "panelIdle");														
		}
		else {						
			panelToken.setBackground("./images/Scr7TokenIncorrecto.png");
		}
	}
	

	break;
	*/
