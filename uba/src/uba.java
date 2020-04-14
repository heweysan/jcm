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
import javax.comm.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.lang.System;

import com.google.gson.Gson;

import RabbitClient.ConnectRabbit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class uba {
	
	private static Gson gson = new Gson();
	
	private static final Logger logger = LogManager.getLogger(uba.class.getName());
	
	

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
			}
		});
	}

	/**
	 * Create the application.
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public uba() throws IOException, TimeoutException {
	
		
		ConnectRabbit myRabbit = new ConnectRabbit("11.50.0.8", 5672, "vh_switch", "pulsar_atm", "p4ssw0rd", 3);
		
		myRabbit.OpenConnection("11.50.0.8", 5672, "pulsar_atm", "p4ssw0rd", "vh_switch", true, 32000);
		
		
		
		logger.debug("this is an DEBUG message");
		logger.info("this is an INFO message");
		logger.warn("this is an WARN message");
		logger.error("this is an ERROR message");		
		logger.fatal("this is an FATAL message");		
		     
		ServerEntry myEntry = new ServerEntry();
		myEntry.setAgent("EJA");
		myEntry.setData("MY DATA");
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
		frame.setBounds(100, 100, 1010, 607);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null); 
		
		final uart srlprt = new uart();		
		final uart2 srlprt2 = new uart2();  //HEWEY SAN AQUI: Pruebas recepcion
		
		JPanel panel_conexion = new JPanel();
		panel_conexion.setBorder(new TitledBorder(null, "Puerto Serial", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_conexion.setBounds(0, 3, 620, 100);
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
		final JComboBox<String> comboBox = new JComboBox(BaudArray);
		comboBox.setBounds(98, 23, 115, 25);
		panel_conexion.add(comboBox);
		
		final JComboBox<String> comboBox_1 = new JComboBox<String>();
		comboBox_1.setBounds(265, 23, 117, 25);
		panel_conexion.add(comboBox_1);
		
		JPanel panel_firmware = new JPanel();
		panel_firmware.setBounds(12, 52, 501, 44);
		panel_conexion.add(panel_firmware);
		panel_firmware.setBorder(new TitledBorder(null, "Firmware:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_firmware.setLayout(null);
		
		final JLabel label = new JLabel("");
		label.setBounds(85, 17, 389, 15);
		panel_firmware.add(label);
		
		final JCheckBox cbLoopBack = new JCheckBox("LoopBack\r\n");
		cbLoopBack.setBounds(514, 23, 87, 24);
		panel_conexion.add(cbLoopBack);
		
	
		JPanel panel_estatus = new JPanel();
		panel_estatus.setBorder(new TitledBorder(null, "Estatus", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_estatus.setBounds(635, 3, 357, 94);
		frame.getContentPane().add(panel_estatus);
		panel_estatus.setLayout(null);
		
		// [+]
		JButton btnRegresar = new JButton("Regresar");
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRegresar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Regresar mouseClicked");
				uart.accept = false;
				uart.rturn = true;
			}
		});
		btnRegresar.setBounds(121, 59, 104, 25);
		panel_estatus.add(btnRegresar);
		
		JButton btnAccept = new JButton("Aceptar");
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnAccept.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Accept mouseClicked");
				uart.rturn = false;
				uart.accept = true;
			}
		});
		btnAccept.setBounds(14, 59, 95, 25);
		panel_estatus.add(btnAccept);
		
		JLabel lblBill = new JLabel("Billete:");
		lblBill.setBounds(14, 26, 80, 15);
		panel_estatus.add(lblBill);
		
		final JLabel lblNewLabel_1 = new JLabel("...");
		lblNewLabel_1.setBounds(63, 26, 313, 15);
		panel_estatus.add(lblNewLabel_1);
				
		
		JPanel panel_comandos = new JPanel();
		panel_comandos.setBorder(new TitledBorder(null, "Comandos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_comandos.setBounds(0, 105, 992, 465);
		panel_comandos.setLayout(null);
		frame.getContentPane().add(panel_comandos);
		
		JButton btnStatusReq = new JButton("Stat Req (11h)");
		btnStatusReq.setBounds(10, 28, 117, 25);
		btnStatusReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x11, protocol.jcmMessage,true);				
			}
		});
		
		panel_comandos.add(btnStatusReq);
		
		JButton btnReset = new JButton("Reset (40h)");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x40, protocol.jcmMessage,true);
			}
		});
		btnReset.setBounds(246, 28, 100, 25);
		panel_comandos.add(btnReset);
		
		JButton btnAck = new JButton("Ack (50h)");
		btnAck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x50, protocol.jcmMessage,true);	
			}
		});
		btnAck.setLocation(139, 28);
		btnAck.setSize(95, 25);
		panel_comandos.add(btnAck);
		
		JButton btnStack1 = new JButton("Stack-1 (41h)");
		btnStack1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x41, protocol.jcmMessage,true);	
			}
		});
		btnStack1.setBounds(358, 28, 117, 25);
		panel_comandos.add(btnStack1);
		
		JButton btnStack2 = new JButton("Stack-2 (42h)");
		btnStack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x42, protocol.jcmMessage,true);	
			}
		});
		btnStack2.setBounds(487, 28, 122, 25);
		panel_comandos.add(btnStack2);
		
		JButton btnReturn = new JButton("Return (43h)");
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x43, protocol.jcmMessage,true);	
			}
		});
		btnReturn.setBounds(621, 28, 116, 25);
		panel_comandos.add(btnReturn);
		
		JButton btnHold = new JButton("Hold (44h)");
		btnHold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)5, (byte)0x44, protocol.jcmMessage,true);	
			}
		});
		btnHold.setBounds(749, 28, 108, 25);
		panel_comandos.add(btnHold);
		
		JButton btnWait = new JButton("Wait (45h)");
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
		btnSecurotyDenom.setBounds(189, 92, 171, 25);
		panel_comandos.add(btnSecurotyDenom);
		
		JButton btnCommunicationMode = new JButton("Communication Mode (C2h)");
		btnCommunicationMode.setBounds(364, 92, 205, 25);
		panel_comandos.add(btnCommunicationMode);
		
		JButton btnInhibit = new JButton("Inhibit (C3h)");
		btnInhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				protocol.jcmMessage[3] = 0x01;
				srlprt.id003_format((byte)0x6, (byte)0xC3, protocol.jcmMessage,false);
			}
		});
		btnInhibit.setBounds(581, 92, 108, 25);
		panel_comandos.add(btnInhibit);
		
		JButton btnDirection = new JButton("Direction (C4h)");
		btnDirection.setBounds(701, 92, 125, 25);
		panel_comandos.add(btnDirection);
		
		JButton btnOptionalFunc = new JButton("Optional Func (C5h)");
		btnOptionalFunc.setBounds(838, 92, 152, 25);
		panel_comandos.add(btnOptionalFunc);
		
		JLabel lblNewLabel_2_1 = new JLabel("Setting Status Request");
		lblNewLabel_2_1.setBounds(10, 129, 167, 14);
		panel_comandos.add(lblNewLabel_2_1);
		
		JButton btnOptionalFuncReq = new JButton("Optional Func (85h)");
		btnOptionalFuncReq.setBounds(10, 192, 179, 25);
		panel_comandos.add(btnOptionalFuncReq);
		
		JButton btnEnableDisDenomReq = new JButton("En/Des Denom (80h)");
		btnEnableDisDenomReq.setBounds(10, 155, 179, 25);
		panel_comandos.add(btnEnableDisDenomReq);
		
		JButton btnInhibitReq = new JButton("Inhibit (83h)");
		btnInhibitReq.setBounds(609, 155, 157, 25);
		panel_comandos.add(btnInhibitReq);
		
		JButton btnDirectionReq = new JButton("Direction (84h)");
		btnDirectionReq.setBounds(778, 155, 167, 25);
		panel_comandos.add(btnDirectionReq);
		
		JButton btnSecurotyDenomReq = new JButton("Security Denom (81h)");
		btnSecurotyDenomReq.setBounds(201, 155, 179, 25);
		panel_comandos.add(btnSecurotyDenomReq);
		
		JButton btnCommunicationModeReq = new JButton("Communication Mode (82h)");
		btnCommunicationModeReq.setBounds(392, 155, 205, 25);
		panel_comandos.add(btnCommunicationModeReq);
		
		JButton btnVersionRequest = new JButton("Version Request (88h)");
		btnVersionRequest.setBounds(201, 192, 179, 25);
		panel_comandos.add(btnVersionRequest);
		
		JButton btnBootVersionrequest = new JButton("Boot Version Request (89h)");
		btnBootVersionrequest.setBounds(392, 192, 205, 25);
		panel_comandos.add(btnBootVersionrequest);
		
		JButton btnCurrencyAssingRequest = new JButton("Currency Assing Req (8Ah)");
		btnCurrencyAssingRequest.setBounds(609, 192, 205, 25);
		panel_comandos.add(btnCurrencyAssingRequest);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("RECYCLER / EXTENSION (F0h + )");
		lblNewLabel_2_1_1.setBounds(10, 229, 218, 14);
		panel_comandos.add(lblNewLabel_2_1_1);
		
		JButton btnStatusRequestExt = new JButton("Stat Req Ext (+1Ah)");
		btnStatusRequestExt.setBounds(10, 255, 163, 25);
		panel_comandos.add(btnStatusRequestExt);
		
		JButton btnStack3 = new JButton("Stack-3 (49h)");
		btnStack3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				srlprt.id003_format((byte)0x5, (byte)0x49, protocol.jcmMessage,false);
			}
		});
		btnStack3.setBounds(185, 255, 152, 25);
		panel_comandos.add(btnStack3);
		
		JButton btnPayOut = new JButton("Pay Out (+4Ah)");
		btnPayOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				srlprt.id003_format_ext((byte)0x9, (byte)0xf0, (byte)0x20, (byte)0x4a, (byte)0x1, (byte)0x2, protocol.jcmMessage);
								
			}
		});
		btnPayOut.setBounds(349, 255, 205, 25);
		panel_comandos.add(btnPayOut);
		
		JButton btnCollect = new JButton("Collect (+4Bh+Data)");
		btnCollect.setBounds(566, 255, 179, 25);
		panel_comandos.add(btnCollect);
		
		JButton btnClear = new JButton("Clear (+4Ch)");
		btnClear.setBounds(756, 255, 144, 25);
		panel_comandos.add(btnClear);
		
		JButton btnEmergencyStop = new JButton("Emergency Stop (+4Dh)");
		btnEmergencyStop.setBounds(10, 292, 187, 25);
		panel_comandos.add(btnEmergencyStop);
		
		JButton btnUnitInformationRequest = new JButton("Unit Information Req (92h)");
		btnUnitInformationRequest.setBounds(612, 329, 205, 25);
		panel_comandos.add(btnUnitInformationRequest);
		
		JButton btnRecycleRefillModeSetting = new JButton("Recycle\u00A0Refill\u00A0Mode\u00A0Setting (D4h+Data)");
		btnRecycleRefillModeSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeSetting.setBounds(10, 329, 289, 25);
		panel_comandos.add(btnRecycleRefillModeSetting);
		
		JButton btnRecycleKeySetting = new JButton("Recycle Key Setting (+D1h+Data)");
		btnRecycleKeySetting.setBounds(469, 292, 244, 25);
		panel_comandos.add(btnRecycleKeySetting);
		
		JButton btnRecycleCountSetting = new JButton("Recycle Count Setting (+D2h+Data)");
		btnRecycleCountSetting.setBounds(729, 292, 236, 25);
		panel_comandos.add(btnRecycleCountSetting);
		
		JButton btnRecycleCurrencySetting = new JButton("Recycle Currency Setting (+D0h+Data)");
		btnRecycleCurrencySetting.setBounds(201, 292, 256, 25);
		panel_comandos.add(btnRecycleCurrencySetting);
		
		JButton btnCurrentCountSetting = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting.setBounds(311, 328, 289, 25);
		panel_comandos.add(btnCurrentCountSetting);
		
		JButton btnRecycleCurrencyReqSetting = new JButton("Recycle Currency Req (+90h)");
		btnRecycleCurrencyReqSetting.setBounds(10, 390, 205, 25);
		panel_comandos.add(btnRecycleCurrencyReqSetting);
		
		JButton btnRecycleCurrencySetting_1_1 = new JButton("Recycle Software Version Req (+93h)");
		btnRecycleCurrencySetting_1_1.setBounds(671, 390, 256, 25);
		panel_comandos.add(btnRecycleCurrencySetting_1_1);
		
		JLabel lblNewLabel_2_1_2 = new JLabel("Setting Status Request (F0h + )");
		lblNewLabel_2_1_2.setBounds(10, 366, 187, 14);
		panel_comandos.add(lblNewLabel_2_1_2);
		
		JButton btnRecycleKeySetting_1 = new JButton("Recycle Key Setting Req (+91h)");
		btnRecycleKeySetting_1.setBounds(225, 390, 224, 25);
		panel_comandos.add(btnRecycleKeySetting_1);
		
		JButton btnRecycleCurrencyReqSetting_1_1 = new JButton("Recycle Count Req (+92h)");
		btnRecycleCurrencyReqSetting_1_1.setBounds(461, 390, 187, 25);
		panel_comandos.add(btnRecycleCurrencyReqSetting_1_1);
		
		JButton btnRecycleCurrencySetting_1_1_1 = new JButton("Recycle Refill Mode Req (+94h)");
		btnRecycleCurrencySetting_1_1_1.setBounds(10, 427, 218, 25);
		panel_comandos.add(btnRecycleCurrencySetting_1_1_1);
		
		JButton btnRecycleCurrencySetting_1_1_1_1 = new JButton("Total Count Req (+A0h)");
		btnRecycleCurrencySetting_1_1_1_1.setBounds(235, 427, 224, 25);
		panel_comandos.add(btnRecycleCurrencySetting_1_1_1_1);
		
		JButton btnRecycleCurrencySetting_1_1_1_1_1 = new JButton("Total Count Clear (+A1h)");
		btnRecycleCurrencySetting_1_1_1_1_1.setBounds(479, 427, 210, 25);
		panel_comandos.add(btnRecycleCurrencySetting_1_1_1_1_1);
		
		JButton btnRecycleCurrencySetting_1_1_1_1_1_1 = new JButton("Current Coount Req (+A2h)");
		btnRecycleCurrencySetting_1_1_1_1_1_1.setBounds(701, 427, 236, 25);
		panel_comandos.add(btnRecycleCurrencySetting_1_1_1_1_1_1);
		
		JButton btnReinhibitch = new JButton("REInhibit (C3h)");
		btnReinhibitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				protocol.jcmMessage[3] = 0x00;
				srlprt.id003_format((byte)0x6, (byte)0xC3, protocol.jcmMessage,false);
			}
		});
		btnReinhibitch.setBounds(581, 123, 144, 25);
		panel_comandos.add(btnReinhibitch);
		
		
		
		// [+]
		
		// [1] Event Open Serial port
		btnOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				uart.baud = Integer.parseInt((String)comboBox.getSelectedItem());
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
}
