package pentomino.flow;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gnu.io.CommPortIdentifier;
import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.core.devices.Tio;
import pentomino.flow.gui.FlowLayout;
import pentomino.flow.gui.ImagePanel;
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
import pentomino.flow.gui.PanelTerminamos;
import pentomino.flow.gui.PanelToken;
import pentomino.flow.gui.admin.PanelAdminContadoresActuales;
import pentomino.flow.gui.admin.PanelAdminContadoresEnCero;
import pentomino.flow.gui.admin.PanelAdminDotarCancelar;
import pentomino.flow.gui.admin.PanelAdminDotarResultados;
import pentomino.flow.gui.admin.PanelAdminError;
import pentomino.flow.gui.admin.PanelAdminEstatusDispositivos;
import pentomino.flow.gui.admin.PanelAdminLogin;
import pentomino.flow.gui.admin.PanelAdminMenu;
import pentomino.flow.gui.admin.PanelAdminResetDispositivos;
import pentomino.flow.gui.admin.PanelAdminUsuarioInvalido;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;

public class Flow {

	public static EventListenerClass c;

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());

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

	public static JcmContadores depositBillsCounter = new JcmContadores();	

	private JFrame mainFrame;

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

	

	public static void main(String[] args) {

		logger.info("----- FLOW MAIN -----");

		JcmGlobalData.isDebug = System.getProperty("os.name").toLowerCase().contains("windows");
		System.out.println(System.getProperty("os.name") + " isDebug[" + JcmGlobalData.isDebug + "]");

		System.out.println("netIsAvailable [" + netIsAvailable() + "]");
		

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
	
	private static boolean netIsAvailable() {    
		System.out.println("netIsAvailable");
		try {			
			Socket socket2 = new Socket();
			socket2.connect(new InetSocketAddress("11.50.0.7", 5672), 5000);			
			socket2.close();
			System.out.println("netIsAvailable true");
			return true;
		} catch (UnknownHostException e) {		
		} catch (IOException e) {		
		}
		System.out.println("netIsAvailable false");
		return false;
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

		initializeJcms();

		GetCurrentCassettesConfig();

		JcmGlobalData.atmId = Config.GetDirective("AtmId", "-----");

		Config.SetPersistence("BoardStatus", "Available");

		initialize();

		//"Recycle Currency Req (+90h)";
		jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[0].jcmMessage);
		jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[1].jcmMessage);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//CURRENT COUNT REQUEST
		jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,Flow.jcms[0].jcmMessage);
		jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,Flow.jcms[1].jcmMessage);

		
		if(!netIsAvailable()) {
			redirect(panelErrorComunicate);
		}
		else {
			redirect(panelIdle);			
		}
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		System.out.println("INITIALIZE...");

		JcmGlobalData.setMaxRecyclableCash(Integer.parseInt(Config.GetDirective("maxRecyclableCash","0")));

		System.out.println("maxRecyclableCash [" + JcmGlobalData.getMaxRecyclableCash() + "]");

		panelContainer.setAlignmentY(Component.TOP_ALIGNMENT);
		panelContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContainer.setBackground(Color.LIGHT_GRAY);
		panelContainer.setBounds(0, 0, 1920, 1080);

		mainFrame = new JFrame("Frame Principal");
		mainFrame.getContentPane().setBackground(Color.GREEN);
		mainFrame.setBounds(100, 100, 1920, 1084);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(panelContainer);
		//mainFrame.setUndecorated(true);  //Con esto ya no tiene frame de ventanita

		panelIdle = new PanelIdle("./images/Scr7Inicio.png","panelIdle",0,null);
		panelMenu = new PanelMenu("./images/Scr7SinRetiroAutorizado.png","panelMenu",0,null);
		panelMenuSinFondo = new PanelMenuSinFondo("./images/Scr7HolaDepositamos.png","panelMenuSinFondo",0,null);
		panelDeposito = new PanelDeposito("./images/Scr7DepositoIndicadores.png","panelDeposito",0,null);			
		panelDebug = new PanelDebug("./images/Scr7Placeholder.png","panelDebug",0,null);
		panelLogin = new PanelLogin("./images/Scr7IdentificateDeposito.png","panelLogin",0,null);
		panelToken = new PanelToken("./images/Scr7ConfirmaToken.png","panelToken",0,null);
		panelTerminamos = new PanelTerminamos("./images/ScrTerminamos.png","panelTerminamos",5000,Flow.panelIdle);
		panelDispense = new PanelDispense("./images/Scr7TomaBilletes.png","panelRetiroParcial",0,null);  //Scr7RetiroParcial  Scr7TomaBilletes
		panelError = new PanelError("./images/Scr7Placeholder.png","panelError",5000,Flow.panelIdle);		
		panelOperacionCancelada = new PanelOperacionCancelada("./images/Scr7OperacionCancelada.png","panelOperacionCancelada",5000,Flow.panelIdle);		
		panelNoTicket = new PanelNoTicket("./images/Scr7NoTicket.png","panelNoTicket",0,Flow.panelTerminamos);
		panelReinicio  = new PanelReinicio("./images/Scr7Placeholder.png","panelReinicio",0,null);
		panelOos = new PanelOos("./images/Scr7FueraDeServicio.png","panelOos",0,null);
		panelErrorComunicate = new PanelErrorComunicate("./images/Scr7Error.png","panelErrorComunicate",0,null);

		//FLUJO ADMINISTRATIVO

		panelAdminLogin = new PanelAdminLogin("./images/SCR_P7Admin_Usuario.png","panelAdminLogin",25000,Flow.panelTerminamos); 
		panelAdminMenu = new PanelAdminMenu("./images/SCR_P7Admin_MenuAdmin.png","panelAdminMenu",25000,Flow.panelTerminamos);
		panelAdminContadoresActuales = new PanelAdminContadoresActuales("./images/SCR_P7Admin_ContadoresActuales.png","panelAdminContadoresActuales",10000,Flow.panelTerminamos);
		panelAdminContadoresEnCero = new PanelAdminContadoresEnCero("./images/SCR_P7Admin_ContadoresActuales.png","panelAdminContadoresEnCero",10000,Flow.panelTerminamos);
		panelAdminDotarCancelar = new PanelAdminDotarCancelar("./images/Scr7Placeholder.png","panelAdminDotarCancelar",0,null);
		panelAdminDotarResultados = new PanelAdminDotarResultados("./images/Scr7Placeholder.png","panelAdminDotarResultados",0,null);
		panelAdminError = new PanelAdminError("./images/Scr7Placeholder.png","panelAdminError",0,null);
		panelAdminEstatusDispositivos = new PanelAdminEstatusDispositivos("./images/SCR_P7Admin_EstatusDispositivos.png","panelAdminEstatusDispositivos",0,null);
		panelAdminUsuarioInvalido = new PanelAdminUsuarioInvalido("./images/SCR_P7Admin_UsuarioInvalido.png","panelAdminUsuarioInvalido",5000,Flow.panelAdminLogin);
		panelAdminResetDispositivos = new PanelAdminResetDispositivos("./images/Scr7Placeholder.png","panelAdminResetDispositivos",5000,Flow.panelAdminEstatusDispositivos);
		
		
		//Valores Iniciales
		PanelIdle.lblAtmId.setText(JcmGlobalData.atmId);


		panelContainer.setLayout(cl);		

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
					PanelDebug.lblBilleteIngresado1.setText("");
					break;
				case "clearbill2":
					PanelDebug.lblBilleteIngresado2.setText("");
					break;		
				case "recyclerBills1":

					PanelDeposito.lblJCMDer.setText(jcms[0].recyclerDenom1 + " / " + jcms[0].recyclerDenom2);
					
					PanelDebug.lblRecycler1.setText(jcms[0].recyclerDenom1 + " " + jcms[0].recyclerDenom2);

					PanelAdminEstatusDispositivos.lblJcm1Denom1.setText(jcms[0].recyclerDenom1);
					PanelAdminEstatusDispositivos.lblJcm1Denom2.setText(jcms[0].recyclerDenom2);

					recyclerBills1Set = true;					
					if(recyclerBills2Set) {

						String broadcastData = "Cassette1-" + jcms[0].recyclerDenom1 + ";Cassette2-" + jcms[0].recyclerDenom2
								+ ";Cassette3-" + jcms[1].recyclerDenom1 + ";Cassette4-" + jcms[1].recyclerDenom2;

						RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, broadcastData);

						recyclerBills1Set = false;
					}
					break;
				case "recyclerBills2":

					PanelDeposito.lblJCMIzq.setText(jcms[1].recyclerDenom1 + " / " + jcms[1].recyclerDenom2);
					
					PanelDebug.lblRecycler2.setText(jcms[1].recyclerDenom1 + " " + jcms[1].recyclerDenom2);

					PanelAdminEstatusDispositivos.lblJcm2Denom1.setText(jcms[1].recyclerDenom1);
					PanelAdminEstatusDispositivos.lblJcm2Denom2.setText(jcms[1].recyclerDenom2);

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
					System.out.println("Hay mensaje de retiro papawh");
					break;
				case "SafeOpen":
					System.out.println("Safe Open");
					redirect(Flow.panelAdminLogin,15000,Flow.panelIdle);	
					break;

				case "moneyIn1":
					//JCM 1  moneyIn
					System.out.println("moneyIn1"); 
					if(jcm1LastBillInserted != 0) {
						jcm1LastBillInsertedWorking = jcm1LastBillInserted;
						jcm1LastBillInserted = 0;
						//UpdateCountersDeposit(0, Integer.toString(jcm1LastBillInsertedWorking));
					}

					break;
				case "moneyIn2":
					//JCM 2
					System.out.println("moneyIn2");
					if(jcm2LastBillInserted != 0) {
						jcm2LastBillInsertedWorking = jcm2LastBillInserted;
						jcm2LastBillInserted = 0;
						//UpdateCountersDeposit(1, Integer.toString(jcm2LastBillInsertedWorking));
					}

					break;
				case "reboot":					
					System.out.println("reboot");					
					String command = "shutdown -r +1";
					Runtime runtime = Runtime.getRuntime();
					try {
						runtime.exec(command);
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}					
					break;
				case "dispenseERROR1":
					PanelDispense.dispenseError();
					break;
				case "dispenseERROR2":
					PanelDispense.dispenseError();
					break;

				}
			}
		});

		//initializeJcms();


		System.out.println("JCM1 INHIBIT DESHABILITAMOS ACEPTADOR");
		jcms[0].jcmMessage[3] = 0x01;
		jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);

		System.out.println("JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
		jcms[1].jcmMessage[3] = 0x01;
		jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);

		//cl.show(panelContainer, "panelIdle");

		//cl.show(panelContainer, panelDeposito);

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

		System.out.println("UpdateCountersDeposit jcm [" + jcm + "] denom [" + denom + "]");

		String cassette = JcmGlobalData.getKey(jcm, denom);
		
		System.out.println("cassete [" + cassette + "]");

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

	/**
	 * Regresa la denominancion de los 4 cessetteros para saber cual hay que buscar al hacer el dispense o deposit.
	 */
	private void GetCurrentCassettesConfig() {	

		JcmGlobalData.jcm1cassetteDataValues = new HashMap<String, String>();
		JcmGlobalData.jcm2cassetteDataValues = new HashMap<String, String>();

		JcmGlobalData.jcm1cassetteDataValues.put("1", Config.GetPersistence("Cassette1Value", "0"));
		JcmGlobalData.jcm1cassetteDataValues.put("2", Config.GetPersistence("Cassette2Value", "0"));

		JcmGlobalData.jcm2cassetteDataValues.put("3", Config.GetPersistence("Cassette3Value", "0"));
		JcmGlobalData.jcm2cassetteDataValues.put("4", Config.GetPersistence("Cassette4Value", "0"));

	}



	private static void initializeJcms() {


		//Identificamos los puertos disponibles
		uart.portList = CommPortIdentifier.getPortIdentifiers();
		int contador = 0;

		System.out.println("COMM contador " + contador);

		while (uart.portList.hasMoreElements()) {

			CommPortIdentifier commPort = (CommPortIdentifier) uart.portList.nextElement();

			System.out.println("COMM " + commPort.getName());
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

			jcms[0].currentOpertion = jcmOperation.Startup;
			jcms[0].openPort("COM3");

			jcms[1].currentOpertion = jcmOperation.Startup;
			jcms[1].openPort("COM4");
		}
		else {
			//Inicializamos los UARTS
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

}

