import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeoutException;
import gnu.io.*;
//import rabbitClient.Demo;
import rabbitClient.WriteToJournal;

import javax.comm.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.lang.System;
import java.text.DecimalFormat;

import com.google.gson.Gson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

import pentomino.common.*;
import rabbitClient.*;


public class uba {
	
	private static Gson gson = new Gson();
	
	private static final Logger logger = LogManager.getLogger(uba.class.getName());
	
	private static String montoDispensar	= "0";	

	static long maxDispenseAmmount = 5000;
	
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					uba window = new uba();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				WriteToJournal.JcmWriteToJournal("", 0, 0, "", "", "");
				
			}
		});
	}

	/**
	 * Create the application.
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public uba() throws IOException, TimeoutException {
	
		/*
		ConnectRabbit myRabbit = new ConnectRabbit("11.50.0.8", 5672, "vh_switch", "pulsar_atm", "p4ssw0rd", 3);
		
		myRabbit.OpenConnection("11.50.0.8", 5672, "pulsar_atm", "p4ssw0rd", "vh_switch", true, 32000);
		*/
		
		
		logger.debug("this is an DEBUG message");
		logger.info("this is an INFO message");
		logger.warn("this is an WARN message");
		logger.error("this is an ERROR message");		
		logger.fatal("this is an FATAL message");		
		     
		ServerEntry myEntry = new ServerEntry();
		myEntry.setAgent("EJA");
		myEntry.setData("ESTE MENSAJE LLEGO DESDE JAVA");
		myEntry.setCommand("MY COMMAND");
		myEntry.setId("ID");
		myEntry.setTimestamp(java.lang.System.currentTimeMillis());
		myEntry.setEvent("WITHDRAWAL");
		myEntry.setDeviceType(DeviceType.DEP.toString());
      
		System.out.println(gson.toJson(myEntry));
			
		Config myConf = new Config();
		myConf.getPulsarParam("HostRabbit");
		
		
		initialize();		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		/*
		try {
			    Socket echoSocket = new Socket("127.0.0.1", 11000);
			    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			    echoSocket.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		
		frame = new JFrame();
		frame.setTitle("RedBlu JCM Driver (ID003)");
		frame.setBounds(100, 100, 1010, 779);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null); 
		
		final uart srlprt = new uart();		
		final uart2 srlprt2 = new uart2();  //HEWEY SAN AQUI: Pruebas recepcion
		
		JPanel panel_conexion = new JPanel();
		panel_conexion.setBorder(new TitledBorder(null, "Puerto Serial", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_conexion.setBounds(0, 3, 643, 100);
		frame.getContentPane().add(panel_conexion);
		panel_conexion.setLayout(null);
		
		JButton btnOpen = new JButton("Abrir");
		btnOpen.setBounds(394, 23, 63, 25);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		panel_conexion.add(btnOpen);
		
		JLabel lblNewLabel = new JLabel("Baud Rate:");
		lblNewLabel.setBounds(12, 23, 88, 25);
		panel_conexion.add(lblNewLabel);
		
		JLabel lblPort = new JLabel("Puerto:");
		lblPort.setBounds(220, 23, 44, 25);
		panel_conexion.add(lblPort);
		
		String[] BaudArray = {"9600","19200","38400"};
		final JComboBox<String> cbBaudRate = new JComboBox(BaudArray);
		cbBaudRate.setBounds(98, 23, 115, 25);
		panel_conexion.add(cbBaudRate);
		
		final JComboBox<String> comboBox_1 = new JComboBox<String>();
		comboBox_1.setBounds(265, 23, 117, 25);
		panel_conexion.add(comboBox_1);
		
		JPanel panel_firmware = new JPanel();
		panel_firmware.setBounds(12, 52, 357, 44);
		panel_conexion.add(panel_firmware);
		panel_firmware.setBorder(new TitledBorder(null, "Firmware:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_firmware.setLayout(null);
		
		final JLabel label = new JLabel(".");
		label.setBounds(10, 18, 329, 15);
		panel_firmware.add(label);
		
		final JCheckBox cbLoopBack = new JCheckBox("LoopBack\r\n");
		cbLoopBack.setBounds(514, 23, 87, 24);
		panel_conexion.add(cbLoopBack);
		
		JPanel panelRecycler = new JPanel();
		panelRecycler.setLayout(null);
		panelRecycler.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(128, 128, 128)), "Recycler:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelRecycler.setBounds(377, 52, 257, 44);
		panel_conexion.add(panelRecycler);
		
		final JLabel lblRecyclerVersion = new JLabel(".");
		lblRecyclerVersion.setBounds(10, 19, 225, 14);
		panelRecycler.add(lblRecyclerVersion);
		
				
	
		JPanel panel_estatus = new JPanel();
		panel_estatus.setBorder(new TitledBorder(null, "Estatus", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_estatus.setBounds(0, 102, 184, 64);
		frame.getContentPane().add(panel_estatus);
		panel_estatus.setLayout(null);
		
		JLabel lblBill = new JLabel("Billete:");
		lblBill.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblBill.setBounds(10, 22, 67, 31);
		panel_estatus.add(lblBill);
		
		final JLabel lblNewLabel_1 = new JLabel("$100");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(104, 26, 107, 23);
		panel_estatus.add(lblNewLabel_1);
				
		
		JPanel panel_comandos = new JPanel();
		panel_comandos.setBorder(new TitledBorder(null, "Comandos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_comandos.setBounds(0, 275, 992, 465);
		panel_comandos.setLayout(null);
		frame.getContentPane().add(panel_comandos);
		
		JButton btnStatusReq = new JButton("Stat Req (11h)");
		btnStatusReq.setBackground(Color.ORANGE);
		btnStatusReq.setBounds(10, 28, 117, 25);
		btnStatusReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x11, protocol.jcmMessage,true);				
			}
		});
		
		panel_comandos.add(btnStatusReq);
		
		JButton btnReset = new JButton("Reset (40h)");
		btnReset.setBackground(Color.ORANGE);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x40, protocol.jcmMessage,true);
			}
		});
		btnReset.setBounds(246, 28, 100, 25);
		panel_comandos.add(btnReset);
		
		JButton btnAck = new JButton("Ack (50h)");
		btnAck.setBackground(Color.ORANGE);
		btnAck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x50, protocol.jcmMessage,true);	
			}
		});
		btnAck.setLocation(139, 28);
		btnAck.setSize(95, 25);
		panel_comandos.add(btnAck);
		
		JButton btnStack1 = new JButton("Stack-1 (41h)");
		btnStack1.setBackground(Color.ORANGE);
		btnStack1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x41, protocol.jcmMessage,true);	
			}
		});
		btnStack1.setBounds(358, 28, 117, 25);
		panel_comandos.add(btnStack1);
		
		JButton btnStack2 = new JButton("Stack-2 (42h)");
		btnStack2.setBackground(Color.ORANGE);
		btnStack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x42, protocol.jcmMessage,true);	
			}
		});
		btnStack2.setBounds(487, 28, 122, 25);
		panel_comandos.add(btnStack2);
		
		JButton btnReturn = new JButton("Return (43h)");
		btnReturn.setBackground(Color.ORANGE);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x43, protocol.jcmMessage,true);	
			}
		});
		btnReturn.setBounds(621, 28, 116, 25);
		panel_comandos.add(btnReturn);
		
		JButton btnHold = new JButton("Hold (44h)");
		btnHold.setBackground(Color.ORANGE);
		btnHold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x44, protocol.jcmMessage,true);	
			}
		});
		btnHold.setBounds(749, 28, 108, 25);
		panel_comandos.add(btnHold);
		
		JButton btnWait = new JButton("Wait (45h)");
		btnWait.setBackground(Color.ORANGE);
		btnWait.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x45, protocol.jcmMessage,true);	
			}
		});
		btnWait.setBounds(869, 28, 117, 25);
		panel_comandos.add(btnWait);
		
		JLabel lblNewLabel_2 = new JLabel("Setting Commands +Data");
		lblNewLabel_2.setBounds(10, 66, 167, 14);
		panel_comandos.add(lblNewLabel_2);
		
		JButton btnEnableDisDenom = new JButton("En/Des Denom (C0h)"); //+DATA
		btnEnableDisDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0xC0, protocol.jcmMessage,true);	
			}
		});
		btnEnableDisDenom.setBounds(10, 92, 167, 25);
		panel_comandos.add(btnEnableDisDenom);
		
		JButton btnSecurotyDenom = new JButton("Security Denom (C1h)");
		btnSecurotyDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSecurotyDenom.setBounds(189, 92, 171, 25);
		panel_comandos.add(btnSecurotyDenom);
		
		JButton btnCommunicationMode = new JButton("Communication Mode (C2h)");
		btnCommunicationMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCommunicationMode.setBounds(364, 92, 205, 25);
		panel_comandos.add(btnCommunicationMode);
		
		JButton btnInhibit = new JButton("Inhibit (C3h)");
		btnInhibit.setBackground(Color.ORANGE);
		btnInhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("INHIBIT");
				protocol.jcmMessage[3] = 0x01;
				srlprt.id003_format((byte)0x6, (byte)0xC3, protocol.jcmMessage,false);
			}
		});
		btnInhibit.setBounds(581, 92, 108, 25);
		panel_comandos.add(btnInhibit);
		
		JButton btnDirection = new JButton("Direction (C4h)");
		btnDirection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDirection.setBounds(701, 92, 125, 25);
		panel_comandos.add(btnDirection);
		
		JButton btnOptionalFunc = new JButton("Optional Func (C5h)");
		btnOptionalFunc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnOptionalFunc.setBounds(838, 92, 152, 25);
		panel_comandos.add(btnOptionalFunc);
		
		JLabel lblNewLabel_2_1 = new JLabel("Setting Status Request");
		lblNewLabel_2_1.setBounds(10, 129, 167, 14);
		panel_comandos.add(lblNewLabel_2_1);
		
		JButton btnOptionalFuncReq = new JButton("Optional Func (85h)");
		btnOptionalFuncReq.setBackground(Color.ORANGE);
		btnOptionalFuncReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x85, protocol.jcmMessage,true);	
			}
		});
		btnOptionalFuncReq.setBounds(10, 192, 179, 25);
		panel_comandos.add(btnOptionalFuncReq);
		
		JButton btnEnableDisDenomReq = new JButton("En/Des Denom (80h)");
		btnEnableDisDenomReq.setBackground(Color.ORANGE);
		btnEnableDisDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x80, protocol.jcmMessage,true);	
			}
		});
		btnEnableDisDenomReq.setBounds(10, 155, 179, 25);
		panel_comandos.add(btnEnableDisDenomReq);
		
		JButton btnInhibitReq = new JButton("Inhibit (83h)");
		btnInhibitReq.setBackground(Color.ORANGE);
		btnInhibitReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(" CONSULTA DE INHIBIT");
				srlprt.id003_format((byte)5, (byte)0x83, protocol.jcmMessage,true);	
			}
		});
		btnInhibitReq.setBounds(609, 155, 157, 25);
		panel_comandos.add(btnInhibitReq);
		
		JButton btnDirectionReq = new JButton("Direction (84h)");
		btnDirectionReq.setBackground(Color.ORANGE);
		btnDirectionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x84, protocol.jcmMessage,true);	
			}
		});
		btnDirectionReq.setBounds(778, 155, 167, 25);
		panel_comandos.add(btnDirectionReq);
		
		JButton btnSecurotyDenomReq = new JButton("Security Denom (81h)");
		btnSecurotyDenomReq.setBackground(Color.ORANGE);
		btnSecurotyDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x81, protocol.jcmMessage,true);	
			}
		});
		btnSecurotyDenomReq.setBounds(201, 155, 179, 25);
		panel_comandos.add(btnSecurotyDenomReq);
		
		JButton btnCommunicationModeReq = new JButton("Communication Mode (82h)");
		btnCommunicationModeReq.setBackground(Color.ORANGE);
		btnCommunicationModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x82, protocol.jcmMessage,true);	
			}
		});
		btnCommunicationModeReq.setBounds(392, 155, 205, 25);
		panel_comandos.add(btnCommunicationModeReq);
		
		JButton btnVersionRequest = new JButton("Version Request (88h)");
		btnVersionRequest.setBackground(Color.ORANGE);
		btnVersionRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x88, protocol.jcmMessage,true);	
			}
		});
		btnVersionRequest.setBounds(201, 192, 179, 25);
		panel_comandos.add(btnVersionRequest);
		
		JButton btnBootVersionrequest = new JButton("Boot Version Request (89h)");
		btnBootVersionrequest.setBackground(Color.ORANGE);
		btnBootVersionrequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x89, protocol.jcmMessage,true);	
			}
		});
		btnBootVersionrequest.setBounds(392, 192, 205, 25);
		panel_comandos.add(btnBootVersionrequest);
		
		JButton btnCurrencyAssingRequest = new JButton("Currency Assing Req (8Ah)");
		btnCurrencyAssingRequest.setBackground(Color.ORANGE);
		btnCurrencyAssingRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x8A, protocol.jcmMessage,true);	
			}
		});
		btnCurrencyAssingRequest.setBounds(609, 192, 205, 25);
		panel_comandos.add(btnCurrencyAssingRequest);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("RECYCLER / EXTENSION (F0h + )");
		lblNewLabel_2_1_1.setBounds(10, 229, 218, 14);
		panel_comandos.add(lblNewLabel_2_1_1);
		
		JButton btnStatusRequestExt = new JButton("Stat Req Ext (+1Ah)");
		btnStatusRequestExt.setBackground(Color.ORANGE);
		btnStatusRequestExt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x7, (byte)0xf0, (byte)0x20, (byte)0x1a, (byte)0x1, (byte)0x2, protocol.jcmMessage);
			}
		});
		btnStatusRequestExt.setBounds(10, 255, 163, 25);
		panel_comandos.add(btnStatusRequestExt);
		
		JButton btnStack3 = new JButton("Stack-3 (49h)");
		btnStack3.setBackground(Color.ORANGE);
		btnStack3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)0x5, (byte)0x49, protocol.jcmMessage,false);
			}
		});
		btnStack3.setBounds(185, 255, 152, 25);
		panel_comandos.add(btnStack3);
		
		JButton btnPayOut = new JButton("Pay Out (+4Ah)");
		btnPayOut.setBackground(Color.ORANGE);
		btnPayOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				srlprt.id003_format_ext((byte)0x9, (byte)0xf0, (byte)0x20, (byte)0x4a, (byte)0x1, (byte)0x1, protocol.jcmMessage);								
			}
		});
		btnPayOut.setBounds(349, 255, 205, 25);
		panel_comandos.add(btnPayOut);
		
		JButton btnCollect = new JButton("Collect (+4Bh+Data)");
		btnCollect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCollect.setBounds(566, 255, 179, 25);
		panel_comandos.add(btnCollect);
		
		JButton btnClear = new JButton("Clear (+4Ch)");
		btnClear.setBackground(Color.ORANGE);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x9, (byte)0xf0, (byte)0x20, (byte)0x4C, (byte)0x1, (byte)0x2, protocol.jcmMessage);
			}
		});
		btnClear.setBounds(756, 255, 144, 25);
		panel_comandos.add(btnClear);
		
		JButton btnEmergencyStop = new JButton("Emergency Stop (+4Dh)");
		btnEmergencyStop.setBackground(Color.ORANGE);
		btnEmergencyStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x9, (byte)0xf0, (byte)0x20, (byte)0x4D, (byte)0x1, (byte)0x2, protocol.jcmMessage);
			}
		});
		btnEmergencyStop.setBounds(10, 292, 187, 25);
		panel_comandos.add(btnEmergencyStop);
		
		JButton btnUnitInformationRequest = new JButton("Unit Information Req (92h)");
		btnUnitInformationRequest.setBackground(Color.ORANGE);
		btnUnitInformationRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)0x5,  (byte)0x92,  protocol.jcmMessage,false);
			}
		});
		btnUnitInformationRequest.setBounds(612, 329, 205, 25);
		panel_comandos.add(btnUnitInformationRequest);
		
		JButton btnRecycleRefillModeSetting = new JButton("Recycle Refill Mode Setting (D4h+Data)");
		btnRecycleRefillModeSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeSetting.setBounds(10, 329, 289, 25);
		panel_comandos.add(btnRecycleRefillModeSetting);
		
		JButton btnRecycleKeySetting = new JButton("Recycle Key Setting (+D1h+Data)");
		btnRecycleKeySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleKeySetting.setBounds(469, 292, 244, 25);
		panel_comandos.add(btnRecycleKeySetting);
		
		JButton btnRecycleCountSetting = new JButton("Recycle Count Setting (+D2h+Data)");
		btnRecycleCountSetting.setBackground(Color.LIGHT_GRAY);
		btnRecycleCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleCountSetting.setBounds(729, 292, 236, 25);
		panel_comandos.add(btnRecycleCountSetting);
		
		JButton btnRecycleCurrencySetting = new JButton("Recycle Currency Setting (+D0h+Data)");
		btnRecycleCurrencySetting.setBackground(Color.ORANGE);
		btnRecycleCurrencySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				protocol.jcmMessage[7] = 0x01;
				protocol.jcmMessage[8] = 0x20;  //0x02:20 0x04:50 0x08:100  0x10:200   0x20:500; 
				protocol.jcmMessage[9] = 0x00;
				protocol.jcmMessage[10] = 0x02;
				srlprt.id003_format_ext((byte)0x0D, (byte)0xf0, (byte)0x20, (byte)0xD0, (byte) 0x20, (byte)0x0, protocol.jcmMessage);
			}
		});
		btnRecycleCurrencySetting.setBounds(201, 292, 256, 25);
		panel_comandos.add(btnRecycleCurrencySetting);
		
		JButton btnCurrentCountSetting = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCurrentCountSetting.setBounds(311, 328, 289, 25);
		panel_comandos.add(btnCurrentCountSetting);
		
		JButton btnRecycleCurrencyReqSetting = new JButton("Recycle Currency Req (+90h)");
		btnRecycleCurrencyReqSetting.setBackground(Color.ORANGE);
		btnRecycleCurrencyReqSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x07, (byte)0xf0, (byte)0x20, (byte)0x90, (byte) 0x40, (byte)0x0, protocol.jcmMessage);
			}
		});
		btnRecycleCurrencyReqSetting.setBounds(10, 390, 205, 25);
		panel_comandos.add(btnRecycleCurrencyReqSetting);
		
		JButton btnRecycleSoftwareVersionReq = new JButton("Recycle Software Version Req (+93h)");
		btnRecycleSoftwareVersionReq.setBackground(Color.ORANGE);
		btnRecycleSoftwareVersionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x07, (byte)0xf0, (byte)0x20, (byte)0x93, (byte) 0x00, (byte)0x0, protocol.jcmMessage);
			}
		});
		btnRecycleSoftwareVersionReq.setBounds(671, 390, 256, 25);
		panel_comandos.add(btnRecycleSoftwareVersionReq);
		
		JLabel lblNewLabel_2_1_2 = new JLabel("Setting Status Request (F0h + )");
		lblNewLabel_2_1_2.setBounds(10, 366, 187, 14);
		panel_comandos.add(lblNewLabel_2_1_2);
		
		JButton btnRecycleKeySettingReq = new JButton("Recycle Key Setting Req (+91h)");
		btnRecycleKeySettingReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleKeySettingReq.setBounds(225, 390, 224, 25);
		panel_comandos.add(btnRecycleKeySettingReq);
		
		JButton btnRecycleCountReq = new JButton("Recycle Count Req (+92h)");
		btnRecycleCountReq.setBackground(Color.ORANGE);
		btnRecycleCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				srlprt.id003_format((byte)0x5, (byte)0x92, protocol.jcmMessage,false);
			}
		});
		btnRecycleCountReq.setBounds(461, 390, 187, 25);
		panel_comandos.add(btnRecycleCountReq);
		
		JButton btnRecycleRefillModeReq = new JButton("Recycle Refill Mode Req (+94h)");
		btnRecycleRefillModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeReq.setBounds(10, 427, 218, 25);
		panel_comandos.add(btnRecycleRefillModeReq);
		
		JButton btnTotalCountReq = new JButton("Total Count Req (+A0h)");
		btnTotalCountReq.setBackground(Color.ORANGE);
		btnTotalCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x07, (byte)0xf0, (byte)0x20, (byte)0xA0, (byte) 0x00, (byte)0x0, protocol.jcmMessage);
			}
		});
		btnTotalCountReq.setBounds(235, 427, 224, 25);
		panel_comandos.add(btnTotalCountReq);
		
		JButton btnTotalCountClear = new JButton("Total Count Clear (+A1h)");
		btnTotalCountClear.setBackground(Color.ORANGE);
		btnTotalCountClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x07, (byte)0xf0, (byte)0x20, (byte)0xA1, (byte) 0x00, (byte)0x0, protocol.jcmMessage);
			}
		});
		btnTotalCountClear.setBounds(479, 427, 210, 25);
		panel_comandos.add(btnTotalCountClear);
		
		JButton btnCurrentCountReq = new JButton("Current Count Req (+A2h)");
		btnCurrentCountReq.setBackground(Color.ORANGE);
		btnCurrentCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format_ext((byte)0x07, (byte)0xf0, (byte)0x20, (byte)0xA2, (byte) 0x00, (byte)0x0, protocol.jcmMessage);
			}
		});
		btnCurrentCountReq.setBounds(701, 427, 236, 25);
		panel_comandos.add(btnCurrentCountReq);
		
		JButton btnReinhibitch = new JButton("REInhibit (C3h)");
		btnReinhibitch.setBackground(Color.ORANGE);
		btnReinhibitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("RE INHIBIT");
				protocol.jcmMessage[3] = 0x00;
				srlprt.id003_format((byte)0x6, (byte)0xC3, protocol.jcmMessage,false);
			}
		});
		btnReinhibitch.setBounds(581, 123, 144, 25);
		panel_comandos.add(btnReinhibitch);
		
		JButton btnReiniciar = new JButton("REINICIAR");
		btnReiniciar.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnReiniciar.setBounds(194, 114, 149, 41);
		frame.getContentPane().add(btnReiniciar);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(653, 11, 332, 253);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		
		
		JButton btn2 = new JButton("2");
		btn2.setBounds(74, 6, 50, 50);
		panel.add(btn2);
		
		JButton btn3 = new JButton("3");
		btn3.setBounds(134, 6, 50, 50);
		panel.add(btn3);
		
		JButton btn4 = new JButton("4");
		btn4.setBounds(14, 66, 50, 50);
		panel.add(btn4);
		
		JButton btn5 = new JButton("5");
		btn5.setBounds(74, 67, 50, 50);
		panel.add(btn5);
		
		JButton btn6 = new JButton("6");
		btn6.setBounds(134, 67, 50, 50);
		panel.add(btn6);
		
		JButton btn7 = new JButton("7");
		btn7.setBounds(14, 127, 50, 50);
		panel.add(btn7);
		
		JButton btn8 = new JButton("8");
		btn8.setBounds(74, 127, 50, 50);
		panel.add(btn8);
		
		JButton btn9 = new JButton("9");
		btn9.setBounds(134, 127, 50, 50);
		panel.add(btn9);
		
		JButton btn0 = new JButton("0");
		btn0.setBounds(74, 188, 50, 50);
		panel.add(btn0);
		
		JButton btnNullIzq = new JButton("");
		btnNullIzq.setEnabled(false);
		btnNullIzq.setBounds(14, 188, 50, 50);
		panel.add(btnNullIzq);
		
		JButton btnNullDer = new JButton("");
		btnNullDer.setEnabled(false);
		btnNullDer.setBounds(134, 188, 50, 50);
		panel.add(btnNullDer);
		
		JButton btnCancel = new JButton("CANCELAR");
		btnCancel.setBackground(Color.RED);
		btnCancel.setBounds(222, 6, 100, 50);
		panel.add(btnCancel);
		
		JButton btnBorrar = new JButton("BORRAR");
		btnBorrar.setBackground(Color.ORANGE);
		btnBorrar.setBounds(222, 66, 100, 50);
		panel.add(btnBorrar);
		
		JButton btnConfirmar = new JButton("CONFIRMAR");
		btnConfirmar.setBackground(Color.GREEN);
		btnConfirmar.setBounds(222, 127, 100, 50);
		panel.add(btnConfirmar);
		
		JButton btnConfirmar_1 = new JButton("");
		btnConfirmar_1.setEnabled(false);
		btnConfirmar_1.setBackground(UIManager.getColor("Button.background"));
		btnConfirmar_1.setBounds(222, 188, 100, 50);
		panel.add(btnConfirmar_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(0, 179, 343, 85);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnDispensar = new JButton("Dispensar");
		btnDispensar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnDispensar.setBounds(25, 11, 163, 51);
		panel_1.add(btnDispensar);
		
		final JLabel lblMonto = new JLabel("$000,000");
		lblMonto.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblMonto.setBounds(213, 9, 134, 51);
		panel_1.add(lblMonto);
		
		JButton btn1 = new JButton("1");
				btn1.setBounds(14, 6, 50, 50);
		panel.add(btn1);
		
		JComboBox cbCass1 = new JComboBox();
		cbCass1.setBounds(378, 126, 88, 22);
		frame.getContentPane().add(cbCass1);
		
		JComboBox cbCass2 = new JComboBox();
		cbCass2.setBounds(487, 126, 98, 22);
		frame.getContentPane().add(cbCass2);
		
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "1";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "2";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "3";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "4";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "5";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "6";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "7";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "8";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "9";
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
					return;
				
				montoDispensar += "0";				
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(montoDispensar.length() == 0 || Long.parseLong(montoDispensar) == 0)
					return;
				
				montoDispensar = montoDispensar.substring(0,montoDispensar.length() - 1);
				lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				montoDispensar = "0";
				lblMonto.setText("");
			}
		});
		
		// [+]
		
		// [1] Event Open Serial port
		btnOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				uart.baud = Integer.parseInt((String)cbBaudRate.getSelectedItem());
				srlprt.openPort(comboBox_1.getSelectedItem().toString());
				if(cbLoopBack.isSelected())
					srlprt2.openPort("COM2");
			}
		});
		// ![1]
		
		// [2]
		MyClass c = new MyClass();
	    c.addMyEventListener(new MyEventListener() {
	      public void myEventOccurred(MyEvent evt) {
	    	System.out.println("myEventOccurred");
	        if(evt.getSource() == "version"){
	        	label.setText(new String(uart.version));
	        }else if (evt.getSource() == "bill"){
	        	lblNewLabel_1.setText(String.format("%d Pesos", uart.bill));
	        }else if (evt.getSource() == "clearbill"){
	        	lblNewLabel_1.setText("");
	        }
	        else if (evt.getSource() == "recyclerVersion"){
	        	lblRecyclerVersion.setText(new String(uart.recyclerVersion));
	        }

	      }
	    });
	    // ![2]
	    
		// [0]
		uart.portList = CommPortIdentifier.getPortIdentifiers();
		while (uart.portList.hasMoreElements()) {
			uart.portId = (CommPortIdentifier) uart.portList.nextElement();
            	comboBox_1.addItem(uart.portId.getName().toString());
        }
	}
	
    public static String baitsToString(String texto, byte[] baits) {
    	String result = texto;
    	for (byte theByte : baits){
    		result += " [" + Integer.toHexString(theByte) + "] ";
        }
    	return result;
    }
    
    static public String customFormat(String pattern, double value ) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        System.out.println(value + "  " + pattern + "  " + output);
        return output;
     }
}
