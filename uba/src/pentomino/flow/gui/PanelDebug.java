package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import pentomino.common.Billete;
import pentomino.common.JcmGlobalData;
import pentomino.common.jcmOperation;
import pentomino.core.devices.CertChange;
import pentomino.core.devices.Tio;
import pentomino.flow.EventListenerClass;
import pentomino.flow.Flow;
import pentomino.flow.MyEvent;
import pentomino.flow.protocol;
import pentomino.flow.gui.helpers.ImagePanel;

public class PanelDebug  extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static JLabel lblBilleteIngresado1 = new JLabel("$0");
	public static JLabel lblBilleteIngresado2 = new JLabel("$0");
	public static JLabel lblContadores1 = new JLabel("rec1/0  rec2/0");
	public static JLabel lblContadores2 = new JLabel("rec1/0  rec2/0");
	public static JLabel lblRecycler1 = new JLabel(".");
	public static JLabel lblRecycler2 = new JLabel(".");

	/**
	 * @wbp.parser.constructor
	 */
	public PanelDebug(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}	
	
	
	public PanelDebug(ImageIcon bgPlaceHolder,String name, int _timeout, ImagePanel _redirect) {
		super(bgPlaceHolder,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	


	@Override
	public void ContentPanel() {



		JPanel panel_comandos = new JPanel();
		panel_comandos.setBounds(10, 328, 1886, 706);		
		panel_comandos.setLayout(null);

		JCheckBox chckbxReciclador1 = new JCheckBox("Reciclador 1");
		chckbxReciclador1.setSelected(true);

		chckbxReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador1.setBounds(725, 26, 262, 51);
		add(chckbxReciclador1);

		JCheckBox chckbxReciclador2 = new JCheckBox("Reciclador 2");		
		chckbxReciclador2.setSelected(true);
		chckbxReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador2.setBounds(979, 26, 233, 51);
		add(chckbxReciclador2);

		JButton btnStatusReq = new JButton("Stat Req (11h)");
		btnStatusReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusReq.setBackground(Color.GREEN);
		btnStatusReq.setBounds(10, 11, 157, 50);
		btnStatusReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) 
					Flow.jcms[0].id003_format((byte) 5, (byte) 0x11, Flow.jcms[0].jcmMessage, true);
				if(chckbxReciclador2.isSelected()) 
					Flow.jcms[1].id003_format((byte) 5, (byte) 0x11, Flow.jcms[1].jcmMessage, true);
			}
		});


		JButton btnReset = new JButton("Reset (40h)");
		btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReset.setBackground(Color.GREEN);
		btnReset.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {

				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].currentOpertion = jcmOperation.Reset; 
					//Primero se piden los estatus
					Flow.jcms[0].id003_format((byte)5, protocol.SSR_VERSION, Flow.jcms[0].jcmMessage,true); //SSR_VERSION 0x88
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].currentOpertion = jcmOperation.Reset; 
					//Primero se piden los estatus
					Flow.jcms[1].id003_format((byte)5, protocol.SSR_VERSION, Flow.jcms[1].jcmMessage,true); //SSR_VERSION 0x88
				}
			}
		});
		btnReset.setBounds(318, 11, 167, 50);
		panel_comandos.add(btnReset);

		JButton btnAck = new JButton("Ack (50h)");
		btnAck.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAck.setBackground(Color.GREEN);
		btnAck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) 
					Flow.jcms[0].id003_format((byte) 5, (byte) 0x50, Flow.jcms[0].jcmMessage, true);
				if(chckbxReciclador2.isSelected()) 
					Flow.jcms[1].id003_format((byte) 5, (byte) 0x50, Flow.jcms[1].jcmMessage, true);
			}
		});
		btnAck.setLocation(177, 11);
		btnAck.setSize(131, 50);
		panel_comandos.add(btnAck);

		JButton btnStack1 = new JButton("Stack-1 (41h)");
		btnStack1.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack1.setBackground(Color.ORANGE);
		btnStack1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x41, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnStack1.setBounds(507, 11, 144, 50);
		panel_comandos.add(btnStack1);

		JButton btnStack2 = new JButton("Stack-2 (42h)");
		btnStack2.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack2.setBackground(Color.ORANGE);
		btnStack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x42, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnStack2.setBounds(666, 11, 157, 50);
		panel_comandos.add(btnStack2);

		JButton btnReturn = new JButton("Return (43h)");
		btnReturn.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReturn.setBackground(Color.ORANGE);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x43, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnReturn.setBounds(833, 11, 144, 50);
		panel_comandos.add(btnReturn);

		JButton btnHold = new JButton("Hold (44h)");
		btnHold.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnHold.setBackground(Color.ORANGE);
		btnHold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x44, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnHold.setBounds(987, 11, 144, 50);
		panel_comandos.add(btnHold);

		JButton btnWait = new JButton("Wait (45h)");
		btnWait.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnWait.setBackground(Color.ORANGE);
		btnWait.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x45, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnWait.setBounds(1153, 11, 145, 50);
		panel_comandos.add(btnWait);

		JLabel lblComandosSettingCommands = new JLabel("Setting Commands +Data");
		lblComandosSettingCommands.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblComandosSettingCommands.setBounds(10, 69, 218, 50);
		panel_comandos.add(lblComandosSettingCommands);

		JButton btnEnableDisDenom = new JButton("En/Des Denom (C0h)"); // +DATA
		btnEnableDisDenom.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnableDisDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0xC0, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnEnableDisDenom.setBounds(762, 119, 203, 50);
		panel_comandos.add(btnEnableDisDenom);

		JButton btnSecurotyDenom = new JButton("Security Denom (C1h)");
		btnSecurotyDenom.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSecurotyDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSecurotyDenom.setBounds(975, 119, 213, 50);
		panel_comandos.add(btnSecurotyDenom);

		JButton btnCommunicationMode = new JButton("Communication Mode (C2h)");
		btnCommunicationMode.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCommunicationMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCommunicationMode.setBounds(207, 119, 234, 50);
		panel_comandos.add(btnCommunicationMode);

		JButton btnInhibit = new JButton("Inhibit (C3h)");
		btnInhibit.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnInhibit.setBackground(Color.GREEN);
		btnInhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					System.out.println("JCM1 INHIBIT DESHABILITAMOS ACEPTADOR");
					Flow.jcms[0].jcmMessage[3] = 0x01;
					Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
				}
				if(chckbxReciclador2.isSelected()) {
					System.out.println("JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
					Flow.jcms[1].jcmMessage[3] = 0x01;
					Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
				}
			}
		});
		btnInhibit.setBounds(445, 119, 146, 50);
		panel_comandos.add(btnInhibit);

		JButton btnDirection = new JButton("Direction (C4h)");
		btnDirection.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDirection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDirection.setBounds(1216, 119, 157, 50);
		panel_comandos.add(btnDirection);

		JButton btnOptionalFunc = new JButton("Optional Func (C5h)");
		btnOptionalFunc.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOptionalFunc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnOptionalFunc.setBounds(10, 119, 187, 50);
		panel_comandos.add(btnOptionalFunc);

		JLabel lblComandosSettingStatusRequest = new JLabel("Setting Status Request");
		lblComandosSettingStatusRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblComandosSettingStatusRequest.setBounds(10, 182, 212, 50);
		panel_comandos.add(lblComandosSettingStatusRequest);

		JButton btnOptionalFuncReq = new JButton("Optional Func (85h)");
		btnOptionalFuncReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOptionalFuncReq.setBackground(Color.ORANGE);
		btnOptionalFuncReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x85, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnOptionalFuncReq.setBounds(1059, 225, 200, 50);
		panel_comandos.add(btnOptionalFuncReq);

		JButton btnEnableDisDenomReq = new JButton("En/Des Denom (80h)");
		btnEnableDisDenomReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnableDisDenomReq.setBackground(Color.ORANGE);
		btnEnableDisDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x80, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnEnableDisDenomReq.setBounds(10, 225, 179, 50);
		panel_comandos.add(btnEnableDisDenomReq);

		JButton btnInhibitReq = new JButton("Inhibit (83h)");
		btnInhibitReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnInhibitReq.setBackground(Color.ORANGE);
		btnInhibitReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(" CONSULTA DE INHIBIT");
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x83, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnInhibitReq.setBounds(712, 225, 157, 50);
		panel_comandos.add(btnInhibitReq);

		JButton btnDirectionReq = new JButton("Direction (84h)");
		btnDirectionReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDirectionReq.setBackground(Color.ORANGE);
		btnDirectionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x84, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnDirectionReq.setBounds(882, 225, 167, 50);
		panel_comandos.add(btnDirectionReq);

		JButton btnSecurotyDenomReq = new JButton("Security Denom (81h)");
		btnSecurotyDenomReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSecurotyDenomReq.setBackground(Color.ORANGE);
		btnSecurotyDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x81, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnSecurotyDenomReq.setBounds(199, 225, 213, 50);
		panel_comandos.add(btnSecurotyDenomReq);

		JButton btnCommunicationModeReq = new JButton("Communication Mode (82h)");
		btnCommunicationModeReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCommunicationModeReq.setBackground(Color.ORANGE);
		btnCommunicationModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 5, (byte) 0x82, Flow.jcms[0].jcmMessage, true);
			}
		});
		btnCommunicationModeReq.setBounds(468, 225, 234, 50);
		panel_comandos.add(btnCommunicationModeReq);

		JButton btnVersionRequest = new JButton("Version Request (88h)");
		btnVersionRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnVersionRequest.setBackground(Color.GREEN);
		btnVersionRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].id003_format((byte) 5, (byte) 0x88, Flow.jcms[0].jcmMessage, true);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].id003_format((byte) 5, (byte) 0x88, Flow.jcms[0].jcmMessage, true);
				}

			}
		});
		btnVersionRequest.setBounds(1269, 225, 218, 50);
		panel_comandos.add(btnVersionRequest);

		JButton btnBootVersionrequest = new JButton("Boot Version Request (89h)");
		btnBootVersionrequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnBootVersionrequest.setBackground(Color.GREEN);
		btnBootVersionrequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].id003_format((byte) 5, (byte) 0x89, Flow.jcms[0].jcmMessage, true);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].id003_format((byte) 5, (byte) 0x89, Flow.jcms[1].jcmMessage, true);
				}
			}
		});
		btnBootVersionrequest.setBounds(10, 285, 256, 50);
		panel_comandos.add(btnBootVersionrequest);

		JLabel lblNewLabel_2_1_1 = new JLabel("RECYCLER / EXTENSION (F0h + )");
		lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_1.setBounds(10, 348, 256, 50);
		panel_comandos.add(lblNewLabel_2_1_1);

		JButton btnStatusRequestExt = new JButton("Stat Req Ext (+1Ah)");
		btnStatusRequestExt.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusRequestExt.setBackground(Color.GREEN);
		btnStatusRequestExt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].id003_format_ext((byte) 0x7, (byte) 0xf0, (byte) 0x20, (byte) 0x1a, (byte) 0x1, (byte) 0x2, Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].id003_format_ext((byte) 0x7, (byte) 0xf0, (byte) 0x20, (byte) 0x1a, (byte) 0x1, (byte) 0x2, Flow.jcms[1].jcmMessage);
				}		

			}
		});
		btnStatusRequestExt.setBounds(256, 460, 194, 50);
		panel_comandos.add(btnStatusRequestExt);

		JButton btnStack3 = new JButton("Stack-3 (49h)");
		btnStack3.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack3.setBackground(Color.ORANGE);
		btnStack3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 0x5, (byte) 0x49, Flow.jcms[0].jcmMessage, false);
			}
		});
		btnStack3.setBounds(476, 460, 157, 50);
		panel_comandos.add(btnStack3);

		JButton btnPayOut = new JButton("Pay Out (+4Ah)");
		btnPayOut.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPayOut.setBackground(Color.ORANGE);
		btnPayOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4a, (byte) 0x1, (byte) 0x1,
						Flow.jcms[0].jcmMessage);
			}
		});
		btnPayOut.setBounds(655, 460, 161, 50);
		panel_comandos.add(btnPayOut);

		JButton btnCollect = new JButton("Collect (+4Bh+Data)");
		btnCollect.setBackground(Color.GREEN);
		btnCollect.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCollect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("COLLECT");

				System.out.println("" + JcmGlobalData.rec1bill1Available + ";" + JcmGlobalData.rec1bill2Available + ";" + JcmGlobalData.rec2bill1Available + ";" + JcmGlobalData.rec2bill2Available );

				if(chckbxReciclador1.isSelected()) {

					// primero el inhibit (que siempre debe estar deshabilitado pero por si acaso)
					Flow.jcms[0].jcmMessage[3] = 0x01;
					Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);

					if(JcmGlobalData.rec1bill1Available > 0) {
						System.out.println("Bajando jcm1 cassete 1");
						Flow.jcms[0].currentOpertion = jcmOperation.CollectCass1;
						Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x1,Flow.jcms[0].jcmMessage);	
					}
					else{
						System.out.println("Nada que bajar de jcm1 cassete 1");
						if(JcmGlobalData.rec1bill2Available > 0) {
							System.out.println("Bajando jcm1 cassete 2");
							Flow.jcms[0].currentOpertion = jcmOperation.CollectCass2;
							Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x2,Flow.jcms[0].jcmMessage);
						}
						else {
							System.out.println("Nada que bajar de jcm1 cassete 2");
							Flow.jcms[0].currentOpertion = jcmOperation.None;
						}

					}
				}


				if(chckbxReciclador2.isSelected()) {

					// primero el inhibit (que siempre debe estar deshabilitado pero por si acaso)
					Flow.jcms[1].jcmMessage[3] = 0x01;
					Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);

					if(JcmGlobalData.rec2bill1Available > 0) {
						System.out.println("Bajando jcm2 cassete 1");
						Flow.jcms[1].currentOpertion = jcmOperation.CollectCass1;
						Flow.jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x1,Flow.jcms[1].jcmMessage);
					}
					else{
						System.out.println("Nada que bajar de jcm2 cassete 1");
						if(JcmGlobalData.rec2bill2Available > 0) {
							System.out.println("Bajando jcm2 cassete 2");
							Flow.jcms[1].currentOpertion = jcmOperation.CollectCass2;
							Flow.jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x2,Flow.jcms[1].jcmMessage);
						}
						else {
							System.out.println("Nada que bajar de jcm2 cassete 2");
						}						
					}
				}				

			}
		});
		btnCollect.setBounds(10, 399, 179, 50);
		panel_comandos.add(btnCollect);

		JButton btnClear = new JButton("Clear (+4Ch)");
		btnClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnClear.setBackground(Color.GREEN);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
							Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
							Flow.jcms[1].jcmMessage);
				}
			}
		});
		btnClear.setBounds(195, 399, 144, 50);
		panel_comandos.add(btnClear);

		JButton btnEmergencyStop = new JButton("Emergency Stop (+4Dh)");
		btnEmergencyStop.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEmergencyStop.setBackground(Color.GREEN);
		btnEmergencyStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4D, (byte) 0x1, (byte) 0x2,
							Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4D, (byte) 0x1, (byte) 0x2,
							Flow.jcms[1].jcmMessage);
				}


			}
		});
		btnEmergencyStop.setBounds(351, 399, 197, 50);
		panel_comandos.add(btnEmergencyStop);


		JButton btnUnitInformationRequest = new JButton("Unit Information Req (92h)");
		btnUnitInformationRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUnitInformationRequest.setBackground(Color.ORANGE);
		btnUnitInformationRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format((byte) 0x5, (byte) 0x92, Flow.jcms[0].jcmMessage, false);
			}
		});
		btnUnitInformationRequest.setBounds(10, 460, 236, 50);
		panel_comandos.add(btnUnitInformationRequest);

		JButton btnRecycleRefillModeSetting = new JButton("Recycle Refill Mode Setting (D4h+Data)");
		btnRecycleRefillModeSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleRefillModeSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeSetting.setBounds(1487, 399, 348, 50);
		panel_comandos.add(btnRecycleRefillModeSetting);

		JButton btnRecycleKeySetting = new JButton("Recycle Key Setting (+D1h+Data)");
		btnRecycleKeySetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleKeySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleKeySetting.setBounds(867, 399, 289, 50);
		panel_comandos.add(btnRecycleKeySetting);

		JButton btnRecycleCountSetting = new JButton("Recycle Count Setting (+D2h+Data)");
		btnRecycleCountSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCountSetting.setBackground(Color.LIGHT_GRAY);
		btnRecycleCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleCountSetting.setBounds(1162, 399, 315, 50);
		panel_comandos.add(btnRecycleCountSetting);

		JButton btnRecycleCurrencySetting = new JButton("Recycle Currency Setting (+D0h+Data)");
		btnRecycleCurrencySetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCurrencySetting.setBackground(Color.GREEN);

		btnRecycleCurrencySetting.setBounds(558, 399, 299, 50);
		panel_comandos.add(btnRecycleCurrencySetting);

		JButton btnCurrentCountSetting = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting.setBackground(Color.GREEN);
		btnCurrentCountSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {

					Flow.jcms[0].jcmMessage[7] = 0x02;  //REC1
					Flow.jcms[0].id003_format_ext((byte) 0x0A, (byte) 0xf0, (byte) 0x20, (byte) 0xE2, (byte) 0x00, (byte) 0x0, Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].jcmMessage[7] = 0x02;  //REC1 0x01
					Flow.jcms[1].id003_format_ext((byte) 0x0A, (byte) 0xf0, (byte) 0x20, (byte) 0xE2, (byte) 0x00, (byte) 0x0, Flow.jcms[1].jcmMessage);
				}
			}
		});
		btnCurrentCountSetting.setBounds(1216, 459, 289, 50);
		panel_comandos.add(btnCurrentCountSetting);

		JButton btnRecycleCurrencyReqSetting = new JButton("Recycle Currency Req (+90h)");
		btnRecycleCurrencyReqSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCurrencyReqSetting.setBackground(Color.GREEN);
		btnRecycleCurrencyReqSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[1].jcmMessage);
				}
			}
		});
		btnRecycleCurrencyReqSetting.setBounds(736, 569, 236, 50);
		panel_comandos.add(btnRecycleCurrencyReqSetting);

		JButton btnRecycleSoftwareVersionReq = new JButton("Recycle Software Version Req (+93h)");
		btnRecycleSoftwareVersionReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleSoftwareVersionReq.setBackground(Color.ORANGE);

		btnRecycleSoftwareVersionReq.setBounds(1516, 569, 334, 50);
		panel_comandos.add(btnRecycleSoftwareVersionReq);

		JLabel lblNewLabel_2_1_2 = new JLabel("Setting Status Request (F0h + )");
		lblNewLabel_2_1_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_2.setBounds(17, 518, 262, 50);
		panel_comandos.add(lblNewLabel_2_1_2);

		JButton btnRecycleKeySettingReq = new JButton("Recycle Key Setting Req (+91h)");
		btnRecycleKeySettingReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleKeySettingReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleKeySettingReq.setBounds(994, 569, 262, 50);
		panel_comandos.add(btnRecycleKeySettingReq);

		JButton btnRecycleCountReq = new JButton("Recycle Count Req (+92h)");
		btnRecycleCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCountReq.setBackground(Color.ORANGE);
		btnRecycleCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x92, (byte) 0x00, (byte) 0x0,
						Flow.jcms[0].jcmMessage);
			}
		});
		btnRecycleCountReq.setBounds(1270, 569, 236, 50);
		panel_comandos.add(btnRecycleCountReq);

		JButton btnRecycleRefillModeReq = new JButton("Recycle Refill Mode Req (+94h)");
		btnRecycleRefillModeReq.setFont(new Font("Dialog", Font.BOLD, 12));

		btnRecycleRefillModeReq.setBounds(10, 569, 270, 50);
		panel_comandos.add(btnRecycleRefillModeReq);

		JButton btnTotalCountReq = new JButton("Total Count Req (+A0h)");
		btnTotalCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountReq.setBackground(Color.GREEN);

		btnTotalCountReq.setBounds(301, 569, 205, 50);
		panel_comandos.add(btnTotalCountReq);

		JButton btnTotalCountClear = new JButton("Total Count Clear (+A1h)");
		btnTotalCountClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountClear.setBackground(Color.GREEN);

		btnTotalCountClear.setBounds(516, 569, 210, 50);
		panel_comandos.add(btnTotalCountClear);

		JButton btnCurrentCountReq = new JButton("Current Count Req (+A2h)");
		btnCurrentCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountReq.setBackground(Color.GREEN);

		btnCurrentCountReq.setBounds(10, 630, 236, 50);
		panel_comandos.add(btnCurrentCountReq);

		JButton btnReinhibitch = new JButton("REInhibit (C3h)");
		btnReinhibitch.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReinhibitch.setBackground(Color.GREEN);

		btnReinhibitch.setBounds(608, 119, 144, 50);
		panel_comandos.add(btnReinhibitch);

		JButton btnCurrencyAssingRequest = new JButton("Currency Assing Req (8Ah)");
		btnCurrencyAssingRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCurrencyAssingRequest.setBounds(1496, 223, 224, 50);
		panel_comandos.add(btnCurrencyAssingRequest);
		btnCurrencyAssingRequest.setBackground(Color.ORANGE);

		JButton btnAbreElectroiman = new JButton("ABRE ELECTROIMAN");

		btnAbreElectroiman.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAbreElectroiman.setBounds(277, 632, 256, 48);
		panel_comandos.add(btnAbreElectroiman);

		JButton btnCierraElectroiman = new JButton("CIERRA ELECTROIMAN");

		btnCierraElectroiman.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCierraElectroiman.setBounds(584, 630, 249, 48);
		panel_comandos.add(btnCierraElectroiman);

		JLabel lblNewLabel = new JLabel("Set denomination (debe estar DISABLE (INHIBIT))");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(559, 353, 425, 41);
		panel_comandos.add(lblNewLabel);

		JButton btnComandosRegresar = new JButton("Regresar");
		btnComandosRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelDeposito);
			}
		});
		btnComandosRegresar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnComandosRegresar.setBackground(Color.ORANGE);
		btnComandosRegresar.setBounds(1332, 26, 207, 73);
		add(btnComandosRegresar);

		JPanel panelJCM1 = new JPanel();
		panelJCM1.setBackground(Color.BLACK);
		panelJCM1.setLayout(null);
		panelJCM1.setOpaque(false);
		panelJCM1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM1.setBounds(10, 92, 630, 225);
		add(panelJCM1);

		//JLabel lblRecycler1 = new JLabel(".");
		lblRecycler1.setForeground(Color.WHITE);
		lblRecycler1.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblRecycler1.setBounds(10, 102, 180, 53);
		panelJCM1.add(lblRecycler1);

		JLabel lblTitleReciclador1 = new JLabel("Reciclador 1");
		lblTitleReciclador1.setForeground(Color.WHITE);
		lblTitleReciclador1.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblTitleReciclador1.setBounds(10, 11, 246, 80);
		panelJCM1.add(lblTitleReciclador1);

		JLabel lblTxtBill1 = new JLabel("Billete:");
		lblTxtBill1.setForeground(Color.WHITE);
		lblTxtBill1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill1.setBounds(10, 166, 144, 46);
		panelJCM1.add(lblTxtBill1);


		lblBilleteIngresado1.setForeground(Color.WHITE);
		lblBilleteIngresado1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado1.setBounds(164, 166, 226, 46);
		panelJCM1.add(lblBilleteIngresado1);

		//JLabel lblContadores1 = new JLabel("rec1/0  rec2/0");
		lblContadores1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblContadores1.setForeground(Color.WHITE);
		lblContadores1.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblContadores1.setBounds(164, 102, 456, 49);
		panelJCM1.add(lblContadores1);

		JButton btnReiniciarJcm1 = new JButton("REINICIAR");
		btnReiniciarJcm1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...				
				Flow.jcms[0].id003_format((byte)5, protocol.SSR_VERSION, Flow.jcms[0].jcmMessage,true); //SSR_VERSION 0x88
			}
		});
		btnReiniciarJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm1.setBounds(281, 11, 209, 80);
		panelJCM1.add(btnReiniciarJcm1);

		JPanel panelJCM2 = new JPanel();
		panelJCM2.setBackground(Color.BLACK);
		panelJCM2.setLayout(null);
		panelJCM2.setOpaque(false);
		panelJCM2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM2.setBounds(667, 92, 630, 225);
		add(panelJCM2);

		//JLabel lblRecycler2 = new JLabel(".");
		lblRecycler2.setForeground(Color.WHITE);
		lblRecycler2.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblRecycler2.setBounds(10, 102, 210, 53);
		panelJCM2.add(lblRecycler2);

		JLabel lblTitleReciclador2 = new JLabel("Reciclador 2");
		lblTitleReciclador2.setForeground(Color.WHITE);
		lblTitleReciclador2.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblTitleReciclador2.setBounds(10, 11, 261, 80);
		panelJCM2.add(lblTitleReciclador2);

		//JLabel lblBilleteIngresado2 = new JLabel("$0");
		lblBilleteIngresado2.setForeground(Color.WHITE);
		lblBilleteIngresado2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado2.setBounds(164, 166, 226, 46);
		panelJCM2.add(lblBilleteIngresado2);

		JLabel lblTxtBill2 = new JLabel("Billete:");
		lblTxtBill2.setForeground(Color.WHITE);
		lblTxtBill2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill2.setBounds(10, 166, 144, 46);
		panelJCM2.add(lblTxtBill2);

		//JLabel lblContadores2 = new JLabel("rec1/0  rec2/0");
		lblContadores2.setHorizontalAlignment(SwingConstants.TRAILING);
		lblContadores2.setForeground(Color.WHITE);
		lblContadores2.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblContadores2.setBounds(164, 110, 430, 37);
		panelJCM2.add(lblContadores2);

		JButton btnReiniciarJcm2 = new JButton("REINICIAR");
		btnReiniciarJcm2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[1].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...
				Flow.jcms[1].id003_format((byte)5, protocol.SSR_VERSION, Flow.jcms[1].jcmMessage,true); //SSR_VERSION 0x88
			}
		});
		btnReiniciarJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm2.setBounds(281, 11, 209, 80);
		panelJCM2.add(btnReiniciarJcm2);

		add(panel_comandos);		

		JButton btnAlarmaOff = new JButton("ALARMA OFF");
		btnAlarmaOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.miTio.alarmOff();
			}
		});
		btnAlarmaOff.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAlarmaOff.setBounds(1412, 632, 203, 48);
		panel_comandos.add(btnAlarmaOff);

		JButton btnAlarmaOn = new JButton("ALARMA ON");
		btnAlarmaOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.miTio.alarmOn();
			}
		});
		btnAlarmaOn.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAlarmaOn.setBounds(1645, 632, 203, 48);
		panel_comandos.add(btnAlarmaOn);

		JButton btnCurrentCountSetting_1 = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {

					Flow.jcms[0].jcmMessage[7] = 0x01;  //REC1
					Flow.jcms[0].id003_format_ext((byte) 0x0A, (byte) 0xf0, (byte) 0x20, (byte) 0xE2, (byte) 0x00, (byte) 0x0, Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].jcmMessage[7] = 0x01;  //REC1 0x01
					Flow.jcms[1].id003_format_ext((byte) 0x0A, (byte) 0xf0, (byte) 0x20, (byte) 0xE2, (byte) 0x00, (byte) 0x0, Flow.jcms[1].jcmMessage);
				}
			}
		});
		btnCurrentCountSetting_1.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountSetting_1.setBackground(Color.GREEN);
		btnCurrentCountSetting_1.setBounds(1561, 460, 289, 50);
		panel_comandos.add(btnCurrentCountSetting_1);

		JButton btnCierraBoveda = new JButton("CIERRA BOVEDA");
		btnCierraBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tio.safeOpen = false;
				EventListenerClass.fireMyEvent(new MyEvent("SafeClosed"));				
			}
		});
		btnCierraBoveda.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCierraBoveda.setBounds(875, 630, 256, 48);
		panel_comandos.add(btnCierraBoveda);

		JButton btnAbreBoveda = new JButton("ABRE BOVEDA");
		btnAbreBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tio.safeOpen = true;
				EventListenerClass.fireMyEvent(new MyEvent("SafeOpen"));
			}
		});
		btnAbreBoveda.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAbreBoveda.setBounds(1150, 630, 249, 48);
		panel_comandos.add(btnAbreBoveda);
		
		JButton btnPaseProduccion = new JButton("PRODUCCION");
		btnPaseProduccion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CertChange.paseProduccion();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnPaseProduccion.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnPaseProduccion.setBackground(Color.RED);
		btnPaseProduccion.setBounds(1021, 286, 238, 100);
		panel_comandos.add(btnPaseProduccion);
		
		JButton btnPaseQA = new JButton("QA");
		btnPaseQA.setBackground(Color.CYAN);
		btnPaseQA.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnPaseQA.setBounds(1482, 284, 238, 100);
		panel_comandos.add(btnPaseQA);

		JButton btnAdminLogin = new JButton("ADMIN LOGIN");
		btnAdminLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminLogin);
			}
		});
		btnAdminLogin.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnAdminLogin.setBounds(1549, 26, 347, 122);
		add(btnAdminLogin);

		JButton btnNewButton = new JButton("ADMIN MENU");
		btnNewButton.setBounds(1549, 195, 347, 122);
		add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 30));



		/** EVENTS  **/
		btnRecycleCurrencySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/*
				SYNC [FCh]
				LNG [xxh]
				EXT CMD [F0h] Extension Command
				UNIT [20h] Unit type [20h]: RECYCLER
				CMD [D0h] RECYCLE CURRENCY SETTING
				
				DATA1 [xxh] RECYCLE CURRENCY DATA
				* Refer to Table DATAn [xxh] for Recycle denomination data format.
				CRC Check Message integrity via CRC method (2bytes)
				
				RECYCLE CURRENCY DATA format
				DATAn
				[xxh] Recycle Denomination DATA1 (1byte)
				[xxh] Recycle Denomination DATA2 (1byte) … Reserved
				[xxh] Recycle Box No. n (1byte)
				 n = The Number of Recycle boxes.
				
				0xD0; DENOM; RESEVADO (0x0h); REC_BOX
				
				 e.g., The data part of this command, when setting 10€ for Recycle Box No. 1 and
				50€ for Recycle Box No. 2, would be as follows (when 10€ is assigned for bit
				2 and 50€ is for bit 4)
				04h + 00h + 01h + 10h + 00h + 02h

				DENOMS: 
						0x02:20 
						0x04:50 
						0x08:100 
						0x10:200 
						0x20:500;
				 */				
				
				if(chckbxReciclador1.isSelected()) {				
					Flow.jcms[0].jcmMessage[7] = 0x01; 
					Flow.jcms[0].jcmMessage[8] = Billete.$50;  
					Flow.jcms[0].jcmMessage[9] = 0x00;
					Flow.jcms[0].jcmMessage[10] = 0x02;												  
					Flow.jcms[0].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, Billete.$50, (byte) 0x0,Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].jcmMessage[7] = 0x01;  
					Flow.jcms[1].jcmMessage[8] = Billete.$50;
					Flow.jcms[1].jcmMessage[9] = 0x00;
					Flow.jcms[1].jcmMessage[10] = 0x02;												  
					Flow.jcms[1].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, Billete.$50, (byte) 0x0,Flow.jcms[1].jcmMessage);
				}
			}
		});

		btnTotalCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {	
					Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
							Flow.jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {	
					Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
							Flow.jcms[1].jcmMessage);
				}
			}
		});		

		btnRecycleSoftwareVersionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x93, (byte) 0x00, (byte) 0x0,
						Flow.jcms[0].jcmMessage);
			}
		});

		btnRecycleRefillModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});


		btnTotalCountClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected())
					Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
							Flow.jcms[0].jcmMessage);
				if(chckbxReciclador2.isSelected())
					Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
							Flow.jcms[1].jcmMessage);
			}
		});

		btnCurrentCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected())
					Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
							Flow.jcms[0].jcmMessage);
				if(chckbxReciclador2.isSelected())
					Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
							Flow.jcms[1].jcmMessage);
			}
		});		

		btnReinhibitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("RE INHIBIT");
				if(chckbxReciclador1.isSelected()) {
					Flow.jcms[0].jcmMessage[3] = 0x00;
					Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
				}
				if(chckbxReciclador2.isSelected()) {
					Flow.jcms[1].jcmMessage[3] = 0x00;
					Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
				}
			}
		});

		btnAbreElectroiman.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Flow.miTio.abreElectroiman();				
			}
		});


		btnCierraElectroiman.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				Flow.miTio.cierraElectroiman();				
			}
		});


		btnCurrencyAssingRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(chckbxReciclador1.isSelected())
					Flow.jcms[0].id003_format((byte) 5, (byte) 0x8A, Flow.jcms[0].jcmMessage, true);
				if(chckbxReciclador2.isSelected())
					Flow.jcms[1].id003_format((byte) 5, (byte) 0x8A, Flow.jcms[1].jcmMessage, true);
			}
		});


	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelDebug]");

	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelDebug");

	}
}
