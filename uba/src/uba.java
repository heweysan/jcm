import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeoutException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.io.CommPortIdentifier;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.AccountType;
import pentomino.common.PinpadMode;
import pentomino.common.Tio;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.gui.ImagePanel;
import pentomino.gui.LoginForm;
import pentomino.gui.ValidaRetiroForm;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.RaspiAgent;
import javax.swing.SwingConstants;
import java.awt.Component;



public class uba {

	private static PinpadMode pinpadMode = PinpadMode.None;	

	private static jcmOperation currentOperation = jcmOperation.None;

	private static JcmContadores contadoresDeposito = new JcmContadores();

	private static String loginUser = "";
	private static String loginPassword = "";

	private static int referenciaNumerica = 0;
	private static int montoRetiro = 0;
	private static String autorizacionDispensar = "";

	private static final Logger logger = LogManager.getLogger(uba.class);

	public static int montoDepositado = 0;

	private static String asteriscos = "";

	private JFrame mainFrame;

	public static boolean recyclerContadores1 = false;
	public static boolean recyclerContadores2 = false;


	uart[] jcms = new uart[2];
	int contador = 0;

	/*  //TODO: TIO
	final Tio miTio = new Tio();
	//*/

	final AgentsQueue agentsQueue = new AgentsQueue();

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

		JcmMonitor t2 = new JcmMonitor();

		t2.start();

		Thread agentsQueueThread = new Thread(agentsQueue, "agentsQueueThread");
		agentsQueueThread.start();


		initialize();

		/* TODO: TIO		
		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	

		//*/	

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		mainFrame = new JFrame("Frame Principal");
		mainFrame.getContentPane().setBackground(Color.DARK_GRAY);
		//mainFrame.setTitle("RedBlu JCM Driver (ID003)");
		mainFrame.setBounds(100, 100, 1920, 1084);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		//mainFrame.setUndecorated(true);  //Con esto ya no tiene frame de ventanita


		//String[] BaudArray = { "9600", "19200", "38400" };		
		//String[] CurrencyArray = { "20", "50", "100", "200", "500" };

		JPanel panelCont = new JPanel();
		panelCont.setAlignmentY(Component.TOP_ALIGNMENT);
		panelCont.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelCont.setBackground(Color.LIGHT_GRAY);
		panelCont.setBounds(0, 0, 1920, 1080);
		ImagePanel panelIdle = new ImagePanel(new ImageIcon("./images/ScrPlaceholder.png").getImage(),"panelIdle");
		ImagePanel panelPrincipal = new ImagePanel(new ImageIcon("./images/ScrPlaceholder.png").getImage(),"panelPrincipal");
		panelPrincipal.setOpaque(false);
		ImagePanel panelComandos = new ImagePanel(new ImageIcon("./images/ScrPlaceholder.png").getImage(),"panelComandos");
		ImagePanel panelLogin = new ImagePanel(new ImageIcon("./images/loginFormClean.png").getImage(),"panelLogin");

		ImagePanel panelToken = new ImagePanel(new ImageIcon("./images/panelToken.png").getImage(),"panelToken");

		CardLayout cl = new CardLayout();
		panelCont.setLayout(cl);


		panelCont.add(panelIdle,"Idle");
		panelIdle.setLayout(null);

		JButton btnDeposito2 = new JButton("");
		btnDeposito2.setOpaque(false);

		btnDeposito2.setBounds(361, 415, 576, 585);
		btnDeposito2.setOpaque(false);
		btnDeposito2.setContentAreaFilled(false);
		btnDeposito2.setBorderPainted(false);


		final JLabel lblDepositUser = new JLabel("");
		lblDepositUser.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblDepositUser.setForeground(Color.WHITE);
		lblDepositUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblDepositUser.setBounds(257, 545, 496, 87);


		panelIdle.add(btnDeposito2);

		JButton btnRetiro2 = new JButton("");		
		btnRetiro2.setOpaque(false);
		btnRetiro2.setContentAreaFilled(false);
		btnRetiro2.setBorderPainted(false);
		btnRetiro2.setBounds(989, 415, 576, 585);
		panelIdle.add(btnRetiro2);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setOpaque(true);
		lblNewLabel.setIcon(new ImageIcon("./images/scrPrincipal.png"));
		lblNewLabel.setBounds(0, 0, 1920, 1080);
		panelIdle.add(lblNewLabel);
		panelCont.add(panelPrincipal,"panelPrincipal");
		panelCont.add(panelComandos, "panelComandos");
		panelCont.add(panelLogin, "panelLogin");
		panelCont.add(panelToken,"panelToken");

		JPanel panelPinPad = new JPanel();
		panelPinPad.setOpaque(false);
		panelPinPad.setBackground(Color.GRAY);
		panelPinPad.setBounds(936, 0, 946, 1080);
		//panelPrincipal.add(panelPinPad);
		panelPinPad.setBorder(null);
		panelPinPad.setLayout(null);

		JButton btn2 = new JButton("2");
		btn2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn2.setBounds(359, 47, 262, 220);
		panelPinPad.add(btn2);

		JButton btn3 = new JButton("3");
		btn3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn3.setBounds(674, 47, 262, 220);
		panelPinPad.add(btn3);

		JButton btn4 = new JButton("4");
		btn4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn4.setBounds(50, 302, 259, 220);
		panelPinPad.add(btn4);

		JButton btn5 = new JButton("5");
		btn5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn5.setBounds(359, 302, 262, 220);
		panelPinPad.add(btn5);

		JButton btn6 = new JButton("6");
		btn6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn6.setBounds(674, 302, 262, 220);
		panelPinPad.add(btn6);

		JButton btn7 = new JButton("7");
		btn7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn7.setBounds(50, 557, 259, 220);
		panelPinPad.add(btn7);

		JButton btn8 = new JButton("8");
		btn8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn8.setBounds(359, 557, 267, 220);
		panelPinPad.add(btn8);

		JButton btn9 = new JButton("9");
		btn9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn9.setBounds(664, 557, 272, 220);
		panelPinPad.add(btn9);

		JButton btn0 = new JButton("0");
		btn0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn0.setBounds(359, 812, 267, 220);
		panelPinPad.add(btn0);

		JButton btnCancel = new JButton("CANCELAR");
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnCancel.setBackground(Color.RED);
		btnCancel.setBounds(50, 812, 259, 220);
		panelPinPad.add(btnCancel);

		JButton btnConfirmar = new JButton("CONFIRMAR");
		
		btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnConfirmar.setBackground(Color.GREEN);
		btnConfirmar.setBounds(664, 812, 272, 220);
		panelPinPad.add(btnConfirmar);

		JButton btn1 = new JButton("1");
		btn1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn1.setBounds(50, 47, 260, 220);
		panelPinPad.add(btn1);







		JLabel lblLoginMensaje = new JLabel("Para depositar, identif\u00EDcate");
		lblLoginMensaje.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginMensaje.setForeground(Color.WHITE);
		lblLoginMensaje.setBounds(89, 139, 837, 70);
		panelLogin.add(lblLoginMensaje);

		panelLogin.add(panelPinPad);


		panelLogin.add(lblDepositUser);

		final JLabel lblDepositPassword = new JLabel("");
		lblDepositPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblDepositPassword.setForeground(Color.WHITE);
		lblDepositPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblDepositPassword.setBounds(257, 793, 496, 87);
		panelLogin.add(lblDepositPassword);

		JButton btnComandos = new JButton("COMANDOS");
		btnComandos.setBounds(10, 11, 263, 113);
		panelLogin.add(btnComandos);
		btnComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont, "comandos");
			}
		});
		btnComandos.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnComandos.setBackground(Color.ORANGE);
		
		final JLabel lblLoginRow1 = new JLabel("");
		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginRow1.setBounds(89, 58, 837, 70);
		panelLogin.add(lblLoginRow1);

		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\loginFormClean.png"));
		lblNewLabel_1.setBounds(0, 0, 1920, 1080);
		panelLogin.add(lblNewLabel_1);

		mainFrame.getContentPane().add(panelCont);


		//tabbedPane.addTab("Principal", (Icon) null, panelPrincipal, null);
		//tabbedPane.setEnabledAt(0, true);
		panelPrincipal.setLayout(null);

		JPanel panel_estatus = new JPanel();
		panel_estatus.setBounds(10, 11, 850, 247);
		panelPrincipal.add(panel_estatus);
		panel_estatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_estatus.setLayout(null);
		panel_estatus.setOpaque(false);

		JPanel panelJCM2 = new JPanel();
		panelJCM2.setOpaque(false);
		panelJCM2.setBounds(440, 11, 400, 225);
		panel_estatus.add(panelJCM2);
		panelJCM2.setLayout(null);
		panelJCM2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lblRecycler2 = new JLabel(".");
		lblRecycler2.setBounds(10, 102, 210, 53);
		panelJCM2.add(lblRecycler2);
		lblRecycler2.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador2 = new JLabel("Reciclador 2");
		lblTitleReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTitleReciclador2.setBounds(10, 11, 144, 28);
		panelJCM2.add(lblTitleReciclador2);

		JLabel lblBilleteIngresado2 = new JLabel("$0");
		lblBilleteIngresado2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado2.setBounds(164, 166, 226, 46);
		panelJCM2.add(lblBilleteIngresado2);

		JLabel lblTxtBill2 = new JLabel("Billete:");
		lblTxtBill2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill2.setBounds(10, 166, 144, 46);
		panelJCM2.add(lblTxtBill2);

		JLabel lblContadores2 = new JLabel("rec1/0  rec2/0");
		lblContadores2.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblContadores2.setBounds(200, 114, 190, 37);
		panelJCM2.add(lblContadores2);

		JButton btnReiniciarJcm2 = new JButton("REINICIAR");		
		btnReiniciarJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm2.setBounds(181, 11, 209, 80);
		panelJCM2.add(btnReiniciarJcm2);

		JPanel panelJCM1 = new JPanel();
		panelJCM1.setBounds(10, 11, 400, 225);
		panel_estatus.add(panelJCM1);
		panelJCM1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM1.setLayout(null);
		panelJCM1.setOpaque(false);

		JLabel lblRecycler1 = new JLabel(".");
		lblRecycler1.setBounds(10, 102, 210, 53);
		panelJCM1.add(lblRecycler1);
		lblRecycler1.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador1 = new JLabel("Reciclador 1");
		lblTitleReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTitleReciclador1.setBounds(10, 11, 161, 28);
		panelJCM1.add(lblTitleReciclador1);

		JLabel lblTxtBill1 = new JLabel("Billete:");
		lblTxtBill1.setBounds(10, 166, 144, 46);
		panelJCM1.add(lblTxtBill1);
		lblTxtBill1.setFont(new Font("Tahoma", Font.BOLD, 30));

		final JLabel lblBilleteIngresado1 = new JLabel("$0");
		lblBilleteIngresado1.setBounds(164, 166, 226, 46);
		panelJCM1.add(lblBilleteIngresado1);
		lblBilleteIngresado1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JLabel lblContadores1 = new JLabel("rec1/0  rec2/0");
		lblContadores1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblContadores1.setBounds(200, 114, 190, 37);
		panelJCM1.add(lblContadores1);

		JButton btnReiniciarJcm1 = new JButton("REINICIAR");
		btnReiniciarJcm1.setBounds(181, 11, 209, 80);
		panelJCM1.add(btnReiniciarJcm1);

		btnReiniciarJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JPanel panelRetiro = new JPanel();
		panelRetiro.setOpaque(false);
		panelRetiro.setBounds(10, 269, 850, 709);
		panelPrincipal.add(panelRetiro);
		panelRetiro.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelRetiro.setLayout(null);


		JPanel panelMensajes = new JPanel();
		panelMensajes.setBounds(10, 11, 830, 63);
		panelRetiro.add(panelMensajes);
		panelMensajes.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelMensajes.setLayout(null);

		final JLabel lblMensajes = new JLabel(".");
		lblMensajes.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblMensajes.setBounds(10, 11, 810, 41);
		panelMensajes.add(lblMensajes);

		JPanel panelInfoOperacion = new JPanel();
		panelInfoOperacion.setBounds(10, 85, 830, 613);
		panelRetiro.add(panelInfoOperacion);
		panelInfoOperacion.setLayout(null);
		panelInfoOperacion.setOpaque(false);

		final JLabel lblOperacion1 = new JLabel(".");
		lblOperacion1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblOperacion1.setBounds(10, 11, 326, 74);
		panelInfoOperacion.add(lblOperacion1);

		final JLabel lblOperacion2 = new JLabel(".");
		lblOperacion2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblOperacion2.setBounds(370, 11, 450, 74);
		panelInfoOperacion.add(lblOperacion2);

		JButton btnOperacion1 = new JButton("Terminar");

		btnOperacion1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				if(currentOperation == jcmOperation.Deposit) {
					//Terminamos el deposito

					DepositOpVO depositOpVO = new DepositOpVO();

					depositOpVO.atmId = "CI01GL0001"; //"CIXXGS0020";
					depositOpVO.amount = (long) montoDepositado;
					depositOpVO.b20 = contadoresDeposito.x20;
					depositOpVO.b50 = contadoresDeposito.x50;
					depositOpVO.b100 = contadoresDeposito.x100;
					depositOpVO.b200 = contadoresDeposito.x200;
					depositOpVO.b500 = contadoresDeposito.x500;
					depositOpVO.b1000 = 0;
					depositOpVO.operatorId = 7007;
					depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					depositOpVO.userName = "007007";

					String billetes = "[" + contadoresDeposito.x20 + "x20|" + contadoresDeposito.x50 + "x50|" + contadoresDeposito.x100 + "x100|" + contadoresDeposito.x200 + "x200|" + contadoresDeposito.x500 + "x500|0x1000]";


					RaspiAgent.WriteToJournal("CASH MANAGEMENT", montoDepositado,0, "","", "PROCESADEPOSITO ConfirmaDeposito " + billetes, AccountType.Administrative, TransactionType.CashManagement);


					try {
						Transactions.ConfirmaDeposito(depositOpVO);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}


				}

				if(currentOperation == jcmOperation.Dispense) {

				}


			}
		});
		btnOperacion1.setBackground(Color.LIGHT_GRAY);
		btnOperacion1.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnOperacion1.setBounds(420, 382, 400, 220);
		panelInfoOperacion.add(btnOperacion1);

		JButton btnComandos_1 = new JButton("COMANDOS");
		btnComandos_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnComandos_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnComandos_1.setBackground(Color.ORANGE);
		btnComandos_1.setBounds(1072, 11, 321, 136);
		panelPrincipal.add(btnComandos_1);
		/*
		Timer btnRetiroAlertBlinker = new Timer(500, new ActionListener() {
			boolean on=false;
			public void actionPerformed(ActionEvent e) {
				// blink the button background on and off
				btnRetiro.setBackground( on ? Color.ORANGE : null);
				on = !on;
			}
		});        
		*/
		
		//TODO: HEWEY AQUI JPanel panelComandos = new JPanel();
		//tabbedPane.addTab("Comandos", (Icon) null, panelComandos, null);
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
				jcms[0].currentOpertion = jcmOperation.Reset; 
				//Primero se piden los estatus
				jcms[0].id003_format((byte)5, protocol.SSR_VERSION, jcms[0].jcmMessage,true); //SSR_VERSION 0x88

				//jcms[0].id003_format((byte) 5, (byte) 0x40, jcms[0].jcmMessage, true);
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

		btnRecycleRefillModeReq.setBounds(10, 569, 270, 50);
		panel_comandos.add(btnRecycleRefillModeReq);

		JButton btnTotalCountReq = new JButton("Total Count Req (+A0h)");
		btnTotalCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountReq.setBackground(Color.ORANGE);

		btnTotalCountReq.setBounds(301, 569, 205, 50);
		panel_comandos.add(btnTotalCountReq);

		JButton btnTotalCountClear = new JButton("Total Count Clear (+A1h)");
		btnTotalCountClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountClear.setBackground(Color.ORANGE);

		btnTotalCountClear.setBounds(516, 569, 210, 50);
		panel_comandos.add(btnTotalCountClear);

		JButton btnCurrentCountReq = new JButton("Current Count Req (+A2h)");
		btnCurrentCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountReq.setBackground(Color.ORANGE);

		btnCurrentCountReq.setBounds(10, 630, 236, 50);
		panel_comandos.add(btnCurrentCountReq);

		JButton btnReinhibitch = new JButton("REInhibit (C3h)");
		btnReinhibitch.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReinhibitch.setBackground(Color.ORANGE);

		btnReinhibitch.setBounds(608, 119, 144, 50);
		panel_comandos.add(btnReinhibitch);

		JButton btnCurrencyAssingRequest = new JButton("Currency Assing Req (8Ah)");
		btnCurrencyAssingRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCurrencyAssingRequest.setBounds(1496, 223, 224, 50);
		panel_comandos.add(btnCurrencyAssingRequest);
		btnCurrencyAssingRequest.setBackground(Color.ORANGE);

		JButton btnCierraBoveda = new JButton("CIERRA BOVEDA");

		btnCierraBoveda.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnCierraBoveda.setBounds(301, 632, 203, 48);
		panel_comandos.add(btnCierraBoveda);

		JButton btnAbreBoveda = new JButton("ABRE BOVEDA");

		btnAbreBoveda.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnAbreBoveda.setBounds(526, 630, 203, 48);
		panel_comandos.add(btnAbreBoveda);

		JButton btnSolicitaRetiro = new JButton("SOLICITA RETIRO");

		btnSolicitaRetiro.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSolicitaRetiro.setBounds(779, 630, 203, 48);
		panel_comandos.add(btnSolicitaRetiro);

		JButton btnRegresar = new JButton("Regresar");
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont, "principal");
			}
		});
		btnRegresar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnRegresar.setBackground(Color.ORANGE);
		btnRegresar.setBounds(599, 24, 157, 50);
		panelComandos.add(btnRegresar);

		JButton btnSalir = new JButton("SALIR");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		btnSalir.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSalir.setBackground(Color.RED);
		btnSalir.setBounds(867, 24, 157, 50);
		panelComandos.add(btnSalir);


		//TODO: LISTENERS
		/* - - - - - - - - -   L I S T E N E R S   - - - - - - - - - */

		
		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				switch(pinpadMode) {
				case loginUser:
					pinpadMode = PinpadMode.loginPassword;
					break;
				case loginPassword:

					if(loginUser.length() <= 0) {
						//No ha ingresado su user
						pinpadMode = PinpadMode.loginUser;						
						return;

					}

					if(loginPassword.length() <= 0) {
						//No ha ingresado su pwd
						pinpadMode = PinpadMode.loginPassword;						
						return;
					}


					System.out.println("Validando usuario....");
					//Validamos el usuario
					CMUserVO user = Transactions.ValidaUsuario(loginUser);

					if(user.isValid) {
						if(user.allowWithdrawals) {
							//Lo dejamos depositar
							System.out.println("Usuario Valido...");

							//CASHIN...
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",
									AccountType.Administrative, TransactionType.ControlMessage);


							Transactions.BorraCashInOPs("CI01GL0001");		

							montoDepositado = 0;
							
							cl.show(panelCont, "panelPrincipal");

						}
						else {
							lblLoginRow1.setText("�Oh no!");
							lblLoginMensaje.setText("       Est�n mal los datos.");
							pinpadMode = PinpadMode.loginUser;
				
							return;
						}
					}
					else {
						lblLoginRow1.setText("�Oh no!");
						lblLoginMensaje.setText("       Est�n mal los datos.");
						pinpadMode = PinpadMode.loginUser;				
						RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);


						return;
					}


					pinpadMode = PinpadMode.None;






					break;
				default:
					break;

				}			

				//pinpadMode = PinpadMode.None;
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginUser = "";
				loginPassword = "";
				autorizacionDispensar = "";
				lblDepositUser.setText("");
				lblDepositPassword.setText("");

				switch(pinpadMode) {
				case None:
					break;
				case loginUser:			
					loginUser = "";
					//LoginForm.tfUser.setText(loginUser);					
					break;
				case loginPassword:	
					loginPassword = "";
					asteriscos = "";
					//LoginForm.tfPassword.setText(asteriscos);
					break;				
				case retiroToken:
					autorizacionDispensar = "";				
					ValidaRetiroForm.textFieldConfirmacion.setText(autorizacionDispensar);
					break;
				default:
					break;						
				}

				cl.show(panelCont, "Idle");
			}
		});

		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("1",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("1",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}			
			}
		});

		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("2",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("2",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}	
			}
		});

		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("3",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("3",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}	
			}
		});

		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("4",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("4",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}	
			}
		});

		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("5",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("5",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}	
			}
		});

		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("6",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("6",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}				}
		});

		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("7",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("7",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}				}
		});

		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("8",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("8",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}				}
		});

		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("9",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("9",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}				}
		});

		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("0",lblDepositPassword);
					break;
				case loginUser:
					textoMontoValidacion("0",lblDepositUser);
					break;
				case retiroToken:
					break;
				default:
					break;				
				}				}
		});

		btnDeposito2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pinpadMode = PinpadMode.loginUser;
				lblLoginMensaje.setText("Para depostiar, identif�cate");
				currentOperation = jcmOperation.Deposit;
				cl.show(panelCont, "panelLogin");

			}
		});


		btnRetiro2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pinpadMode = PinpadMode.loginUser;
				currentOperation = jcmOperation.Dispense;
				lblLoginMensaje.setText("Para retirar, identif�cate");
				cl.show(panelCont, "panelLogin");
			}
		});



		ValidaRetiroForm.btnAceptar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("Validamos que los datos ingresados sean correctos");

				if(ValidaRetiroForm.textFieldConfirmacion.getText().length() == 0) {
					System.out.println("NO SON IGUALES");
					ValidaRetiroForm.textFieldConfirmacion.setText("");
					pinpadMode = PinpadMode.retiroToken;
					return;
				}


				if(Integer.parseInt(ValidaRetiroForm.textFieldConfirmacion.getText()) != referenciaNumerica) {
					System.out.println("NO SON IGUALES");
					ValidaRetiroForm.textFieldConfirmacion.setText("");
					pinpadMode = PinpadMode.retiroToken;
					return;
				}

				//Cerramos la ventana, comenzamos a dispensar.
				ValidaRetiroForm.validationDialog.setVisible(false);
				ValidaRetiroForm.validationDialog.dispose();
				pinpadMode = PinpadMode.None;

				lblOperacion1.setText("Monto: ");

				Dispensar();								
			}

		});

		ValidaRetiroForm.btnCancelar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("ValidaRetirForm Cancelar");				

				ValidaRetiroForm.textFieldConfirmacion.setText("");

				ValidaRetiroForm.textFieldConfirmacion.setText("");

				//Cerramos la ventana, comenzamos a dispensar.
				ValidaRetiroForm.validationDialog.setVisible(false);
				ValidaRetiroForm.validationDialog.dispose();
				pinpadMode = PinpadMode.None;


			}

		});

		btnReiniciarJcm1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				jcms[0].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...				
				jcms[0].id003_format((byte)5, protocol.SSR_VERSION, jcms[0].jcmMessage,true); //SSR_VERSION 0x88
			}
		});

		btnReiniciarJcm2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				jcms[1].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...
				jcms[1].id003_format((byte)5, protocol.SSR_VERSION, jcms[1].jcmMessage,true); //SSR_VERSION 0x88
			}
		});

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

		btnTotalCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});		

		btnRecycleSoftwareVersionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x93, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});

		btnRecycleRefillModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});


		btnTotalCountClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
			}
		});

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

		btnReinhibitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("RE INHIBIT");
				jcms[0].jcmMessage[3] = 0x00;
				jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
			}
		});

		btnCierraBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* TODO: TIO
				miTio.cierraBoveda();
				//*/
			}
		});


		btnAbreBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* TODO: TIO
				miTio.abreBoveda();
				//*/
			}
		});

		btnSolicitaRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				//tabbedPane.setSelectedIndex(0);

				referenciaNumerica = getRandomDoubleBetweenRange(111111,999999);
				montoRetiro = getRandomDoubleBetweenRange(20,1500);						

				/*
				btnRetiro.setEnabled(true);
				btnRetiroAlertBlinker.start();
				*/
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



		EventListenerClass c = new EventListenerClass();
		c.addMyEventListener(new MyEventListener() {
			public void myEventOccurred(MyEvent evt) {
				System.out.println("myEventOccurred [" + evt.getSource() + "]");

				switch(evt.getSource().toString()) {

				case "bill1":							

					switch(jcms[0].bill)
					{
					case 20:
						contadoresDeposito.x20++;
						break;
					case 50:
						contadoresDeposito.x50++;
						break;
					case 100:
						contadoresDeposito.x100++;
						break;
					case 200:
						contadoresDeposito.x200++;
						break;
					case 500:
						contadoresDeposito.x500++;
						break;
					}

					NumberFormat format1 = NumberFormat.getCurrencyInstance();
					montoDepositado += jcms[0].bill;					
					String currency1 = format1.format(montoDepositado);
					String billete1 = format1.format(jcms[0].bill);

					System.out.println(currency1 + " - " + billete1);
					System.out.println(format1.format(currency1));
					lblOperacion1.setText(format1.format(currency1));
					lblBilleteIngresado1.setText(billete1);
					break;
				case "bill2":			 
					switch(jcms[1].bill)
					{
					case 20:
						contadoresDeposito.x20++;
						break;
					case 50:
						contadoresDeposito.x50++;
						break;
					case 100:
						contadoresDeposito.x100++;
						break;
					case 200:
						contadoresDeposito.x200++;
						break;
					case 500:
						contadoresDeposito.x500++;
						break;
					}
					montoDepositado += jcms[1].bill;					
					System.out.println("$" + montoDepositado);
					lblOperacion2.setText("$" + montoDepositado);
					lblBilleteIngresado2.setText("$" + jcms[1].bill);
					break;					
				case "clearbill1":
					lblBilleteIngresado1.setText("");
					break;
				case "clearbill2":
					lblBilleteIngresado2.setText("");
					break;		
				case "recyclerBillsA1":					
					lblRecycler1.setText(jcms[0].recyclerDenom1 + " " + jcms[0].recyclerDenom2);
					break;
				case "recyclerBillsA2":					
					lblRecycler2.setText(jcms[1].recyclerDenom1 + " " + jcms[1].recyclerDenom2);
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



		cl.show(panelCont, "Idle");

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


	private void textoMontoValidacion(String digito, JLabel detLabel) {

		switch(pinpadMode) {
		case None:
			break;
		case loginUser:
			if (loginUser.length() > 7)				
				return;
			loginUser += digito;
			//LoginForm.tfUser.setText(loginUser);
			detLabel.setText(loginUser);			
			break;
		case loginPassword:
			if (loginPassword.length() > 7)
				return;
			loginPassword += digito;
			asteriscos += "*";
			//LoginForm.tfPassword.setText(asteriscos);
			detLabel.setText(asteriscos);
			break;		
		case retiroToken:
			if (autorizacionDispensar.length() > 7)
				return;
			autorizacionDispensar += digito;
			ValidaRetiroForm.textFieldConfirmacion.setText(autorizacionDispensar);
			detLabel.setText(autorizacionDispensar);
			break;
		default:
			break;				
		}		

	}


	public static int getRandomDoubleBetweenRange(double min, double max){

		int x = (int) ((Math.random()*((max-min)+1))+min);
		return x;

	}


	void Dispensar() {


		/*
		jcms[0].currentOpertion = jcmOperation.PreDispense;
		jcms[1].currentOpertion = jcmOperation.PreDispense;
		 */

		//Checamos los contadores actuales
		jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,jcms[0].jcmMessage);

		jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,jcms[1].jcmMessage);


		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean enEspera = true;

		while(enEspera) {

			if(jcms[0].recyclerContadores1 && jcms[1].recyclerContadores1){
				enEspera = false;
			}
			else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		//Checamos que tenga algo de dinero.
		if(jcms[0].contadores.Cass1Available == 0 && jcms[0].contadores.Cass2Available == 0 && jcms[1].contadores.Cass1Available == 0 && jcms[1].contadores.Cass2Available == 0){
			System.out.println("No hay dinero en los caseteros para dispensar");
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
		System.out.println("Billetes a dipensar de JCM1 CASS1" + jcms[0].jcmCass1);

		solicitado = solicitado - (jcms[0].jcmCass1 * jcms[0].contadores.Cass1Denom);

		System.out.println("Faltante [" + solicitado + "]");


		jcms[0].jcmCass2 = solicitado / jcms[0].contadores.Cass2Denom;
		System.out.println("Billetes a dipensar de JCM1 CASS2" + jcms[0].jcmCass2);

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
		System.out.println("Billetes a dipensar de JCM2 CASS1" + jcms[1].jcmCass1);

		//Reviamos si necestia mas de los que tiene
		if(jcms[1].jcmCass1 > jcms[1].contadores.Cass1Available) {
			jcms[1].jcmCass1 = jcms[1].contadores.Cass1Available;
		}

		solicitado = solicitado - (jcms[1].jcmCass1 * jcms[1].contadores.Cass1Denom);

		System.out.println("Faltante [" + solicitado + "]");

		jcms[1].jcmCass2 = solicitado / jcms[1].contadores.Cass2Denom;
		System.out.println("Billetes a dipensar de JCM2 CASS2" + jcms[1].jcmCass2);

		//Reviamos si necestia mas de los que tiene
		if(jcms[1].jcmCass2 > jcms[1].contadores.Cass2Available) {
			jcms[1].jcmCass2 = jcms[1].contadores.Cass2Available;
		}

		solicitado = solicitado - (jcms[1].jcmCass2 * jcms[1].contadores.Cass2Denom);


		System.out.println("jcm1cass1 [" + jcms[0].jcmCass1 + "] jcm1cass2 [" + jcms[0].jcmCass2 + "] jcm2cass1 [" + jcms[1].jcmCass1 + "]jcm2cass2 [" + jcms[1].jcmCass2 + "]" );

		System.out.println("Faltante [" + solicitado + "]");

		// Primero deshabilitamos el que acepte billetes	


		System.out.println("Retirar");

		ArrayList<JcmCassetero> orden = new ArrayList<JcmCassetero>();

		JcmCassetero cass = null;

		//500, 50, 100,200

		if(jcms[0].contadores.Cass1Available > 0) {
			cass = new JcmCassetero();
			cass.Jcm = 0;
			cass.Cassete = 1;
			cass.Denomincacion = jcms[0].contadores.Cass1Denom;
			orden.add(cass);
		}

		if(jcms[0].contadores.Cass2Available > 0) {
			cass = new JcmCassetero();
			cass.Jcm = 0;
			cass.Cassete = 2;
			cass.Denomincacion = jcms[0].contadores.Cass2Denom;
			orden.add(cass);
		}

		if(jcms[1].contadores.Cass1Available > 0) {
			cass = new JcmCassetero();
			cass.Jcm = 1;
			cass.Cassete = 1;
			cass.Denomincacion = jcms[1].contadores.Cass1Denom;
			orden.add(cass);
		}

		if(jcms[1].contadores.Cass2Available > 0) {
			cass = new JcmCassetero();
			cass.Jcm = 1;
			cass.Cassete = 2;
			cass.Denomincacion = jcms[1].contadores.Cass2Denom;
			orden.add(cass);
		}

		System.out.println("PRE ORDEN");
		for(JcmCassetero dat : orden) {
			System.out.println("" + dat.getDenomincacion());
		}


		orden.sort(new DenominacionSorter());

		System.out.println("POST ORDEN");
		for(JcmCassetero dat : orden) {
			System.out.println("" + dat.getDenomincacion());
		}


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


	class DenominacionSorter implements Comparator<JcmCassetero> 
	{
		@Override
		public int compare(JcmCassetero o1, JcmCassetero o2) {
			return o2.getDenomincacion().compareTo(o1.getDenomincacion());
		}
	}
}
