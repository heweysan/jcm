package pentomino.flow;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.io.CommPortIdentifier;
import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.Tio;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.gui.FlowLayout;
import pentomino.gui.ImagePanel;
import pentomino.gui.PanelDebug;
import pentomino.gui.PanelDeposito;
import pentomino.gui.PanelError;
import pentomino.gui.PanelIdle;
import pentomino.gui.PanelLogin;
import pentomino.gui.PanelMenu;
import pentomino.gui.PanelOperacionCancelada;
import pentomino.gui.PanelRetiraBilletes;
import pentomino.gui.PanelRetiroParcial;
import pentomino.gui.PanelToken;
import pentomino.gui.PinpadListener;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;

public class Flow {

	public static EventListenerClass c;

	//https://logging.apache.org/log4j/2.x/manual/layouts.html
	private static final Logger logger = LogManager.getLogger(Flow.class.getName());

	public static ImagePanel panelTerminamos;
	public static ImagePanel panelOperacionCanceladaHolder;
	public static ImagePanel panelErrorHolder; 
	public static ImagePanel panelLoginHolder;
	public static ImagePanel panelTokenHolder;
	public static ImagePanel panelIdleHolder;
	public static ImagePanel panelMenuHolder;
	public static ImagePanel panelDepositoHolder;
	public static ImagePanel panelComandosHolder;
	public static ImagePanel panelRetiraBilletesHolder;
	public static ImagePanel panelRetiroParcialHolder;

	public static JcmContadores contadoresDeposito = new JcmContadores();	

	public static double montoRetiro = 0;
	public static int montoDepositado = 0;

	private JFrame mainFrame;

	public static boolean recyclerContadores1 = false;
	public static boolean recyclerContadores2 = false;

	private static boolean recyclerBills1Set = false;
	private static boolean recyclerBills2Set = false;


	/* Variables de control para saber que ya dispensaron todos los caseteros*/
	public static boolean jcm1cass1Dispensed = false;
	public static boolean jcm1cass2Dispensed = false;
	public static boolean jcm2cass1Dispensed = false;
	public static boolean jcm2cass2Dispensed = false;

	public static JPanel panelContainer = new JPanel();
	private static FlowLayout cl = new FlowLayout();

	public static uart[] jcms = new uart[2];
	int contador = 0;


	public final static Tio miTio = new Tio();

	final AgentsQueue agentsQueue = new AgentsQueue();
	final CmQueue cmQueue = new CmQueue();
	final DTAServer dtaServer = new DTAServer();


	public static void main(String[] args) {

		logger.info("----- FLOW MAIN -----");

		JcmGlobalData.isDebug = System.getProperty("os.name").toLowerCase().contains("windows");
		System.out.println(System.getProperty("os.name") + " isDebug[" + JcmGlobalData.isDebug + "]");

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

		JcmMonitor t2 = new JcmMonitor();
		t2.start();

		Thread agentsQueueThread = new Thread(agentsQueue, "agentsQueueThread");
		agentsQueueThread.start();

		Thread cmQueueThread = new Thread(cmQueue, "cmQueueThread");
		cmQueueThread.start();

		dtaServer.SetupRabbitListener();		

		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	

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


		panelIdleHolder = new ImagePanel(new ImageIcon("./images/Scr7Inicio.png").getImage(),"panelIdle");
		PanelIdle panelIdle = new PanelIdle();
		panelIdleHolder.add(panelIdle.getPanel());

		panelMenuHolder = new ImagePanel(new ImageIcon("./images/Scr7SinRetiroAutorizado.png").getImage(),"panelMenu");
		PanelMenu panelMenu = new PanelMenu();
		panelMenuHolder.add(panelMenu.getPanel());

		panelDepositoHolder = new ImagePanel(new ImageIcon("./images/Scr7MontoIngresado.png").getImage(),"panelDeposito");
		PanelDeposito panelDeposito = new PanelDeposito();
		panelDepositoHolder.add(panelDeposito.getPanel());

		panelComandosHolder = new ImagePanel(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelComandos");
		PanelDebug panelComandos = new PanelDebug();
		panelComandosHolder.add(panelComandos.getPanel());		

		panelLoginHolder = new ImagePanel(new ImageIcon("./images/Scr7IdentificateDeposito.png").getImage(),"panelLogin");
		PinpadListener panelLogin = new PanelLogin();
		panelLoginHolder.add(panelLogin.getPanel());

		panelTokenHolder = new ImagePanel(new ImageIcon("./images/Scr7ConfirmaToken.png").getImage(),"panelToken");
		PanelToken panelToken = new PanelToken();
		panelTokenHolder.add(panelToken.getPanel());

		panelTerminamos = new ImagePanel(new ImageIcon("./images/ScrTerminamos.png").getImage(),"panelTerminamos");

		panelRetiraBilletesHolder = new ImagePanel(new ImageIcon("./images/ScrRetiraBilletes.png").getImage(),"panelRetiraBilletes");
		PanelRetiraBilletes panelRetiraBilletes = new PanelRetiraBilletes();
		panelRetiraBilletesHolder.add(panelRetiraBilletes.getPanel());


		panelRetiroParcialHolder = new ImagePanel(new ImageIcon("./images/Scr7RetiroParcial.png").getImage(),"panelRetiroParcial");
		PanelRetiroParcial panelRetiroParcial = new PanelRetiroParcial();
		panelRetiroParcialHolder.add(panelRetiroParcial.getPanel());

		panelErrorHolder = new ImagePanel(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelError",5000,"panelIdle");
		PanelError panelError = new PanelError();
		panelErrorHolder.add(panelError.getPanel());

		panelOperacionCanceladaHolder = new ImagePanel(new ImageIcon("./images/Scr7OperacionCancelada.png").getImage(),"panelOperacionCancelada",5000,"panelIdle");
		PanelOperacionCancelada panelOperacionCancelada = new PanelOperacionCancelada();
		panelOperacionCanceladaHolder.add(panelOperacionCancelada.getPanel());


		panelContainer.setLayout(cl);		
		panelContainer.add(panelIdleHolder,"panelIdle");
		panelContainer.add(panelMenuHolder,"panelMenu");
		panelContainer.add(panelDepositoHolder,"panelDeposito");
		panelContainer.add(panelComandosHolder, "panelComandos");
		panelContainer.add(panelLoginHolder, "panelLogin");
		panelContainer.add(panelTokenHolder,"panelToken");
		panelContainer.add(panelTerminamos,"panelTerminamos");
		panelContainer.add(panelRetiraBilletesHolder,"panelRetiraBilletes");
		panelContainer.add(panelRetiroParcialHolder,"panelRetiroParcial");
		panelContainer.add(panelErrorHolder,"panelError");
		panelContainer.add(panelOperacionCanceladaHolder,"panelOperacionCancelada");		


		JLabel lblRetiraBilletesMontoDispensarParcial = new JLabel("New label");
		lblRetiraBilletesMontoDispensarParcial.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiraBilletesMontoDispensarParcial.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensarParcial.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblRetiraBilletesMontoDispensarParcial.setBounds(501, 677, 622, 153);
		panelRetiroParcialHolder.add(lblRetiraBilletesMontoDispensarParcial);


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

		Timer btnRetiroAlertBlinker = new Timer(500, new ActionListener() {
			boolean on=false;
			public void actionPerformed(ActionEvent e) {
				// blink the button background on and off
				btnRetiro.setBackground( on ? Color.ORANGE : null);
				on = !on;
			}
		});        
		 */
		
		String atmId = Config.GetDirective("AtmId", "");

		c = new EventListenerClass();
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
					myObj.atmId = atmId; //CIXXGS0020 CI01GL0001
					myObj.amount = (long) billType;
					myObj.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					myObj.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
					myObj.notesDetails = "1x" + billType;

					montoDepositado += billType;					

					Transactions.InsertaCashInOp(myObj);

					RaspiAgent.WriteToJournal("CASH MANAGEMENT", (double)billType,0, "","", "PROCESADEPOSITO PreDeposito", AccountType.Administrative, TransactionType.ControlMessage);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, "1x" + billType);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInReceived, "" + billType);
					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted, "" + montoDepositado);


					System.out.println("$" + montoDepositado);
					panelDeposito.lblMontoDepositado.setText("$" + montoDepositado);
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
					myObj2.atmId = atmId; //CIXXGS0020 CI01GL0001
					myObj2.amount = (long) billType2;
					myObj2.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					myObj2.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
					myObj2.notesDetails = "1x" + billType2;

					Transactions.InsertaCashInOp(myObj2);

					RaspiAgent.WriteToJournal("CASH MANAGEMENT", (double)billType2,0, "","", "PROCESADEPOSITO PreDeposito", AccountType.Administrative, TransactionType.ControlMessage);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, "1x" + billType2);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInReceived, "" + billType2);


					montoDepositado += billType2;					
					System.out.println("$" + montoDepositado);
					panelDeposito.lblMontoDepositado.setText("$" + montoDepositado);
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

		if(contador == 0 && JcmGlobalData.isDebug) {
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


	public static void redirect(ImagePanel target, int timeout, String timeoutTarget) {
	
		Flow.cl.show(panelContainer, target,timeout,timeoutTarget);
	
	}

	public static void redirect(String target) {
		
		Flow.cl.show(panelContainer, target);
	
	}
	
	public static void redirect(ImagePanel target) {
		
		Flow.cl.show(panelContainer, target);
		target.screenTimerCancel();
	
	}
	
	public static void actualizaContadoresRecicladores() {
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

}



/*
btnPinPadConfirmar.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {

		switch(CurrentUser.pinpadMode) {
		case loginUser:					
			System.out.println("loginUser");
			if(currentOperation == jcmOperation.Deposit) {
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
					montoDepositado = 0;
					cl.show(panelContainer, "panelDeposito");
					Transactions.BorraCashInOPs(atmId); //CIXXGS0020 CI01GL0001								
				}
				else {						
					if(!user.success) {
						System.out.println("loginUser deposit success NO");
						//Si es deposito ya lo dejamos pasar
						CurrentUser.pinpadMode = PinpadMode.None;
						RaspiAgent.WriteToJournal("CASH MANAGEMENT", 0, 0, "", "", "VALIDAUSUARIO IsValid EXCEPTION",AccountType.Administrative, TransactionType.ControlMessage);
						montoDepositado = 0;
						cl.show(panelContainer, "panelDeposito");
						Transactions.BorraCashInOPs(atmId); //CIXXGS0020;CI01GL0001
					}
					else {	
						if(++CurrentUser.loginAttempts >= 2) {
							//Intentos superados
							CurrentUser.setLoginUser("");
							CurrentUser.loginPassword = "";
							CurrentUser.loginAttempts = 0;
							CurrentUser.tokenAttempts = 0;
							cl.show(panelContainer, panelOperacionCancelada,5000,"panelIdle");
						}
						else {
							CurrentUser.setLoginUser("");
							CurrentUser.loginPassword = "";
							lblLoginUser.setText("");
							lblLoginPassword.setText("");
							CurrentUser.asteriscos = "";
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
						CurrentUser.setLoginUser("");
						CurrentUser.loginPassword = "";
						lblLoginUser.setText("");
						lblLoginPassword.setText("");
						CurrentUser.asteriscos = "";
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
						cl.show(panelContainer, "panelDeposito");
						Transactions.BorraCashInOPs(atmId); //CIXXGS0020;CI01GL0001);
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
					cmWithdrawalVo.atmId = atmId; //CIXXGS0020;CI01GL0001
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
										cmWithdrawalVo.atmId = atmId; //CIXXGS0020;CI01GL0001
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

 */		

