package pentomino.flow;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.io.CommPortIdentifier;
import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.cashmanagement.vo.CmMessageRequest;
import pentomino.cashmanagement.vo.CmWithdrawal;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.AccountType;
import pentomino.common.Afd;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.Ptr;
import pentomino.common.Tio;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.gui.DebugButtons;
import pentomino.gui.FlowLayout;
import pentomino.gui.ImagePanel;
import pentomino.gui.PanelDebug;
import pentomino.gui.PanelMenu;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;

public class Flow {
	
	private static final Logger logger = LogManager.getLogger(Flow.class.getName());

	private static boolean isDebug = false;

	private static jcmOperation currentOperation = jcmOperation.None;
	private static DispenseStatus dispenseStatus = DispenseStatus.None; 

	private static JcmContadores contadoresDeposito = new JcmContadores();	

	private static double montoRetiro = 0;
	public static int montoDepositado = 0;
	private static String token;

	private static String asteriscos = "";

	private JFrame mainFrame;

	public static boolean recyclerContadores1 = false;
	public static boolean recyclerContadores2 = false;

	private static boolean recyclerBills1Set = false;
	private static boolean recyclerBills2Set = false;

	private static Timer screenTimerTimeout = new Timer();

	/* Variables de control para saber que ya dispensaron todos los caseteros*/
	private boolean jcm1cass1Dispensed = false;
	private boolean jcm1cass2Dispensed = false;
	private boolean jcm2cass1Dispensed = false;
	private boolean jcm2cass2Dispensed = false;

	public static JPanel panelContainer = new JPanel();
	public static FlowLayout cl = new FlowLayout();
	
	public static uart[] jcms = new uart[2];
	int contador = 0;

	
	public final static Tio miTio = new Tio();
	
	final AgentsQueue agentsQueue = new AgentsQueue();
	final CmQueue cmQueue = new CmQueue();
	final DTAServer dtaServer = new DTAServer();

	
	public static void main(String[] args) {

		logger.info("----- FLOW MAIN -----");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Flow window = new Flow();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * FLOW
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public Flow() throws IOException, TimeoutException {

		//https://logging.apache.org/log4j/2.x/manual/layouts.html

		isDebug = System.getProperty("os.name").toLowerCase().contains("windows");
		System.out.println(System.getProperty("os.name") + " isDebug[" + isDebug + "]");

		JcmMonitor t2 = new JcmMonitor();
		t2.start();

		Thread agentsQueueThread = new Thread(agentsQueue, "agentsQueueThread");
		agentsQueueThread.start();


		Thread cmQueueThread = new Thread(cmQueue, "cmQueueThread");
		cmQueueThread.start();

		dtaServer.SetupRabbitListener();		

		//TIO	
		if(!isDebug) {
			Thread tioThread = new Thread(miTio, "Tio Thread");
			tioThread.start();	
		}
		
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {


		JcmGlobalData.maxRecyclableCash = Integer.parseInt(Config.GetDirective("maxRecyclableCash","1500"));
		
		panelContainer.setAlignmentY(Component.TOP_ALIGNMENT);
		panelContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContainer.setBackground(Color.LIGHT_GRAY);
		panelContainer.setBounds(0, 0, 1920, 1080);

		mainFrame = new JFrame("Frame Principal");
		mainFrame.getContentPane().setBackground(Color.DARK_GRAY);
		mainFrame.setBounds(100, 100, 1920, 1084);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(panelContainer);
		//mainFrame.setUndecorated(true);  //Con esto ya no tiene frame de ventanita

		ImagePanel panelIdle = new ImagePanel(new ImageIcon("./images/Scr7Inicio.png").getImage(),"panelIdle");
		/*
		panelIdle.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("Entre a panelIdle");				
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("Sali de panelIdle");
			}
		});
		*/
		ImagePanel panelMenuHolder = new ImagePanel(new ImageIcon("./images/Scr7SinRetiroAutorizado.png").getImage(),"panelMenu");
		PanelMenu panelMenu = new PanelMenu();
		panelMenuHolder.add(panelMenu.getPanel());
		
		ImagePanel panelDeposito = new ImagePanel(new ImageIcon("./images/Scr7MontoIngresado.png").getImage(),"panelDeposito");
		
		ImagePanel panelComandosHolder = new ImagePanel(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelComandos");
		PanelDebug panelComandos = new PanelDebug();
		panelComandosHolder.add(panelComandos.getPanel());		
		
		ImagePanel panelLogin = new ImagePanel(new ImageIcon("./images/Scr7IdentificateDeposito.png").getImage(),"panelLogin");
		ImagePanel panelToken = new ImagePanel(new ImageIcon("./images/Scr7ConfirmaToken.png").getImage(),"panelToken");
		ImagePanel panelTerminamos = new ImagePanel(new ImageIcon("./images/ScrTerminamos.png").getImage(),"panelTerminamos");
		ImagePanel panelRetiraBilletes = new ImagePanel(new ImageIcon("./images/ScrRetiraBilletes.png").getImage(),"panelRetiraBilletes");
		ImagePanel panelRetiroParcial = new ImagePanel(new ImageIcon("./images/Scr7RetiroParcial.png").getImage(),"panelRetiroParcial");
		ImagePanel panelError = new ImagePanel(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelError");
		ImagePanel panelOperacionCancelada = new ImagePanel(new ImageIcon("./images/Scr7OperacionCancelada.png").getImage(),"panelOperacionCancelada");
		
		panelOperacionCancelada.panelRedirect = "panelIdle";
		panelOperacionCancelada.screenTimeOut = 7000;
		panelError.panelRedirect = "panelIdle";
		panelError.screenTimeOut = 7000;
		

		/*
		panelIdle.setLayout(null);
		panelMenu.setLayout(null);
		panelDeposito.setLayout(null);
		panelComandos.setLayout(null);
		panelLogin.setLayout(null);
		panelToken.setLayout(null);
		panelTerminamos.setLayout(null);
		panelRetiraBilletes.setLayout(null);
		panelRetiroParcial.setLayout(null);
		panelError.setLayout(null);
		panelOperacionCancelada.setLayout(null);
		*/
		
		DebugButtons miPinPad = new DebugButtons();
						
		panelContainer.setLayout(cl);		
		panelContainer.add(panelIdle,"panelIdle");
		panelContainer.add(panelMenuHolder,"panelMenu");
		panelContainer.add(panelDeposito,"panelDeposito");
		panelContainer.add(panelComandosHolder, "panelComandos");
		panelContainer.add(panelLogin, "panelLogin");
		panelContainer.add(panelToken,"panelToken");
		panelContainer.add(panelTerminamos,"panelTerminamos");
		panelContainer.add(panelRetiraBilletes,"panelRetiraBilletes");
		panelContainer.add(panelRetiroParcial,"panelRetiroParcial");
		panelContainer.add(panelError,"panelError");
		panelContainer.add(panelOperacionCancelada,"panelOperacionCancelada");		
		panelContainer.add(miPinPad.panelDebugButtons,"miPipad");

		JLabel lblPanelError = new JLabel("");
		lblPanelError.setHorizontalAlignment(SwingConstants.CENTER);
		lblPanelError.setFont(new Font("Tahoma", Font.PLAIN, 60));
		lblPanelError.setForeground(Color.WHITE);
		lblPanelError.setBounds(10, 585, 1877, 103);
		panelError.add(lblPanelError);

		JLabel lblRetiraBilletesMontoDispensarParcial = new JLabel("New label");
		lblRetiraBilletesMontoDispensarParcial.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiraBilletesMontoDispensarParcial.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensarParcial.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblRetiraBilletesMontoDispensarParcial.setBounds(501, 677, 622, 153);
		panelRetiroParcial.add(lblRetiraBilletesMontoDispensarParcial);


		/*** PANEL IDLE  ***/		
		
		panelIdle.add(new DebugButtons().getPanel());

		JButton btnIdleSalir = new JButton("SALIR");
		btnIdleSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JButton btnIdle = new JButton("");
		btnIdle.setBounds(0, 0, 1920, 1080);		
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnIdle.setOpaque(false);
		btnIdle.setContentAreaFilled(false);
		btnIdle.setBorderPainted(false);		
		panelIdle.add(btnIdle);

		/*		
		Timer screenTimer = new Timer();
		screenTimer.schedule(new TimerTask() {
			@Override
			public void run() {				                
				//Revisamos si hay retiros listos									
				//panelIdle.setBackground("./images/BTN7_NO.png");						
				//screenTimer.cancel();
				cl.show(panelContainer, "miPipad");
			}
		}, 3000);
		 //*/


		
		panelRetiraBilletes.add(new DebugButtons().getPanel());
		
		JLabel lblRetiraBilletesMontoDispensar = new JLabel("New label");
		lblRetiraBilletesMontoDispensar.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiraBilletesMontoDispensar.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblRetiraBilletesMontoDispensar.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
		panelRetiraBilletes.add(lblRetiraBilletesMontoDispensar);

		/***
		 * - - - - - - - - - -   P A N E L   P I N P A D   - - - - - - - - - - 
		 */

		JPanel panelPinPad = new JPanel();
		panelPinPad.setOpaque(false);
		panelPinPad.setBackground(Color.GRAY);
		panelPinPad.setBounds(936, 0, 946, 1080);
		panelPinPad.setBorder(null);
		panelPinPad.setLayout(null);


		JButton btnPinPad1 = new JButton(new ImageIcon("./images/BTN7_1.png"));
		btnPinPad1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad1.setBounds(50, 47, 260, 220);
		btnPinPad1.setOpaque(false);
		btnPinPad1.setContentAreaFilled(false);
		btnPinPad1.setBorderPainted(false);
		panelPinPad.add(btnPinPad1);

		JButton btnPinPad2 = new JButton(new ImageIcon("./images/BTN7_2.png"));
		btnPinPad2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad2.setBounds(359, 47, 262, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad2.setContentAreaFilled(false);
		btnPinPad2.setBorderPainted(false);
		panelPinPad.add(btnPinPad2);

		JButton btnPinPad3 = new JButton(new ImageIcon("./images/BTN7_3.png"));
		btnPinPad3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad3.setBounds(674, 47, 262, 220);
		btnPinPad3.setOpaque(false);
		btnPinPad3.setContentAreaFilled(false);
		btnPinPad3.setBorderPainted(false);
		panelPinPad.add(btnPinPad3);

		JButton btnPinPad4 = new JButton(new ImageIcon("./images/BTN7_4.png"));
		btnPinPad4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad4.setBounds(50, 302, 259, 220);
		btnPinPad4.setOpaque(false);
		btnPinPad4.setContentAreaFilled(false);
		btnPinPad4.setBorderPainted(false);
		panelPinPad.add(btnPinPad4);

		JButton btnPinPad5 = new JButton(new ImageIcon("./images/BTN7_5.png"));
		btnPinPad5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad5.setBounds(359, 302, 262, 220);
		btnPinPad5.setOpaque(false);
		btnPinPad5.setContentAreaFilled(false);
		btnPinPad5.setBorderPainted(false);
		panelPinPad.add(btnPinPad5);

		JButton btnPinPad6 = new JButton(new ImageIcon("./images/BTN7_6.png"));
		btnPinPad6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad6.setBounds(674, 302, 262, 220);
		btnPinPad6.setOpaque(false);
		btnPinPad6.setContentAreaFilled(false);
		btnPinPad6.setBorderPainted(false);
		panelPinPad.add(btnPinPad6);

		JButton btnPinPad7 = new JButton(new ImageIcon("./images/BTN7_7.png"));
		btnPinPad7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad7.setBounds(50, 557, 259, 220);
		btnPinPad7.setOpaque(false);
		btnPinPad7.setContentAreaFilled(false);
		btnPinPad7.setBorderPainted(false);
		panelPinPad.add(btnPinPad7);

		JButton btnPinPad8 = new JButton(new ImageIcon("./images/BTN7_8.png"));
		btnPinPad8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad8.setBounds(359, 557, 267, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad8.setContentAreaFilled(false);
		btnPinPad8.setBorderPainted(false);
		panelPinPad.add(btnPinPad8);

		JButton btnPinPad9 = new JButton(new ImageIcon("./images/BTN7_9.png"));
		btnPinPad9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad9.setBounds(664, 557, 272, 220);
		btnPinPad9.setOpaque(false);
		btnPinPad9.setContentAreaFilled(false);
		btnPinPad9.setBorderPainted(false);
		panelPinPad.add(btnPinPad9);

		JButton btnPinPad0 = new JButton(new ImageIcon("./images/BTN7_0.png"));
		btnPinPad0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad0.setBounds(359, 812, 267, 220);
		btnPinPad0.setOpaque(false);
		btnPinPad0.setContentAreaFilled(false);
		btnPinPad0.setBorderPainted(false);
		panelPinPad.add(btnPinPad0);

		JButton btnPinPadCancel = new JButton(new ImageIcon("./images/BTN7_NO.png"));
		btnPinPadCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadCancel.setBackground(Color.RED);
		btnPinPadCancel.setBounds(50, 812, 259, 220);
		btnPinPadCancel.setOpaque(false);
		btnPinPadCancel.setContentAreaFilled(false);
		btnPinPadCancel.setBorderPainted(false);
		panelPinPad.add(btnPinPadCancel);

		JButton btnPinPadConfirmar = new JButton(new ImageIcon("./images/BTN7_OK.png"));		
		btnPinPadConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadConfirmar.setBackground(Color.GREEN);
		btnPinPadConfirmar.setBounds(664, 812, 272, 220);
		btnPinPadConfirmar.setOpaque(false);
		btnPinPadConfirmar.setContentAreaFilled(false);
		btnPinPadConfirmar.setBorderPainted(false);
		panelPinPad.add(btnPinPadConfirmar);	

	
		

		
		JLabel lblTokenConfirmacion = new JLabel(".");
		lblTokenConfirmacion.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenConfirmacion.setForeground(Color.WHITE);
		lblTokenConfirmacion.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblTokenConfirmacion.setBounds(190, 773, 583, 66);
		panelToken.add(lblTokenConfirmacion);

		JLabel lblToken = new JLabel(".");
		lblToken.setForeground(Color.WHITE);
		lblToken.setHorizontalAlignment(SwingConstants.CENTER);
		lblToken.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblToken.setBounds(190, 594, 583, 66);
		panelToken.add(lblToken);

		JLabel lblTokenMontoRetiro = new JLabel(".");
		lblTokenMontoRetiro.setForeground(Color.WHITE);
		lblTokenMontoRetiro.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMontoRetiro.setFont(new Font("Tahoma", Font.BOLD, 99));
		lblTokenMontoRetiro.setBounds(190, 321, 583, 136);
		panelToken.add(lblTokenMontoRetiro);
		
		panelToken.add(new DebugButtons().getPanel());
		
		JLabel lblTokenMensaje = new JLabel(".");
		lblTokenMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTokenMensaje.setForeground(Color.WHITE);
		lblTokenMensaje.setBounds(190, 93, 583, 75);
		panelToken.add(lblTokenMensaje);


		/** - - - - - - - - - -   P A N E L   L O G I N   - - - - - - - - - -  */
		

		final JLabel lblLoginUser = new JLabel("");
		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(257, 625, 496, 87);
		panelLogin.add(lblLoginUser);
		panelLogin.add(new DebugButtons().getPanel());
		
		final JLabel lblLoginPassword = new JLabel("");
		lblLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginPassword.setForeground(Color.WHITE);
		lblLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginPassword.setBounds(257, 793, 496, 87);
		panelLogin.add(lblLoginPassword);

		panelLogin.add(new DebugButtons().getPanel());
		
		final JLabel lblLoginRow1 = new JLabel("");
		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginRow1.setBounds(89, 58, 837, 70);
		panelLogin.add(lblLoginRow1);

		panelLogin.add(panelPinPad);

		JButton btnOperacion1 = new JButton(new ImageIcon("./images/BTN7Aceptar.png"));
		btnOperacion1.setBounds(547, 757, 782, 159);
		panelDeposito.add(btnOperacion1);

		panelDeposito.add(new DebugButtons().getPanel());
		
		btnOperacion1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				switch(currentOperation) {
				case Deposit:

					//Terminamos el deposito
					DepositOpVO depositOpVO = new DepositOpVO();

					if(isDebug) {						
						montoDepositado = 3720;	
						depositOpVO.atmId = "CI01GL0001"; //"CIXXGS0020";
						depositOpVO.amount = (long) montoDepositado;
						depositOpVO.b20 = 1;
						depositOpVO.b50 = 2;
						depositOpVO.b100 = 3;
						depositOpVO.b200 = 4;
						depositOpVO.b500 = 5;
						depositOpVO.b1000 = 6;
						depositOpVO.operatorId = Integer.parseInt(CurrentUser.loginUser);
						depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
						depositOpVO.userName = CurrentUser.loginUser;							
					}
					else {
						depositOpVO.atmId = "CI01GL0001"; //"CIXXGS0020";
						depositOpVO.amount = (long) montoDepositado;
						depositOpVO.b20 = contadoresDeposito.x20;
						depositOpVO.b50 = contadoresDeposito.x50;
						depositOpVO.b100 = contadoresDeposito.x100;
						depositOpVO.b200 = contadoresDeposito.x200;
						depositOpVO.b500 = contadoresDeposito.x500;
						depositOpVO.b1000 = 0;
						depositOpVO.operatorId = Integer.parseInt(CurrentUser.loginUser);
						depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
						depositOpVO.userName = CurrentUser.loginUser;										
					}

					String billetes = "[" + depositOpVO.b20 + "x20|" + depositOpVO.b50 + "x50|" + depositOpVO.b100 + "x100|" + depositOpVO.b200 + "x200|" + depositOpVO.b500 + "x500|" + depositOpVO.b1000 + "x1000]";
					String billetesNotesValidated = "" + depositOpVO.b20 + "x20;" + depositOpVO.b50 + "x50;" + depositOpVO.b100 + "x100;" + depositOpVO.b200 + "x200;" + depositOpVO.b500 + "x500;" + depositOpVO.b1000 + "x1000";

					Transactions.ConfirmaDeposito(depositOpVO);

					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted,"" + montoDepositado);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, billetesNotesValidated);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInEndOk, "" + montoDepositado);
					RaspiAgent.WriteToJournal("CASH MANAGEMENT", montoDepositado,0, "","", "PROCESADEPOSITO ConfirmaDeposito " + billetes, AccountType.Administrative, TransactionType.CashManagement);

					Ptr.printDeposit(depositOpVO);

					//Terminamos el deposito, mandamos a la pantalla y regresamos a idle.
					cl.show(panelContainer, panelTerminamos, 5000, "panelIdle");				
					
					break;
				case Dispense:
					break;
				default:
					break;
				}
			}
		});
		btnOperacion1.setContentAreaFilled(false);
		btnOperacion1.setBorderPainted(false);
		btnOperacion1.setOpaque(false);
		btnOperacion1.setFont(new Font("Tahoma", Font.BOLD, 40));

		final JLabel lblOperacion2 = new JLabel(".");
		lblOperacion2.setHorizontalAlignment(SwingConstants.CENTER);
		lblOperacion2.setBounds(651, 484, 566, 130);
		panelDeposito.add(lblOperacion2);
		lblOperacion2.setForeground(Color.WHITE);
		lblOperacion2.setFont(new Font("Tahoma", Font.BOLD, 50));


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
		
		btnPinPadConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				switch(CurrentUser.pinpadMode) {
				case loginUser:					
					System.out.println("loginUser");
					if(currentOperation == jcmOperation.Deposit) {
						System.out.println("loginUser deposit");
						//No ha ingresado su user
						if(CurrentUser.loginUser.length() <= 0) {						
							CurrentUser.pinpadMode = PinpadMode.loginUser;						
							return;
						}

						System.out.println("Validando usuario....");
						//Validamos el usuario
						CMUserVO user = Transactions.ValidaUsuario(CurrentUser.loginUser);
						System.out.println("loginUser success[" +  user.success +"] success [" + user.isValid + "]");


						if(user.success && user.isValid) {
							System.out.println("loginUser deposit success y isvalid");
							//Si es deposito ya lo dejamos pasar
							CurrentUser.pinpadMode = PinpadMode.None;
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
							montoDepositado = 0;
							cl.show(panelContainer, "panelDeposito");
							Transactions.BorraCashInOPs("CI01GL0001");								
						}
						else {						
							if(!user.success) {
								System.out.println("loginUser deposit success NO");
								//Si es deposito ya lo dejamos pasar
								CurrentUser.pinpadMode = PinpadMode.None;
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
								montoDepositado = 0;
								cl.show(panelContainer, "panelDeposito");
								Transactions.BorraCashInOPs("CI01GL0001");
							}
							else {	
								if(++CurrentUser.loginAttempts >= 2) {
									//Intentos superados
									CurrentUser.loginUser = "";
									CurrentUser.loginPassword = "";
									CurrentUser.loginAttempts = 0;
									CurrentUser.tokenAttempts = 0;
									cl.show(panelContainer, panelOperacionCancelada,5000,"panelIdle");
								}
								else {
									CurrentUser.loginUser = "";
									CurrentUser.loginPassword = "";
									lblLoginUser.setText("");
									lblLoginPassword.setText("");
									asteriscos = "";
									CurrentUser.pinpadMode = PinpadMode.loginUser;		
									RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
									panelLogin.setBackground("./images/Scr7UsuarioIncorrecto.png");
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
					if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
						CurrentUser.pinpadMode = PinpadMode.loginUser;						
						return;
					}

					System.out.println("Validando usuario....");
					//Validamos el usuario
					CMUserVO user = Transactions.ValidaUsuario(CurrentUser.loginUser);

					System.out.println("loginPassword success[" +  user.success +"] success [" + user.isValid + "]");

					if(user.success && user.isValid) {
						System.out.println("loginPassword success y isvalid");
						switch(currentOperation) {						
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
								cl.show(panelContainer, "panelToken");
							}
							else {
								System.out.println("loginPassword success y isvalid y dispense y NO allowsWithdrawals");
								CurrentUser.loginUser = "";
								CurrentUser.loginPassword = "";
								lblLoginUser.setText("");
								lblLoginPassword.setText("");
								asteriscos = "";
								CurrentUser.pinpadMode = PinpadMode.loginUser;

								if(++CurrentUser.loginAttempts >= 2) {
									cl.show(panelContainer, "panelOperacionCancelada");
									screenTimeout(5000, "panelIdle");
								}
								else {	
									lblLoginRow1.setText("¡Oh no! No tiene permisos para hacer retiros.");
									panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
									RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
								}
							}
							break;
						default:
							System.out.println("Validando usuario.... 4");
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							asteriscos = "";
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
								cl.show(panelContainer, "panelDeposito");
								Transactions.BorraCashInOPs("CI01GL0001");
								break;
							case Dispense:								
								System.out.println("Validando usuario.... 7");
								lblLoginUser.setText("");
								lblLoginPassword.setText("");
								CurrentUser.loginUser = "";
								CurrentUser.loginPassword = "";
								asteriscos = "";
								CurrentUser.pinpadMode = PinpadMode.loginUser;
								CurrentUser.loginAttempts++;
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
								panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
								break;
							default:
								lblLoginUser.setText("");
								lblLoginPassword.setText("");
								CurrentUser.loginUser = "";
								CurrentUser.loginPassword = "";
								asteriscos = "";
								CurrentUser.pinpadMode = PinpadMode.loginUser;								
								panelLogin.setBackground("./images/Scr7DatosIncorrectos.png");
								break;
							}							
						}
						else {

							System.out.println("loginPassword success isValid NO");
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							asteriscos = "";
							if(++CurrentUser.loginAttempts >= 2) {
								cl.show(panelContainer, "panelOperacionCancelada");
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
							cl.show(panelContainer, panelError,10000,"panelIdle");							
						}
						else {

							CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
							cmWithdrawalVo.atmId = "CI01GL0001";
							cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.loginUser);
							cmWithdrawalVo.password = CurrentUser.loginPassword;
							cmWithdrawalVo.reference = CurrentUser.referencia;
							cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
							cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
							cmWithdrawalVo.amount = JcmGlobalData.montoDispensar;


							System.out.println("ConfirmaRetiro");
							if(!Transactions.ConfirmaRetiro(cmWithdrawalVo)) {
								System.out.println("Usuario sin permiso para dispensar!");
								lblPanelError.setText("Lo sentimos, no se pude procesar su petición");
								cl.show(panelContainer, panelError,10000,"panelIdle");
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
											cl.show(panelContainer, "panelTerminamos");

											Ptr.print("SI",new HashMap<String,String>());
											Timer screenTimer2 = new Timer();
											screenTimer2.schedule(new TimerTask() {
												@Override
												public void run() {				                
													//Revisamos si hay retiros listos
													cl.show(panelContainer,"panelIdle");											
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
										cl.show(panelContainer, "panelRetiraBilletes");
										break;
									case Partial:
										lblRetiraBilletesMontoDispensarParcial.setText("$" + JcmGlobalData.montoDispensar);
										cl.show(panelContainer, "panelRetiroParcial");
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
												cl.show(panelContainer, "panelTerminamos");
												RaspiAgent.Broadcast(DeviceEvent.AFD_DispenseOk, "" + JcmGlobalData.montoDispensar);
												RaspiAgent.WriteToJournal("Withdrawal", montoRetiro,0, "","", "Withdrawal DispenseOk", AccountType.Other, TransactionType.Withdrawal);
												CmWithdrawal cmWithdrawalVo = new CmWithdrawal();
												cmWithdrawalVo.atmId = "CI01GL0001";
												cmWithdrawalVo.operatorId = Integer.parseInt(CurrentUser.loginUser);
												cmWithdrawalVo.password = CurrentUser.loginPassword;
												cmWithdrawalVo.reference = CurrentUser.referencia;
												cmWithdrawalVo.token = CurrentUser.tokenConfirmacion;
												cmWithdrawalVo.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();

												System.out.println("ConfirmaRetiro");

												Ptr.printDispense(montoRetiro,CurrentUser.loginUser);
												Timer screenTimer2 = new Timer();
												screenTimer2.schedule(new TimerTask() {
													@Override
													public void run() {
														//Revisamos si hay retiros listos

														cl.show(panelContainer,"panelIdle");

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
							cl.show(panelContainer, panelOperacionCancelada, 5000, "panelIdle");														
						}
						else {						
							panelToken.setBackground("./images/Scr7TokenIncorrecto.png");
						}
					}


					break;
				default:
					break;

				}
				
			}
		});



		btnPinPadCancel.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {

				screenTimerTimeout.cancel();

				CurrentUser.cleanPinpadData();
				lblLoginUser.setText("");
				lblLoginPassword.setText("");
				lblTokenConfirmacion.setText("");
				asteriscos = "";
								
				cl.show(panelContainer,panelOperacionCancelada,5000, "panelIdle");
			}
		});

		btnPinPad1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:					
					break;
				case loginPassword:
					textoMontoValidacion("1",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("1",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("1",lblTokenConfirmacion);
					break;
				default:
					break;				
				}			
			}
		});

		btnPinPad2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("2",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("2",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("2",lblTokenConfirmacion);
					break;
				default:
					break;				
				}	
			}
		});

		btnPinPad3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("3",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("3",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("3",lblTokenConfirmacion);
					break;
				default:
					break;				
				}	
			}
		});

		btnPinPad4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("4",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("4",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("4",lblTokenConfirmacion);
					break;
				default:
					break;				
				}	
			}
		});

		btnPinPad5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("5",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("5",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("5",lblTokenConfirmacion);
					break;
				default:
					break;				
				}	
			}
		});

		btnPinPad6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("6",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("6",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("6",lblTokenConfirmacion);
					break;
				default:
					break;				
				}				}
		});

		btnPinPad7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("7",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("7",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("7",lblTokenConfirmacion);
					break;
				default:
					break;				
				}				}
		});

		btnPinPad8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("8",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("8",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("8",lblTokenConfirmacion);
					break;
				default:
					break;				
				}				}
		});

		btnPinPad9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("9",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("9",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("9",lblTokenConfirmacion);
					break;
				default:
					break;				
				}				}
		});

		btnPinPad0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(CurrentUser.pinpadMode) {
				case None:
					break;
				case loginPassword:
					textoMontoValidacion("0",lblLoginPassword);
					break;
				case loginUser:
					textoMontoValidacion("0",lblLoginUser);
					break;
				case retiroToken:
					textoMontoValidacion("0",lblTokenConfirmacion);
					break;
				default:
					break;				
				}				}
		});

		
		panelMenu.btnMenuDeposito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screenTimerTimeout.cancel();
				panelToken.remove(panelPinPad);
				panelLogin.remove(panelPinPad);
				panelLogin.add(panelPinPad);
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);
				asteriscos = "";
				
				lblLoginUser.setLocation(257, 625);
				lblLoginPassword.setText(CurrentUser.loginPassword);
				lblLoginUser.setText(CurrentUser.loginUser);
				currentOperation = jcmOperation.Deposit;
				panelLogin.setBackground("./images/Scr7IdentificateDeposito.png");
				cl.show(panelContainer, "panelLogin");
				screenTimeout(7000, "panelOperacionCancelada");
			}
		});


		panelMenu.btnMenuRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				screenTimerTimeout.cancel();

				panelToken.remove(panelPinPad);
				panelLogin.remove(panelPinPad);
				panelLogin.add(panelPinPad);				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);			
				asteriscos = "";
				lblLoginUser.setLocation(257, 525);
				lblLoginPassword.setText(CurrentUser.loginPassword);
				lblLoginUser.setText(CurrentUser.loginUser);
				
				currentOperation = jcmOperation.Dispense;

				panelLogin.setBackground("./images/Scr7IngresaDatos.png");
				cl.show(panelContainer, "panelLogin");
				screenTimeout(7000, "panelOperacionCancelada");

				CmMessageRequest request =  CmQueue.queueList.getFirst();

				token = "" + request.token;
				montoRetiro = request.amount;
				lblTokenMontoRetiro.setText("$" + montoRetiro);
				lblToken.setText(token);
				CurrentUser.tokenConfirmacion = "";	
				CurrentUser.referencia = request.reference;
				lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);				
			}
		});


		
		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(CmQueue.queueList.isEmpty()) {
					System.out.println("SIN RETIROS");
					panelMenuHolder.setBackground("./images/Scr7SinRetiroAutorizado.png");
					panelMenu.btnMenuRetiro.setEnabled(false);
				}else {
					System.out.println("CON RETIROS");
					panelMenuHolder.setBackground("./images/Scr7RetiroAutorizado.png");				
					panelMenu.btnMenuRetiro.setEnabled(true);	
				}
				cl.show(panelContainer,panelMenuHolder,5000,"panelIdle");

			}
		});

		
		EventListenerClass c = new EventListenerClass();
		c.addMyEventListener(new MyEventListener() {
			public void myEventOccurred(MyEvent evt) {
				//FIRE
				System.out.println("myEventOccurred [" + evt.getSource() + "]");

				switch(evt.getSource().toString()) {

				case "escrow1":
					RaspiAgent.Broadcast(DeviceEvent.DEP_ItemsInserted, "JCM 1");
					break;
				case "escrow2":
					RaspiAgent.Broadcast(DeviceEvent.DEP_ItemsInserted, "JCM 2");
					break;
				case "bill1":							
					int billType = jcms[0].bill;
					switch(billType)
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

					CashInOpVO myObj = new CashInOpVO();
					myObj.atmId = "CI01GL0001";
					myObj.amount = (long) billType;
					myObj.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					myObj.operatorId = Integer.parseInt(CurrentUser.loginUser);
					myObj.notesDetails = "1x" + billType;

					montoDepositado += billType;					

					Transactions.InsertaCashInOp(myObj);

					RaspiAgent.WriteToJournal("CASH MANAGEMENT", (double)billType,0, "","", "PROCESADEPOSITO PreDeposito", AccountType.Administrative, TransactionType.ControlMessage);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, "1x" + billType);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInReceived, "" + billType);
					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted, "" + montoDepositado);


					System.out.println("$" + montoDepositado);
					lblOperacion2.setText("$" + montoDepositado);
					panelComandos.lblBilleteIngresado1.setText("$" + billType);
					break;
				case "bill2":		
					int billType2 = jcms[1].bill;
					switch(billType2)
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

					CashInOpVO myObj2 = new CashInOpVO();
					myObj2.atmId = "CI01GL0001";
					myObj2.amount = (long) billType2;
					myObj2.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					myObj2.operatorId = Integer.parseInt(CurrentUser.loginUser);
					myObj2.notesDetails = "1x" + billType2;

					Transactions.InsertaCashInOp(myObj2);

					RaspiAgent.WriteToJournal("CASH MANAGEMENT", (double)billType2,0, "","", "PROCESADEPOSITO PreDeposito", AccountType.Administrative, TransactionType.ControlMessage);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, "1x" + billType2);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInReceived, "" + billType2);


					montoDepositado += billType2;					
					System.out.println("$" + montoDepositado);
					lblOperacion2.setText("$" + montoDepositado);
					panelComandos.lblBilleteIngresado2.setText("$" + billType2);
					break;					
				case "clearbill1":
					panelComandos.lblBilleteIngresado1.setText("");
					break;
				case "clearbill2":
					panelComandos.lblBilleteIngresado2.setText("");
					break;		
				case "recyclerBills1":

					panelComandos.lblRecycler1.setText(jcms[0].recyclerDenom1 + " " + jcms[0].recyclerDenom2);

					recyclerBills1Set = true;					
					if(recyclerBills2Set) {

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;

						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills1Set = false;
					}
					break;
				case "recyclerBills2":

					panelComandos.lblRecycler2.setText(jcms[1].recyclerDenom1 + " " + jcms[1].recyclerDenom2);

					recyclerBills2Set = true;
					if(recyclerBills1Set) {						

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;


						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills2Set = false;
					}

					break;
				case "recyclerContadores1":

					panelComandos.lblContadores1.setText(jcms[0].recyclerContadores);
					break;
				case "recyclerContadores2":

					panelComandos.lblContadores2.setText(jcms[1].recyclerContadores);
					break;		
				case "dispensedCass11":					
					RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (jcms[0].cuantos2 * jcms[0].contadores.Cass2Denom));
					RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + jcms[0].cuantos2  + "x" +  jcms[0].contadores.Cass2Denom);
					jcm1cass1Dispensed = true;
					break;
				case "dispensedCass21":
					RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (jcms[1].cuantos2 * jcms[1].contadores.Cass2Denom));
					RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + jcms[1].cuantos2  + "x" +  jcms[1].contadores.Cass2Denom);
					jcm1cass2Dispensed = true;					
					break;
				case "dispensedCass12":
					jcm2cass1Dispensed = true;
					break;
				case "dispensedCass22":
					jcm2cass2Dispensed = true;
					break;					
				case "presentOk1":
					RaspiAgent.Broadcast(DeviceEvent.AFD_PresentOk, "JCM[1]");
					break;
				case "presentOk2":
					RaspiAgent.Broadcast(DeviceEvent.AFD_PresentOk, "JCM[2]");
					break;
				case "mediaTaken1":
					RaspiAgent.Broadcast(DeviceEvent.AFD_MediaTaken, "JCM[1]");
					break;
				case "mediaTaken2":
					RaspiAgent.Broadcast(DeviceEvent.AFD_MediaTaken, "JCM[2]");
					break;
				case "widthdrawalRequest":
					System.out.println("Hay mensaje de retiro papawh");
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
				contador++;
			}			
		}

		if(contador == 0 && isDebug) {
			jcms[0] = new uart(1);
			jcms[0].portId = null;
			jcms[0].baud = 9600;
			jcms[0].id = 1;
			
			jcms[1] = new uart(2);
			jcms[1].portId = null;
			jcms[1].baud = 9600;
			jcms[1].id = 2;
		}
		else {
			//Inicializamos los UARTS
			for(int i = 0; i < contador; i++) {
				jcms[i].currentOpertion = jcmOperation.Startup;
				jcms[i].openPort(jcms[i].portId.getName().toString());
			}
		}


		cl.show(panelContainer, "panelIdle");	
	}


	public static void redirect(String panel) {
		cl.show(panelContainer, panel);		
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

		screenTimerTimeout.cancel();

		switch(CurrentUser.pinpadMode) {
		case None:
			break;
		case loginUser:
			if (CurrentUser.loginUser.length() > 7)				
				return;
			CurrentUser.loginUser += digito;
			detLabel.setText(CurrentUser.loginUser);			
			break;
		case loginPassword:
			if (CurrentUser.loginPassword.length() > 7)
				return;
			CurrentUser.loginPassword += digito;
			asteriscos += "*";
			detLabel.setText(asteriscos);
			break;		
		case retiroToken:
			if (CurrentUser.tokenConfirmacion.length() > 16)
				return;
			CurrentUser.tokenConfirmacion += digito;			
			detLabel.setText(CurrentUser.tokenConfirmacion);
			break;
		default:
			break;				
		}		

	}



	boolean validateDispense() {

		if(isDebug) {
			dispenseStatus = DispenseStatus.Complete;
			return true;
		}

		if(montoRetiro < 20) {
			dispenseStatus = DispenseStatus.NotDispensable;
			System.out.println("De origen no se puede dispensar [menor a 20]");
			return false;
		}        

		double sobrante = 0;
		if(montoRetiro < 40) {
			sobrante = montoRetiro - 20;
		}
		else {
			sobrante = montoRetiro % 10;
		}

		System.out.println("sobrante [" + sobrante + "]");

		if(sobrante > 0)
			dispenseStatus = DispenseStatus.Partial;


		JcmGlobalData.montoDispensar = montoRetiro - sobrante;

		//Checamos los contadores actuales        
		actualizaContadoresRecicladores();       

		double disponible = JcmGlobalData.totalCashInRecyclers1 + JcmGlobalData.totalCashInRecyclers2;
		System.out.println("Disponible para dispensar [" + disponible + "]");

		//No hay dinero para dispensar
		if(disponible == 0) {
			dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}

		//Checamos que tenga algo de dinero.
		if(jcms[0].contadores.Cass1Available == 0 && jcms[0].contadores.Cass2Available == 0 && jcms[1].contadores.Cass1Available == 0 && jcms[1].contadores.Cass2Available == 0){
			System.out.println("No hay dinero en los caseteros para dispensar");
			dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}


		//Si es mas de lo que tenemos dispensamos todo lo que tenemos como parcial.
		if(montoRetiro > disponible) {			
			JcmGlobalData.montoDispensar = disponible;
			System.out.println("Retiro parcial mas dinero del que hay");
			dispenseStatus = DispenseStatus.Partial;
			jcms[0].jcmCass1 = jcms[0].contadores.Cass1Available;
			jcms[0].jcmCass2 = jcms[0].contadores.Cass2Available;
			jcms[1].jcmCass1 = jcms[1].contadores.Cass1Available;
			jcms[1].jcmCass2 = jcms[1].contadores.Cass2Available;
			return true;			
		}		


		System.out.println("Solicitado [" + montoRetiro + "] disponible [" + disponible + "] sobrante [" + (montoRetiro - disponible) + "]");


		int iBuffer = 0;

		if(JcmGlobalData.availableBillsForRecycling.containsKey(jcms[0].contadores.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(jcms[0].contadores.Cass1Available);
			JcmGlobalData.availableBillsForRecycling.put(jcms[0].contadores.Cass1Denom, jcms[0].contadores.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(jcms[0].contadores.Cass1Denom, jcms[0].contadores.Cass1Available);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(jcms[0].contadores.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(jcms[0].contadores.Cass2Available);
			JcmGlobalData.availableBillsForRecycling.put(jcms[0].contadores.Cass2Denom, jcms[0].contadores.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(jcms[0].contadores.Cass2Denom, jcms[0].contadores.Cass2Available + iBuffer);


		if(JcmGlobalData.availableBillsForRecycling.containsKey(jcms[1].contadores.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(jcms[1].contadores.Cass1Available);
			JcmGlobalData.availableBillsForRecycling.put(jcms[1].contadores.Cass1Denom, jcms[1].contadores.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(jcms[1].contadores.Cass1Denom, jcms[1].contadores.Cass1Available + iBuffer);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(jcms[1].contadores.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(jcms[1].contadores.Cass2Available);
			JcmGlobalData.availableBillsForRecycling.put(jcms[1].contadores.Cass2Denom, jcms[1].contadores.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(jcms[1].contadores.Cass2Denom, jcms[1].contadores.Cass2Available + iBuffer);


		if(Afd.denominateInfo(JcmGlobalData.montoDispensar)) {
			//TODO: Ahorita TODOS los cassettes deben ser diferentes...
			//Se puede dispensar

			//revisamos si hay cambio o no.
			if(JcmGlobalData.dispenseChange > 0){				//Dispensado parcial				
				System.out.println("HAY CAMBIO [" +JcmGlobalData.dispenseChange + "]" );
				dispenseStatus = DispenseStatus.Partial;
			}
			else
				dispenseStatus = DispenseStatus.Complete;

			//Seteamos los valores para cada casetero			
			jcms[0].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(jcms[0].contadores.Cass1Denom, 0);
			jcms[0].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(jcms[0].contadores.Cass2Denom, 0);
			jcms[1].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(jcms[1].contadores.Cass1Denom, 0);
			jcms[1].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(jcms[1].contadores.Cass2Denom, 0);

		}
		else {			
			dispenseStatus = DispenseStatus.NotDispensable;
			return false;
		}

		System.out.println("jcm1cass1 [" + jcms[0].jcmCass1 + "] jcm1cass2 [" + jcms[0].jcmCass2 + "] jcm2cass1 [" + jcms[1].jcmCass1 + "]jcm2cass2 [" + jcms[1].jcmCass2 + "]" );

		return true;

	}


	private void actualizaContadoresRecicladores() {
		jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,jcms[0].jcmMessage);

		jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,jcms[1].jcmMessage);


		boolean enEspera = true;

		while(enEspera) {

			if(jcms[0].recyclerContadoresSet && jcms[1].recyclerContadoresSet){
				enEspera = false;
			}
			else {
				Timer screenTimer = new Timer();					
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {				                
						screenTimer.cancel();
					}
				}, 500);
			}
		}	
	}


	public void screenTimeout(int milliseconds, String targetPanel) {
		screenTimerTimeout = new Timer();
		screenTimerTimeout.schedule(new TimerTask() {
			@Override
			public void run() {
				cl.show(panelContainer,targetPanel);
				screenTimerTimeout.cancel();
			}
		}, milliseconds);
	}


	private void dispense() {
		System.out.println("Retirar jcms[0].jcmCass1 [" + jcms[0].jcmCass1 + "] jcms[0].jcmCass2 [" + jcms[0].jcmCass2 + "] jcms[1].jcmCass1 [" + jcms[1].jcmCass1 + "] jcms[1].jcmCass2 [" + jcms[1].jcmCass2 + "]" );

		// Iniciamos el dispensado
		jcm1cass1Dispensed = false;
		jcm1cass2Dispensed = false;
		jcm2cass1Dispensed = false;
		jcm2cass2Dispensed = false;

		//Checamos para JCM1
		if(jcms[0].jcmCass1 > 0 || jcms[0].jcmCass2 > 0) {				
			//El primer denominate se genera aqui:
			RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (jcms[0].jcmCass1 * jcms[0].contadores.Cass1Denom));
			RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + jcms[0].jcmCass1  + "x" +  jcms[0].contadores.Cass1Denom);
			System.out.println("Deshabilitamos JCM1 para dispense");
			jcms[0].currentOpertion = jcmOperation.Dispense;

			// primero el inhibit
			jcms[0].jcmMessage[3] = 0x01;
			jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
		}
		else {
			jcm1cass1Dispensed = true;
			jcm1cass2Dispensed = true;
		}


		Timer screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//System.out.println("Esperando que termine dispense 1 ");
				if(jcm1cass1Dispensed && jcm1cass2Dispensed) {
					System.out.println("Dispense 1 terminado");
					if(jcms[1].jcmCass1 > 0 || jcms[1].jcmCass2 > 0) {

						//El primer denominate se genera aqui:
						RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (jcms[1].jcmCass1 * jcms[1].contadores.Cass1Denom));
						RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + jcms[1].jcmCass1  + "x" +  jcms[1].contadores.Cass1Denom);

						System.out.println("Deshabilitamos JCM2 para dispense");
						jcms[1].currentOpertion = jcmOperation.Dispense;

						// primero el inhibit
						jcms[1].jcmMessage[3] = 0x01;
						jcms[1].id003_format((byte) 0x6, (byte) 0xC3, jcms[1].jcmMessage, false);
					}
					else{						
						System.out.println("Nada que dispensar del JCM[2]");
						jcm2cass1Dispensed = true;
						jcm2cass2Dispensed = true;
					}
					screenTimerDispense.cancel();
				}            	
			}
		}, 1000,1000);
	}

	class DenominacionSorter implements Comparator<JcmCassette> 
	{
		@Override
		public int compare(JcmCassette o1, JcmCassette o2) {
			return o2.Denomination.compareTo(o1.Denomination);
		}
	}
}


