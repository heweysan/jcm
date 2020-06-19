package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CmReverse;
import pentomino.cashmanagement.vo.CmWithdrawal;
import pentomino.common.AccountType;
import pentomino.common.BusinessEvent;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.core.devices.Afd;
import pentomino.core.devices.Ptr;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.jcmagent.BEA;
import pentomino.jcmagent.RaspiAgent;



public class PanelToken extends ImagePanel implements PinpadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static JLabel lblToken = new JLabel(".");
	public static JLabel lblTokenConfirmacion = new JLabel(".");
	public static JLabel lblTokenMensaje = new JLabel(".");
	public static JLabel lblTokenMontoRetiro = new JLabel(".");

	/**
	 * @wbp.parser.constructor
	 */
	public PanelToken(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}	

	
	@Override
	public void ContentPanel() {



		lblTokenConfirmacion.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenConfirmacion.setForeground(Color.WHITE);
		lblTokenConfirmacion.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblTokenConfirmacion.setBounds(190, 773, 583, 66);
		add(lblTokenConfirmacion);


		lblToken.setForeground(Color.WHITE);
		lblToken.setHorizontalAlignment(SwingConstants.CENTER);
		lblToken.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblToken.setBounds(190, 594, 583, 66);
		add(lblToken);


		lblTokenMontoRetiro.setForeground(Color.WHITE);
		lblTokenMontoRetiro.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMontoRetiro.setFont(new Font("Tahoma", Font.BOLD, 99));
		lblTokenMontoRetiro.setBounds(190, 321, 583, 136);
		add(lblTokenMontoRetiro);


		lblTokenMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMensaje.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTokenMensaje.setForeground(Color.WHITE);
		lblTokenMensaje.setBounds(22, 93, 957, 75);
		add(lblTokenMensaje);

		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);

		add(panelPinpad.getPanel());


	}

	public void pinKeyReceived(PinpadEvent event) {

		PinKey digito = event.key();		
		Flow.panelToken.screenTimerReset(7000,Flow.panelOperacionCancelada);

		switch(digito){

		case _Cancel:		
			System.out.println("Es cancel Papawh");        	
			CurrentUser.cleanPinpadData();
			lblToken.setText("");										
			Flow.redirect(Flow.panelOperacionCancelada,5000, Flow.panelIdle);
			break;
		case _Ok:

			switch(CurrentUser.pinpadMode) {


			case retiroToken:

				if(CurrentUser.token.equalsIgnoreCase(CurrentUser.tokenConfirmacion)) {

					//Primero validamos si hay dinero para el retiro y de cuanto
					if(!Afd.validateDispense()) {
						System.out.println("No se puede dispensar en este momento.");							
						PanelError.lblPanelError.setText("No se puede dispensar en este momento.");
						Flow.redirect(Flow.panelError,10000,Flow.panelIdle);							
					}
					else {

						String atmId = Config.GetDirective("AtmId", "");						

						CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
						cmWithdrawalVo.atmId = atmId;
						cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.loginUser);
						cmWithdrawalVo.password = CurrentUser.loginPassword;
						cmWithdrawalVo.reference = CurrentUser.reference;
						cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
						cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
						cmWithdrawalVo.amount = CurrentUser.WithdrawalRequested;//JcmGlobalData.montoDispensar;

						if(!Transactions.ConfirmaRetiro(cmWithdrawalVo)) {
							System.out.println("Usuario sin permiso para dispensar!");
							PanelError.lblPanelError.setText("Lo siento, no pude procesar tu petición");
							Flow.redirect(Flow.panelError,7000,Flow.panelIdle);
						}
						else {

							//Se comprobo que si se puede intentar el dispensado de esa cantidad.
							//Preparamos el retiro.
							CurrentUser.pinpadMode = PinpadMode.None;
							Config.SetPersistence("BoardStatus", "Busy");
							BEA.BusinessEvent(BusinessEvent.SessionStart, false, true,"");
							//Quitamos el retiro del queue
							CmQueue.queueList.removeFirst();
							CmQueue.ClosePendingWithdrawal(cmWithdrawalVo.reference);

							switch(CurrentUser.dispenseStatus) {
							case Complete:
								Flow.panelDispense.setBackground("./images/ScrRetiraBilletes.png");
								PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
								PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + CurrentUser.WithdrawalDispense);									
								break;
							case Partial:
								Flow.panelDispense.setBackground("./images/Scr7RetiroParcial.png");
								PanelDispense.lblRetiraBilletesMontoDispensar.setBounds(1193, 923, 551, 111);
								PanelDispense.lblRetiraBilletesMontoDispensar.setText("$" + CurrentUser.WithdrawalDispense);
								break;
							default:
								break;
							}							

							Flow.redirect(Flow.panelDispense);

							PanelDispense.dispense();	

							Timer screenTimerDispense = new Timer();
							screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
								@Override
								public void run() {
									//Esperamos que ya se dispensara de todos los jcms.
									if(JcmGlobalData.jcm1cass1Dispensed && JcmGlobalData.jcm1cass2Dispensed && JcmGlobalData.jcm2cass1Dispensed && JcmGlobalData.jcm2cass2Dispensed) {
										screenTimerDispense.cancel();
										
										RaspiAgent.Broadcast(DeviceEvent.AFD_DispenseOk, "" + CurrentUser.WithdrawalDispense);
										RaspiAgent.WriteToJournal("FinancialTransacction", CurrentUser.WithdrawalDispense,0, "",CurrentUser.loginUser, "Withdrawal DispenseOk " + JcmGlobalData.denominateInfoToString(), AccountType.Other, TransactionType.Withdrawal);

										System.out.println("CAMBIO [" + CurrentUser.WithdrawalChange + "]");
										if( CurrentUser.WithdrawalChange > 0) {
											CmReverse cmReverseVo = new CmReverse();
											cmReverseVo.atmId = atmId; 
											cmReverseVo.operatorId = Integer.parseInt(CurrentUser.loginUser);
											cmReverseVo.password = CurrentUser.loginPassword;
											cmReverseVo.movementId = CurrentUser.movementId;
											cmReverseVo.amount =  CurrentUser.WithdrawalChange;
											cmReverseVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();

											//NOTIFICAMOS A CM EL REVERSE DE LO QUE SOBRO
											Transactions.WithdrawalReverse(cmReverseVo);
										}

										if(!Ptr.printDispense(CurrentUser.WithdrawalRequested,CurrentUser.loginUser)){
											//Si no pudo imprimir lo mandamos a la pantalla de no impresion.
											Flow.redirect(Flow.panelNoTicket,5000,Flow.panelTerminamos);											
										}
										else {
											Flow.redirect(Flow.panelTerminamos);
										}
										
									}
								}
							}, 1000,2000);
						}
					}
				}
				else {

					CurrentUser.tokenConfirmacion = "";						
					lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);

					if( ++CurrentUser.tokenAttempts >= 2) {						
						CurrentUser.cleanPinpadData();																				
						Flow.redirect(Flow.panelOperacionCancelada, 5000, Flow.panelIdle);														
					}
					else {						
						Flow.panelToken.setBackground("./images/Scr7TokenIncorrecto.png");
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

	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelToken");
		CurrentUser.pinpadMode = PinpadMode.retiroToken;

		
	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelToken");
		
	}

}


