package pentomino.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelPinpad {
	
	public JPanel contentPanel;
	//https://www.javaworld.com/article/2077333/mr-happy-object-teaches-custom-events.html
	
	private List<PinpadListener> _listeners = new ArrayList<PinpadListener>();
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public PanelPinpad() {
		
		contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setBackground(Color.blue);
		contentPanel.setBounds(0, 0, 642, 94);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
		
		contentPanel.setOpaque(false);
		contentPanel.setBackground(Color.GRAY);
		contentPanel.setBounds(936, 0, 946, 1080);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);


		JButton btnPinPad1 = new JButton(new ImageIcon("./images/BTN7_1.png"));
		btnPinPad1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad1.setBounds(50, 47, 260, 220);
		btnPinPad1.setOpaque(false);
		btnPinPad1.setContentAreaFilled(false);
		btnPinPad1.setBorderPainted(false);
		contentPanel.add(btnPinPad1);

		JButton btnPinPad2 = new JButton(new ImageIcon("./images/BTN7_2.png"));
		btnPinPad2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad2.setBounds(359, 47, 262, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad2.setContentAreaFilled(false);
		btnPinPad2.setBorderPainted(false);
		contentPanel.add(btnPinPad2);

		JButton btnPinPad3 = new JButton(new ImageIcon("./images/BTN7_3.png"));
		btnPinPad3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad3.setBounds(684, 47, 262, 220);
		btnPinPad3.setOpaque(false);
		btnPinPad3.setContentAreaFilled(false);
		btnPinPad3.setBorderPainted(false);
		contentPanel.add(btnPinPad3);

		JButton btnPinPad4 = new JButton(new ImageIcon("./images/BTN7_4.png"));
		btnPinPad4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad4.setBounds(50, 302, 259, 220);
		btnPinPad4.setOpaque(false);
		btnPinPad4.setContentAreaFilled(false);
		btnPinPad4.setBorderPainted(false);
		contentPanel.add(btnPinPad4);

		JButton btnPinPad5 = new JButton(new ImageIcon("./images/BTN7_5.png"));
		btnPinPad5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad5.setBounds(359, 302, 262, 220);
		btnPinPad5.setOpaque(false);
		btnPinPad5.setContentAreaFilled(false);
		btnPinPad5.setBorderPainted(false);
		contentPanel.add(btnPinPad5);

		JButton btnPinPad6 = new JButton(new ImageIcon("./images/BTN7_6.png"));
		btnPinPad6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad6.setBounds(674, 302, 262, 220);
		btnPinPad6.setOpaque(false);
		btnPinPad6.setContentAreaFilled(false);
		btnPinPad6.setBorderPainted(false);
		contentPanel.add(btnPinPad6);

		JButton btnPinPad7 = new JButton(new ImageIcon("./images/BTN7_7.png"));
		btnPinPad7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad7.setBounds(50, 557, 259, 220);
		btnPinPad7.setOpaque(false);
		btnPinPad7.setContentAreaFilled(false);
		btnPinPad7.setBorderPainted(false);
		contentPanel.add(btnPinPad7);

		JButton btnPinPad8 = new JButton(new ImageIcon("./images/BTN7_8.png"));
		btnPinPad8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad8.setBounds(359, 557, 267, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad8.setContentAreaFilled(false);
		btnPinPad8.setBorderPainted(false);
		contentPanel.add(btnPinPad8);

		JButton btnPinPad9 = new JButton(new ImageIcon("./images/BTN7_9.png"));
		btnPinPad9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad9.setBounds(664, 557, 272, 220);
		btnPinPad9.setOpaque(false);
		btnPinPad9.setContentAreaFilled(false);
		btnPinPad9.setBorderPainted(false);
		contentPanel.add(btnPinPad9);

		JButton btnPinPad0 = new JButton(new ImageIcon("./images/BTN7_0.png"));
		btnPinPad0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad0.setBounds(359, 812, 267, 220);
		btnPinPad0.setOpaque(false);
		btnPinPad0.setContentAreaFilled(false);
		btnPinPad0.setBorderPainted(false);
		contentPanel.add(btnPinPad0);

		JButton btnPinPadCancel = new JButton(new ImageIcon("./images/BTN7_NO.png"));
		btnPinPadCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadCancel.setBackground(Color.RED);
		btnPinPadCancel.setBounds(50, 812, 259, 220);
		btnPinPadCancel.setOpaque(false);
		btnPinPadCancel.setContentAreaFilled(false);
		btnPinPadCancel.setBorderPainted(false);
		contentPanel.add(btnPinPadCancel);

		JButton btnPinPadConfirmar = new JButton(new ImageIcon("./images/BTN7_OK.png"));		
		btnPinPadConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadConfirmar.setBackground(Color.GREEN);
		btnPinPadConfirmar.setBounds(664, 812, 272, 220);
		btnPinPadConfirmar.setOpaque(false);
		btnPinPadConfirmar.setContentAreaFilled(false);
		btnPinPadConfirmar.setBorderPainted(false);
		contentPanel.add(btnPinPadConfirmar);		
		
		
		btnPinPad1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._1);
			}
		});

		btnPinPad2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._2);
			}
		});

		btnPinPad3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._3);
			}
		});

		btnPinPad4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._4);
			}
		});

		btnPinPad5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._5);
			}
		});

		btnPinPad6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._6);
			}
		});

		btnPinPad7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._7);
			}
		});

		btnPinPad8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._8);
			}
		});

		btnPinPad9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._9);
			}
		});

		btnPinPad0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._0);
			}
		});
		
		

		btnPinPadCancel.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {

				_firePinKeyEvent(PinKey._Cancel);				
				
			}
		});
		
		btnPinPadConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				_firePinKeyEvent(PinKey._Ok);
				
				/*
				switch(CurrentUser.pinpadMode) {
				case loginUser:					
					System.out.println("loginUser");
					if(Flow.currentOperation == jcmOperation.Deposit) {
						System.out.println("loginUser deposit");
						//No ha ingresado su user
						if(CurrentUser.getLoginUser().length() <= 0) {						
							CurrentUser.pinpadMode = PinpadMode.loginUser;						
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
							Flow.cl.show(Flow.panelContainer, "panelDeposito");
							Transactions.BorraCashInOPs(atmId); 								
						}
						else {						
							if(!user.success) {
								System.out.println("loginUser deposit success NO");
								//Si es deposito ya lo dejamos pasar
								CurrentUser.pinpadMode = PinpadMode.None;
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
								Flow.montoDepositado = 0;
								Flow.cl.show(Flow.panelContainer, "panelDeposito");
								Transactions.BorraCashInOPs(atmId); 
							}
							else {	
								if(++CurrentUser.loginAttempts >= 2) {
									//Intentos superados
									CurrentUser.setLoginUser("");
									CurrentUser.loginPassword = "";
									CurrentUser.loginAttempts = 0;
									CurrentUser.tokenAttempts = 0;
									Flow.cl.show(Flow.panelContainer, panelOperacionCancelada,5000,"panelIdle");
								}
								else {
									CurrentUser.setLoginUser("");
									CurrentUser.loginPassword = "";
									Flow.panelLogin.lblLoginUser.setText("");
									Flow.panelLogin.lblLoginPassword.setText("");
									CurrentUser.asteriscos = "";
									CurrentUser.pinpadMode = PinpadMode.loginUser;		
									RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
									Flow.panelLogin.setBackground("./images/Scr7UsuarioIncorrecto.png");
								}
								return;
							}
						}
					}
					else {
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
						switch(Flow.currentOperation) {						
						case Dispense:
							//Si puede retirar lo dejamos pasar
							if(user.allowWithdrawals) {
								//El unico que dejamos pasar para dispensado
								System.out.println("loginPassword success y isvalid y dispense y allowsWithdrawals");
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
								panelToken.remove(panelPinPad);
								panelLogin.remove(panelPinPad);
								panelToken.add(panelPinPad);
								CurrentUser.pinpadMode = PinpadMode.retiroToken;
								panelToken.setBackground("./images/Scr7ConfirmaToken.png");
								Flow.Flow.cl.show(Flow.panelContainer, "panelToken");
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
									Flow.Flow.cl.show(Flow.panelContainer, "panelOperacionCancelada");
									screenTimeout(5000, "panelIdle");
								}
								else {	
									panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
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
							panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
							break;
						}
					}
					else {						
						if(!user.success) {
							System.out.println("loginPassword success NO");

							switch(currentOperation) {

							case Deposit:
								System.out.println("Validando usuario.... 6");
								//Si es deposito ya lo dejamos pasar
								CurrentUser.pinpadMode = PinpadMode.None;
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
								montoDepositado = 0;
								Flow.Flow.cl.show(Flow.panelContainer, "panelDeposito");
								Transactions.BorraCashInOPs(atmId); 
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
								panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
								break;
							default:
								lblLoginUser.setText("");
								lblLoginPassword.setText("");
								CurrentUser.setLoginUser("");
								CurrentUser.loginPassword = "";
								CurrentUser.asteriscos = "";
								CurrentUser.pinpadMode = PinpadMode.loginUser;								
								panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
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
								Flow.Flow.cl.show(Flow.panelContainer, "panelOperacionCancelada");
								screenTimeout(5000, "panelIdle");
							}else {

								CurrentUser.pinpadMode = PinpadMode.loginUser;		
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
								panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
							}
							return;
						}
					}

					break;
				case retiroToken:

					System.out.println("token["  + token + "] valdiation[" + CurrentUser.tokenConfirmacion + "]");
					if(token.equalsIgnoreCase(CurrentUser.tokenConfirmacion)) {

						if(!validateDispense()) {
							System.out.println("No se puede dispensar en este momento.");							
							lblPanelError.setText("No se puede dispensar en este momento.");
							Flow.Flow.cl.show(Flow.panelContainer, panelError,10000,"panelIdle");							
						}
						else {

							CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
							cmWithdrawalVo.atmId = atmId); 
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
								Flow.Flow.cl.show(Flow.panelContainer, panelError,10000,"panelIdle");
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
											Flow.Flow.cl.show(Flow.panelContainer, "panelTerminamos");

											Ptr.print("SI",new HashMap<String,String>());
											Timer screenTimer2 = new Timer();
											screenTimer2.schedule(new TimerTask() {
												@Override
												public void run() {				                
													//Revisamos si hay retiros listos
													Flow.Flow.cl.show(Flow.panelContainer,"panelIdle");											
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
										Flow.Flow.cl.show(Flow.panelContainer, "panelRetiraBilletes");
										break;
									case Partial:
										lblRetiraBilletesMontoDispensarParcial.setText("$" + JcmGlobalData.montoDispensar);
										Flow.Flow.cl.show(Flow.panelContainer, "panelRetiroParcial");
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
												Flow.Flow.cl.show(Flow.panelContainer, "panelTerminamos");
												RaspiAgent.Broadcast(DeviceEvent.AFD_DispenseOk, "" + JcmGlobalData.montoDispensar);
												RaspiAgent.WriteToJournal("Withdrawal", montoRetiro,0, "","", "Withdrawal DispenseOk", AccountType.Other, TransactionType.Withdrawal);
												CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
												cmWithdrawalVo.atmId = atmId); 
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

														Flow.Flow.cl.show(Flow.panelContainer,"panelIdle");

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
				default:
					break;

				}
			*/	
			}
		});
		
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
		
	public JPanel getPanel() {
		return contentPanel;
	}
	
    public synchronized void addPinKeyListener( PinpadListener l ) {
    	//System.out.println("addPinKeyListener");
        _listeners.add( l );
    }
    
    public synchronized void removePinKeyListener( PinpadListener l ) {
    	//System.out.println("removePinKeyListener");
        _listeners.remove( l );
    }
     
    private synchronized void _firePinKeyEvent(PinKey key) {
    	
    	PinpadEvent mood = new PinpadEvent( this, key );
        Iterator<PinpadListener> listeners = _listeners.iterator();
        while( listeners.hasNext() ) {
            ( (PinpadListener) listeners.next() ).pinKeyReceived( mood );
        }
    }


}
