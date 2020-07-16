package pentomino.core.devices;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.util.JposPropertiesConst;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.jcmagent.RaspiAgent;




public class Ptr {

private static boolean printing = false;

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());

	private static final String FIFO = "jcmprinter";

	private static CupsClient cupsClient;
	private static CupsPrinter cupsPrinter;
	
	private static int spoolCount = 0;
	
	/**
	 * Esta variable indica si el satatus de usb de la impreso marco si movio el motor de impresion.
	 * Si no lo movio por el momento asumimos que no pudo imprimir nada y que se quedo en spool en CUPS.
	 * Si CUPS marco error pues este ya vale gorro.
	 * Pero si CUPS dijo que si peude que se quedara en spool y no imprimiera realmente.
	 * 
	 * Asumimos de incioq ue no pudo imprimir. Solo si se movio el motor es TRUE
	 */
	private static boolean usbPrintingStatus = false;
	
	private static int usbTestingCounter = 0;
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	
	public static void initializeCupsClient() {
		try {
			System.out.println("Inicialiando CUPS client");
			cupsClient = new CupsClient("127.0.0.1", 631);
			URL printerURL = new URL("http://127.0.0.1:631/printers/CUSTOM_Engineering_TG2480-H");
			cupsPrinter = cupsClient.getPrinter(printerURL);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Inicialiando CUPS client EXCEPTION");
			e.printStackTrace();
		}
		System.out.println("Inicialiando CUPS client END");
	}

	
	public static void BroadcastFullStatus() {

		System.out.println("PTR BroadcastFullStatus");
	
		//Si esta imprimiendo no hacemos el check de status
		if(printing)
			return;

		boolean existe = false;
		String line;
		try {

			Process p = Runtime.getRuntime().exec(new String[] {"sh", "-c", "ps -a | grep ReadSingleStatus"});
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null)
			{
				System.out.println(line);
				if(line.contains("ReadSingleStatus")) {
					existe = true;
					break;
				}

			}
		} catch (Exception err) {
			System.out.println(err);
		} 

		if(!existe){			
			String command = "./ReadSingleStatus";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(command);
			} catch (IOException ex) {						
				ex.printStackTrace();
			}
		}


		Timer screenTimerDispense = new Timer();
		
		JcmGlobalData.printerStatus = "OK";
		JcmGlobalData.printerReady = true;
		
		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {			
				try{	     		         

					BufferedReader in = new BufferedReader(new FileReader(FIFO));

					while(in.ready()){
						String ptrStatus = in.readLine();
						System.out.println("ptrStatus [" + ptrStatus + "]");
						logger.info("ptrStatus [" + ptrStatus + "]");
						
						String[] arrOfStr = ptrStatus.split("\\|"); //Se usa \\ ya que | es un metacaracter en regex por loq ue hay que escaparlo
						if(arrOfStr[0].toString().equalsIgnoreCase("ERROR") || arrOfStr[0].toString().equalsIgnoreCase("FATAL")) {							
							RaspiAgent.Broadcast(DeviceEvent.PTR_Status, "ERROR");	
							RaspiAgent.Broadcast(DeviceEvent.PTR_DetailStatus, arrOfStr[1].toString());
							JcmGlobalData.printerStatus = arrOfStr[1].toString();
							JcmGlobalData.printerReady = false;
							System.out.println("printerReady 1 [" + JcmGlobalData.printerReady + "]");
						}
						else {
							RaspiAgent.Broadcast(DeviceEvent.PTR_Status, "OK");
							
							if(arrOfStr[0].toString().equalsIgnoreCase("PAPER")) {
								
								if(arrOfStr[1].toString().equalsIgnoreCase("No paper")) {		
									System.out.println("NO PAPER");
									JcmGlobalData.printerReady = false;
									JcmGlobalData.printerStatus = " SIN PAPEL";
									System.out.println("printerReady 3 [" + JcmGlobalData.printerReady + "]");
									RaspiAgent.Broadcast(DeviceEvent.PTR_PaperThreshold, "PAPER OUT");
								}
								if(arrOfStr[1].toString().equalsIgnoreCase("Near paper end")) {
									System.out.println("BAJO NIVEL DE PAPEL");
									JcmGlobalData.printerStatus += " BAJO NIVEL DE PAPEL";
									RaspiAgent.Broadcast(DeviceEvent.PTR_PaperThreshold, "PAPER LOW");
								}
							}
							if(arrOfStr[0].toString().equalsIgnoreCase("STATUS")) {								
								RaspiAgent.Broadcast(DeviceEvent.PTR_DetailStatus,  arrOfStr[1]);
							}
						}

					}					
					System.out.println("printerReady 4 [" + JcmGlobalData.printerReady + "]");
					in.close();
					screenTimerDispense.cancel();
				}catch(IOException ex){
					logger.error(ex);
					System.err.println("IO Exception at buffered read!!");					
				}
			}
		}, TimeUnit.SECONDS.toMillis(1),TimeUnit.SECONDS.toMillis(1)); 

	}	

	
	/**
	 * Este metodo revisa el estatus de la impresora mientras se esta imprimiendo.
	 * La idea es que tome todos los mensajes hasta que llegue el de motor.
	 * El de motor ya que es el unico estatus que tengo ahorita para ver que si esta imprimiendo algo.
	 */
	public static void PrintingStatus() {

		System.out.println("PTR PrintingStatus");
		printing = true;
		usbPrintingStatus = false;
		usbTestingCounter = 0;
		
		boolean existe = false;
		String line;
		try {

			Process p = Runtime.getRuntime().exec(new String[] {"sh", "-c", "ps -a | grep ReadPrintingStatus"});
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null){
				
				if(line.contains("ReadPrintingStatus")) {
					existe = true;
					break;
				}
			}
		} catch (Exception err) {
			System.out.println(err);
		} 

		if(!existe) {			
			String command = "./ReadPrintingStatus";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(command);
			} catch (IOException ex) {						
				ex.printStackTrace();
			}
		}


		
		spoolCount = 0;
		
		Timer screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {			
				try{	
					if(++usbTestingCounter >= 10 ) {						
						printing = false;
						screenTimerDispense.cancel();
					}
					
					BufferedReader in = new BufferedReader(new FileReader(FIFO));

					while(in.ready()){					
						
						
						String ptrStatus = in.readLine();
						System.out.println(ptrStatus);
						String[] arrOfStr = ptrStatus.split("\\|");  //Se usa \\ ya que | es un metacaracter en regex por loq ue hay que escaparlo
						if(arrOfStr[0].equalsIgnoreCase("ERROR") || arrOfStr[0].equalsIgnoreCase("FATAL")) {
							System.out.println("PrintingStatus ERROR");
							RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, arrOfStr[1].toString());
							printing = false;
							screenTimerDispense.cancel();
							break;
						}
						else {							
							if(arrOfStr[0].equalsIgnoreCase("STATUS")) {
								//Lo asumo como que esta bien ya que esta moviendo el motor, ergo hay papel y todo el show
								if(arrOfStr[1].equalsIgnoreCase("Drag paper motor on")) {
									System.out.println("PrintingStatus OK");
									usbPrintingStatus = true;
									printing = false;
									screenTimerDispense.cancel();
									break;
								}
								if(arrOfStr[1].equalsIgnoreCase("spooling")) {
									System.out.println("spooling");									
									if(++spoolCount >= 4) {
										usbPrintingStatus = false;
										printing = false;									
										screenTimerDispense.cancel();
										break;
									}
								}
							}							
						}

					}					
					in.close();
				}catch(IOException ex){
					System.err.println("IO Exception at buffered read!!");					
				}
			}
		}, TimeUnit.SECONDS.toMillis(1),TimeUnit.SECONDS.toMillis(1)); 

		
	}
	
	public static void debugPrintCups() {

		try { 
			if(cupsClient == null)
				initializeCupsClient();

			System.out.println("Descriptipn[" + cupsPrinter.getDescription() + "]");

			System.out.println("MEDIA DEFAULT[" + cupsPrinter.getMediaDefault() + "]");

			for(String media : cupsPrinter.getMediaSupported()){
				System.out.println("MEDIA [" + media + "]");
			} 

			for(String media : cupsPrinter.getMimeTypesSupported()){
				System.out.println("MIME [" + media + "]");
			}

			System.out.println("Resolution Default[" + cupsPrinter.getResolutionDefault() + "]");
			for(String media : cupsPrinter.getResolutionSupported()){
				System.out.println("resolution [" + media + "]");
			}


			InputStream textStream = null; 
			try { 
				textStream = new FileInputStream("./Form/retiroout.txt"); 
			} catch (FileNotFoundException ffne) { 

				System.out.println(ffne.getMessage());
			} 

			PrintJob printJob = new PrintJob.Builder(textStream).build();

			System.out.println("page format [" + printJob.getPageFormat() + "]");

			System.out.println("page resolution [" +printJob.getResolution() + "]");

			Map<String,String> data = printJob.getAttributes();

			for (Map.Entry<String, String> entry : data.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}



		}catch (Exception ignored){
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			if(ignored.getMessage() != null)
				System.out.println(ignored.getMessage());
			else
				ignored.printStackTrace();

		}
	}

	public static void debugPrintJposUsb() {
		System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");     


		// instantiate a new jpos.POSPrinter object
		POSPrinter printer = new POSPrinter();


		System.out.println("COM CLAIM 1");
		try
		{
			//Open Printer
			System.out.println("----- 1");
			printer.open("CUSTOM TG2480H POS Printer USB Linux");
			System.out.println("----- 2");
			printer.claim(1);
			System.out.println("----- 3");
			printer.setDeviceEnabled(true);
			System.out.println("----- 4");
			//Print a Text String
			System.out.println("----- 5");
			//printer.printImmediate(POSPrinterConst.PTR_S_RECEIPT, "PRINTER");
			printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Print Test");

			//Close Printer
			System.out.println("----- 6");
			printer.setDeviceEnabled(false);
			System.out.println("----- 7");
			printer.release();
			System.out.println("----- 8");
			printer.close();
			System.out.println("----- 9");
		}
		catch(JposException e)
		{
			// display any errors that come up
			e.printStackTrace();
		}
		finally
		{
			// close the printer object
			try
			{
				printer.close();
			}
			catch (Exception e) {}
		}
	}

	public static boolean printDeposit(DepositOpVO depositOpVO) {

		System.out.println("Ptr.printDeposit"); 

		Date date = new Date(); 

		currencyFormat.setMaximumFractionDigits(0);

		Map<String,String> printMap = new HashMap<String,String>();
		printMap.put("<fecha>",String.format("%1$-15s",dateFormat.format(date)));
		printMap.put("<hora>",String.format("%1$-15s",timeFormat.format(date)));
		printMap.put("<monto>",currencyFormat.format(depositOpVO.amount));
		printMap.put("<b020>",String.format("%1$-5s",depositOpVO.b20));
		printMap.put("<b050>",String.format("%1$-5s",depositOpVO.b50));
		printMap.put("<b100>",String.format("%1$-5s",depositOpVO.b100));
		printMap.put("<b200>",String.format("%1$-5s",depositOpVO.b200));
		printMap.put("<b500>",String.format("%1$-5s",depositOpVO.b500));
		printMap.put("<monto20>",String.format("%1$9s",currencyFormat.format(depositOpVO.b20 * 20)));
		printMap.put("<monto50>",String.format("%1$9s",currencyFormat.format(depositOpVO.b50 * 50)));
		printMap.put("<monto100>",String.format("%1$9s",currencyFormat.format(depositOpVO.b100 * 100)));
		printMap.put("<monto200>",String.format("%1$9s",currencyFormat.format(depositOpVO.b200 * 200)));
		printMap.put("<monto500>",String.format("%1$9s",currencyFormat.format(depositOpVO.b500 * 500)));
		printMap.put("<referencia>",CurrentUser.movementId);
		printMap.put("<operacion>",Config.GetPersistence("TxCASHMANAGEMENTCounter","0"));
		printMap.put("<usuario>",depositOpVO.userName);		

		return print("deposito",printMap);

	}

	public static boolean printContadores() {

		System.out.println("Ptr.printContadores"); 

		Date date = new Date(); 

		currencyFormat.setMaximumFractionDigits(0);

		int total = 0;
		int total20 = 0;
		int total50 = 0;
		int total100 = 0;
		int total200 = 0;
		int total500 = 0;
		int total1000 = 0;

		int b20 = Integer.parseInt(Config.GetPersistence("Accepted20", "0"));
		int b50 = Integer.parseInt(Config.GetPersistence("Accepted50", "0"));
		int b100 = Integer.parseInt(Config.GetPersistence("Accepted100", "0"));
		int b200 = Integer.parseInt(Config.GetPersistence("Accepted200", "0"));
		int b500 = Integer.parseInt(Config.GetPersistence("Accepted500", "0"));
		int b1000 = Integer.parseInt(Config.GetPersistence("Accepted1000", "0"));

		total20 = 20 * b20;
		total50 = 50 * b50;
		total100 = 100 * b100;
		total200 = 200 * b200;
		total500 = 500 * b500;
		total1000 = 1000 * b1000;

		total = total20 + total50 + total100 + total200 + total500 + total1000; 

		Map<String,String> printMap = new HashMap<String,String>();
		printMap.put("<fecha>",String.format("%1$-15s",dateFormat.format(date)));
		printMap.put("<hora>",String.format("%1$-15s",timeFormat.format(date)));
		printMap.put("<monto>",currencyFormat.format(total));
		printMap.put("<b020>",String.format("%1$-5s",b20));
		printMap.put("<b050>",String.format("%1$-5s",b50));
		printMap.put("<b100>",String.format("%1$-5s",b100));
		printMap.put("<b200>",String.format("%1$-5s",b200));
		printMap.put("<b500>",String.format("%1$-5s",b500));
		printMap.put("<b1000>",String.format("%1$-5s",b1000));
		printMap.put("<monto20>",String.format("%1$9s",currencyFormat.format(total20)));
		printMap.put("<monto50>",String.format("%1$9s",currencyFormat.format(total50)));
		printMap.put("<monto100>",String.format("%1$9s",currencyFormat.format(total100)));
		printMap.put("<monto200>",String.format("%1$9s",currencyFormat.format(total200)));
		printMap.put("<monto500>",String.format("%1$9s",currencyFormat.format(total500)));
		printMap.put("<monto1000>",String.format("%1$9s",currencyFormat.format(total1000)));
		printMap.put("<usuario>",CurrentUser.loginUser);		
		printMap.put("<corte>",Config.GetPersistence("CorteCount", "-1"));

		return print("contadores",printMap);

	}

	public static boolean printDispense(double montoRetiro, String currentUser) {

		System.out.println("Prt.printDispense"); 

		Date date = new Date(); 

		currencyFormat.setMaximumFractionDigits(0);

		Map<String,String> printMap = new HashMap<String,String>();
		printMap.put("<fecha>",String.format("%1$-15s",dateFormat.format(date)));
		printMap.put("<hora>",String.format("%1$-15s",timeFormat.format(date)));
		printMap.put("<monto>",currencyFormat.format(montoRetiro));		
		printMap.put("<referencia>",CurrentUser.movementId);
		printMap.put("<operacion>",Config.GetPersistence("TxRETIROCASHMANAGEMENTCounter","0"));
		printMap.put("<usuario>",currentUser);		

		return print("retiro",printMap);

	}

	public static boolean printContadoresTest() {

		System.out.println("Ptr.printContadoresTest"); 				
		return print("contadores",new HashMap<String,String>());
	}

	public static boolean printDepositTest() {

		System.out.println("Ptr.printDepositTest"); 
		return print("deposito",new HashMap<String,String>());
	}

	public static boolean printDispenseTest() {

		System.out.println("Prt.printDispenseTest");	
		return print("retiro",new HashMap<String,String>());
	}


	/**
	 * Este metodo prepara el archivo con la forma que se va a imprimir.
	 * 
	 * @return si pudo hacer e archivo con la forma de impresion regresa el InputSTream , de lo contrario NULL
	 */
	private static InputStream prepareForm(String form, Map<String,String> formData) {
		BufferedReader reader;
		FileWriter fw = null;
		try {
			fw = new FileWriter("./Form/" + form + "out.txt");
		} catch (IOException e1) {			
			System.out.println("Ptr.print IOException");
			e1.printStackTrace();
			logger.error(e1);
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			return null;
		}		

		try {
			reader = new BufferedReader(new FileReader("./Form/" + form + ".txt"));
			String line = reader.readLine();
			while (line != null) {
				for (Entry<String, String> entry : formData.entrySet()) {
					line = line.replace(entry.getKey() ,entry.getValue() );
				}

				fw.write(line + System.getProperty("line.separator"));

				line = reader.readLine();

			}
			reader.close();
			fw.close();
		}
		catch( FileNotFoundException fe) {
			System.out.println("No se encontro el archivo [./Form/" + form + ".txt]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "FORM NOT FOUND");
			logger.error(fe);
			return null;


		} catch (IOException e) {
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "EXCEPTION ");
			e.printStackTrace();
			logger.error(e);
			return null;
		}



		// Input the file
		InputStream textStream = null; 
		try { 
			textStream = new FileInputStream("./Form/" + form + "out.txt"); 
		} catch (FileNotFoundException ffne) { 

			System.out.println(ffne.getMessage());
		} 
		if (textStream == null) { 
			return null; 
		}
		
		return textStream;
	}
	
	public static boolean print(String form, Map<String,String> formData) {

		System.out.println("printerReady [" + JcmGlobalData.printerReady + "]");
		
		if(!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;			
		}
		
		InputStream textStream = prepareForm(form, formData);
		
		if(textStream == null)
			return false;


		// CUPS Printing 


		try { 


			if(cupsClient == null)
				initializeCupsClient();
				
				
			PrintJob printJob = new PrintJob.Builder(textStream).build();
			
			
			PrintingStatus();

			cupsPrinter.setName(form);			
			PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
			
			
			int jobId = printRequestResult.getJobId();
			System.out.println("Printer jobId [" + jobId + "]");

			JobStateEnum jobStatus = cupsPrinter.getJobStatus(jobId);
						
			
			switch(jobStatus) {
			case ABORTED:
				System.out.println("jobStatus ABORTED");
				break;
			case CANCELED:
				System.out.println("jobStatus CANCELED");
				break;
			case COMPLETED:
				System.out.println("jobStatus COMPLETED");
				break;
			case PENDING:
				System.out.println("jobStatus PENDING");
				break;
			case PENDING_HELD:
				System.out.println("jobStatus PENDING_HELD");
				break;
			case PROCESSING:
				System.out.println("jobStatus PROCESSING");
				break;
			case PROCESSING_STOPPED:
				System.out.println("jobStatus PROCESSING_STOPPED");
				break;
			default:
				break;
				
			}
		
			//Para CUPS si pudo imprimir, aunque sea en spooling (COmo que no hay papel por ejemplo)
			if(printRequestResult.isSuccessfulResult()) {				
				System.out.println("Cups print OK");
				
				//Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por el movimiento del motoro de impresion)
				while(printing) {
					try {						
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(usbPrintingStatus) {
					System.out.println("USB print Status OK");
					RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, printRequestResult.getResultDescription());
				}
				else {
					System.out.println("USB print Status FAIL");
					System.out.println("Cancelling job [" + jobId + "] [" + cupsClient.cancelJob(jobId) + "]");					
					RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				}
			}
			else {				
				System.out.println("Cups print FAILED");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, printRequestResult.getResultDescription());
				return false;
			}


		}catch (Exception ignored){
			System.out.println("EXCEPTION print EXCEPTION");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			printing = false;
			if(ignored.getMessage() != null)
				System.out.println(ignored.getMessage());
			else
				ignored.printStackTrace();

			return false;

		}	


		return true;
	}

}


