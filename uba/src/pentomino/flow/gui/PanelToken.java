package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CmWithdrawal;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.core.devices.Afd;
import pentomino.core.devices.Ptr;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;



public class PanelToken  implements PinpadListener{

	public JPanel contentPanel = new JPanel();

	public static JLabel lblToken = new JLabel(".");
	public static JLabel lblTokenConfirmacion = new JLabel(".");
	JLabel lblTokenMensaje = new JLabel(".");
	public static JLabel lblTokenMontoRetiro = new JLabel(".");
	
	public PanelToken() {
		
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
		
		contentPanel.add(new DebugButtons().getPanel());
		
		
		lblTokenConfirmacion.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenConfirmacion.setForeground(Color.WHITE);
		lblTokenConfirmacion.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblTokenConfirmacion.setBounds(190, 773, 583, 66);
		contentPanel.add(lblTokenConfirmacion);

		
		lblToken.setForeground(Color.WHITE);
		lblToken.setHorizontalAlignment(SwingConstants.CENTER);
		lblToken.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblToken.setBounds(190, 594, 583, 66);
		contentPanel.add(lblToken);

		
		lblTokenMontoRetiro.setForeground(Color.WHITE);
		lblTokenMontoRetiro.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMontoRetiro.setFont(new Font("Tahoma", Font.BOLD, 99));
		lblTokenMontoRetiro.setBounds(190, 321, 583, 136);
		contentPanel.add(lblTokenMontoRetiro);
		
		
		lblTokenMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTokenMensaje.setForeground(Color.WHITE);
		lblTokenMensaje.setBounds(190, 93, 583, 75);
		contentPanel.add(lblTokenMensaje);
		
		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);
		
		contentPanel.add(panelPinpad.getPanel());
			
		
	}
	
	public JPanel getPanel() {
		return contentPanel;
	}	
	
	public void pinKeyReceived(PinpadEvent event) {

		PinKey digito = event.key();		
		Flow.panelTokenHolder.screenTimerReset(7000,"");
		
		switch(digito){
        
		case _Cancel:		
			System.out.println("Es cancel Papawh");        	
			CurrentUser.cleanPinpadData();
			lblToken.setText("");										
			Flow.redirect(Flow.panelOperacionCanceladaHolder,5000, "panelIdle");
		break;
		case _Ok:
			
			switch(CurrentUser.pinpadMode) {
			
			
			case retiroToken:
			
				System.out.println("token["  + CurrentUser.token + "] valdiation[" + CurrentUser.tokenConfirmacion + "]");
				if(CurrentUser.token.equalsIgnoreCase(CurrentUser.tokenConfirmacion)) {

					if(!Afd.validateDispense()) {
						System.out.println("No se puede dispensar en este momento.");							
						PanelError.lblPanelError.setText("No se puede dispensar en este momento.");
						Flow.redirect(Flow.panelErrorHolder,10000,"panelIdle");							
					}
					else {

						String atmId = Config.GetDirective("AtmId", "");						
						
						CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
						cmWithdrawalVo.atmId = atmId;
						cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
						cmWithdrawalVo.password = CurrentUser.loginPassword;
						cmWithdrawalVo.reference = CurrentUser.referencia;
						cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
						cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
						cmWithdrawalVo.amount = JcmGlobalData.montoDispensar;
						
						if(!Transactions.ConfirmaRetiro(cmWithdrawalVo)) {
							System.out.println("Usuario sin permiso para dispensar!");
							PanelError.lblPanelError.setText("Lo siento,no pude procesar tu petición");
							Flow.redirect(Flow.panelErrorHolder,7000,"panelIdle");
						}
						else {

							//Preparamos el retiro
							CurrentUser.token = "";
							CurrentUser.pinpadMode = PinpadMode.None;
							
							//TODO: HEWEY AQUI SE QUITA EL ELEMENTO DEL QUEUE DE RETIROS
							//Quitamos el retiro del queue
							CmQueue.queueList.removeFirst();
							CmQueue.ClosePendingWithdrawal(cmWithdrawalVo.reference);

							if(JcmGlobalData.isDebug) {
								
								switch(CurrentUser.dispenseStatus) {
								case Complete:
									Flow.panelDispenseHolder.setBackground("./images/ScrRetiraBilletes.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.dispenseAmount);									
									break;
								case Partial:
									Flow.panelDispenseHolder.setBackground("./images/Scr7RetiroParcial.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(501, 677, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.dispenseAmount);
																		
									break;
								default:
									break;
								}							
								
								Flow.redirect(Flow.panelDispenseHolder);
								
								PanelDispense.dispense();								
								
								Timer screenTimer = new Timer();
								screenTimer.schedule(new TimerTask() {
									@Override
									public void run() {	  
										//Este es ahi para la broma que se quede unos segundos "Dispensando"									
										
										if(!Ptr.printDispense(Flow.montoRetiro,CurrentUser.getLoginUser())){
											//Si no pudo imprimir lo mandamos a la pantalla de no impresion.
											Flow.redirect(Flow.panelNoTicketHolder,5000,"panelTerminamos");
											Flow.panelTerminamosHolder.screenTimeOut = 5000;
											Flow.panelTerminamosHolder.panelRedirect = "panelIdle";
											
										}
										else {
											Flow.redirect(Flow.panelTerminamosHolder,5000,"panelIdle");
										}	
									}
								}, 3000);
							}
							else {
								//Preparamos el retiro
								switch(CurrentUser.dispenseStatus) {
								case Complete:
									Flow.panelDispenseHolder.setBackground("./images/ScrRetiraBilletes.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.dispenseAmount);
									
									break;
								case Partial:
									Flow.panelDispenseHolder.setBackground("./images/Scr7RetiroParcial.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(501, 677, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.dispenseAmount);
																		
									break;
								default:
									break;
								}

								Flow.redirect(Flow.panelDispenseHolder);
								
								
								System.out.println("ConfirmaRetiro");

								//dispense();
								PanelDispense.dispense();	

								Timer screenTimerDispense = new Timer();
								screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
									@Override
									public void run() {
										//Esperamos que ya se dispensara de todos los jcms.
										if(Flow.jcm1cass1Dispensed && Flow.jcm1cass2Dispensed && Flow.jcm2cass1Dispensed && Flow.jcm2cass2Dispensed) {
											
											RaspiAgent.Broadcast(DeviceEvent.AFD_DispenseOk, "" + JcmGlobalData.montoDispensar);
											RaspiAgent.WriteToJournal("Withdrawal", Flow.montoRetiro,0, "","", "Withdrawal DispenseOk", AccountType.Other, TransactionType.Withdrawal);
											CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
											cmWithdrawalVo.atmId = atmId; 
											cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
											cmWithdrawalVo.password = CurrentUser.loginPassword;
											cmWithdrawalVo.reference = CurrentUser.referencia;
											cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
											cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();

											System.out.println("ConfirmaRetiro");

											if(!Ptr.printDispense(Flow.montoRetiro,CurrentUser.getLoginUser())){
												//Si no pudo imprimir lo mandamos a la pantalla de no impresion.
												Flow.redirect(Flow.panelNoTicketHolder,7000,"panelTerminamos");
												Flow.panelTerminamosHolder.screenTimeOut = 7000;
											}
											else {
												Flow.redirect(Flow.panelTerminamosHolder,7000,"panelIdle");
											}											
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
						Flow.redirect(Flow.panelOperacionCanceladaHolder, 5000, "panelIdle");														
					}
					else {						
						Flow.panelTokenHolder.setBackground("./images/Scr7TokenIncorrecto.png");
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

		case retiroToken:
					if (CurrentUser.tokenConfirmacion.length() > 16)
						return;
					CurrentUser.tokenConfirmacion += digito.getDigit();			
					lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);
					break;
		default:
			break;				
		}	
		break;
        }

	}

	
	
}


