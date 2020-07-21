package pentomino.flow;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.io.CommPortIdentifier;
import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.common.AccountType;
import pentomino.common.Billete;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.NetUtils;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.core.devices.Tio;
import pentomino.flow.gui.PanelDebug;
import pentomino.flow.gui.PanelDeposito;
import pentomino.flow.gui.PanelDispense;
import pentomino.flow.gui.PanelError;
import pentomino.flow.gui.PanelErrorComunicate;
import pentomino.flow.gui.PanelIdle;
import pentomino.flow.gui.PanelLogin;
import pentomino.flow.gui.PanelMenu;
import pentomino.flow.gui.PanelMenuSinFondo;
import pentomino.flow.gui.PanelNoTicket;
import pentomino.flow.gui.PanelOos;
import pentomino.flow.gui.PanelOperacionCancelada;
import pentomino.flow.gui.PanelReinicio;
import pentomino.flow.gui.PanelSplash;
import pentomino.flow.gui.PanelTerminamos;
import pentomino.flow.gui.PanelToken;
import pentomino.flow.gui.admin.PanelAdminContadoresActuales;
import pentomino.flow.gui.admin.PanelAdminContadoresEnCero;
import pentomino.flow.gui.admin.PanelAdminDetalleError;
import pentomino.flow.gui.admin.PanelAdminDotarCancelar;
import pentomino.flow.gui.admin.PanelAdminDotarResultados;
import pentomino.flow.gui.admin.PanelAdminError;
import pentomino.flow.gui.admin.PanelAdminEstatusDispositivos;
import pentomino.flow.gui.admin.PanelAdminIniciando;
import pentomino.flow.gui.admin.PanelAdminLogin;
import pentomino.flow.gui.admin.PanelAdminLoginTienda;
import pentomino.flow.gui.admin.PanelAdminMenu;
import pentomino.flow.gui.admin.PanelAdminPruebaImpresion;
import pentomino.flow.gui.admin.PanelAdminResetDispositivos;
import pentomino.flow.gui.admin.PanelAdminUsuarioInvalido;
import pentomino.flow.gui.helpers.FlowLayout;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;

public class Flow {

	public static EventListenerClass c;

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());

	public static ImagePanel panelSplash;

	public static ImagePanel panelTerminamos;
	public static ImagePanel panelOperacionCancelada;
	public static ImagePanel panelError; 
	public static ImagePanel panelLogin;
	public static ImagePanel panelToken;
	public static ImagePanel panelIdle;
	public static ImagePanel panelMenu;
	public static ImagePanel panelDeposito;
	public static ImagePanel panelDebug;
	public static ImagePanel panelDispense;
	public static ImagePanel panelNoTicket;
	public static ImagePanel panelMenuSinFondo;
	public static ImagePanel panelReinicio;
	public static ImagePanel panelOos;
	public static ImagePanel panelErrorComunicate;

	//FLUJO ADMNISTRATIVO
	public static ImagePanel panelAdminLogin;	
	public static ImagePanel panelAdminDotarCancelar;
	public static ImagePanel panelAdminDotarResultados;
	public static ImagePanel panelAdminError;	
	public static ImagePanel panelAdminMenu;
	public static ImagePanel panelAdminContadoresActuales;
	public static ImagePanel panelAdminContadoresEnCero;
	public static ImagePanel panelAdminEstatusDispositivos;
	public static ImagePanel panelAdminUsuarioInvalido;
	public static ImagePanel panelAdminResetDispositivos;
	public static ImagePanel panelAdminIniciando;
	public static ImagePanel panelAdminDetalleError;
	public static ImagePanel panelAdminPruebaImpresion;	
	public static ImagePanel panelAdminLoginTienda;
	
	
	public static ImageIcon botonOk = new ImageIcon("./images/BTN7_OK.png");
	public static ImageIcon botonNo = new ImageIcon("./images/BTN7_NO.png");
	public static ImageIcon botonAceptar = new ImageIcon("./images/BTN7Aceptar.png");
	public static ImageIcon botonAdminCancelar = new ImageIcon("./images/BTN_7p_Admin_Cancelar.png");
	public static ImageIcon botonAdminImprimir = new ImageIcon("./images/BTN_7p_Admin_Imprimir.png");
	public static ImageIcon botonAdminImprimirContadores = new ImageIcon("./images/BTN_7p_Admin_imprimir contadores.png");	
	public static ImageIcon botonAdminSalir = new ImageIcon("./images/BTN_7p_Admin_Salir.png");	
	public static ImageIcon bgPlaceHolder = new ImageIcon("./images/Scr7Placeholder.png");
	public static ImageIcon botonAdmin = new ImageIcon("./images/BTN7_ADMIN.png");
	
	public static ImageIcon bgAdminUsuario = new ImageIcon("./images/SCR_P7Admin_Usuario.png");
	public static ImageIcon bgAdminPassword = new ImageIcon("./images/SCR_P7Admin_Contrasena.png");
	public static ImageIcon bgAdminLoginTienda  = new ImageIcon("./images/Scr7IngresaDatos.png");
	

	public static JcmContadores depositBillsCounter = new JcmContadores();	

	private static JFrame mainFrame;

	private static boolean recyclerBills1Set = false;
	private static boolean recyclerBills2Set = false;

	public static JPanel panelContainer = new JPanel();
	private static FlowLayout cl = new FlowLayout();

	public static uart[] jcms = new uart[2];

	public final static Tio miTio = new Tio();

	final AgentsQueue agentsQueue = new AgentsQueue();
	final CmQueue cmQueue = new CmQueue();
	final DTAServer dtaServer = new DTAServer();


	public static int jcm1LastBillInserted = 0; 
	public static int jcm2LastBillInserted = 0;

	public static int jcm1LastBillInsertedWorking = 0; 
	public static int jcm2LastBillInsertedWorking = 0;

	public static Timer adminTimer = new Timer();
	public static Timer fasciaTimer = new Timer();

	/**
	 * Variable que indica si se esta dentro del lapso de tiempo que peude estar abierta la boveda sin sonar la alarma.
	 */
	public static boolean isAdminTime = false;
	public static boolean isFasciaTime = false;
	

	public static void main(String[] args) {

		//System.out.println("->" + TimeUnit.MINUTES.toMillis(10));

		logger.info("----- FLOW MAIN -----");

		JcmGlobalData.isDebug = System.getProperty("os.name").toLowerCase().contains("windows");

		initialize();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					new Flow();					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Activa un timer que dura n minutos, tiempo en el cual se puede abrir la boveda sin generar alarma.
	 * 
	 */
	public static void timerBoveda() {

		logger.info("Timer Boveda inciando.");
		System.out.println("Timer Boveda inciando.");
		adminTimer = new Timer();
		isAdminTime = true;		
		
		Flow.miTio.alarmOff();
		Flow.miTio.abreElectroiman();

		adminTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Timer Boveda terminado.");	
				System.out.println("Timer Boveda terminado.");
				Flow.miTio.cierraElectroiman();
				isAdminTime = false;				
				adminTimer.cancel();
			}
		}, TimeUnit.MINUTES.toMillis(10)); //10 original
	}

	/**
	 * Activa un timer que dura n minutos, tiempo en el cual se puede abrir la fascia sin generar alarma.
	 * Si despues del tiempo establecido no se ha cerrado la fascia se dispara la alarma.
	 */
	public static void timerFascia() {

		logger.info("Timer Fascia inciando.");		
		fasciaTimer = new Timer();
		isFasciaTime = true;		

		fasciaTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Timer Fascia terminado.");
				System.out.println("Timer Fascia terminado.");
				isFasciaTime = false;
				fasciaTimer.cancel();
			}
		}, TimeUnit.MINUTES.toMillis(10));
	}	
	
	
	
	
	
	/**
	 * FLOW
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public Flow() throws IOException, TimeoutException {

		RaspiAgent.WriteToJournal("INIT", 0, 0, "", "", "INICIO FLUJO V 0.0.1", AccountType.None, TransactionType.Administrative);
		RaspiAgent.Broadcast(DeviceEvent.DEVICEBUS_Version, "0.0.0.1");

		JcmGlobalData.PreloadConfigVariables();

		initializeJcms();		

		JcmMonitor t2 = new JcmMonitor();
		t2.start();

		
		
		Thread agentsQueueThread = new Thread(agentsQueue, "agentsQueueThread");
		agentsQueueThread.start();

		Thread cmQueueThread = new Thread(cmQueue, "cmQueueThread");
		cmQueueThread.start();

		dtaServer.SetupRabbitListener();		

		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();

		logger.info("JCM1 INHIBIT DESHABILITAMOS ACEPTADOR");
		jcms[0].jcmMessage[3] = 0x01;
		jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);

		logger.info("JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
		jcms[1].jcmMessage[3] = 0x01;
		jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);

		loadGuiElements();


		//Actualizamos los valores de reciclaje si es que no concuerdan con el config
		System.out.println("Seteando valores de cassetteros");

		JcmGlobalData.rec1bill1Denom = Integer.parseInt(Config.GetDirective("Jcm1Denom1", "20"));
		JcmGlobalData.rec1bill2Denom = Integer.parseInt(Config.GetDirective("Jcm1Denom2", "50"));
		JcmGlobalData.rec2bill1Denom = Integer.parseInt(Config.GetDirective("Jcm2Denom1", "100"));
		JcmGlobalData.rec2bill2Denom = Integer.parseInt(Config.GetDirective("Jcm2Denom2", "200"));

		Config.SetPersistence("Cassette1Value", Integer.toString(JcmGlobalData.rec1bill1Denom));
		Config.SetPersistence("Cassette2Value", Integer.toString(JcmGlobalData.rec1bill2Denom));
		Config.SetPersistence("Cassette3Value", Integer.toString(JcmGlobalData.rec2bill1Denom));
		Config.SetPersistence("Cassette4Value", Integer.toString(JcmGlobalData.rec2bill2Denom));

		
		CurrentUser.currentOperation = jcmOperation.PreIdle;
		
		Flow.jcms[0].jcmMessage[7] = 0x01; 
		Flow.jcms[0].jcmMessage[8] =  (byte) denomToByte(JcmGlobalData.rec1bill2Denom);  
		Flow.jcms[0].jcmMessage[9] = 0x00;
		Flow.jcms[0].jcmMessage[10] = 0x02;												  
		Flow.jcms[0].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, (byte)  denomToByte(JcmGlobalData.rec1bill1Denom), (byte) 0x0,Flow.jcms[0].jcmMessage);


		Flow.jcms[1].jcmMessage[7] = 0x01;  
		Flow.jcms[1].jcmMessage[8] =  (byte) denomToByte(JcmGlobalData.rec2bill2Denom);
		Flow.jcms[1].jcmMessage[9] = 0x00;
		Flow.jcms[1].jcmMessage[10] = 0x02;												  
		Flow.jcms[1].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, (byte) denomToByte(JcmGlobalData.rec2bill1Denom), (byte) 0x0,Flow.jcms[1].jcmMessage);

		/*
		//Revisamos las denimincaciones y cantidad de dinero que tiene la cajita
		Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[0].jcmMessage);
		Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[1].jcmMessage);

		//Revisamos las denimincaciones y cantidad de dinero que tiene la cajita
		Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,Flow.jcms[0].jcmMessage);
		Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,Flow.jcms[1].jcmMessage);
		*/

		Config.SetPersistence("BoardStatus", "Available");

		
	
		//*
		if(NetUtils.netIsAvailable()) {
			redirect(panelIdle);			
		}
		else {
			redirect(panelErrorComunicate);			
		}
		//*/

	}

	private byte denomToByte(int denom) {

		switch(denom) {
		case 20:
			//System.out.println("demon [" + denom + "]");
			return Billete.$20;			
		case 50:
			//System.out.println("demon [" + denom + "]");
			return Billete.$50;			
		case 100:
			//System.out.println("demon [" + denom + "]");
			return Billete.$100;			
		case 200:
			//System.out.println("demon [" + denom + "]");
			return Billete.$200;			
		case 500:
			//System.out.println("demon [" + denom + "]");
			return Billete.$500;			
		}

		return Billete.$500;
	}


	/**
	 * Crea los objetos del gui y muestra la pantalla de splash en lo que se carga todo lo demas.
	 */
	private static void initialize() {

		logger.info("INITIALIZE...");


		panelContainer.setAlignmentY(Component.TOP_ALIGNMENT);
		panelContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContainer.setBackground(Color.BLUE);
		panelContainer.setBounds(0, 0, 1920, 1080);

		mainFrame = new JFrame("Frame Principal");
		mainFrame.getContentPane().setBackground(Color.GREEN);
		mainFrame.setBounds(100, 100, 1920, 1084);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(panelContainer);
		
		if(!JcmGlobalData.isDebug)
			mainFrame.setUndecorated(true);  //Con esto ya no tiene frame de ventanita

		panelContainer.setLayout(cl);

		panelSplash = new PanelSplash("./images/SCR_P7Admin_Espera.png","panelSplash",0,null);

		panelContainer.add(panelSplash,"panelSplash");

		Flow.cl.show(panelContainer, panelSplash);	

		mainFrame.setVisible(true);


	}

	/**
	 * Carga todas las pantallas del Flujo
	 */
	private void loadGuiElements() {

		logger.debug("loadGuiElements");


		panelIdle = new PanelIdle("./images/Scr7Inicio.png","panelIdle",0,null);
		panelMenu = new PanelMenu("./images/Scr7SinRetiroAutorizado.png","panelMenu",0,null);
		panelMenuSinFondo = new PanelMenuSinFondo("./images/Scr7HolaDepositamos.png","panelMenuSinFondo",0,null);
		panelDeposito = new PanelDeposito("./images/Scr7DepositoIndicadores.png","panelDeposito",0,null);			
		panelDebug = new PanelDebug(bgPlaceHolder,"panelDebug",0,null);
		panelLogin = new PanelLogin("./images/Scr7IdentificateDeposito.png","panelLogin",0,null);
		panelToken = new PanelToken("./images/Scr7ConfirmaToken.png","panelToken",0,null);
		panelTerminamos = new PanelTerminamos("./images/Scr7Terminamos.png","panelTerminamos",5000,Flow.panelIdle);
		panelDispense = new PanelDispense("./images/Scr7TomaBilletes.png","panelRetiroParcial",0,null);  //Scr7RetiroParcial  Scr7TomaBilletes
		panelError = new PanelError(bgPlaceHolder,"panelError",5000,Flow.panelIdle);		
		panelOperacionCancelada = new PanelOperacionCancelada("./images/Scr7OperacionCancelada.png","panelOperacionCancelada",TimeUnit.SECONDS.toMillis(3),Flow.panelIdle);		
		panelNoTicket = new PanelNoTicket("./images/Scr7NoTicket.png","panelNoTicket",0,Flow.panelTerminamos);
		panelReinicio  = new PanelReinicio(bgPlaceHolder,"panelReinicio",0,null);
		panelOos = new PanelOos("./images/Scr7FueraDeServicio.png","panelOos",0,null);
		panelErrorComunicate = new PanelErrorComunicate("./images/Scr7SinConexion.png","panelErrorComunicate",0,null);

		//FLUJO ADMINISTRATIVO

		panelAdminLogin = new PanelAdminLogin(bgAdminUsuario,"panelAdminLogin",25000,Flow.panelTerminamos); 
		panelAdminMenu = new PanelAdminMenu("./images/SCR_P7Admin_MenuAdmin.png","panelAdminMenu",0,null);
		panelAdminContadoresActuales = new PanelAdminContadoresActuales("./images/SCR_P7Admin_ContadoresActuales.png","panelAdminContadoresActuales",10000,Flow.panelTerminamos);
		panelAdminContadoresEnCero = new PanelAdminContadoresEnCero("./images/SCR_P7Admin_ContadoresActuales.png","panelAdminContadoresEnCero",10000,Flow.panelTerminamos);
		panelAdminDotarCancelar = new PanelAdminDotarCancelar("./images/SCR_P7Admin_ValidarCancelacion.png","panelAdminDotarCancelar",0,null);
		panelAdminDotarResultados = new PanelAdminDotarResultados("./images/SCR_P7Admin_OkRegistro.png","panelAdminDotarResultados",0,null);
		panelAdminError = new PanelAdminError(bgPlaceHolder,"panelAdminError",0,null);
		panelAdminEstatusDispositivos = new PanelAdminEstatusDispositivos("./images/SCR_P7Admin_EstatusDispositivos.png","panelAdminEstatusDispositivos",0,null);
		panelAdminUsuarioInvalido = new PanelAdminUsuarioInvalido("./images/SCR_P7Admin_UsuarioInvalido.png","panelAdminUsuarioInvalido",5000,Flow.panelAdminLogin);
		panelAdminResetDispositivos = new PanelAdminResetDispositivos(bgPlaceHolder,"panelAdminResetDispositivos",TimeUnit.SECONDS.toMillis(5),Flow.panelAdminEstatusDispositivos);
		panelAdminDetalleError = new PanelAdminDetalleError(bgPlaceHolder,"panelAdminDetalleError",TimeUnit.SECONDS.toMillis(5),Flow.panelAdminLogin);
		panelAdminPruebaImpresion = new PanelAdminPruebaImpresion("./images/SCR_P7Admin_PruebaImpresion.png","panelAdminPruebaImpresion",0,null);
		panelAdminLoginTienda = new PanelAdminLoginTienda(bgAdminLoginTienda,"panelAdminLoginTienda",0,null);
		panelAdminIniciando = new PanelAdminIniciando("./images/SCR_P7Admin_Iniciando.png","panelAdminIniciando",TimeUnit.SECONDS.toMillis(3),Flow.panelAdminLoginTienda);
		
		//Valores Iniciales
		PanelIdle.lblAtmId.setText(JcmGlobalData.atmId);

		panelContainer.add(panelIdle,"panelIdle");
		panelContainer.add(panelMenu,"panelMenu");
		panelContainer.add(panelDeposito,"panelDeposito");
		panelContainer.add(panelDebug, "panelDebug");
		panelContainer.add(panelLogin, "panelLogin");
		panelContainer.add(panelToken,"panelToken");
		panelContainer.add(panelTerminamos,"panelTerminamos");
		panelContainer.add(panelDispense,"panelRetiroParcial");
		panelContainer.add(panelError,"panelError");
		panelContainer.add(panelOperacionCancelada,"panelOperacionCancelada");		
		panelContainer.add(panelNoTicket,"panelNoTicket");		
		panelContainer.add(panelMenuSinFondo,"panelMenuSinFondo");
		panelContainer.add(panelReinicio,"panelReinicio");
		panelContainer.add(panelOos,"panelOos");		
		panelContainer.add(panelErrorComunicate,"panelErrorComunicate");

		//FLUJO ADMINISTRATIVO
		panelContainer.add(panelAdminLogin,"panelAdminLogin");		
		panelContainer.add(panelAdminMenu,"panelAdminMenu");
		panelContainer.add(panelAdminContadoresActuales,"panelAdminContadoresActuales");
		panelContainer.add(panelAdminContadoresEnCero,"panelAdminContadoresEnCero");		
		panelContainer.add(panelAdminDotarCancelar,"panelAdminDotarCancelar");
		panelContainer.add(panelAdminDotarResultados,"panelAdminDotarResultados");
		panelContainer.add(panelAdminError,"panelAdminError");
		panelContainer.add(panelAdminEstatusDispositivos,"panelAdminEstatusDispositivos");
		panelContainer.add(panelAdminUsuarioInvalido,"panelAdminUsuarioInvalido");
		panelContainer.add(panelAdminResetDispositivos,"panelAdminResetDispositivos");
		panelContainer.add(panelAdminIniciando,"panelAdminIniciando");
		panelContainer.add(panelAdminDetalleError,"panelAdminDetalleError");
		panelContainer.add(panelAdminPruebaImpresion,"panelAdminPruebaImpresion");
		panelContainer.add(panelAdminLoginTienda,"panelAdminLoginTienda");

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
					int billType = jcms[0].currentInsertedBill;
					boolean isRecyclable1 = jcms[0].recycleCurrentInsertedBill;

					switch(billType)
					{
					case 20:						
						jcm1LastBillInserted = 20;
						depositBillsCounter.x20++;
						if(isRecyclable1) {													
							jcms[0].cassettes.get(20).Available++;
						}
						break;
					case 50:
						jcm1LastBillInserted = 50;
						depositBillsCounter.x50++;
						if(isRecyclable1) {
							jcms[0].cassettes.get(50).Available++;
						}
						break;
					case 100:
						jcm1LastBillInserted = 100;
						depositBillsCounter.x100++;
						if(isRecyclable1) {
							jcms[0].cassettes.get(100).Available++;
						}
						break;
					case 200:
						jcm1LastBillInserted = 200;
						depositBillsCounter.x200++;
						if(isRecyclable1) {
							jcms[0].cassettes.get(200).Available++;
						}
						break;
					case 500:
						jcm1LastBillInserted = 500;
						depositBillsCounter.x500++;
						if(isRecyclable1) {
							jcms[0].cassettes.get(500).Available++;
						}
						break;
					}

					if(isRecyclable1) {
						JcmGlobalData.totalCashInRecyclers += jcm1LastBillInserted;	
						JcmGlobalData.totalCashInRecycler1 += jcm1LastBillInserted;  //Dinero que se puede dispensar
					}

					CashInOpVO myObj = new CashInOpVO();
					myObj.atmId = atmId; 
					myObj.amount = (long) billType;
					myObj.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					myObj.operatorId = Integer.parseInt(CurrentUser.loginUser);
					myObj.notesDetails = "1x" + billType;

					CurrentUser.totalAmountInserted += billType;					

					//TODO: HEWEY, Cuando no hay red que se hace?
					if(JcmGlobalData.netIsAvailable)						
						Transactions.InsertaCashInOp(myObj);

					RaspiAgent.WriteToJournal("CASH MANAGEMENT", (double)billType,0, "",CurrentUser.loginUser, "PROCESADEPOSITO PreDeposito", AccountType.Administrative, TransactionType.ControlMessage);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, "1x" + billType);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInReceived, "" + billType);
					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted, "" + CurrentUser.totalAmountInserted);

					UpdateCountersDeposit(0, Integer.toString(jcm1LastBillInserted),isRecyclable1);

					System.out.println("$" + CurrentUser.totalAmountInserted);
					PanelDeposito.lblMontoDepositado.setText("$" + CurrentUser.totalAmountInserted);
					PanelDebug.lblBilleteIngresado1.setText("$" + billType);
					break;
				case "bill2":		
					int billType2 = jcms[1].currentInsertedBill;
					boolean isRecyclable2 = jcms[1].recycleCurrentInsertedBill;;

					switch(billType2)
					{
					case 20:
						jcm2LastBillInserted = 20;
						depositBillsCounter.x20++;
						if(isRecyclable2) {
							jcms[1].cassettes.get(20).Available++;
						}
						break;
					case 50:
						jcm2LastBillInserted = 50;
						depositBillsCounter.x50++;
						if(isRecyclable2) {
							jcms[1].cassettes.get(50).Available++;
						}
						break;
					case 100:
						jcm2LastBillInserted = 100;
						depositBillsCounter.x100++;
						if(isRecyclable2) {
							jcms[1].cassettes.get(100).Available++;
						}
						break;
					case 200:
						jcm2LastBillInserted = 200;
						depositBillsCounter.x200++;
						if(isRecyclable2) {
							jcms[1].cassettes.get(200).Available++;
						}
						break;
					case 500:
						jcm2LastBillInserted = 500;
						depositBillsCounter.x500++;
						if(isRecyclable2) {
							jcms[1].cassettes.get(500).Available++;
						}
						break;
					}


					if(isRecyclable2) {
						JcmGlobalData.totalCashInRecyclers += jcm2LastBillInserted;	
						JcmGlobalData.totalCashInRecycler2 += jcm2LastBillInserted;  //Dinero que se puede dispensar
					}

					CashInOpVO myObj2 = new CashInOpVO();
					myObj2.atmId = atmId; 
					myObj2.amount = (long) billType2;
					myObj2.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					myObj2.operatorId = Integer.parseInt(CurrentUser.loginUser);
					myObj2.notesDetails = "1x" + billType2;

					//TODO: HEWEY, Cuando no hay red que se hace?
					if(JcmGlobalData.netIsAvailable)
						Transactions.InsertaCashInOp(myObj2);

					RaspiAgent.WriteToJournal("CASH MANAGEMENT", (double)billType2,0, "",CurrentUser.loginUser, "PROCESADEPOSITO PreDeposito", AccountType.Administrative, TransactionType.ControlMessage);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, "1x" + billType2);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInReceived, "" + billType2);
					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted, "" + CurrentUser.totalAmountInserted);

					UpdateCountersDeposit(1, Integer.toString(jcm2LastBillInserted),isRecyclable2);

					CurrentUser.totalAmountInserted += billType2;					
					System.out.println("$" + CurrentUser.totalAmountInserted);
					PanelDeposito.lblMontoDepositado.setText("$" + CurrentUser.totalAmountInserted);
					PanelDebug.lblBilleteIngresado2.setText("$" + billType2);
					break;					
				case "clearbill1":
					PanelDeposito.bussy1 = false;
					PanelDebug.lblBilleteIngresado1.setText("");
					break;
				case "clearbill2":
					PanelDeposito.bussy2 = false;
					PanelDebug.lblBilleteIngresado2.setText("");
					break;		
				case "recyclerBills1":

					PanelDeposito.lblJCMIzq.setText("$" + jcms[0].recyclerDenom1 + " / $" + jcms[0].recyclerDenom2);

					PanelDebug.lblRecycler1.setText(jcms[0].recyclerDenom1 + " " + jcms[0].recyclerDenom2);

					PanelAdminEstatusDispositivos.lblJcm1Denom.setText("$" + jcms[0].recyclerDenom1 + ", " + "$" + jcms[0].recyclerDenom2);
					
					recyclerBills1Set = true;					
					if(recyclerBills2Set) {

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;

						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills1Set = false;
					}
					break;
				case "recyclerBills2":

					PanelDeposito.lblJCMDer.setText("$" + jcms[1].recyclerDenom1 + " / $" + jcms[1].recyclerDenom2);

					PanelDebug.lblRecycler2.setText(jcms[1].recyclerDenom1 + " " + jcms[1].recyclerDenom2);

					PanelAdminEstatusDispositivos.lblJcm2Denom.setText("$" + jcms[1].recyclerDenom1 + ", " + "$" + jcms[1].recyclerDenom2);					

					recyclerBills2Set = true;
					if(recyclerBills1Set) {				

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;

						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills2Set = false;
					}

					break;
				case "recyclerContadores1":
					PanelDebug.lblContadores1.setText(jcms[0].recyclerContadores);
					break;
				case "recyclerContadores2":
					PanelDebug.lblContadores2.setText(jcms[1].recyclerContadores);
					break;		
				case "dispensedCass11":					
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[0].billCounters.Cass1Denom);					
					JcmGlobalData.jcm1cass1Dispensed = true;
					break;
				case "dispensedCass21":
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[0].billCounters.Cass2Denom);					
					JcmGlobalData.jcm1cass2Dispensed = true;					
					break;
				case "dispensedCass12":
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[1].billCounters.Cass1Denom);					
					JcmGlobalData.jcm2cass1Dispensed = true;
					break;
				case "dispensedCass22":
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[1].billCounters.Cass2Denom);					
					JcmGlobalData.jcm2cass2Dispensed = true;
					break;					
				case "presentOk1":
					RaspiAgent.Broadcast(DeviceEvent.AFD_PresentOk, "JCM[1]");					
					break;
				case "presentOk2":
					RaspiAgent.Broadcast(DeviceEvent.AFD_PresentOk, "JCM[2]");
					break;
				case "mediaTaken11":
					UpdateCountersDispense(0, Integer.toString(jcms[0].billCounters.Cass1Denom));
					RaspiAgent.Broadcast(DeviceEvent.AFD_MediaTaken, "JCM[1]");
					break;
				case "mediaTaken21":
					UpdateCountersDispense(0, Integer.toString(jcms[0].billCounters.Cass2Denom));
					RaspiAgent.Broadcast(DeviceEvent.AFD_MediaTaken, "JCM[1]");
					break;
				case "mediaTaken12":
					UpdateCountersDispense(1, Integer.toString(jcms[1].billCounters.Cass1Denom));
					RaspiAgent.Broadcast(DeviceEvent.AFD_MediaTaken, "JCM[2]");
					break;
				case "mediaTaken22":
					UpdateCountersDispense(1, Integer.toString(jcms[1].billCounters.Cass2Denom));
					RaspiAgent.Broadcast(DeviceEvent.AFD_MediaTaken, "JCM[2]");
					break;
				case "widthdrawalRequest":
					RaspiAgent.Broadcast(DeviceEvent.DEVICEBUS_Information, "New widthdrawal request arrvied.");
					System.out.println("Hay mensaje de retiro.");
					break;
				case "SafeOpen":
					System.out.println("Safe Open");

					//Si se abre la boveda y esta en tiempo admin mandamos a adminlogin
					if(isAdminTime) {
						System.out.println("Boveda abierta autorizada");
						miTio.alarmOff(); //redundante paranoico
						adminTimer.cancel();
						//Bajamos el perno
						Flow.miTio.cierraElectroiman();
						//redirect(Flow.panelAdminIniciando);
					}						
					else {
						System.out.println("Boveda abierta NO autorizada activando alarma");
						miTio.alarmOn();
					}
					
					break;
				case "SafeClosed":
					System.out.println("Safe Closed");
					
					//Bajamos el perno
					Flow.miTio.cierraElectroiman();
					
					if(isAdminTime) {						
						isAdminTime = false;
						adminTimer.cancel();
					}
					break;
					
				case "CabinetOpen":
					System.out.println("Cabinet Open");

					/*
					//Si se abre la boveda y esta en tiempo admin mandamos a adminlogin
					if(isAdminTime) {
						System.out.println("Fascia abierta autorizada");
						miTio.alarmOff(); //redundante paranoico						
					}						
					else {
						System.out.println("Fascia abierta NO autorizada activando alarma");
						//miTio.alarmOn();
					}
					*/
					break;
				case "CabinetClosed":
					System.out.println("Cabinet Closed");
					//Si se abre la boveda y esta en tiempo admin mandamos a adminlogin
					if(isFasciaTime) {
						System.out.println("Fascia cerrada autorizada, desactivamos timer.");	
						isFasciaTime = false;
						fasciaTimer.cancel();
					}
					break;

				case "moneyIn1":
					//JCM 1  moneyIn
					System.out.println("moneyIn1"); 
					if(jcm1LastBillInserted != 0) {
						jcm1LastBillInsertedWorking = jcm1LastBillInserted;
						jcm1LastBillInserted = 0;						
					}

					break;
				case "moneyIn2":
					//JCM 2 moneyIn
					System.out.println("moneyIn2");
					if(jcm2LastBillInserted != 0) {
						jcm2LastBillInsertedWorking = jcm2LastBillInserted;
						jcm2LastBillInserted = 0;					
					}

					break;
				case "reboot":					
					System.out.println("reboot");					
					String command = "shutdown -r +1";
					Runtime runtime = Runtime.getRuntime();
					try {
						runtime.exec(command);
					} catch (IOException ex) {						
						ex.printStackTrace();
					}					
					break;
				case "dispenseERROR1":
					PanelDispense.dispenseError();
					break;
				case "dispenseERROR2":
					PanelDispense.dispenseError();
					break;
				case "accepting1":
					PanelDeposito.bussy1 = true;
					break;
				case "accepting2":
					PanelDeposito.bussy2 = true;
					break;
				case "collected11":
					UpdateCountersCollect(0,Integer.toString(JcmGlobalData.rec1bill1Denom));
					break;
				case "collected21":
					UpdateCountersCollect(0,Integer.toString(JcmGlobalData.rec1bill2Denom));
					break;
				case "collected12":
					UpdateCountersCollect(1,Integer.toString(JcmGlobalData.rec2bill1Denom));
					break;
				case "collected22":
					UpdateCountersCollect(1,Integer.toString(JcmGlobalData.rec2bill2Denom));
					break;
				case "flushing":
					System.out.println("Flushing, mandamos a fuera de linea un momento....");
					
					break;
				case "flushed":
					System.out.println("Flushed, regresamos a modo operacion");
					
					break;
				case "noteError11":
					System.out.println("Error en dispenado jcm1 denom1");
					UpdateCountersCollect(0,Integer.toString(JcmGlobalData.rec1bill1Denom));
					break;
				case "noteError12":
					System.out.println("Error en dispenado jcm1 denom2");
					UpdateCountersCollect(0,Integer.toString(JcmGlobalData.rec1bill2Denom));
					break;
				case "noteError21":
					System.out.println("Error en dispenado jcm2 denom1");
					UpdateCountersCollect(1,Integer.toString(JcmGlobalData.rec2bill1Denom));
					break;
				case "noteError22":
					System.out.println("Error en dispenado jcm2 denom2");
					UpdateCountersCollect(1,Integer.toString(JcmGlobalData.rec2bill2Denom));
					break;	
				}
			}
		});


	}


	/**
	 * 
	 * @param denom Es la denomonacion que vamos a tomar el cassette y hacer para hacer el update
	 * 
	 * Cassette[n]Original  = Los dotados mas los que se han metido
	 * Cassette[n]Dispensed = los dispensados
	 * Cassette[n]Total		= Original menos los dispensados
	 */
	private void UpdateCountersDispense(Integer jcm, String denom) {

		System.out.println("UpdateCountersDispense jcm[" + jcm + "] denom [" + denom + "]" );
		String cassette = JcmGlobalData.getKey(jcm, denom);

		int dispensedValue = Integer.parseInt(Config.GetPersistence("Cassette" + cassette + "Dispensed", "0"));
		int totalValue = Integer.parseInt(Config.GetPersistence("Cassette" + cassette + "Total", "0"));

		totalValue--;
		dispensedValue++;

		Config.SetPersistence("Cassette" + cassette + "Dispensed", Integer.toString(dispensedValue));
		Config.SetPersistence("Cassette" + cassette + "Total", Integer.toString(totalValue));			
	}


	private void UpdateCountersCollect(Integer jcm, String denom) {

		System.out.println("UpdateCountersCollect jcm[" + jcm + "] denom [" + denom + "]" );
		String cassette = JcmGlobalData.getKey(jcm, denom);


		int totalValue = Integer.parseInt(Config.GetPersistence("Cassette" + cassette + "Total", "0"));
		int originalValue = Integer.parseInt(Config.GetPersistence("Cassette" + cassette + "Original", "0"));
	
		totalValue--;		
		originalValue--;

		Config.SetPersistence("Cassette" + cassette + "Total", Integer.toString(totalValue));
		Config.SetPersistence("Cassette" + cassette + "Original", Integer.toString(originalValue));

		switch(cassette) {
		case "1":
			Flow.jcms[jcm].billCounters.Cass1Available = Flow.jcms[0].billCounters.Cass1Available - 1;
			if(Flow.jcms[jcm].billCounters.Cass1Available < 0)
				Flow.jcms[jcm].billCounters.Cass1Available = 0;
			break;
		case "2":
			Flow.jcms[jcm].billCounters.Cass2Available = Flow.jcms[0].billCounters.Cass2Available - 1;
			if(Flow.jcms[jcm].billCounters.Cass2Available < 0)
				Flow.jcms[jcm].billCounters.Cass2Available = 0;
			break;
		case "3":
			Flow.jcms[jcm].billCounters.Cass1Available = Flow.jcms[0].billCounters.Cass1Available - 1;
			if(Flow.jcms[jcm].billCounters.Cass1Available < 0)
				Flow.jcms[jcm].billCounters.Cass1Available = 0;
			break;
		case "4":
			Flow.jcms[jcm].billCounters.Cass2Available = Flow.jcms[0].billCounters.Cass2Available - 1;
			if(Flow.jcms[jcm].billCounters.Cass2Available < 0)
				Flow.jcms[jcm].billCounters.Cass2Available = 0;
			break;
		}
		
		UpdateCountersAcepted(denom);
	}



	/**
	 * 
	 * @param cassette El cassette que vamos a tomar para hacer el update
	 * 
	 * NOTA: Solo se debe usar si el billete se va a reciclado.
	 * Cassette[n]Original  = Los dotados mas los que se han metido
	 * Cassette[n]Dispensed = los dispensados
	 * Cassette[n]Total		= Original menos los dispensados
	 */
	private void UpdateCountersDeposit(Integer jcm, String denom, boolean isRecyclable) {

		String cassette = JcmGlobalData.getKey(jcm, denom);

		System.out.println("UpdateCountersDeposit jcm [" + jcm + "] denom [" + denom + "] cassete [" + cassette + "]");

		if(!isRecyclable) {
			//No esta en las denomonaciones lo mandamos a accepted
			System.out.println("No esta en las denomonaciones lo mandamos a accepted o es directo a AC");
			UpdateCountersAcepted(denom);
		}
		else {

			int totalValue = Integer.parseInt(Config.GetPersistence("Cassette" + cassette + "Total", "0"));
			int originalValue = Integer.parseInt(Config.GetPersistence("Cassette" + cassette + "Original", "0"));

			System.out.println("UpdateCountersDeposit PREVIO totalValue [" + totalValue + "] originalValue [" + originalValue + "]");

			totalValue++;
			originalValue++;

			System.out.println("UpdateCountersDeposit FINAL totalValue [" + totalValue + "] originalValue [" + originalValue + "]");

			Config.SetPersistence("Cassette" + cassette + "Total", Integer.toString(totalValue));
			Config.SetPersistence("Cassette" + cassette + "Original", Integer.toString(originalValue));
		}

	}




	/**
	 * 
	 * @param demon La denominacion que queremos aumentar de depositados
	 * 
	 * NOTA: Solo se debe usar si el billete se va a reciclado.
	 * Accepted[denom] cuantos de esa denominacion ha recibido	 
	 */
	private void UpdateCountersAcepted(String denom) {

		System.out.println("UpdateCountersAcepted denom [" + denom + "]");
		int accepted = Integer.parseInt(Config.GetPersistence("Accepted" + denom, "0"));
		accepted++;

		Config.SetPersistence("Accepted" + denom, Integer.toString(accepted));

	}


	private static void initializeJcms() {


		logger.info("Inicializando JCMS");

		//Identificamos los puertos disponibles
		uart.portList = CommPortIdentifier.getPortIdentifiers();
		int contador = 0;

		logger.info("Puertos COMM encontrados [" + contador  + "]");

		while (uart.portList.hasMoreElements()) {

			CommPortIdentifier commPort = (CommPortIdentifier) uart.portList.nextElement();

			logger.debug("COMM [" + commPort.getName() + "]");
			//Checamos que sea un com{x} port
			if (commPort.getName().toUpperCase().contains("COM")  || commPort.getName().toUpperCase().contains("TTYUSB") ) {

				logger.debug("Puerto [" + commPort.getName().toUpperCase() + "]");

				jcms[contador] = new uart(contador + 1);
				jcms[contador].portId = commPort;
				jcms[contador].baud = 9600;
				jcms[contador].id = contador + 1;
				contador++;
			}			
		}

		//Este es para debug en maquina local con loopback de puertos comm.
		if(contador == 0 && JcmGlobalData.isDebug) {
			jcms[0] = new uart(1);
			jcms[0].portId = null;
			jcms[0].baud = 9600;
			jcms[0].id = 1;

			jcms[1] = new uart(2);
			jcms[1].portId = null;
			jcms[1].baud = 9600;
			jcms[1].id = 2;

			jcms[0].currentOpertion = jcmOperation.Startup;
			jcms[0].openPort("COM3");

			jcms[1].currentOpertion = jcmOperation.Startup;
			jcms[1].openPort("COM4");
		}
		else {
			//Inicializamos los UARTS
			logger.debug("Inicializando UARTS");
			for(int i = 0; i < contador; i++) {
				jcms[i].currentOpertion = jcmOperation.Startup;
				jcms[i].openPort(jcms[i].portId.getName().toString());
			}
		}
	}




	/**
	 * 
	 * @param target  - A que pantalla se va a ir
	 * @param timeout - El timeout de la nueva pantalla
	 * @param timeoutTarget - A donde se va a ir la pantalla despues del time out
	 */
	public static void redirect(ImagePanel target, int timeout, ImagePanel timeoutTarget) {
		Flow.cl.show(panelContainer, target,timeout,timeoutTarget);	
	}


	public static void redirect(ImagePanel target, long timeout, ImagePanel timeoutTarget) {
		Flow.cl.show(panelContainer, target,timeout,timeoutTarget);	
	}

	public static void redirect(ImagePanel target) {
		Flow.cl.show(panelContainer, target);	
	}


	public static void actualizaContadoresRecicladores() {

		jcms[0].recyclerContadoresSet = false;
		jcms[1].recyclerContadoresSet = false;

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

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}

	}

}

