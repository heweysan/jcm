import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.util.concurrent.TimeoutException;
import gnu.io.*;

//import javax.comm.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import java.lang.System;
import java.text.DecimalFormat;

import com.google.gson.Gson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.UIManager;

import pentomino.common.*;
import pentomino.jcmagent.RaspiAgent;
import pentomino.jcmagent.ServerEntry;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;



public class uba {
	
	private static PinpadMode pinpadMode = PinpadMode.None;
	
	private static String retiroUser = "";
	private static String retiroPassword = "";
	private static String retiroClave = "";
	private static String depositoUser = "";
	private static String depositoPassword = "";
	
	private static int referenciaNumerica = 0;
	private static int montoRetiro = 0;

	private static Gson gson = new Gson();

	private static final Logger logger = LogManager.getLogger(uba.class);

	private static String montoDispensar = "0";
	private static String autorizacionDispensar = "";
	private static String autorizacionDispensarCopia = "";
	private static String asteriscos = "";
	
	private static jcmOperation currentOperation = jcmOperation.None;

	private JFrame mainFrame;
	
	private static boolean retiroMonto = false;
	private static boolean retiroAutorizacion = false;

	private static int iMonotoDispensar = 0;
	private static boolean validAuthorization = false;

	uart[] jcms = new uart[2];
	int contador = 0;
	private JTextField textFieldDepositoUser;
	private JTextField textFieldDepositoPassword;

	//final Tio miTio = new Tio();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					uba window = new uba();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public uba() throws IOException, TimeoutException {

		//https://logging.apache.org/log4j/2.x/manual/layouts.html

		logger.debug("this is an DEBUG message");
		logger.info("this is an INFO message");
		logger.warn("this is an WARN message");
		logger.error("this is an ERROR message");
		logger.fatal("this is an FATAL message");

		// Thread t1 = new Thread(new JcmMonitor ());
		// t1.start();

		JcmMonitor t2 = new JcmMonitor();

		t2.start();

		/*
		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	
		*/
		initialize();	

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		mainFrame = new JFrame();
		mainFrame.getContentPane().setBackground(Color.DARK_GRAY);
		mainFrame.setTitle("RedBlu JCM Driver (ID003)");
		mainFrame.setBounds(100, 100, 1920, 1084);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		
		
		String[] BaudArray = { "9600", "19200", "38400" };

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1904, 1050);
		mainFrame.getContentPane().add(tabbedPane);

		JPanel panelPrincipal = new JPanel();
		tabbedPane.addTab("Principal", (Icon) null, panelPrincipal, null);
		tabbedPane.setEnabledAt(0, true);
		panelPrincipal.setLayout(null);

		JPanel panel_estatus = new JPanel();
		panel_estatus.setBounds(10, 11, 850, 403);
		panelPrincipal.add(panel_estatus);
		panel_estatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_estatus.setLayout(null);

		JPanel panelJCM2 = new JPanel();
		panelJCM2.setBounds(440, 11, 400, 381);
		panel_estatus.add(panelJCM2);
		panelJCM2.setLayout(null);
		panelJCM2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lblRecycler2 = new JLabel(".");
		lblRecycler2.setBounds(10, 225, 380, 53);
		panelJCM2.add(lblRecycler2);
		lblRecycler2.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador2 = new JLabel("Reciclador 2");
		lblTitleReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTitleReciclador2.setBounds(10, 11, 144, 28);
		panelJCM2.add(lblTitleReciclador2);

		JLabel lblBilleteIngresado2 = new JLabel("$0");
		lblBilleteIngresado2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado2.setBounds(164, 330, 226, 46);
		panelJCM2.add(lblBilleteIngresado2);

		JLabel lblTxtBill2 = new JLabel("Billete:");
		lblTxtBill2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill2.setBounds(10, 330, 144, 46);
		panelJCM2.add(lblTxtBill2);

		JLabel lblContadores2 = new JLabel("100x20 100x50 x100x100 100x200 100x500");
		lblContadores2.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblContadores2.setBounds(10, 280, 380, 37);
		panelJCM2.add(lblContadores2);

		JButton btnReiniciarJcm2 = new JButton("REINICIAR");
		btnReiniciarJcm2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentOperation = jcmOperation.Reset;
				jcms[1].currentOpertion = jcmOperation.Reset;
				jcms[1].id003_format((byte) 5, (byte) 0x40, jcms[1].jcmMessage, true);
			}
		});
		btnReiniciarJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm2.setBounds(181, 11, 209, 59);
		panelJCM2.add(btnReiniciarJcm2);

		JPanel panel_firmware2 = new JPanel();
		panel_firmware2.setLayout(null);
		panel_firmware2.setBorder(new TitledBorder(null, "Firmware:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_firmware2.setBounds(10, 85, 380, 59);
		panelJCM2.add(panel_firmware2);

		JLabel firmwareLabel2 = new JLabel(".");
		firmwareLabel2.setBounds(10, 18, 360, 30);
		panel_firmware2.add(firmwareLabel2);

		JPanel panelRecycler2 = new JPanel();
		panelRecycler2.setLayout(null);
		panelRecycler2.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(128, 128, 128)), "Recycler:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelRecycler2.setBounds(10, 155, 380, 59);
		panelJCM2.add(panelRecycler2);

		JLabel lblRecyclerVersion2 = new JLabel(".");
		lblRecyclerVersion2.setBounds(10, 19, 360, 29);
		panelRecycler2.add(lblRecyclerVersion2);

		JPanel panelJCM1 = new JPanel();
		panelJCM1.setBounds(10, 11, 400, 381);
		panel_estatus.add(panelJCM1);
		panelJCM1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM1.setLayout(null);

		JLabel lblRecycler1 = new JLabel(".");
		lblRecycler1.setBounds(10, 225, 380, 53);
		panelJCM1.add(lblRecycler1);
		lblRecycler1.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador1 = new JLabel("Reciclador 1");
		lblTitleReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTitleReciclador1.setBounds(10, 11, 161, 28);
		panelJCM1.add(lblTitleReciclador1);

		JLabel lblTxtBill1 = new JLabel("Billete:");
		lblTxtBill1.setBounds(10, 330, 144, 46);
		panelJCM1.add(lblTxtBill1);
		lblTxtBill1.setFont(new Font("Tahoma", Font.BOLD, 30));

		final JLabel lblBilleteIngresado1 = new JLabel("$0");
		lblBilleteIngresado1.setBounds(164, 330, 226, 46);
		panelJCM1.add(lblBilleteIngresado1);
		lblBilleteIngresado1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JLabel lblContadores1 = new JLabel("100x20 100x50 x100x100 100x200 100x500");
		lblContadores1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblContadores1.setBounds(10, 280, 380, 37);
		panelJCM1.add(lblContadores1);

		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 948, 850, 63);
		panelPrincipal.add(panel_1);
		panel_1.setLayout(null);
		
		final JLabel lblMensajes = new JLabel(".");
		lblMensajes.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblMensajes.setBounds(10, 11, 830, 41);
		panel_1.add(lblMensajes);



		JPanel panel_firmware1 = new JPanel();
		panel_firmware1.setBounds(10, 85, 380, 59);
		panelJCM1.add(panel_firmware1);
		panel_firmware1
		.setBorder(new TitledBorder(null, "Firmware:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_firmware1.setLayout(null);

		final JLabel firmwareLabel1 = new JLabel(".");
		firmwareLabel1.setFont(new Font("Tahoma", Font.BOLD, 11));
		firmwareLabel1.setBounds(10, 18, 360, 30);
		panel_firmware1.add(firmwareLabel1);

		JPanel panelRecycler1 = new JPanel();
		panelRecycler1.setBounds(10, 155, 380, 59);
		panelJCM1.add(panelRecycler1);
		panelRecycler1.setLayout(null);
		panelRecycler1.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(128, 128, 128)), "Recycler:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		final JLabel lblRecyclerVersion1 = new JLabel(".");
		lblRecyclerVersion1.setBounds(10, 19, 360, 29);
		panelRecycler1.add(lblRecyclerVersion1);

		JButton btnReiniciarJcm1 = new JButton("REINICIAR");
		btnReiniciarJcm1.setBounds(181, 11, 209, 59);
		panelJCM1.add(btnReiniciarJcm1);
		btnReiniciarJcm1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentOperation = jcmOperation.Reset;
				jcms[0].currentOpertion = jcmOperation.Reset;
				jcms[0].id003_format((byte) 5, (byte) 0x40, jcms[0].jcmMessage, true);
			}
		});
		btnReiniciarJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JPanel panelRetiro = new JPanel();
		panelRetiro.setBounds(10, 680, 850, 257);
		panelPrincipal.add(panelRetiro);
		panelRetiro.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelRetiro.setLayout(null);

		final JLabel lblMonto = new JLabel("$000,000");
		lblMonto.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblMonto.setBounds(553, 11, 287, 95);
		panelRetiro.add(lblMonto);

		JButton btnAutorizacion = new JButton("Autorizaci\u00F3n");
		btnAutorizacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retiroMonto = false;
				retiroAutorizacion = true;
				pinpadMode = PinpadMode.retiroClave;
			}
		});
		btnAutorizacion.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnAutorizacion.setBounds(10, 125, 275, 100);
		panelRetiro.add(btnAutorizacion);

		final JLabel lblAutorizacion = new JLabel(".\r\n");
		lblAutorizacion.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblAutorizacion.setBounds(304, 128, 275, 95);
		panelRetiro.add(lblAutorizacion);

		JPanel panelDev = new JPanel();
		tabbedPane.addTab("New tab", null, panelDev, null);
		panelDev.setLayout(null);
		
		
		JButton btnDispensar = new JButton("Retirar");
		btnDispensar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				
				//Redone for larger OK button
		         JOptionPane theOptionPane = new JOptionPane("MI MENSAJE FELIZ",JOptionPane.INFORMATION_MESSAGE);
		         JPanel buttonPanel = (JPanel)theOptionPane.getComponent(1);
		        // get the handle to the ok button
		        JButton buttonOk = (JButton)buttonPanel.getComponent(0);
		        // set the text
		        buttonOk.setText(" OK ");
		        buttonOk.setPreferredSize(new Dimension(200,100));  //Set Button size here
		        buttonOk.validate();
		        JDialog theDialog = theOptionPane.createDialog(null,"MI TITULO");
		        theDialog.setVisible(true);  //present your new optionpane to the world.
				
		        
		        //Checamos que tenga algo de dinero.
		        if(jcms[0].contadores.Cass1Available == 0 && jcms[0].contadores.Cass2Available == 0 && jcms[1].contadores.Cass1Available == 0 && jcms[1].contadores.Cass2Available == 0){
		        	
		        }
				
				 JScrollPane scrollpane = new JScrollPane(); 
			       String categories[] = { "1. Problem One Problem One Problem One Problem One Problem One Problem One Problem One Problem One Problem One", "2. Problem Two", "3. Extended Family", "4. Extended Family", "5. Extended Family"
			               ,"6. Extended Family","7. Extended Family","8. Extended Family","9. Extended Family"};
			       JList list = new JList(categories);

			       scrollpane = new JScrollPane(list);

			       JPanel panel = new JPanel(); 
			       panelPrincipal.add(scrollpane);
			       scrollpane.setViewportView(list);
			       JOptionPane.showMessageDialog(null, scrollpane, "Error List",  
			                                              JOptionPane.PLAIN_MESSAGE);
				
				if (autorizacionDispensar.length() == 0 || !autorizacionDispensar.equalsIgnoreCase(Integer.toString(referenciaNumerica))) {
					lblMensajes.setText("Autorizacón inválida, favor de verificar.");
					JOptionPane.showMessageDialog(mainFrame, "Autorizacón inválida, favor de verificar.", "No se puede dispensar.", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				
				//Revisamos que tanto billetes podemos dispensar
				int solicitado =  montoRetiro;
				int jcm1Total = 0;
				int jcm2Total = 0;
				
				//revisamos del JCM 1
				
				//cuantos de  jcm1 cass 1 
				
				System.out.println("JCM1 Cass1Denom [" + jcms[0].contadores.Cass1Denom + "][" + jcms[0].contadores.Cass1Available + "]");
				System.out.println("JCM1 Cass2Denom [" + jcms[0].contadores.Cass2Denom + "][" + jcms[0].contadores.Cass2Available + "]");
				
				System.out.println("JCM2 Cass1Denom [" + jcms[1].contadores.Cass1Denom + "][" + jcms[1].contadores.Cass1Available + "]");
				System.out.println("JCM2 Cass2Denom [" + jcms[1].contadores.Cass2Denom + "][" + jcms[1].contadores.Cass2Available + "]");
				
				jcm1Total += jcms[0].contadores.Cass1Denom * jcms[0].contadores.Cass1Available;
				jcm1Total += jcms[0].contadores.Cass2Denom * jcms[0].contadores.Cass2Available;				
				
				jcm2Total += jcms[1].contadores.Cass1Denom * jcms[1].contadores.Cass1Available;
				jcm2Total += jcms[1].contadores.Cass2Denom * jcms[1].contadores.Cass2Available;
				
				
				System.out.println("jcm1Total [" + jcm1Total + "] jcm2Total [" + jcm2Total + "]");
				
				
				jcms[0].jcmCass1 = solicitado / jcms[0].contadores.Cass1Denom;
																				
				//Reviamos si necestia mas de los que tiene
				if(jcms[0].jcmCass1 > jcms[0].contadores.Cass1Available) {
					jcms[0].jcmCass1 = jcms[0].contadores.Cass1Available;
				}
				System.out.println("" + jcms[0].jcmCass1);
				
				solicitado = solicitado - (jcms[0].jcmCass1 * jcms[0].contadores.Cass1Denom);
				
				System.out.println("Faltante [" + solicitado + "]");
				
				
				jcms[0].jcmCass2 = solicitado / jcms[0].contadores.Cass2Denom;
				System.out.println("" + jcms[0].jcmCass2);
																
				//Reviamos si necestia mas de los que tiene
				if(jcms[0].jcmCass2 > jcms[0].contadores.Cass2Available) {
					jcms[0].jcmCass2 = jcms[0].contadores.Cass2Available;
				}
				
				solicitado = solicitado - (jcms[0].jcmCass2 * jcms[0].contadores.Cass2Denom);
				
				System.out.println("Faltante [" + solicitado + "]");
				
				//revisamos del JCM 2
				
				//cuantos de  jcm2 cass 1 
				
				jcm2Total += jcms[1].contadores.Cass1Denom * jcms[1].contadores.Cass1Available;
								
				jcms[1].jcmCass1 = solicitado / jcms[1].contadores.Cass1Denom;
				System.out.println("" + jcms[1].jcmCass1);
																
				//Reviamos si necestia mas de los que tiene
				if(jcms[1].jcmCass1 > jcms[1].contadores.Cass1Available) {
					jcms[1].jcmCass1 = jcms[1].contadores.Cass1Available;
				}
				
				solicitado = solicitado - (jcms[1].jcmCass1 * jcms[1].contadores.Cass1Denom);
				
				System.out.println("Faltante [" + solicitado + "]");
				
				jcms[1].jcmCass2 = solicitado / jcms[1].contadores.Cass2Denom;
				System.out.println("" + jcms[1].jcmCass2);
																
				//Reviamos si necestia mas de los que tiene
				if(jcms[1].jcmCass2 > jcms[1].contadores.Cass2Available) {
					jcms[1].jcmCass2 = jcms[1].contadores.Cass2Available;
				}
				
				solicitado = solicitado - (jcms[1].jcmCass2 * jcms[1].contadores.Cass2Denom);
				
				
				System.out.println("jcm1cass1 [" + jcms[0].jcmCass1 + "] jcm1cass2 [" + jcms[0].jcmCass2 + "] jcm2cass1 [" + jcms[1].jcmCass1 + "]jcm2cass2 [" + jcms[1].jcmCass2 + "]" );
				
				System.out.println("Faltante [" + solicitado + "]");
				
				// Primero deshabilitamos el que acepte billetes
				
				

				System.out.println("Retirar");

				retiroMonto = false;
				retiroAutorizacion = false;

				iMonotoDispensar = 0;
				validAuthorization = false;

				// Iniciamos el dispensado
				
				//Checamos para JCM1
				if(jcms[0].jcmCass1 > 0 || jcms[0].jcmCass2 > 0) {	
					
					System.out.println("Deshabilitamos JCM1 para dispense");
					jcms[0].currentOpertion = jcmOperation.Dispense;
	
					// primero el inhibit
					jcms[0].jcmMessage[3] = 0x01;
					jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
				}
				
				//Checamos para JCM2
				if(jcms[1].jcmCass1 > 0 || jcms[1].jcmCass2 > 0) {
					
					System.out.println("Deshabilitamos JCM2 para dispense");
					jcms[1].currentOpertion = jcmOperation.Dispense;
	
					// primero el inhibit
					jcms[1].jcmMessage[3] = 0x01;
					jcms[1].id003_format((byte) 0x6, (byte) 0xC3, jcms[1].jcmMessage, false);
				}			

			}
		});
		btnDispensar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnDispensar.setBounds(602, 125, 238, 100);
		panelRetiro.add(btnDispensar);
		
		JLabel lblReferencia = new JLabel(".\r\n");
		lblReferencia.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblReferencia.setBounds(10, 11, 275, 95);
		panelRetiro.add(lblReferencia);

		JPanel panelPinPad = new JPanel();
		panelPinPad.setBackground(Color.GRAY);
		panelPinPad.setBounds(870, 11, 1019, 923);
		panelPrincipal.add(panelPinPad);
		panelPinPad.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelPinPad.setLayout(null);

		JButton btn2 = new JButton("2");
		btn2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn2.setBounds(240, 11, 200, 200);
		panelPinPad.add(btn2);

		JButton btn3 = new JButton("3");
		btn3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn3.setBounds(471, 11, 200, 200);
		panelPinPad.add(btn3);

		JButton btn4 = new JButton("4");
		btn4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn4.setBounds(10, 242, 200, 200);
		panelPinPad.add(btn4);

		JButton btn5 = new JButton("5");
		btn5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn5.setBounds(240, 242, 200, 200);
		panelPinPad.add(btn5);

		JButton btn6 = new JButton("6");
		btn6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn6.setBounds(471, 242, 200, 200);
		panelPinPad.add(btn6);

		JButton btn7 = new JButton("7");
		btn7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn7.setBounds(10, 473, 200, 200);
		panelPinPad.add(btn7);

		JButton btn8 = new JButton("8");
		btn8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn8.setBounds(240, 473, 200, 200);
		panelPinPad.add(btn8);

		JButton btn9 = new JButton("9");
		btn9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn9.setBounds(471, 473, 200, 200);
		panelPinPad.add(btn9);

		JButton btn0 = new JButton("0");
		btn0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn0.setBounds(240, 706, 200, 200);
		panelPinPad.add(btn0);

		JButton btnNullIzq = new JButton("");
		btnNullIzq.setEnabled(false);
		btnNullIzq.setBounds(10, 706, 200, 200);
		panelPinPad.add(btnNullIzq);

		JButton btnNullDer = new JButton("");
		btnNullDer.setEnabled(false);
		btnNullDer.setBounds(471, 706, 200, 200);
		panelPinPad.add(btnNullDer);

		JButton btnCancel = new JButton("CANCELAR");
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnCancel.setBackground(Color.RED);
		btnCancel.setBounds(709, 11, 300, 200);
		panelPinPad.add(btnCancel);

		JButton btnBorrar = new JButton("BORRAR");
		btnBorrar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnBorrar.setBackground(Color.ORANGE);
		btnBorrar.setBounds(709, 242, 300, 200);
		panelPinPad.add(btnBorrar);

		JButton btnConfirmar = new JButton("CONFIRMAR");
		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pinpadMode = PinpadMode.None;
			}
		});
		btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnConfirmar.setBackground(Color.GREEN);
		btnConfirmar.setBounds(709, 473, 300, 200);
		panelPinPad.add(btnConfirmar);

		JButton btnConfirmar_1 = new JButton("");
		btnConfirmar_1.setEnabled(false);
		btnConfirmar_1.setBackground(UIManager.getColor("Button.background"));
		btnConfirmar_1.setBounds(709, 706, 300, 200);
		panelPinPad.add(btnConfirmar_1);

		JButton btn1 = new JButton("1");
		btn1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn1.setBounds(10, 11, 200, 200);
		panelPinPad.add(btn1);

		JPanel panelDeposito = new JPanel();
		panelDeposito.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelDeposito.setBounds(10, 422, 850, 247);
		panelPrincipal.add(panelDeposito);
		panelDeposito.setLayout(null);
		
		JPanel panelUserLogin = new JPanel();
		panelUserLogin.setBackground(Color.ORANGE);
		panelUserLogin.setBounds(0, 0, 800, 600);
		panelUserLogin.setLayout(null);
		
		
		
		JLabel lblNewLabel = new JLabel("Ingresa tu n\u00FAmero de usuario y contrase\u00F1a");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblNewLabel.setBounds(63, 31, 540, 66);
		panelUserLogin.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("USUARIO");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pinpadMode = PinpadMode.depositoUser;
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnNewButton.setBounds(26, 113, 276, 102);
		panelUserLogin.add(btnNewButton);
		
		textFieldDepositoUser = new JTextField();
		textFieldDepositoUser.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDepositoUser.setText("007007");
		textFieldDepositoUser.setFont(new Font("Tahoma", Font.BOLD, 22));
		textFieldDepositoUser.setBounds(331, 114, 407, 102);
		panelUserLogin.add(textFieldDepositoUser);
		textFieldDepositoUser.setColumns(10);
		
		JButton btnContrasea = new JButton("CONTRASE\u00D1A");
		btnContrasea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pinpadMode = PinpadMode.depositoPassword;
			}
		});
		btnContrasea.setFont(new Font("Tahoma", Font.PLAIN, 22));
		btnContrasea.setBounds(26, 241, 276, 102);
		panelUserLogin.add(btnContrasea);
		
		textFieldDepositoPassword = new JTextField();
		textFieldDepositoPassword.setText("007007");
		textFieldDepositoPassword.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDepositoPassword.setFont(new Font("Tahoma", Font.BOLD, 22));
		textFieldDepositoPassword.setColumns(10);
		textFieldDepositoPassword.setBounds(331, 242, 407, 102);
		panelUserLogin.add(textFieldDepositoPassword);
		
		JButton btnNewButton_1 = new JButton("INGRESAR");
		
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnNewButton_1.setBounds(387, 372, 351, 115);
		panelUserLogin.add(btnNewButton_1);
	
		
		final JDialog loginDialog = new JDialog(mainFrame,"LOGIN", true);
		loginDialog.setAlwaysOnTop(true);
		loginDialog.setModalityType(ModalityType.MODELESS);
		loginDialog.setBounds(20, 20, 800, 800);
		
		loginDialog.getContentPane().setLayout(null);
		//loginDialog.getContentPane().add(panelUserLogin);
		
		
		JButton btnDeposito = new JButton("Depositar");
		btnDeposito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				
				
				loginDialog.setVisible(true);
				
			}
		});
		btnDeposito.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnDeposito.setBounds(10, 15, 367, 168);
		panelDeposito.add(btnDeposito);
		
		JLabel lblTxtUser = new JLabel("Usuario:");
		lblTxtUser.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtUser.setBounds(409, 23, 143, 46);
		panelDeposito.add(lblTxtUser);
		
		// TODO: HEWEY AQUI!
		loginDialog.getContentPane().add(panelUserLogin);
		//panelDev.add(panelUserLogin);
		

		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("1", lblMonto, lblAutorizacion);
			}
		});

		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("2", lblMonto, lblAutorizacion);
			}
		});

		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("3", lblMonto, lblAutorizacion);
			}
		});

		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("4", lblMonto, lblAutorizacion);
			}
		});

		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("5", lblMonto, lblAutorizacion);
			}
		});

		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("6", lblMonto, lblAutorizacion);
			}
		});

		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("7", lblMonto, lblAutorizacion);
			}
		});

		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("8", lblMonto, lblAutorizacion);
			}
		});

		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("9", lblMonto, lblAutorizacion);
			}
		});

		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("0", lblMonto, lblAutorizacion);
			}
		});
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				switch(pinpadMode) {
				case None:
					break;
				case depositoUser:
					if (depositoUser.length() ==  0)
						return;
					depositoUser = depositoUser.substring(0, depositoUser.length() - 1);
										
					textFieldDepositoUser.setText(depositoUser);
					
					break;
				case depositoPassword:
					
					if (depositoPassword.length()  ==  0)
						return;
					depositoPassword = depositoPassword.substring(0, depositoPassword.length() - 1);
					
					textFieldDepositoPassword.setText(depositoPassword);
					break;
				case retiroUser:
					break;
				case retiroPassword:
					break;
				case retiroClave:
					if (autorizacionDispensar.length() ==  0)
						return;
					autorizacionDispensar = autorizacionDispensar.substring(0, autorizacionDispensar.length() - 1);
					
					lblAutorizacion.setText(autorizacionDispensar);
					break;
					default:
						break;
						
				}
				
				

			}
		});

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case depositoUser:
														
					textFieldDepositoUser.setText("");
					
					break;
				case depositoPassword:
					
					
					
					textFieldDepositoPassword.setText("");
					break;
				case retiroUser:
					break;
				case retiroPassword:
					break;
				case retiroClave:
			
					
					lblAutorizacion.setText("");
					break;
					default:
						break;
						
				}	
			}
		});

		JPanel panelComandos = new JPanel();
		tabbedPane.addTab("Comandos\r\n", (Icon) null, panelComandos, null);
		panelComandos.setLayout(null);

		JPanel panel_comandos = new JPanel();
		panel_comandos.setBounds(10, 113, 1860, 706);
		panelComandos.add(panel_comandos);
		panel_comandos.setLayout(null);



		JCheckBox chckbxReciclador1 = new JCheckBox("Reciclador 1");
		chckbxReciclador1.setSelected(true);

		chckbxReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador1.setBounds(18, 24, 148, 51);
		panelComandos.add(chckbxReciclador1);

		JCheckBox chckbxReciclador2 = new JCheckBox("Reciclador 2");		
		chckbxReciclador2.setSelected(true);
		chckbxReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador2.setBounds(194, 24, 148, 51);
		panelComandos.add(chckbxReciclador2);


		JButton btnStatusReq = new JButton("Stat Req (11h)");
		btnStatusReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusReq.setBackground(Color.ORANGE);
		btnStatusReq.setBounds(10, 11, 157, 50);
		btnStatusReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x11, jcms[0].jcmMessage, true);
			}
		});

		panel_comandos.add(btnStatusReq);

		JButton btnReset = new JButton("Reset (40h)");
		btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReset.setBackground(Color.GREEN);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentOperation = jcmOperation.Reset;
				jcms[0].id003_format((byte) 5, (byte) 0x40, jcms[0].jcmMessage, true);
			}
		});
		btnReset.setBounds(318, 11, 167, 50);
		panel_comandos.add(btnReset);

		JButton btnAck = new JButton("Ack (50h)");
		btnAck.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAck.setBackground(Color.ORANGE);
		btnAck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x50, jcms[0].jcmMessage, true);
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
				jcms[0].id003_format((byte) 5, (byte) 0x41, jcms[0].jcmMessage, true);
			}
		});
		btnStack1.setBounds(507, 11, 144, 50);
		panel_comandos.add(btnStack1);

		JButton btnStack2 = new JButton("Stack-2 (42h)");
		btnStack2.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack2.setBackground(Color.ORANGE);
		btnStack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x42, jcms[0].jcmMessage, true);
			}
		});
		btnStack2.setBounds(666, 11, 157, 50);
		panel_comandos.add(btnStack2);

		JButton btnReturn = new JButton("Return (43h)");
		btnReturn.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReturn.setBackground(Color.ORANGE);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x43, jcms[0].jcmMessage, true);
			}
		});
		btnReturn.setBounds(833, 11, 144, 50);
		panel_comandos.add(btnReturn);

		JButton btnHold = new JButton("Hold (44h)");
		btnHold.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnHold.setBackground(Color.ORANGE);
		btnHold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x44, jcms[0].jcmMessage, true);
			}
		});
		btnHold.setBounds(987, 11, 144, 50);
		panel_comandos.add(btnHold);

		JButton btnWait = new JButton("Wait (45h)");
		btnWait.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnWait.setBackground(Color.ORANGE);
		btnWait.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x45, jcms[0].jcmMessage, true);
			}
		});
		btnWait.setBounds(1153, 11, 145, 50);
		panel_comandos.add(btnWait);

		JLabel lblNewLabel_2 = new JLabel("Setting Commands +Data");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(10, 69, 167, 50);
		panel_comandos.add(lblNewLabel_2);

		JButton btnEnableDisDenom = new JButton("En/Des Denom (C0h)"); // +DATA
		btnEnableDisDenom.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnableDisDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0xC0, jcms[0].jcmMessage, true);
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
		btnInhibit.setBackground(Color.ORANGE);
		btnInhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("INHIBIT");
				jcms[0].jcmMessage[3] = 0x01;
				jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
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

		JLabel lblNewLabel_2_1 = new JLabel("Setting Status Request");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1.setBounds(10, 182, 167, 50);
		panel_comandos.add(lblNewLabel_2_1);

		JButton btnOptionalFuncReq = new JButton("Optional Func (85h)");
		btnOptionalFuncReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOptionalFuncReq.setBackground(Color.ORANGE);
		btnOptionalFuncReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x85, jcms[0].jcmMessage, true);
			}
		});
		btnOptionalFuncReq.setBounds(1059, 225, 200, 50);
		panel_comandos.add(btnOptionalFuncReq);

		JButton btnEnableDisDenomReq = new JButton("En/Des Denom (80h)");
		btnEnableDisDenomReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnableDisDenomReq.setBackground(Color.ORANGE);
		btnEnableDisDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x80, jcms[0].jcmMessage, true);
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
				jcms[0].id003_format((byte) 5, (byte) 0x83, jcms[0].jcmMessage, true);
			}
		});
		btnInhibitReq.setBounds(712, 225, 157, 50);
		panel_comandos.add(btnInhibitReq);

		JButton btnDirectionReq = new JButton("Direction (84h)");
		btnDirectionReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDirectionReq.setBackground(Color.ORANGE);
		btnDirectionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x84, jcms[0].jcmMessage, true);
			}
		});
		btnDirectionReq.setBounds(882, 225, 167, 50);
		panel_comandos.add(btnDirectionReq);

		JButton btnSecurotyDenomReq = new JButton("Security Denom (81h)");
		btnSecurotyDenomReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSecurotyDenomReq.setBackground(Color.ORANGE);
		btnSecurotyDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x81, jcms[0].jcmMessage, true);
			}
		});
		btnSecurotyDenomReq.setBounds(199, 225, 213, 50);
		panel_comandos.add(btnSecurotyDenomReq);

		JButton btnCommunicationModeReq = new JButton("Communication Mode (82h)");
		btnCommunicationModeReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCommunicationModeReq.setBackground(Color.ORANGE);
		btnCommunicationModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x82, jcms[0].jcmMessage, true);
			}
		});
		btnCommunicationModeReq.setBounds(468, 225, 234, 50);
		panel_comandos.add(btnCommunicationModeReq);

		JButton btnVersionRequest = new JButton("Version Request (88h)");
		btnVersionRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnVersionRequest.setBackground(Color.ORANGE);
		btnVersionRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x88, jcms[0].jcmMessage, true);
			}
		});
		btnVersionRequest.setBounds(1269, 225, 218, 50);
		panel_comandos.add(btnVersionRequest);

		JButton btnBootVersionrequest = new JButton("Boot Version Request (89h)");
		btnBootVersionrequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnBootVersionrequest.setBackground(Color.ORANGE);
		btnBootVersionrequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x89, jcms[0].jcmMessage, true);
			}
		});
		btnBootVersionrequest.setBounds(10, 285, 256, 50);
		panel_comandos.add(btnBootVersionrequest);

		JLabel lblNewLabel_2_1_1 = new JLabel("RECYCLER / EXTENSION (F0h + )");
		lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_1.setBounds(10, 348, 218, 50);
		panel_comandos.add(lblNewLabel_2_1_1);

		JButton btnStatusRequestExt = new JButton("Stat Req Ext (+1Ah)");
		btnStatusRequestExt.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusRequestExt.setBackground(Color.ORANGE);
		btnStatusRequestExt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x7, (byte) 0xf0, (byte) 0x20, (byte) 0x1a, (byte) 0x1, (byte) 0x2,
						jcms[0].jcmMessage);
			}
		});
		btnStatusRequestExt.setBounds(559, 459, 194, 50);
		panel_comandos.add(btnStatusRequestExt);

		JButton btnStack3 = new JButton("Stack-3 (49h)");
		btnStack3.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack3.setBackground(Color.ORANGE);
		btnStack3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 0x5, (byte) 0x49, jcms[0].jcmMessage, false);
			}
		});
		btnStack3.setBounds(779, 459, 157, 50);
		panel_comandos.add(btnStack3);

		JButton btnPayOut = new JButton("Pay Out (+4Ah)");
		btnPayOut.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPayOut.setBackground(Color.ORANGE);
		btnPayOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4a, (byte) 0x1, (byte) 0x1,
						jcms[0].jcmMessage);
			}
		});
		btnPayOut.setBounds(958, 459, 161, 50);
		panel_comandos.add(btnPayOut);

		JButton btnCollect = new JButton("Collect (+4Bh+Data)");
		btnCollect.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCollect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCollect.setBounds(10, 399, 179, 50);
		panel_comandos.add(btnCollect);

		JButton btnClear = new JButton("Clear (+4Ch)");
		btnClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnClear.setBackground(Color.ORANGE);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
						jcms[0].jcmMessage);
			}
		});
		btnClear.setBounds(195, 399, 144, 50);
		panel_comandos.add(btnClear);

		JButton btnEmergencyStop = new JButton("Emergency Stop (+4Dh)");
		btnEmergencyStop.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEmergencyStop.setBackground(Color.ORANGE);
		btnEmergencyStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4D, (byte) 0x1, (byte) 0x2,
						jcms[0].jcmMessage);
			}
		});
		btnEmergencyStop.setBounds(351, 399, 197, 50);
		panel_comandos.add(btnEmergencyStop);

		JButton btnUnitInformationRequest = new JButton("Unit Information Req (92h)");
		btnUnitInformationRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUnitInformationRequest.setBackground(Color.ORANGE);
		btnUnitInformationRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 0x5, (byte) 0x92, jcms[0].jcmMessage, false);
			}
		});
		btnUnitInformationRequest.setBounds(313, 459, 236, 50);
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
		btnRecycleCurrencySetting.setBackground(Color.ORANGE);
		btnRecycleCurrencySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].jcmMessage[7] = 0x01;
				jcms[0].jcmMessage[8] = 0x10; // 0x02:20 0x04:50 0x08:100 0x10:200 0x20:500;
				jcms[0].jcmMessage[9] = 0x00;
				jcms[0].jcmMessage[10] = 0x02;
				jcms[0].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, (byte) 0x08, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});
		btnRecycleCurrencySetting.setBounds(558, 399, 299, 50);
		panel_comandos.add(btnRecycleCurrencySetting);

		JButton btnCurrentCountSetting = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCurrentCountSetting.setBounds(10, 458, 289, 50);
		panel_comandos.add(btnCurrentCountSetting);

		JButton btnRecycleCurrencyReqSetting = new JButton("Recycle Currency Req (+90h)");
		btnRecycleCurrencyReqSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCurrencyReqSetting.setBackground(Color.ORANGE);
		btnRecycleCurrencyReqSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});
		btnRecycleCurrencyReqSetting.setBounds(736, 569, 236, 50);
		panel_comandos.add(btnRecycleCurrencyReqSetting);

		JButton btnRecycleSoftwareVersionReq = new JButton("Recycle Software Version Req (+93h)");
		btnRecycleSoftwareVersionReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleSoftwareVersionReq.setBackground(Color.ORANGE);
		btnRecycleSoftwareVersionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x93, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});
		btnRecycleSoftwareVersionReq.setBounds(1516, 569, 334, 50);
		panel_comandos.add(btnRecycleSoftwareVersionReq);

		JLabel lblNewLabel_2_1_2 = new JLabel("Setting Status Request (F0h + )");
		lblNewLabel_2_1_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_2.setBounds(17, 518, 205, 50);
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
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x92, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});
		btnRecycleCountReq.setBounds(1270, 569, 236, 50);
		panel_comandos.add(btnRecycleCountReq);

		JButton btnRecycleRefillModeReq = new JButton("Recycle Refill Mode Req (+94h)");
		btnRecycleRefillModeReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleRefillModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeReq.setBounds(10, 569, 270, 50);
		panel_comandos.add(btnRecycleRefillModeReq);

		JButton btnTotalCountReq = new JButton("Total Count Req (+A0h)");
		btnTotalCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountReq.setBackground(Color.ORANGE);
		btnTotalCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});
		btnTotalCountReq.setBounds(301, 569, 205, 50);
		panel_comandos.add(btnTotalCountReq);

		JButton btnTotalCountClear = new JButton("Total Count Clear (+A1h)");
		btnTotalCountClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountClear.setBackground(Color.ORANGE);
		btnTotalCountClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});
		btnTotalCountClear.setBounds(516, 569, 210, 50);
		panel_comandos.add(btnTotalCountClear);

		JButton btnCurrentCountReq = new JButton("Current Count Req (+A2h)");
		btnCurrentCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountReq.setBackground(Color.ORANGE);
		btnCurrentCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected())
					jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
							jcms[0].jcmMessage);
				if(chckbxReciclador2.isSelected())
					jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
							jcms[1].jcmMessage);
			}
		});		

		btnCurrentCountReq.setBounds(10, 630, 236, 50);
		panel_comandos.add(btnCurrentCountReq);

		JButton btnReinhibitch = new JButton("REInhibit (C3h)");
		btnReinhibitch.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReinhibitch.setBackground(Color.ORANGE);
		btnReinhibitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("RE INHIBIT");
				jcms[0].jcmMessage[3] = 0x00;
				jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
			}
		});
		btnReinhibitch.setBounds(608, 119, 144, 50);
		panel_comandos.add(btnReinhibitch);

		JButton btnCurrencyAssingRequest = new JButton("Currency Assing Req (8Ah)");
		btnCurrencyAssingRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCurrencyAssingRequest.setBounds(1496, 223, 224, 50);
		panel_comandos.add(btnCurrencyAssingRequest);
		btnCurrencyAssingRequest.setBackground(Color.ORANGE);
		
		JButton btnCierraBoveda = new JButton("CIERRA BOVEDA");
		btnCierraBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//miTio.cierraBoveda();
			}
		});
		btnCierraBoveda.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCierraBoveda.setBounds(301, 632, 203, 48);
		panel_comandos.add(btnCierraBoveda);
		
		JButton btnAbreBoveda = new JButton("ABRE BOVEDA");
		btnAbreBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//miTio.abreBoveda();
			}
		});
		btnAbreBoveda.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAbreBoveda.setBounds(526, 630, 203, 48);
		panel_comandos.add(btnAbreBoveda);
		
		JButton btnSolicitaRetiro = new JButton("SOLICITA RETIRO");
		btnSolicitaRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				referenciaNumerica = getRandomDoubleBetweenRange(111111,999999);
				montoRetiro = getRandomDoubleBetweenRange(20,5000);
				lblReferencia.setText(Integer.toString(referenciaNumerica));
				lblMonto.setText(customFormat("$###,###.###", montoRetiro));
			}
		});
		btnSolicitaRetiro.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSolicitaRetiro.setBounds(779, 630, 203, 48);
		panel_comandos.add(btnSolicitaRetiro);
		
		JButton btnCierraBoveda21 = new JButton("CIERRA BOVEDA 21");
		btnCierraBoveda21.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//miTio.cierraBoveda21();
			}
		});
		btnCierraBoveda21.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCierraBoveda21.setBounds(1059, 632, 203, 48);
		panel_comandos.add(btnCierraBoveda21);
		
		JButton btnAbreBoveda21 = new JButton("ABRE BOVEDA 21");
		btnAbreBoveda21.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//miTio.abreBoveda21();
			}
		});
		btnAbreBoveda21.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAbreBoveda21.setBounds(1284, 630, 203, 48);
		panel_comandos.add(btnAbreBoveda21);
		
		
		
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginDialog.setVisible(false);
			}
		});




		btnCurrencyAssingRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(chckbxReciclador1.isSelected())
					jcms[0].id003_format((byte) 5, (byte) 0x8A, jcms[0].jcmMessage, true);
				if(chckbxReciclador2.isSelected())
					jcms[1].id003_format((byte) 5, (byte) 0x8A, jcms[1].jcmMessage, true);
			}
		});

		/* ------ BORRAR ------------ */

		MyClass c = new MyClass();
		c.addMyEventListener(new MyEventListener() {
			public void myEventOccurred(MyEvent evt) {
				System.out.println("myEventOccurred [" + evt.getSource() + "]");

				switch(evt.getSource().toString()) {
				case "version1":
					firmwareLabel1.setText(new String(jcms[0].version));
					break;
				case "version2":
					firmwareLabel2.setText(new String(jcms[1].version));
					break;					
				case "bill1":
					lblBilleteIngresado1.setText(String.format("%d", jcms[0].bill));
					break;
				case "bill2":
					lblBilleteIngresado2.setText(String.format("%d", jcms[1].bill));
					break;					
				case "clearbill1":
					lblBilleteIngresado1.setText("");
					break;
				case "clearbill2":
					lblBilleteIngresado2.setText("");
					break;					
				case "recyclerVersion1":
					lblRecyclerVersion1.setText(new String(jcms[0].recyclerVersion));
					break;
				case "recyclerVersion2":
					lblRecyclerVersion2.setText(new String(jcms[1].recyclerVersion));
					break;					
				case "recyclerBillsA1":
					lblRecycler1.setText(jcms[0].recyclerOneA + " " + jcms[0].recyclerOneB);
					break;
				case "recyclerBillsA2":
					lblRecycler2.setText(jcms[1].recyclerOneA + " " + jcms[1].recyclerOneB);
					break;
				case "recyclerContadores1":
					lblContadores1.setText(jcms[0].recyclerContadores);
					break;
				case "recyclerContadores2":
					lblContadores2.setText(jcms[1].recyclerContadores);
					break;
				}


			}
		});
		// ![2]




		//Identificamos los puertos disponibles
		uart.portList = CommPortIdentifier.getPortIdentifiers();
		contador = 0;

		while (uart.portList.hasMoreElements()) {

			CommPortIdentifier commPort = (CommPortIdentifier) uart.portList.nextElement();


			//Checamos que sea un com{x} port
			if (commPort.getName().toUpperCase().contains("COM")  || commPort.getName().toUpperCase().contains("TTYUSB") ) {

				System.out.println("Puerto [" + commPort.getName().toUpperCase() + "]");

				jcms[contador] = new uart(contador + 1);
				jcms[contador].portId = commPort;
				jcms[contador].baud = 9600;
				jcms[contador].id = contador + 1;
				//jcms[contador].openPort(commPort.getName().toString());

				contador++;
			}			
		}

		//Inicializamos los UARTS
		for(int i = 0; i < contador; i++) {
			jcms[i].openPort(jcms[i].portId.getName().toString());
		}

	}

	public static String baitsToString(String texto, byte[] baits) {
		String result = texto;
		for (byte theByte : baits) {
			result += " [" + Integer.toHexString(theByte) + "] ";
		}
		return result;
	}

	static public String customFormat(String pattern, double value) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter.format(value);
	}

	private void textoMontoValidacion(String digito, JLabel lblMonto, JLabel lblAutorizacion) {
		
		switch(pinpadMode) {
		case None:
			break;
		case depositoUser:
			if (depositoUser.length() > 7)
				return;
			depositoUser += digito;
			
			textFieldDepositoUser.setText(depositoUser);
			
			break;
		case depositoPassword:
			
			if (depositoPassword.length() > 7)
				return;
			depositoPassword += digito;
			
			textFieldDepositoPassword.setText(depositoPassword);
			break;
		case retiroUser:
			break;
		case retiroPassword:
			break;
		case retiroClave:
			if (autorizacionDispensar.length() > 5)
				return;

			autorizacionDispensar += digito;
			
			lblAutorizacion.setText(autorizacionDispensar);
			break;
			default:
				break;
				
		}		
		
	}
	
	
	public static int getRandomDoubleBetweenRange(double min, double max){

	    int x = (int) ((Math.random()*((max-min)+1))+min);

	    return x;

	}
}
