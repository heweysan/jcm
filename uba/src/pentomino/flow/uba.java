package pentomino.flow;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

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
import pentomino.gui.ImagePanel;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.RaspiAgent;

public class uba {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(uba.class.getName());

	private static boolean isDebug = false;

	private static PinpadMode pinpadMode = PinpadMode.None;
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

	private static JPanel panelContainer = new JPanel();
	private static CardLayout cl = new CardLayout();

	uart[] jcms = new uart[2];
	int contador = 0;

	//*  //TODO: TIO
	final Tio miTio = new Tio();
	//*/

	final AgentsQueue agentsQueue = new AgentsQueue();
	final CmQueue cmQueue = new CmQueue();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		logger.info("UBA MAIN");

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

		JcmMonitor t2 = new JcmMonitor();
		t2.start();

		Thread agentsQueueThread = new Thread(agentsQueue, "agentsQueueThread");
		agentsQueueThread.start();


		Thread cmQueueThread = new Thread(cmQueue, "cmQueueThread");
		cmQueueThread.start();
		//HEWEY QUITAR 
		//currentOperation = jcmOperation.Startup;

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


		//JPanel panelCont = new JPanel();
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
		ImagePanel panelInicio = new ImagePanel(new ImageIcon("./images/Scr7SinRetiroAutorizado.png").getImage(),"panelInicio");
		ImagePanel panelInicioConRetiro = new ImagePanel(new ImageIcon("./images/Scr7RetiroAutorizado.png").getImage(),"panelInicioConRetiro");
		ImagePanel panelDeposito = new ImagePanel(new ImageIcon("./images/Scr7MontoIngresado.png").getImage(),"panelDeposito");
		ImagePanel panelComandos = new ImagePanel(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelComandos");
		ImagePanel panelLogin = new ImagePanel(new ImageIcon("./images/loginFormClean.png").getImage(),"panelLogin");
		ImagePanel panelToken = new ImagePanel(new ImageIcon("./images/panelToken.png").getImage(),"panelToken");
		ImagePanel panelTerminamos = new ImagePanel(new ImageIcon("./images/ScrTerminamos.png").getImage(),"panelTerminamos");
		ImagePanel panelRetiraBilletes = new ImagePanel(new ImageIcon("./images/ScrRetiraBilletes.png").getImage(),"panelRetiraBilletes");
		ImagePanel panelRetiroParcial = new ImagePanel(new ImageIcon("./images/Scr7RetiroParcial.png").getImage(),"panelRetiroParcial");
		ImagePanel panelError = new ImagePanel(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelError");

		panelIdle.setLayout(null);
		panelInicio.setLayout(null);
		panelInicioConRetiro.setLayout(null);
		panelDeposito.setLayout(null);
		panelComandos.setLayout(null);
		panelLogin.setLayout(null);
		panelToken.setLayout(null);
		panelTerminamos.setLayout(null);
		panelRetiraBilletes.setLayout(null);
		panelRetiroParcial.setLayout(null);
		panelError.setLayout(null);



		panelContainer.setLayout(cl);

		panelContainer.add(panelIdle,"panelIdle");
		panelContainer.add(panelInicio,"panelInicio");
		panelContainer.add(panelInicioConRetiro,"panelInicioConRetiro");
		panelContainer.add(panelDeposito,"panelDeposito");
		panelContainer.add(panelComandos, "panelComandos");
		panelContainer.add(panelLogin, "panelLogin");
		panelContainer.add(panelToken,"panelToken");
		panelContainer.add(panelTerminamos,"panelTerminamos");
		panelContainer.add(panelRetiraBilletes,"panelRetiraBilletes");
		panelContainer.add(panelRetiroParcial,"panelRetiroParcial");
		panelContainer.add(panelError,"panelError");

		JLabel lblPanelError = new JLabel("New label");
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

		JButton btnIdleComandos = new JButton("COMANDOS");
		btnIdleComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnIdleComandos.setBackground(Color.ORANGE);
		btnIdleComandos.setBounds(10, 11, 200, 73);
		btnIdleComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelContainer,"panelComandos");	
			}
		});

		panelIdle.add(btnIdleComandos);	


		JButton btnIdleSalir = new JButton("SALIR");
		btnIdleSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnIdleSalir.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnIdleSalir.setBackground(Color.RED);
		btnIdleSalir.setBounds(220, 11, 200, 73);
		panelIdle.add(btnIdleSalir);
		JButton btnIdle = new JButton("");
		btnIdle.setBounds(0, 0, 1920, 1080);		
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnIdle.setOpaque(false);
		btnIdle.setContentAreaFilled(false);
		btnIdle.setBorderPainted(false);
		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(CmQueue.queueList.isEmpty())				
					cl.show(panelContainer,"panelInicio");				
				else
					cl.show(panelContainer,"panelInicioConRetiro");

				screenTimeout(10000);

			}
		});
		panelIdle.add(btnIdle);


		JButton btnRetiraBilletesComandos = new JButton("COMANDOS");
		btnRetiraBilletesComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelContainer,"panelComandos");
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

		BufferedImage btnAceptar = null;
		try {
			btnAceptar = ImageIO.read(new File("./images/BTN7Aceptar.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedImage btnDeposito = null;
		try {
			btnDeposito = ImageIO.read(new File("./images/BTN7Deposito.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		BufferedImage btnRetiro = null;
		try {
			btnRetiro = ImageIO.read(new File("./images/BTN7Retiro.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/** - - - - - - - - - -   P A N E L   I N I C I O  S I N  R E T I R O - - - - - - - - - -  */

		JButton btnMenuDeposito = new JButton(new ImageIcon(btnDeposito));
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setBounds(360, 502, 492, 498);
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setContentAreaFilled(false);
		btnMenuDeposito.setBorderPainted(false);
		panelInicio.add(btnMenuDeposito);

		final JLabel lblLoginUser = new JLabel("");
		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(257, 545, 496, 87);
		panelLogin.add(lblLoginUser);

		JButton btnMenuComandos = new JButton("COMANDOS");
		btnMenuComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnMenuComandos.setBackground(Color.ORANGE);
		btnMenuComandos.setBounds(10, 11, 200, 73);
		btnMenuComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelContainer,"panelComandos");
			}
		});
		panelInicio.add(btnMenuComandos);

		JButton btnMenuSalir = new JButton("SALIR");
		btnMenuSalir.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnMenuSalir.setBackground(Color.RED);
		btnMenuSalir.setBounds(220, 11, 200, 73);
		btnMenuSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panelInicio.add(btnMenuSalir);



		/** - - - - - - - - - -   P A N E L   I N I C I O  C O N   R E T I R O - - - - - - - - - -  */

		JButton btnInicioDepositoConRetiro = new JButton(new ImageIcon(btnDeposito));		
		btnInicioDepositoConRetiro.setBounds(360, 502, 492, 498);
		btnInicioDepositoConRetiro.setOpaque(false);
		btnInicioDepositoConRetiro.setContentAreaFilled(false);
		btnInicioDepositoConRetiro.setBorderPainted(false);
		panelInicioConRetiro.add(btnInicioDepositoConRetiro);

		final JLabel lblLoginUserConRetiro = new JLabel("");
		lblLoginUserConRetiro.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginUserConRetiro.setForeground(Color.WHITE);
		lblLoginUserConRetiro.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUserConRetiro.setBounds(257, 545, 496, 87);
		panelInicioConRetiro.add(lblLoginUserConRetiro);

		JButton btnIdleComandosConRetiro = new JButton("COMANDOS");
		btnIdleComandosConRetiro.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnIdleComandosConRetiro.setBackground(Color.ORANGE);
		btnIdleComandosConRetiro.setBounds(10, 11, 200, 73);
		btnIdleComandosConRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelContainer,"panelComandos");
			}
		});
		panelInicioConRetiro.add(btnIdleComandosConRetiro);

		JButton btnIdleSalirConRetiro = new JButton("SALIR");
		btnIdleSalirConRetiro.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnIdleSalirConRetiro.setBackground(Color.RED);
		btnIdleSalirConRetiro.setBounds(220, 11, 200, 73);
		btnIdleSalirConRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panelInicioConRetiro.add(btnIdleSalirConRetiro);

		JButton btnIdleRetiro = new JButton(new ImageIcon(btnRetiro));		
		btnIdleRetiro.setOpaque(false);
		btnIdleRetiro.setContentAreaFilled(false);
		btnIdleRetiro.setBorderPainted(false);
		btnIdleRetiro.setBounds(989, 502, 492, 498);

		panelInicioConRetiro.add(btnIdleRetiro);


		JButton btnTokenComandos = new JButton("COMANDOS");
		btnTokenComandos.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnTokenComandos.setBackground(Color.ORANGE);
		btnTokenComandos.setBounds(10, 11, 200, 73);
		btnTokenComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelContainer,"panelComandos");
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
				cl.show(panelContainer, "panelComandos");
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

		JButton btnDepositoComandos = new JButton("COMANDOS");
		btnDepositoComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(panelContainer, "panelComandos");
			}
		});
		btnDepositoComandos.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnDepositoComandos.setBackground(Color.ORANGE);
		btnDepositoComandos.setBounds(1569, 11, 321, 136);
		panelDeposito.add(btnDepositoComandos);

		JButton btnDepositoIdle = new JButton("IDLE");
		btnDepositoIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Revisamos si hay retiros listos
				if(CmQueue.queueList.isEmpty())				
					cl.show(panelContainer,"panelIdle");
				else
					cl.show(panelContainer,"panelInicioConRetiro");
			}
		});
		btnDepositoIdle.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnDepositoIdle.setBackground(Color.GREEN);
		btnDepositoIdle.setBounds(1220, 11, 321, 136);
		panelDeposito.add(btnDepositoIdle);

		JButton btnOperacion1 = new JButton(new ImageIcon(btnAceptar));
		btnOperacion1.setBounds(651, 757, 782, 159);
		panelDeposito.add(btnOperacion1);


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
					cl.show(panelContainer, "panelTerminamos");
					Timer screenTimer = new Timer();
					screenTimer.schedule(new TimerTask() {
						@Override
						public void run() {				                
							//Revisamos si hay retiros listos
							if(CmQueue.queueList.isEmpty())				
								cl.show(panelContainer,"panelInicio");
							else
								cl.show(panelContainer,"panelInicioConRetiro");
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
		btnOperacion1.setContentAreaFilled(false);
		btnOperacion1.setBorderPainted(false);
		btnOperacion1.setOpaque(false);
		//btnOperacion1.setBackground(Color.WHITE);
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

		JPanel panel_comandos = new JPanel();
		panel_comandos.setBounds(10, 328, 1886, 706);
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
		btnVersionRequest.setBackground(Color.GREEN);
		btnVersionRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxReciclador1.isSelected()) {
					jcms[0].id003_format((byte) 5, (byte) 0x88, jcms[0].jcmMessage, true);
				}
				if(chckbxReciclador2.isSelected()) {
					jcms[1].id003_format((byte) 5, (byte) 0x88, jcms[0].jcmMessage, true);
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
				cl.show(panelContainer, "panelDeposito");
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

		JPanel panelJCM1 = new JPanel();
		panelJCM1.setBackground(Color.BLACK);
		panelJCM1.setLayout(null);
		panelJCM1.setOpaque(false);
		panelJCM1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM1.setBounds(10, 92, 630, 225);
		panelComandos.add(panelJCM1);

		JLabel lblRecycler1 = new JLabel(".");
		lblRecycler1.setForeground(Color.WHITE);
		lblRecycler1.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblRecycler1.setBounds(10, 102, 180, 53);
		panelJCM1.add(lblRecycler1);

		JLabel lblTitleReciclador1 = new JLabel("Reciclador 1");
		lblTitleReciclador1.setForeground(Color.WHITE);
		lblTitleReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblTitleReciclador1.setBounds(10, 11, 161, 28);
		panelJCM1.add(lblTitleReciclador1);

		JLabel lblTxtBill1 = new JLabel("Billete:");
		lblTxtBill1.setForeground(Color.WHITE);
		lblTxtBill1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill1.setBounds(10, 166, 144, 46);
		panelJCM1.add(lblTxtBill1);

		JLabel lblBilleteIngresado1 = new JLabel("$0");
		lblBilleteIngresado1.setForeground(Color.WHITE);
		lblBilleteIngresado1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado1.setBounds(164, 166, 226, 46);
		panelJCM1.add(lblBilleteIngresado1);

		JLabel lblContadores1 = new JLabel("rec1/0  rec2/0");
		lblContadores1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblContadores1.setForeground(Color.WHITE);
		lblContadores1.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblContadores1.setBounds(164, 102, 456, 49);
		panelJCM1.add(lblContadores1);

		JButton btnReiniciarJcm1 = new JButton("REINICIAR");
		btnReiniciarJcm1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...				
				jcms[0].id003_format((byte)5, protocol.SSR_VERSION, jcms[0].jcmMessage,true); //SSR_VERSION 0x88
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
		panelJCM2.setBounds(1034, 92, 630, 225);
		panelComandos.add(panelJCM2);

		JLabel lblRecycler2 = new JLabel(".");
		lblRecycler2.setForeground(Color.WHITE);
		lblRecycler2.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblRecycler2.setBounds(10, 102, 210, 53);
		panelJCM2.add(lblRecycler2);

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
		lblContadores2.setBounds(164, 110, 430, 37);
		panelJCM2.add(lblContadores2);

		JButton btnReiniciarJcm2 = new JButton("REINICIAR");
		btnReiniciarJcm2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[1].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...
				jcms[1].id003_format((byte)5, protocol.SSR_VERSION, jcms[1].jcmMessage,true); //SSR_VERSION 0x88
			}
		});
		btnReiniciarJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm2.setBounds(281, 11, 209, 80);
		panelJCM2.add(btnReiniciarJcm2);

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
							cl.show(panelContainer, "panelDeposito");
							Transactions.BorraCashInOPs("CI01GL0001");
							break;
						case Dispense:
							//Si puede retirar lo dejamos pasar
							if(user.allowWithdrawals) {																
								RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid TRUE",AccountType.Administrative, TransactionType.ControlMessage);
								panelToken.remove(panelPinPad);
								panelLogin.remove(panelPinPad);
								panelToken.add(panelPinPad);
								pinpadMode = PinpadMode.retiroToken;
								cl.show(panelContainer, "panelToken");								
								break;
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
								cl.show(panelContainer, "panelDeposito");
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

					break;
				case retiroToken:
					System.out.println("VALIDAMOS QUE SEAN IGUALES");

					System.out.println(token + " - " + CurrentUser.tokenConfirmacion);
					if(token.equalsIgnoreCase(CurrentUser.tokenConfirmacion)) {

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


							if(!validateDisepnse()) {
								System.out.println("No se puede dispensar en este momento.");							
								lblPanelError.setText("No se puede dispensar en este momento.");
								cl.show(panelContainer, "panelError");
								screenTimeout(5000);
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
									System.out.println("Usuario sin permiso para dispansar!");
									lblPanelError.setText("Lo sentimos, no se pude procesar su petición");
									cl.show(panelContainer, "panelError");
									screenTimeout(5000);
								}
								else {
									//Preparamos el retiro
									token = "";
									pinpadMode = PinpadMode.None;

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

				screenTimerTimeout.cancel();

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

				//Revisamos si hay retiros listos
				cl.show(panelContainer,"panelIdle");
			}
		});

		btnPinPad1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(pinpadMode) {
				case None:
					System.out.println("NINGUNO PAPA!");
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

		btnMenuDeposito.addActionListener(new ActionListener() {
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
				cl.show(panelContainer, "panelLogin");
			}
		});

		btnInicioDepositoConRetiro.addActionListener(new ActionListener() {
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
				cl.show(panelContainer, "panelLogin");
			}
		});

		btnIdleRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			


				panelToken.remove(panelPinPad);
				panelLogin.remove(panelPinPad);
				panelLogin.add(panelPinPad);
				pinpadMode = PinpadMode.loginUser;
				CurrentUser.loginUser = "";
				CurrentUser.loginPassword = "";
				asteriscos = "";
				lblLoginMensaje.setText("Para retirar, identifícate");
				lblLoginPassword.setText(CurrentUser.loginPassword);
				lblLoginUser.setText(CurrentUser.loginUser);
				currentOperation = jcmOperation.Dispense;
				cl.show(panelContainer, "panelLogin");

				CmMessageRequest request =  CmQueue.queueList.getFirst();

				token = "" + request.token;
				montoRetiro = request.amount;
				lblTokenMontoRetiro.setText("$" + montoRetiro);
				lblToken.setText(token);
				CurrentUser.tokenConfirmacion = "";	
				CurrentUser.referencia = request.reference;
				lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);
				/*
				//Tomamos los datos del retiro que exite
				CmMessageRequest request =  CmQueue.queueList.getFirst();

				token = "" + request.token;
				montoRetiro = request.amount;	

				panelToken.remove(panelPinPad);
				panelLogin.remove(panelPinPad);
				panelToken.add(panelPinPad);

				pinpadMode = PinpadMode.loginUser;
				currentOperation = jcmOperation.Dispense;
				lblLoginMensaje.setText("Para retirar, identifícate");
				lblTokenMontoRetiro.setText("$" + montoRetiro);
				lblToken.setText(token);
				CurrentUser.tokenConfirmacion = "";	
				CurrentUser.referencia = request.reference;
				lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);

				cl.show(panelCont, "panelLogin");
				 */
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
				//jcms[contador].openPort(commPort.getName().toString());

				contador++;
			}			
		}

		//Inicializamos los UARTS
		for(int i = 0; i < contador; i++) {
			jcms[i].currentOpertion = jcmOperation.Startup;
			jcms[i].openPort(jcms[i].portId.getName().toString());
		}

		cl.show(panelContainer, "panelIdle");	
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


	boolean validateDisepnse() {


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
			if(JcmGlobalData.dispenseChange > 0)
			{				//Dispensado parcial				
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

		/*
		// Primero deshabilitamos el que acepte billetes	

		System.out.println("Retirar");

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
		 */
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


	public void screenTimeout(int milliseconds) {
		screenTimerTimeout = new Timer();
		screenTimerTimeout.schedule(new TimerTask() {
			@Override
			public void run() {
				cl.show(panelContainer,"panelIdle");
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


