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
import pentomino.common.Afd;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.Ptr;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.DispenseStatus;
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

					if(!validateDispense()) {
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

							//TODO: HEWEY AQUI SE QUITA EL ELEMENTO DEL QUEUE DE RETIROS
							//Quitamos el retiro del queue
							CmQueue.queueList.removeFirst();
							CmQueue.ClosePendingWithdrawal(cmWithdrawalVo.reference);

							if(JcmGlobalData.isDebug) {
								
								if(Flow.montoRetiro % 2 == 0) {
									JcmGlobalData.montoDispensar = Flow.montoRetiro;									
									Flow.panelDispenseHolder.setBackground("./images/ScrRetiraBilletes.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("");
								}else {
									JcmGlobalData.montoDispensar = (Flow.montoRetiro-1) / 2;
									Flow.panelDispenseHolder.setBackground("./images/Scr7RetiroParcial.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);//.setBounds(501, 677, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.montoDispensar);									
								}
								
								Flow.redirect(Flow.panelDispenseHolder);
								
								Timer screenTimer = new Timer();
								screenTimer.schedule(new TimerTask() {
									@Override
									public void run() {				  
																			
										
										if(!Ptr.printDispense(Flow.montoRetiro,CurrentUser.getLoginUser())){
											//Si no pudo imprimir lo mandamos a la pantalla de no impresion.
											Flow.redirect(Flow.panelNoTicketHolder,7000,"panelTerminamos");
											Flow.panelTerminamosHolder.screenTimeOut = 7000;
										}
										else {
											Flow.redirect(Flow.panelTerminamosHolder,7000,"panelIdle");
										}	
										
										Timer screenTimer2 = new Timer();
										screenTimer2.schedule(new TimerTask() {
											@Override
											public void run() {				                
												//Revisamos si hay retiros listos
												Flow.redirect(Flow.panelIdleHolder);
																							
												screenTimer.cancel();
												screenTimer2.cancel();
											}
										}, 3000);

									}
								}, 3000);
							}
							else {


								//Preparamos el retiro
								CurrentUser.token = "";
								CurrentUser.pinpadMode = PinpadMode.None;
								
								switch(CurrentUser.dispenseStatus) {
								case Complete:
									Flow.panelDispenseHolder.setBackground("./images/ScrRetiraBilletes.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.montoDispensar);
									
									break;
								case Partial:
									Flow.panelDispenseHolder.setBackground("./images/Scr7RetiroParcial.png");
									PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(501, 677, 622, 153);
									PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + JcmGlobalData.montoDispensar);
																		
									break;
								default:
									break;
								}

								Flow.redirect(Flow.panelDispenseHolder);
								System.out.println("ConfirmaRetiro");

								dispense();


								Timer screenTimerDispense = new Timer();
								screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
									@Override
									public void run() {
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

	private void dispense() {
		System.out.println("Retirar Flow.jcms[0].jcmCass1 [" + Flow.jcms[0].jcmCass1 + "] Flow.jcms[0].jcmCass2 [" + Flow.jcms[0].jcmCass2 + "] Flow.jcms[1].jcmCass1 [" + Flow.jcms[1].jcmCass1 + "] Flow.jcms[1].jcmCass2 [" + Flow.jcms[1].jcmCass2 + "]" );

		// Iniciamos el dispensado
		Flow.jcm1cass1Dispensed = false;
		Flow.jcm1cass2Dispensed = false;
		Flow.jcm2cass1Dispensed = false;
		Flow.jcm2cass2Dispensed = false;

		//Checamos para JCM1
		if(Flow.jcms[0].jcmCass1 > 0 || Flow.jcms[0].jcmCass2 > 0) {				
			//El primer denominate se genera aqui:
			RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (Flow.jcms[0].jcmCass1 * Flow.jcms[0].contadores.Cass1Denom));
			RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + Flow.jcms[0].jcmCass1  + "x" +  Flow.jcms[0].contadores.Cass1Denom);
			System.out.println("Deshabilitamos JCM1 para dispense");
			Flow.jcms[0].currentOpertion = jcmOperation.Dispense;

			// primero el inhibit
			Flow.jcms[0].jcmMessage[3] = 0x01;
			Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
		}
		else {
			Flow.jcm1cass1Dispensed = true;
			Flow.jcm1cass2Dispensed = true;
		}


		Timer screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//System.out.println("Esperando que termine dispense 1 ");
				if(Flow.jcm1cass1Dispensed && Flow.jcm1cass2Dispensed) {
					System.out.println("Dispense 1 terminado");
					if(Flow.jcms[1].jcmCass1 > 0 || Flow.jcms[1].jcmCass2 > 0) {

						//El primer denominate se genera aqui:
						RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (Flow.jcms[1].jcmCass1 * Flow.jcms[1].contadores.Cass1Denom));
						RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + Flow.jcms[1].jcmCass1  + "x" +  Flow.jcms[1].contadores.Cass1Denom);

						System.out.println("Deshabilitamos JCM2 para dispense");
						Flow.jcms[1].currentOpertion = jcmOperation.Dispense;

						// primero el inhibit
						Flow.jcms[1].jcmMessage[3] = 0x01;
						Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
					}
					else{						
						System.out.println("Nada que dispensar del JCM[2]");
						Flow.jcm2cass1Dispensed = true;
						Flow.jcm2cass2Dispensed = true;
					}
					screenTimerDispense.cancel();
				}            	
			}
		}, 1000,1000);
	}
	
	boolean validateDispense() {

		if(JcmGlobalData.isDebug) {
			CurrentUser.dispenseStatus = DispenseStatus.Complete;
			return true;
		}

		if(Flow.montoRetiro < 20) {
			CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
			System.out.println("De origen no se puede dispensar [menor a 20]");
			return false;
		}        

		double sobrante = 0;
		if(Flow.montoRetiro < 40) {
			sobrante = Flow.montoRetiro - 20;
		}
		else {
			sobrante = Flow.montoRetiro % 10;
		}

		System.out.println("sobrante [" + sobrante + "]");

		if(sobrante > 0)
			CurrentUser.dispenseStatus = DispenseStatus.Partial;


		JcmGlobalData.montoDispensar = Flow.montoRetiro - sobrante;

		//Checamos los contadores actuales        
		Flow.actualizaContadoresRecicladores();       

		double disponible = JcmGlobalData.totalCashInRecyclers1 + JcmGlobalData.totalCashInRecyclers2;
		System.out.println("Disponible para dispensar [" + disponible + "]");

		//No hay dinero para dispensar
		if(disponible == 0) {
			CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}

		//Checamos que tenga algo de dinero.
		if(Flow.jcms[0].contadores.Cass1Available == 0 && Flow.jcms[0].contadores.Cass2Available == 0 && Flow.jcms[1].contadores.Cass1Available == 0 && Flow.jcms[1].contadores.Cass2Available == 0){
			System.out.println("No hay dinero en los caseteros para dispensar");
			CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}


		//Si es mas de lo que tenemos dispensamos todo lo que tenemos como parcial.
		if(Flow.montoRetiro > disponible) {			
			JcmGlobalData.montoDispensar = disponible;
			System.out.println("Retiro parcial mas dinero del que hay");
			CurrentUser.dispenseStatus = DispenseStatus.Partial;
			Flow.jcms[0].jcmCass1 = Flow.jcms[0].contadores.Cass1Available;
			Flow.jcms[0].jcmCass2 = Flow.jcms[0].contadores.Cass2Available;
			Flow.jcms[1].jcmCass1 = Flow.jcms[1].contadores.Cass1Available;
			Flow.jcms[1].jcmCass2 = Flow.jcms[1].contadores.Cass2Available;
			return true;			
		}		


		System.out.println("Solicitado [" + Flow.montoRetiro + "] disponible [" + disponible + "] sobrante [" + (Flow.montoRetiro - disponible) + "]");


		int iBuffer = 0;

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[0].contadores.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[0].contadores.Cass1Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass1Denom, Flow.jcms[0].contadores.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass1Denom, Flow.jcms[0].contadores.Cass1Available);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[0].contadores.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[0].contadores.Cass2Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass2Denom, Flow.jcms[0].contadores.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass2Denom, Flow.jcms[0].contadores.Cass2Available + iBuffer);


		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[1].contadores.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[1].contadores.Cass1Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass1Denom, Flow.jcms[1].contadores.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass1Denom, Flow.jcms[1].contadores.Cass1Available + iBuffer);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[1].contadores.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[1].contadores.Cass2Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass2Denom, Flow.jcms[1].contadores.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass2Denom, Flow.jcms[1].contadores.Cass2Available + iBuffer);


		if(Afd.denominateInfo(JcmGlobalData.montoDispensar)) {
			//TODO: Ahorita TODOS los cassettes deben ser diferentes...
			//Se puede dispensar

			//revisamos si hay cambio o no.
			if(JcmGlobalData.dispenseChange > 0){				//Dispensado parcial				
				System.out.println("HAY CAMBIO [" +JcmGlobalData.dispenseChange + "]" );
				CurrentUser.dispenseStatus = DispenseStatus.Partial;
			}
			else
				CurrentUser.dispenseStatus = DispenseStatus.Complete;

			//Seteamos los valores para cada casetero			
			Flow.jcms[0].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].contadores.Cass1Denom, 0);
			Flow.jcms[0].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].contadores.Cass2Denom, 0);
			Flow.jcms[1].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].contadores.Cass1Denom, 0);
			Flow.jcms[1].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].contadores.Cass2Denom, 0);

		}
		else {			
			CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
			return false;
		}

		System.out.println("jcm1cass1 [" + Flow.jcms[0].jcmCass1 + "] jcm1cass2 [" + Flow.jcms[0].jcmCass2 + "] jcm2cass1 [" + Flow.jcms[1].jcmCass1 + "]jcm2cass2 [" + Flow.jcms[1].jcmCass2 + "]" );

		return true;

	}
}


