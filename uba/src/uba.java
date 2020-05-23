import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.io.CommPortIdentifier;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.PinpadMode;
import pentomino.common.Ptr;
import pentomino.common.Tio;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.gui.ImagePanel;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.RaspiAgent;
import javax.swing.JTextField;

public class uba {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(uba.class.getName());

	private static boolean isDebug = false;

	private static PinpadMode pinpadMode = PinpadMode.None;
	private static jcmOperation currentOperation = jcmOperation.None;

	private static JcmContadores contadoresDeposito = new JcmContadores();	

	private static int montoRetiro = 0;
	public static int montoDepositado = 0;
	private static String token;

	private static String asteriscos = "";

	private JFrame mainFrame;

	public static boolean recyclerContadores1 = false;
	public static boolean recyclerContadores2 = false;

	private static boolean recyclerBills1Set = false;
	private static boolean recyclerBills2Set = false;


	/* Variables de control apra saber queya dispensaron todos los caseteros*/
	private boolean jcm1cass1Dispensed = false;
	private boolean jcm1cass2Dispensed = false;
	private boolean jcm2cass1Dispensed = false;
	private boolean jcm2cass2Dispensed = false;


	uart[] jcms = new uart[2];
	int contador = 0;

	//*  //TODO: TIO
	final Tio miTio = new Tio();
	//*/

	final AgentsQueue agentsQueue = new AgentsQueue();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		logger.info("UBA MAIN");
		//System.setProperty("log4j.configurationFile", "/resources/log4j2.properties");

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

		isDebug = System.getProperty("os.name").toLowerCase().contains("windows");
		System.out.println(System.getProperty("os.name") + " isDebug[" + isDebug + "]");

		System.out.println("[LOG4J ----------------------------]");
		logger.debug("this is an DEBUG message");
		logger.info("this is an INFO message");
		logger.warn("this is an WARN message");
		logger.error("this is an ERROR message");
		logger.fatal("this is an FATAL message");
		System.out.println("[LOG4J ----------------------------]");

		JcmMonitor t2 = new JcmMonitor();
		t2.start();

		Thread agentsQueueThread = new Thread(agentsQueue, "agentsQueueThread");
		agentsQueueThread.start();

		//HEWEY QUITAR currentOperation = jcmOperation.Startup;

		initialize();

		//* TODO: TIO	
		if(!isDebug) {
			Thread tioThread = new Thread(miTio, "Tio Thread");
			tioThread.start();	
		}
		//*/	

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {


		JcmGlobalData.maxRecyclableChash = Integer.parseInt(Config.GetDirective("maxRecyclableChash","1500"));


		JPanel panelCont = new JPanel();
		panelCont.setAlignmentY(Component.TOP_ALIGNMENT);
		panelCont.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelCont.setBackground(Color.LIGHT_GRAY);
		panelCont.setBounds(0, 0, 1920, 1080);

		mainFrame = new JFrame("Frame Principal");
		mainFrame.getContentPane().setBackground(Color.DARK_GRAY);
		mainFrame.setBounds(100, 100, 1920, 1084);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(panelCont);
		//mainFrame.setUndecorated(true);  //Con esto ya no tiene frame de ventanita


		ImagePanel panelIdle = new ImagePanel(new ImageIcon("./images/ScrIdle.png").getImage(),"panelIdle");
		ImagePanel panelInicio = new ImagePanel(new ImageIcon("./images/ScrPrincipal.png").getImage(),"panelInicio");
		ImagePanel panelDeposito = new ImagePanel(new ImageIcon("./images/ScrPlaceholder.png").getImage(),"panelDeposito");
		ImagePanel panelComandos = new ImagePanel(new ImageIcon("./images/ScrPlaceholder.png").getImage(),"panelComandos");
		ImagePanel panelLogin = new ImagePanel(new ImageIcon("./images/loginFormClean.png").getImage(),"panelLogin");
		ImagePanel panelToken = new ImagePanel(new ImageIcon("./images/panelToken.png").getImage(),"panelToken");
		ImagePanel panelTerminamos = new ImagePanel(new ImageIcon("./images/ScrTerminamos.png").getImage(),"panelTerminamos");
		ImagePanel panelRetiraBilletes = new ImagePanel(new ImageIcon("./images/ScrRetiraBilletes.png").getImage(),"panelRetiraBilletes");

		panelIdle.setLayout(null);
		panelInicio.setLayout(null);
		panelDeposito.setLayout(null);
		panelComandos.setLayout(null);
		panelLogin.setLayout(null);
		panelToken.setLayout(null);
		panelTerminamos.setLayout(null);
		panelRetiraBilletes.setLayout(null);


		CardLayout cl = new CardLayout();
		panelCont.setLayout(cl);

		panelCont.add(panelIdle,"panelIdle");
		panelCont.add(panelInicio,"panelInicio");
		panelCont.add(panelDeposito,"panelDeposito");
		panelCont.add(panelComandos, "panelComandos");
		panelCont.add(panelLogin, "panelLogin");
		panelCont.add(panelToken,"panelToken");
		panelCont.add(panelTerminamos,"panelTerminamos");
		panelCont.add(panelRetiraBilletes,"panelRetiraBilletes");


		/*** PANEL IDLE  ***/
		JButton btnIdle = new JButton("");
		btnIdle.setBounds(0, 0, 1920, 1080);		
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnIdle.setOpaque(false);
		btnIdle.setContentAreaFilled(false);
		btnIdle.setBorderPainted(false);
		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont,"panelInicio");
			}
		});
		panelIdle.add(btnIdle);



		JButton btnRetiraBilletesComandos = new JButton("COMANDOS");
		btnRetiraBilletesComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont,"panelComandos");
			}
		});
		btnRetiraBilletesComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnRetiraBilletesComandos.setBackground(Color.ORANGE);
		btnRetiraBilletesComandos.setBounds(9, 8, 200, 73);
		panelRetiraBilletes.add(btnRetiraBilletesComandos);

		JButton btnRetiraBilletesSalir = new JButton("SALIR");
		btnRetiraBilletesSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnRetiraBilletesSalir.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnRetiraBilletesSalir.setBackground(Color.RED);
		btnRetiraBilletesSalir.setBounds(219, 8, 200, 73);
		panelRetiraBilletes.add(btnRetiraBilletesSalir);

		final JLabel lblRetiraBilletesMonto = new JLabel("New label");
		lblRetiraBilletesMonto.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblRetiraBilletesMonto.setForeground(Color.WHITE);
		lblRetiraBilletesMonto.setBounds(29, 131, 379, 100);
		panelRetiraBilletes.add(lblRetiraBilletesMonto);

		JLabel lblRetiraBilletesDispensado = new JLabel("New label");
		lblRetiraBilletesDispensado.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblRetiraBilletesDispensado.setForeground(Color.WHITE);
		lblRetiraBilletesDispensado.setBounds(29, 264, 379, 100);
		panelRetiraBilletes.add(lblRetiraBilletesDispensado);

		JLabel lblRetiraBilletesFaltante = new JLabel("New label");
		lblRetiraBilletesFaltante.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblRetiraBilletesFaltante.setForeground(Color.WHITE);
		lblRetiraBilletesFaltante.setBounds(29, 391, 379, 100);
		panelRetiraBilletes.add(lblRetiraBilletesFaltante);

		/***
		 * - - - - - - - - - -   P A N E L   P I N P A D   - - - - - - - - - - 
		 */

		JPanel panelPinPad = new JPanel();
		panelPinPad.setOpaque(false);
		panelPinPad.setBackground(Color.GRAY);
		panelPinPad.setBounds(936, 0, 946, 1080);
		panelPinPad.setBorder(null);
		panelPinPad.setLayout(null);

		JButton btnPinPad1 = new JButton();
		btnPinPad1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad1.setBounds(50, 47, 260, 220);
		btnPinPad1.setOpaque(false);
		btnPinPad1.setContentAreaFilled(false);
		btnPinPad1.setBorderPainted(false);
		panelPinPad.add(btnPinPad1);

		JButton btnPinPad2 = new JButton();
		btnPinPad2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad2.setBounds(359, 47, 262, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad2.setContentAreaFilled(false);
		btnPinPad2.setBorderPainted(false);
		panelPinPad.add(btnPinPad2);

		JButton btnPinPad3 = new JButton();
		btnPinPad3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad3.setBounds(674, 47, 262, 220);
		btnPinPad3.setOpaque(false);
		btnPinPad3.setContentAreaFilled(false);
		btnPinPad3.setBorderPainted(false);
		panelPinPad.add(btnPinPad3);

		JButton btnPinPad4 = new JButton();
		btnPinPad4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad4.setBounds(50, 302, 259, 220);
		btnPinPad4.setOpaque(false);
		btnPinPad4.setContentAreaFilled(false);
		btnPinPad4.setBorderPainted(false);
		panelPinPad.add(btnPinPad4);

		JButton btnPinPad5 = new JButton();
		btnPinPad5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad5.setBounds(359, 302, 262, 220);
		btnPinPad5.setOpaque(false);
		btnPinPad5.setContentAreaFilled(false);
		btnPinPad5.setBorderPainted(false);
		panelPinPad.add(btnPinPad5);

		JButton btnPinPad6 = new JButton();
		btnPinPad6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad6.setBounds(674, 302, 262, 220);
		btnPinPad6.setOpaque(false);
		btnPinPad6.setContentAreaFilled(false);
		btnPinPad6.setBorderPainted(false);
		panelPinPad.add(btnPinPad6);

		JButton btnPinPad7 = new JButton();
		btnPinPad7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad7.setBounds(50, 557, 259, 220);
		btnPinPad7.setOpaque(false);
		btnPinPad7.setContentAreaFilled(false);
		btnPinPad7.setBorderPainted(false);
		panelPinPad.add(btnPinPad7);

		JButton btnPinPad8 = new JButton();
		btnPinPad8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad8.setBounds(359, 557, 267, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad8.setContentAreaFilled(false);
		btnPinPad8.setBorderPainted(false);
		panelPinPad.add(btnPinPad8);

		JButton btnPinPad9 = new JButton();
		btnPinPad9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad9.setBounds(664, 557, 272, 220);
		btnPinPad9.setOpaque(false);
		btnPinPad9.setContentAreaFilled(false);
		btnPinPad9.setBorderPainted(false);
		panelPinPad.add(btnPinPad9);

		JButton btnPinPad0 = new JButton();
		btnPinPad0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad0.setBounds(359, 812, 267, 220);
		btnPinPad0.setOpaque(false);
		btnPinPad0.setContentAreaFilled(false);
		btnPinPad0.setBorderPainted(false);
		panelPinPad.add(btnPinPad0);

		JButton btnPinPadCancel = new JButton();
		btnPinPadCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadCancel.setBackground(Color.RED);
		btnPinPadCancel.setBounds(50, 812, 259, 220);
		btnPinPadCancel.setOpaque(false);
		btnPinPadCancel.setContentAreaFilled(false);
		btnPinPadCancel.setBorderPainted(false);
		panelPinPad.add(btnPinPadCancel);

		JButton btnPinPadConfirmar = new JButton();		
		btnPinPadConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadConfirmar.setBackground(Color.GREEN);
		btnPinPadConfirmar.setBounds(664, 812, 272, 220);
		btnPinPadConfirmar.setOpaque(false);
		btnPinPadConfirmar.setContentAreaFilled(false);
		btnPinPadConfirmar.setBorderPainted(false);
		panelPinPad.add(btnPinPadConfirmar);


		/** - - - - - - - - - -   P A N E L   I D L E   - - - - - - - - - -  */

		JButton btnIdleDeposito = new JButton("");
		btnIdleDeposito.setOpaque(false);
		btnIdleDeposito.setBounds(361, 415, 576, 585);
		btnIdleDeposito.setOpaque(false);
		btnIdleDeposito.setContentAreaFilled(false);
		btnIdleDeposito.setBorderPainted(false);
		panelInicio.add(btnIdleDeposito);

		final JLabel lblLoginUser = new JLabel("");
		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(257, 545, 496, 87);
		panelLogin.add(lblLoginUser);

		JButton btnIdleComandos = new JButton("COMANDOS");
		btnIdleComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnIdleComandos.setBackground(Color.ORANGE);
		btnIdleComandos.setBounds(10, 11, 200, 73);
		btnIdleComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont,"panelComandos");
			}
		});
		panelInicio.add(btnIdleComandos);

		JButton btnIdleSalir = new JButton("SALIR");
		btnIdleSalir.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnIdleSalir.setBackground(Color.RED);
		btnIdleSalir.setBounds(220, 11, 200, 73);
		btnIdleSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panelInicio.add(btnIdleSalir);

		JButton btnIdleRetiro = new JButton("");		
		btnIdleRetiro.setOpaque(false);
		btnIdleRetiro.setContentAreaFilled(false);
		btnIdleRetiro.setBorderPainted(false);
		btnIdleRetiro.setBounds(989, 415, 576, 585);
		panelInicio.add(btnIdleRetiro);

		JButton btnTokenComandos = new JButton("COMANDOS");
		btnTokenComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnTokenComandos.setBackground(Color.ORANGE);
		btnTokenComandos.setBounds(10, 11, 200, 73);
		btnTokenComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont,"panelComandos");
			}
		});
		panelToken.add(btnTokenComandos);

		JLabel lblTokenConfirmacion = new JLabel(".");
		lblTokenConfirmacion.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenConfirmacion.setForeground(Color.WHITE);
		lblTokenConfirmacion.setFont(new Font("Tahoma", Font.BOLD, 50));
		lblTokenConfirmacion.setBounds(190, 861, 583, 66);
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

		JButton btnTokenSalir = new JButton("SALIR");		
		btnTokenSalir.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnTokenSalir.setBackground(Color.RED);
		btnTokenSalir.setBounds(220, 11, 200, 73);
		btnTokenSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panelToken.add(btnTokenSalir);

		JLabel lblTokenMensaje = new JLabel(".");
		lblTokenMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokenMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTokenMensaje.setForeground(Color.WHITE);
		lblTokenMensaje.setBounds(190, 93, 583, 75);
		panelToken.add(lblTokenMensaje);


		/** - - - - - - - - - -   P A N E L   L O G I N   - - - - - - - - - -  */



		JButton btnLoginSalir = new JButton("SALIR");
		btnLoginSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnLoginSalir.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnLoginSalir.setBackground(Color.RED);
		btnLoginSalir.setBounds(220, 11, 200, 73);
		panelLogin.add(btnLoginSalir);

		JLabel lblLoginMensaje = new JLabel("Para depositar, identif\u00EDcate");
		lblLoginMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginMensaje.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblLoginMensaje.setForeground(Color.WHITE);
		lblLoginMensaje.setBounds(89, 139, 837, 70);
		panelLogin.add(lblLoginMensaje);


		final JLabel lblLoginPassword = new JLabel("");
		lblLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginPassword.setForeground(Color.WHITE);
		lblLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginPassword.setBounds(257, 793, 496, 87);
		panelLogin.add(lblLoginPassword);

		JButton btnLoginComandos = new JButton("COMANDOS");
		btnLoginComandos.setBounds(10, 11, 200, 73);		
		btnLoginComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont, "panelComandos");
			}
		});
		btnLoginComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnLoginComandos.setBackground(Color.ORANGE);
		panelLogin.add(btnLoginComandos);

		final JLabel lblLoginRow1 = new JLabel("");
		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginRow1.setBounds(89, 58, 837, 70);
		panelLogin.add(lblLoginRow1);

		panelLogin.add(panelPinPad);



		JPanel panel_estatus = new JPanel();
		panel_estatus.setBounds(10, 11, 1060, 247);
		panelDeposito.add(panel_estatus);
		panel_estatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_estatus.setLayout(null);
		panel_estatus.setOpaque(false);

		JPanel panelJCM2 = new JPanel();
		panelJCM2.setOpaque(false);
		panelJCM2.setBounds(550, 11, 500, 225);
		panel_estatus.add(panelJCM2);
		panelJCM2.setLayout(null);
		panelJCM2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lblRecycler2 = new JLabel(".");
		lblRecycler2.setForeground(Color.WHITE);
		lblRecycler2.setBounds(10, 102, 210, 53);
		panelJCM2.add(lblRecycler2);
		lblRecycler2.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador2 = new JLabel("Reciclador 2");
		lblTitleReciclador2.setForeground(Color.WHITE);
		lblTitleReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTitleReciclador2.setBounds(10, 11, 144, 28);
		panelJCM2.add(lblTitleReciclador2);

		JLabel lblBilleteIngresado2 = new JLabel("$0");
		lblBilleteIngresado2.setForeground(Color.WHITE);
		lblBilleteIngresado2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado2.setBounds(164, 166, 226, 46);
		panelJCM2.add(lblBilleteIngresado2);

		JLabel lblTxtBill2 = new JLabel("Billete:");
		lblTxtBill2.setForeground(Color.WHITE);
		lblTxtBill2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill2.setBounds(10, 166, 144, 46);
		panelJCM2.add(lblTxtBill2);

		JLabel lblContadores2 = new JLabel("rec1/0  rec2/0");
		lblContadores2.setHorizontalAlignment(SwingConstants.TRAILING);
		lblContadores2.setForeground(Color.WHITE);
		lblContadores2.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblContadores2.setBounds(164, 110, 326, 37);
		panelJCM2.add(lblContadores2);

		JButton btnReiniciarJcm2 = new JButton("REINICIAR");		
		btnReiniciarJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm2.setBounds(281, 11, 209, 80);
		panelJCM2.add(btnReiniciarJcm2);

		JPanel panelJCM1 = new JPanel();
		panelJCM1.setBounds(10, 11, 500, 225);
		panel_estatus.add(panelJCM1);
		panelJCM1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM1.setLayout(null);
		panelJCM1.setOpaque(false);

		JLabel lblRecycler1 = new JLabel(".");
		lblRecycler1.setForeground(Color.WHITE);
		lblRecycler1.setBounds(10, 102, 180, 53);
		panelJCM1.add(lblRecycler1);
		lblRecycler1.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador1 = new JLabel("Reciclador 1");
		lblTitleReciclador1.setForeground(Color.WHITE);
		lblTitleReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTitleReciclador1.setBounds(10, 11, 161, 28);
		panelJCM1.add(lblTitleReciclador1);

		JLabel lblTxtBill1 = new JLabel("Billete:");
		lblTxtBill1.setForeground(Color.WHITE);
		lblTxtBill1.setBounds(10, 166, 144, 46);
		panelJCM1.add(lblTxtBill1);
		lblTxtBill1.setFont(new Font("Tahoma", Font.BOLD, 30));

		final JLabel lblBilleteIngresado1 = new JLabel("$0");
		lblBilleteIngresado1.setForeground(Color.WHITE);
		lblBilleteIngresado1.setBounds(164, 166, 226, 46);
		panelJCM1.add(lblBilleteIngresado1);
		lblBilleteIngresado1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JLabel lblContadores1 = new JLabel("rec1/0  rec2/0");
		lblContadores1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblContadores1.setForeground(Color.WHITE);
		lblContadores1.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblContadores1.setBounds(164, 102, 326, 49);
		panelJCM1.add(lblContadores1);

		JButton btnReiniciarJcm1 = new JButton("REINICIAR");
		btnReiniciarJcm1.setBounds(281, 11, 209, 80);
		panelJCM1.add(btnReiniciarJcm1);

		btnReiniciarJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JPanel panelRetiro = new JPanel();
		panelRetiro.setOpaque(false);
		panelRetiro.setBounds(10, 269, 1060, 709);
		panelDeposito.add(panelRetiro);
		panelRetiro.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelRetiro.setLayout(null);


		JPanel panelMensajes = new JPanel();
		panelMensajes.setBounds(10, 11, 1040, 63);
		panelRetiro.add(panelMensajes);
		panelMensajes.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelMensajes.setLayout(null);

		final JLabel lblMensajes = new JLabel(".");
		lblMensajes.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblMensajes.setBounds(10, 11, 1020, 41);
		panelMensajes.add(lblMensajes);

		JPanel panelInfoOperacion = new JPanel();
		panelInfoOperacion.setForeground(Color.WHITE);
		panelInfoOperacion.setBounds(10, 85, 830, 613);
		panelRetiro.add(panelInfoOperacion);
		panelInfoOperacion.setLayout(null);
		panelInfoOperacion.setOpaque(false);

		final JLabel lblOperacion1 = new JLabel("Depositado:");
		lblOperacion1.setForeground(Color.WHITE);
		lblOperacion1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblOperacion1.setBounds(29, 103, 326, 74);
		panelInfoOperacion.add(lblOperacion1);

		final JLabel lblOperacion2 = new JLabel(".");
		lblOperacion2.setForeground(Color.WHITE);
		lblOperacion2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblOperacion2.setBounds(370, 103, 450, 74);
		panelInfoOperacion.add(lblOperacion2);

		JButton btnOperacion1 = new JButton("Terminar");

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
					cl.show(panelCont, "panelTerminamos");
					Timer screenTimer = new Timer();
					screenTimer.schedule(new TimerTask() {
						@Override
						public void run() {				                
							cl.show(panelCont, "panelInicio");
							screenTimer.cancel();
						}
					}, 3000);
					
					break;
				case Dispense:
					break;
				default:
					break;
				}				

			}
		});
		btnOperacion1.setBackground(Color.GREEN);
		btnOperacion1.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnOperacion1.setBounds(420, 382, 400, 220);
		panelInfoOperacion.add(btnOperacion1);

		JLabel lblNewLabel_3 = new JLabel("Ingresa los billetes uno por uno en los aceptadores.");
		lblNewLabel_3.setForeground(Color.WHITE);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblNewLabel_3.setBounds(10, 11, 810, 65);
		panelInfoOperacion.add(lblNewLabel_3);

		JButton btnDepositoComandos = new JButton("COMANDOS");
		btnDepositoComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont, "panelComandos");
			}
		});
		btnDepositoComandos.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnDepositoComandos.setBackground(Color.ORANGE);
		btnDepositoComandos.setBounds(1569, 11, 321, 136);
		panelDeposito.add(btnDepositoComandos);

		JButton btnDepositoIdle = new JButton("IDLE");
		btnDepositoIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont, "panelInicio");
			}
		});
		btnDepositoIdle.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnDepositoIdle.setBackground(Color.GREEN);
		btnDepositoIdle.setBounds(1220, 11, 321, 136);
		panelDeposito.add(btnDepositoIdle);
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



		JPanel panel_comandos = new JPanel();
		panel_comandos.setBounds(10, 113, 1886, 706);
		panelComandos.add(panel_comandos);
		panel_comandos.setLayout(null);



		JCheckBox chckbxReciclador1 = new JCheckBox("Reciclador 1");
		chckbxReciclador1.setSelected(true);

		chckbxReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador1.setBounds(542, 26, 148, 51);
		panelComandos.add(chckbxReciclador1);

		JCheckBox chckbxReciclador2 = new JCheckBox("Reciclador 2");		
		chckbxReciclador2.setSelected(true);
		chckbxReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador2.setBounds(718, 26, 148, 51);
		panelComandos.add(chckbxReciclador2);


		JButton btnStatusReq = new JButton("Stat Req (11h)");
		btnStatusReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusReq.setBackground(Color.GREEN);
		btnStatusReq.setBounds(10, 11, 157, 50);
		btnStatusReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) 
					jcms[0].id003_format((byte) 5, (byte) 0x11, jcms[0].jcmMessage, true);
				if(chckbxReciclador2.isSelected()) 
					jcms[1].id003_format((byte) 5, (byte) 0x11, jcms[1].jcmMessage, true);
			}
		});

		panel_comandos.add(btnStatusReq);

		JButton btnReset = new JButton("Reset (40h)");
		btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReset.setBackground(Color.GREEN);
		btnReset.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				
				if(chckbxReciclador1.isSelected()) {
					jcms[0].currentOpertion = jcmOperation.Reset; 
					//Primero se piden los estatus
					jcms[0].id003_format((byte)5, protocol.SSR_VERSION, jcms[0].jcmMessage,true); //SSR_VERSION 0x88
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].currentOpertion = jcmOperation.Reset; 
					//Primero se piden los estatus
					jcms[1].id003_format((byte)5, protocol.SSR_VERSION, jcms[1].jcmMessage,true); //SSR_VERSION 0x88
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
					jcms[0].id003_format((byte) 5, (byte) 0x50, jcms[0].jcmMessage, true);
				if(chckbxReciclador2.isSelected()) 
					jcms[1].id003_format((byte) 5, (byte) 0x50, jcms[1].jcmMessage, true);
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

		JLabel lblComandosSettingCommands = new JLabel("Setting Commands +Data");
		lblComandosSettingCommands.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblComandosSettingCommands.setBounds(10, 69, 218, 50);
		panel_comandos.add(lblComandosSettingCommands);

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
		btnInhibit.setBackground(Color.GREEN);
		btnInhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					System.out.println("JCM1 INHIBIT DESHABILITAMOS ACEPTADOR");
					jcms[0].jcmMessage[3] = 0x01;
					jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
				}
				if(chckbxReciclador2.isSelected()) {
					System.out.println("JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
					jcms[1].jcmMessage[3] = 0x01;
					jcms[1].id003_format((byte) 0x6, (byte) 0xC3, jcms[1].jcmMessage, false);
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
		btnBootVersionrequest.setBackground(Color.GREEN);
		btnBootVersionrequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					jcms[0].id003_format((byte) 5, (byte) 0x89, jcms[0].jcmMessage, true);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].id003_format((byte) 5, (byte) 0x89, jcms[1].jcmMessage, true);
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
					jcms[0].id003_format_ext((byte) 0x7, (byte) 0xf0, (byte) 0x20, (byte) 0x1a, (byte) 0x1, (byte) 0x2,jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].id003_format_ext((byte) 0x7, (byte) 0xf0, (byte) 0x20, (byte) 0x1a, (byte) 0x1, (byte) 0x2,jcms[1].jcmMessage);
				}		
				
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
		btnCollect.setBackground(Color.ORANGE);
		btnCollect.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCollect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(chckbxReciclador1.isSelected()) {
					jcms[0].currentOpertion = jcmOperation.CollectCass1; 

					// primero el inhibit
					jcms[0].jcmMessage[3] = 0x01;
					jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
					jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x0,
							jcms[0].jcmMessage);
				}

				if(chckbxReciclador2.isSelected()) {
					jcms[1].currentOpertion = jcmOperation.CollectCass1;
					// primero el inhibit
					jcms[1].jcmMessage[3] = 0x01;
					jcms[1].id003_format((byte) 0x6, (byte) 0xC3, jcms[1].jcmMessage, false);
					jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x0,
							jcms[1].jcmMessage);
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
					jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
						jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
						jcms[1].jcmMessage);
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
					jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4D, (byte) 0x1, (byte) 0x2,
							jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4D, (byte) 0x1, (byte) 0x2,
							jcms[1].jcmMessage);
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
		btnRecycleCurrencySetting.setBackground(Color.GREEN);

		btnRecycleCurrencySetting.setBounds(558, 399, 299, 50);
		panel_comandos.add(btnRecycleCurrencySetting);

		JButton btnCurrentCountSetting = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting.setBackground(Color.GREEN);
		btnCurrentCountSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					
					jcms[0].jcmMessage[7] = 0x01;  //REC1
					jcms[0].jcmMessage[8] = 0x00; //CUANTOS EN EL REC2
					jcms[0].jcmMessage[9] = 0x00; //RESERVADO
					jcms[0].jcmMessage[10] = 0x02; //REC2
					jcms[0].id003_format_ext((byte) 0x0A, (byte) 0xf0, (byte) 0x20, (byte) 0xE2, (byte) 0x00, (byte) 0x0, jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].jcmMessage[7] = 0x01;  //REC1
					jcms[1].jcmMessage[8] = 0x00; //CUANTOS EN EL REC2
					jcms[1].jcmMessage[9] = 0x00; //RESERVADO
					jcms[1].jcmMessage[10] = 0x02; //REC2
					jcms[1].id003_format_ext((byte) 0x0A, (byte) 0xf0, (byte) 0x20, (byte) 0xE2, (byte) 0x00, (byte) 0x0, jcms[1].jcmMessage);
				}
			}
		});
		btnCurrentCountSetting.setBounds(10, 458, 289, 50);
		panel_comandos.add(btnCurrentCountSetting);

		JButton btnRecycleCurrencyReqSetting = new JButton("Recycle Currency Req (+90h)");
		btnRecycleCurrencyReqSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCurrencyReqSetting.setBackground(Color.GREEN);
		btnRecycleCurrencyReqSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, jcms[1].jcmMessage);
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
		
		JLabel lblNewLabel = new JLabel("Set denomination (debe estar DISABLE (INHIBIT))");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(559, 347, 425, 41);
		panel_comandos.add(lblNewLabel);

		JButton btnComandosRegresar = new JButton("Regresar");
		btnComandosRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelCont, "panelDeposito");
			}
		});
		btnComandosRegresar.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnComandosRegresar.setBackground(Color.ORANGE);
		btnComandosRegresar.setBounds(10, 11, 207, 73);
		panelComandos.add(btnComandosRegresar);

		JButton btnComandosSalir = new JButton("SALIR");
		btnComandosSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		btnComandosSalir.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnComandosSalir.setBackground(Color.RED);
		btnComandosSalir.setBounds(227, 11, 200, 73);
		panelComandos.add(btnComandosSalir);

		btnPinPadConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				switch(pinpadMode) {
				case loginUser:
					pinpadMode = PinpadMode.loginPassword;
					break;
				case loginPassword:

					//No ha ingresado su user o pwd
					if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
						pinpadMode = PinpadMode.loginUser;						
						return;
					}

					System.out.println("Validando usuario....");
					//Validamos el usuario
					CMUserVO user = Transactions.ValidaUsuario(CurrentUser.loginUser);


					if(user.success && user.isValid) {
						switch(currentOperation) {

						case Deposit:
							//Si es deposito ya lo dejamos pasar
							pinpadMode = PinpadMode.None;
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
							montoDepositado = 0;
							cl.show(panelCont, "panelDeposito");
							Transactions.BorraCashInOPs("CI01GL0001");
							break;
						case Dispense:
							//Si puede retirar lo dejamos pasar
							if(user.allowWithdrawals) {
								pinpadMode = PinpadMode.None;
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
								cl.show(panelCont, "panelToken");
							}
							else {
								CurrentUser.loginUser = "";
								CurrentUser.loginPassword = "";
								pinpadMode = PinpadMode.loginUser;	
								lblLoginRow1.setText("¡Oh no!");
								lblLoginMensaje.setText("No tiene permisos para hacer retiros.");
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
							}
							break;
						default:
							pinpadMode = PinpadMode.loginUser;
							lblLoginRow1.setText("¡Oh no!");
							lblLoginMensaje.setText("Algo salió mal, intenta nuevamente.");
							break;
						}
					}
					else {						
						if(!user.success) {
							switch(currentOperation) {

							case Deposit:
								//Si es deposito ya lo dejamos pasar
								pinpadMode = PinpadMode.None;
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
								montoDepositado = 0;
								cl.show(panelCont, "panelDeposito");
								Transactions.BorraCashInOPs("CI01GL0001");
								break;
							case Dispense:								
								CurrentUser.loginUser = "";
								CurrentUser.loginPassword = "";
								pinpadMode = PinpadMode.loginUser;	
								lblLoginRow1.setText("¡Oh no!");
								lblLoginMensaje.setText("Algo salió mal, no podemos darte el servicio en este momento.");
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);

								break;
							default:
								pinpadMode = PinpadMode.loginUser;
								lblLoginRow1.setText("¡Oh no!");
								lblLoginMensaje.setText("Algo salió mal, intenta nuevamente.");
								break;
							}							
						}
						else {
							lblLoginRow1.setText("¡Oh no!");
							lblLoginMensaje.setText("       Están mal los datos.");
							CurrentUser.loginUser = "";
							CurrentUser.loginPassword = "";
							pinpadMode = PinpadMode.loginUser;		
							RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid FALSE",AccountType.Administrative, TransactionType.ControlMessage);
							return;
						}
					}


					pinpadMode = PinpadMode.None;

					break;
				case retiroToken:
					System.out.println("VALIDAMOS QUE SEAN IGUALES");

					System.out.println(token + " - " + CurrentUser.tokenConfirmacion);
					if(token.equalsIgnoreCase(CurrentUser.tokenConfirmacion)) {
						//Preparamos el retiro
						token = "";
						pinpadMode = PinpadMode.None;
						cl.show(panelCont, "panelRetiraBilletes");

						lblRetiraBilletesMonto.setText("$" + montoRetiro);

						Dispensar();


						if(isDebug) {													
							Timer screenTimer = new Timer();
							screenTimer.schedule(new TimerTask() {
								@Override
								public void run() {				                
									cl.show(panelCont, "panelTerminamos");
									Ptr.print("SI",new HashMap<String,String>());
									Timer screenTimer2 = new Timer();
									screenTimer2.schedule(new TimerTask() {
										@Override
										public void run() {				                
											cl.show(panelCont, "panelInicio");
											screenTimer.cancel();
											screenTimer2.cancel();
										}
									}, 3000);

								}
							}, 3000);
						}
						else
						{
							Timer screenTimerDispense = new Timer();
							screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
								@Override
								public void run() {
									if(jcm1cass1Dispensed && jcm1cass2Dispensed && jcm2cass1Dispensed && jcm2cass2Dispensed) {
										cl.show(panelCont, "panelTerminamos");
										RaspiAgent.Broadcast(DeviceEvent.AFD_DispenseOk, "" + montoRetiro);
										RaspiAgent.WriteToJournal("Withdrawal", montoRetiro,0, "","", "Withdrawal DispenseOk", AccountType.Other, TransactionType.Withdrawal);
										Ptr.printDispense(montoRetiro,CurrentUser.loginUser);
										Timer screenTimer2 = new Timer();
										screenTimer2.schedule(new TimerTask() {
											@Override
											public void run() {
												cl.show(panelCont, "panelInicio");
												screenTimerDispense.cancel();
												screenTimer2.cancel();
											}
										}, 1000);
									}
								}
							}, 1000,2000);

						}


						/*
						cl.show(panelCont, "panelTerminamos");
						Timer screenTimer = new Timer();						
						screenTimer.schedule(new TimerTask() {
				            @Override
				            public void run() {				                
				            	cl.show(panelCont, "panelInicio");
				                screenTimer.cancel();
				            }
				        }, 3000);
						 */
					}
					else {
						CurrentUser.tokenConfirmacion = "";
						lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);

						lblTokenMensaje.setText("Los valores no son iguales, verifica.");
					}


					break;
				default:
					break;

				}			

				//pinpadMode = PinpadMode.None;
			}
		});



		btnPinPadCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CurrentUser.loginUser = "";
				CurrentUser.loginPassword = "";
				CurrentUser.tokenConfirmacion = "";
				lblLoginUser.setText("");
				lblLoginPassword.setText("");

				switch(pinpadMode) {
				case None:
					break;
				case loginUser:			
					CurrentUser.loginUser = "";										
					break;
				case loginPassword:	
					CurrentUser.loginPassword = "";
					asteriscos = "";					
					break;				
				case retiroToken:
					CurrentUser.tokenConfirmacion = "";
					lblTokenConfirmacion.setText("");
					break;
				default:
					break;						
				}

				cl.show(panelCont, "panelInicio");
			}
		});

		btnPinPad1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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
				switch(pinpadMode) {
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

		btnIdleDeposito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelToken.remove(panelPinPad);
				panelLogin.remove(panelPinPad);
				panelLogin.add(panelPinPad);
				pinpadMode = PinpadMode.loginUser;
				CurrentUser.loginUser = "";
				CurrentUser.loginPassword = "";
				asteriscos = "";
				lblLoginMensaje.setText("Para depositar, identifícate");
				lblLoginPassword.setText(CurrentUser.loginPassword);
				lblLoginUser.setText(CurrentUser.loginUser);
				currentOperation = jcmOperation.Deposit;
				cl.show(panelCont, "panelLogin");

			}
		});


		btnIdleRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				token = "" + getRandomDoubleBetweenRange(1111,9999);
				montoRetiro = getRandomDoubleBetweenRange(120,1100);	

				panelToken.remove(panelPinPad);
				panelLogin.remove(panelPinPad);
				panelToken.add(panelPinPad);

				pinpadMode = PinpadMode.retiroToken;
				currentOperation = jcmOperation.Dispense;
				lblLoginMensaje.setText("Para retirar, identifícate");
				lblTokenMontoRetiro.setText("$" + montoRetiro);
				lblToken.setText(token);
				CurrentUser.tokenConfirmacion = "";
				lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);

				cl.show(panelCont, "panelToken");
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
				
				//0xD0; DENOM; RESEVADO (0x0h); REC_BOX
				
				if(chckbxReciclador1.isSelected()) {				
					jcms[0].jcmMessage[7] = 0x01;  //REC1
					jcms[0].jcmMessage[8] = 0x08; // 0x02:20 0x04:50 0x08:100 0x10:200 0x20:500;
					jcms[0].jcmMessage[9] = 0x00;
					jcms[0].jcmMessage[10] = 0x02;
					jcms[0].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, (byte) 0x10, (byte) 0x0,jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].jcmMessage[7] = 0x01;  
					jcms[1].jcmMessage[8] = 0x02; // 0x02:20 0x04:50 0x08:100 0x10:200 0x20:500;
					jcms[1].jcmMessage[9] = 0x00;
					jcms[1].jcmMessage[10] = 0x02; //REC2
					jcms[1].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, (byte) 0x04, (byte) 0x0,jcms[1].jcmMessage);
				}
			}
		});

		btnTotalCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {	
					jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
				}
				if(chckbxReciclador2.isSelected()) {	
					jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
							jcms[1].jcmMessage);
				}
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
				if(chckbxReciclador1.isSelected())
					jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
						jcms[0].jcmMessage);
				if(chckbxReciclador2.isSelected())
					jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
							jcms[1].jcmMessage);
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
				if(chckbxReciclador1.isSelected()) {
					jcms[0].jcmMessage[3] = 0x00;
					jcms[0].id003_format((byte) 0x6, (byte) 0xC3, jcms[0].jcmMessage, false);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].jcmMessage[3] = 0x00;
					jcms[1].id003_format((byte) 0x6, (byte) 0xC3, jcms[1].jcmMessage, false);
				}
			}
		});

		btnCierraBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//* TODO: TIO
				miTio.cierraBoveda();
				//*/
			}
		});


		btnAbreBoveda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//* TODO: TIO
				miTio.abreBoveda();
				//*/
			}
		});

		btnSolicitaRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				token = "" + getRandomDoubleBetweenRange(111111,999999) + "" +getRandomDoubleBetweenRange(111111,999999);
				montoRetiro = getRandomDoubleBetweenRange(20,1500);
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
					lblBilleteIngresado1.setText("$" + billType);
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
					lblBilleteIngresado2.setText("$" + billType2);
					break;					
				case "clearbill1":
					lblBilleteIngresado1.setText("");
					break;
				case "clearbill2":
					lblBilleteIngresado2.setText("");
					break;		
				case "recyclerBills1":

					lblRecycler1.setText(jcms[0].recyclerDenom1 + " " + jcms[0].recyclerDenom2);

					recyclerBills1Set = true;					
					if(recyclerBills2Set) {

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;

						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills1Set = false;
					}
					break;
				case "recyclerBills2":

					lblRecycler2.setText(jcms[1].recyclerDenom1 + " " + jcms[1].recyclerDenom2);

					recyclerBills2Set = true;
					if(recyclerBills1Set) {						

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;


						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills2Set = false;
					}

					break;
				case "recyclerContadores1":

					lblContadores1.setText(jcms[0].recyclerContadores);
					break;
				case "recyclerContadores2":

					lblContadores2.setText(jcms[1].recyclerContadores);
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
			jcms[i].currentOpertion = jcmOperation.Startup;
			jcms[i].openPort(jcms[i].portId.getName().toString());
		}



		//cl.show(panelCont, "panelInicio");
		cl.show(panelCont, "panelIdle");	
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


	public static int getRandomDoubleBetweenRange(double min, double max){

		int x = (int) ((Math.random()*((max-min)+1))+min);
		return x;

	}


	boolean Dispensar() {


		if(isDebug) {
			return true;
		}

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

		//JcmCassetero cass = null;

		//500, 50, 100,200

		if(jcms[0].contadores.Cass1Available > 0) {
			JcmCassetero cass = new JcmCassetero();
			cass.Jcm = 0;
			cass.Cassete = 1;
			cass.Denomincacion = jcms[0].contadores.Cass1Denom;
			orden.add(cass);
		}

		if(jcms[0].contadores.Cass2Available > 0) {
			JcmCassetero cass = new JcmCassetero();
			cass.Jcm = 0;
			cass.Cassete = 2;
			cass.Denomincacion = jcms[0].contadores.Cass2Denom;
			orden.add(cass);
		}

		if(jcms[1].contadores.Cass1Available > 0) {
			JcmCassetero cass = new JcmCassetero();
			cass = new JcmCassetero();
			cass.Jcm = 1;
			cass.Cassete = 1;
			cass.Denomincacion = jcms[1].contadores.Cass1Denom;
			orden.add(cass);
		}

		if(jcms[1].contadores.Cass2Available > 0) {
			JcmCassetero cass = new JcmCassetero();
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

		return true;
	}


	class DenominacionSorter implements Comparator<JcmCassetero> 
	{
		@Override
		public int compare(JcmCassetero o1, JcmCassetero o2) {
			return o2.getDenomincacion().compareTo(o1.getDenomincacion());
		}
	}
}


