package pentomino.flow;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
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
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.core.devices.Tio;
import pentomino.flow.gui.FlowLayout;
import pentomino.flow.gui.ImagePanel;
import pentomino.flow.gui.ImagePanelOld;
import pentomino.flow.gui.PanelDebug;
import pentomino.flow.gui.PanelDeposito;
import pentomino.flow.gui.PanelDispense;
import pentomino.flow.gui.PanelError;
import pentomino.flow.gui.PanelIdle;
import pentomino.flow.gui.PanelLogin;
import pentomino.flow.gui.PanelMenu;
import pentomino.flow.gui.PanelNoTicket;
import pentomino.flow.gui.PanelOperacionCancelada;
import pentomino.flow.gui.PanelToken;
import pentomino.flow.gui.PinpadListener;
import pentomino.flow.gui.admin.PanelAdminContadoresActuales;
import pentomino.flow.gui.admin.PanelAdminContadoresEnCero;
import pentomino.flow.gui.admin.PanelAdminDotarCancelar;
import pentomino.flow.gui.admin.PanelAdminDotarResultados;
import pentomino.flow.gui.admin.PanelAdminError;
import pentomino.flow.gui.admin.PanelAdminEstatusDispositivos;
import pentomino.flow.gui.admin.PanelAdminLogin;
import pentomino.flow.gui.admin.PanelAdminMenu;
import pentomino.jcmagent.AgentsQueue;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;

public class Flow {

	public static EventListenerClass c;

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());

	public static ImagePanelOld panelTerminamosHolder;
	public static ImagePanelOld panelOperacionCanceladaHolder;
	public static ImagePanelOld panelErrorHolder; 
	public static ImagePanelOld panelLoginHolder;
	public static ImagePanelOld panelTokenHolder;
	public static ImagePanelOld panelIdleHolder;
	public static ImagePanelOld panelMenuHolder;
	public static ImagePanelOld panelDepositoHolder;
	public static ImagePanelOld panelComandosHolder;
	public static ImagePanelOld panelDispenseHolder;
	public static ImagePanelOld panelNoTicketHolder;

	//FLUJO ADMNISTRATIVO
	public static ImagePanel panelAdminLogin;	
	public static ImagePanel panelAdminDotarCancelar;
	public static ImagePanel panelAdminDotarResultados;
	public static ImagePanel panelAdminError;	
	public static ImagePanel panelAdminMenu;
	public static ImagePanel panelAdminContadoresActuales;
	public static ImagePanel panelAdminContadoresEnCero;
	public static ImagePanel panelAdminEstatusDispositivos;

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

		//Seteamos algunas cosas de origen
		if(JcmGlobalData.isDebug) {

			initializeJcms();

			jcms[0].recyclerDenom1 = "20";
			jcms[0].recyclerDenom2 = "50";
			jcms[1].recyclerDenom1 = "100";
			jcms[1].recyclerDenom2 = "200";

		}

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

		GetCurrentCassettesConfig();



		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		System.out.println("INITIALIZE...");

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


		panelIdleHolder = new ImagePanelOld(new ImageIcon("./images/Scr7Inicio.png").getImage(),"panelIdle");
		PanelIdle panelIdle = new PanelIdle();
		panelIdleHolder.add(panelIdle.getPanel());

		panelMenuHolder = new ImagePanelOld(new ImageIcon("./images/Scr7SinRetiroAutorizado.png").getImage(),"panelMenu");
		PanelMenu panelMenu = new PanelMenu();
		panelMenuHolder.add(panelMenu.getPanel());

		panelDepositoHolder = new ImagePanelOld(new ImageIcon("./images/Scr7MontoIngresado.png").getImage(),"panelDeposito");
		PanelDeposito panelDeposito = new PanelDeposito();
		panelDepositoHolder.add(panelDeposito.getPanel());

		panelComandosHolder = new ImagePanelOld(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelComandos");
		PanelDebug panelComandos = new PanelDebug();
		panelComandosHolder.add(panelComandos.getPanel());		

		panelLoginHolder = new ImagePanelOld(new ImageIcon("./images/Scr7IdentificateDeposito.png").getImage(),"panelLogin");
		PinpadListener panelLogin = new PanelLogin();
		panelLoginHolder.add(panelLogin.getPanel());

		panelTokenHolder = new ImagePanelOld(new ImageIcon("./images/Scr7ConfirmaToken.png").getImage(),"panelToken");
		PanelToken panelToken = new PanelToken();
		panelTokenHolder.add(panelToken.getPanel());

		panelTerminamosHolder = new ImagePanelOld(new ImageIcon("./images/ScrTerminamos.png").getImage(),"panelTerminamos");

		panelDispenseHolder = new ImagePanelOld(new ImageIcon("./images/ScrRetiraBilletes.png").getImage(),"panelRetiroParcial");
		PanelDispense panelDispense = new PanelDispense();
		panelDispenseHolder.add(panelDispense.getPanel());

		panelErrorHolder = new ImagePanelOld(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelError",5000,"panelIdle");
		PanelError panelError = new PanelError();
		panelErrorHolder.add(panelError.getPanel());

		panelOperacionCanceladaHolder = new ImagePanelOld(new ImageIcon("./images/Scr7OperacionCancelada.png").getImage(),"panelOperacionCancelada",5000,"panelIdle");
		PanelOperacionCancelada panelOperacionCancelada = new PanelOperacionCancelada();
		panelOperacionCanceladaHolder.add(panelOperacionCancelada.getPanel());

		panelNoTicketHolder = new ImagePanelOld(new ImageIcon("./images/Scr7NoTicket.png").getImage(),"panelNoTicket",5000,"panelTerminamos");
		PanelNoTicket panelPanelNoTicket = new PanelNoTicket();
		panelNoTicketHolder.add(panelPanelNoTicket.getPanel());



		panelAdminLogin = new PanelAdminLogin(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminLogin",25000,"panelTerminamos"); 
		panelAdminMenu = new PanelAdminMenu(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminMenu",25000,"panelTerminamos");
		panelAdminContadoresActuales = new PanelAdminContadoresActuales(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminContadoresActuales",10000,"panelTerminamos");
		panelAdminContadoresEnCero = new PanelAdminContadoresEnCero(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminContadoresEnCero",10000,"panelTerminamos");
		panelAdminDotarCancelar = new PanelAdminDotarCancelar(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminDotarCancelar");
		panelAdminDotarResultados = new PanelAdminDotarResultados(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminDotarResultados");
		panelAdminError = new PanelAdminError(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminError");
		panelAdminEstatusDispositivos = new PanelAdminEstatusDispositivos(new ImageIcon("./images/Scr7Placeholder.png").getImage(),"panelAdminEstatusDispositivos");


		panelContainer.setLayout(cl);		
		panelContainer.add(panelIdleHolder,"panelIdle");
		panelContainer.add(panelMenuHolder,"panelMenu");
		panelContainer.add(panelDepositoHolder,"panelDeposito");
		panelContainer.add(panelComandosHolder, "panelComandos");
		panelContainer.add(panelLoginHolder, "panelLogin");
		panelContainer.add(panelTokenHolder,"panelToken");
		panelContainer.add(panelTerminamosHolder,"panelTerminamos");
		panelContainer.add(panelDispenseHolder,"panelRetiroParcial");
		panelContainer.add(panelErrorHolder,"panelError");
		panelContainer.add(panelOperacionCanceladaHolder,"panelOperacionCancelada");		
		panelContainer.add(panelNoTicketHolder,"panelNoTicket");

		//FLUJO ADMINISTRATIVO
		panelContainer.add(panelAdminLogin,"panelAdminLogin");		
		panelContainer.add(panelAdminMenu,"panelAdminMenu");
		panelContainer.add(panelAdminContadoresActuales,"panelAdminContadoresActuales");
		panelContainer.add(panelAdminContadoresEnCero,"panelAdminContadoresEnCero");		
		panelContainer.add(panelAdminDotarCancelar,"panelAdminDotarCancelar");
		panelContainer.add(panelAdminDotarResultados,"panelAdminDotarResultados");
		panelContainer.add(panelAdminError,"panelAdminError");
		panelContainer.add(panelAdminEstatusDispositivos,"panelAdminEstatusDispositivos");

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
						depositBillsCounter.x20++;
						jcm1LastBillInserted = 20;
						jcms[0].cassettes.get(20).Available++;
						break;
					case 50:
						jcms[0].cassettes.get(50).Available++;
						jcm1LastBillInserted = 50;
						depositBillsCounter.x50++;
						break;
					case 100:
						jcms[0].cassettes.get(100).Available++;
						jcm1LastBillInserted = 100;
						depositBillsCounter.x100++;
						break;
					case 200:
						jcms[0].cassettes.get(200).Available++;
						jcm1LastBillInserted = 200;
						depositBillsCounter.x200++;
						break;
					case 500:
						jcms[0].cassettes.get(500).Available++;
						jcm1LastBillInserted = 500;
						depositBillsCounter.x500++;
						break;
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

					System.out.println("$" + CurrentUser.totalAmountInserted);
					PanelDeposito.lblMontoDepositado.setText("$" + CurrentUser.totalAmountInserted);
					panelComandos.lblBilleteIngresado1.setText("$" + billType);
					break;
				case "bill2":		
					int billType2 = jcms[1].bill;
					switch(billType2)
					{
					case 20:
						jcms[1].cassettes.get(20).Available++;
						jcm2LastBillInserted = 20;
						depositBillsCounter.x20++;
						break;
					case 50:
						jcms[1].cassettes.get(50).Available++;
						jcm2LastBillInserted = 50;
						depositBillsCounter.x50++;
						break;
					case 100:
						jcms[1].cassettes.get(100).Available++;
						jcm2LastBillInserted = 100;
						depositBillsCounter.x100++;
						break;
					case 200:
						jcms[1].cassettes.get(200).Available++;
						jcm2LastBillInserted = 200;
						depositBillsCounter.x200++;
						break;
					case 500:
						jcms[1].cassettes.get(500).Available++;
						jcm2LastBillInserted = 500;
						depositBillsCounter.x500++;
						break;
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

					CurrentUser.totalAmountInserted += billType2;					
					System.out.println("$" + CurrentUser.totalAmountInserted);
					PanelDeposito.lblMontoDepositado.setText("$" + CurrentUser.totalAmountInserted);
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
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[0].billCounters.Cass1Denom);
					UpdateCountersDispense(0, Integer.toString(jcms[0].billCounters.Cass1Denom));
					JcmGlobalData.jcm1cass1Dispensed = true;
					break;
				case "dispensedCass21":
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[1].billCounters.Cass1Denom);
					UpdateCountersDispense(0, Integer.toString(jcms[0].billCounters.Cass2Denom));
					JcmGlobalData.jcm1cass2Dispensed = true;					
					break;
				case "dispensedCass12":
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[0].billCounters.Cass2Denom);
					UpdateCountersDispense(1, Integer.toString(jcms[1].billCounters.Cass1Denom));
					JcmGlobalData.jcm2cass1Dispensed = true;
					break;
				case "dispensedCass22":
					RaspiAgent.Broadcast(DeviceEvent.AFD_SubdispenseOk, "" + jcms[1].billCounters.Cass2Denom);
					UpdateCountersDispense(1, Integer.toString(jcms[1].billCounters.Cass2Denom));
					JcmGlobalData.jcm2cass2Dispensed = true;
					break;					
				case "presentOk1":
					RaspiAgent.Broadcast(DeviceEvent.AFD_PresentOk, "JCM[1]");
					//TODO AQUI: UpdateCountersDispense(jcms[0].recyclerDenom1);
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
				case "SafeOpen":
					System.out.println("Safe Open");
					redirect(Flow.panelAdminLogin,15000,"panelIdle");	
					break;
				
				case "moneyIn1":
					//JCM 1  moneyIn
					System.out.println("moneyIn1"); 
					if(jcm1LastBillInserted != 0) {
						jcm1LastBillInsertedWorking = jcm1LastBillInserted;
						jcm1LastBillInserted = 0;
						UpdateCountersDeposit(0, Integer.toString(jcm1LastBillInsertedWorking));
					}
					
					break;
				case "moneyIn2":
					//JCM 2
					System.out.println("moneyIn2");
					if(jcm2LastBillInserted != 0) {
						jcm2LastBillInsertedWorking = jcm2LastBillInserted;
						jcm2LastBillInserted = 0;
						UpdateCountersDeposit(1, Integer.toString(jcm2LastBillInsertedWorking));
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
				}
			}
		});

		initializeJcms();

		cl.show(panelContainer, "panelIdle");

		//cl.show(panelContainer, "panelAdminEstatusDispositivos");

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
	private void UpdateCountersDeposit(Integer jcm, String denom) {

		System.out.println("UpdateCountersDeposit jcm [" + jcm + "] denom [" + denom + "]");

		String cassette = JcmGlobalData.getKey(jcm, denom);

		if(cassette.isEmpty()) {
			//No esta en las denomonaciones lo mandamos a accepted
			System.out.println("No esta en las denomonaciones lo mandamos a accepted");
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


		//Accepted20				
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

	public static void redirect(ImagePanelOld target, int timeout, String timeoutTarget) {	
		Flow.cl.show(panelContainer, target,timeout,timeoutTarget);	
	}

	public static void redirect(JPanel target, int timeout, String timeoutTarget) {	
		Flow.cl.show(panelContainer, target,timeout,timeoutTarget);	
	}

	public static void redirect(String target) {		
		Flow.cl.show(panelContainer, target);	
	}

	public static void redirect(ImagePanelOld target) {		
		Flow.cl.show(panelContainer, target);
		target.screenTimerCancel();	
	}
	
	public static void redirect(JPanel target) {		
		Flow.cl.show(panelContainer, target);
		//target.screenTimerCancel();	
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
		}	
	}

}

