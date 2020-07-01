package pentomino.core.devices;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.util.JposPropertiesConst;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.DeviceEvent;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.jcmagent.RaspiAgent;



public class Ptr {

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());

	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


	public static void main(String[] args) {

		
		printJposUsb();
		/*
		try {
			System.out.println("INI");
			printMethod1();
			System.out.println("FIN");
		} catch (PrintException | IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Trono aca!");
			e1.printStackTrace();
		}
		*/
	}

	private static void printCups() {

		try { 
			CupsClient cupsClient = new CupsClient("127.0.0.1", 631);

			URL printerURL = new URL("http://127.0.0.1:631/printers/CUSTOM_Engineering_TG2480-H");
			CupsPrinter cupsPrinter = cupsClient.getPrinter(printerURL);

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

	private static void printJposUsb() {
		System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");     

		// instantiate a new jpos.POSPrinter object
		POSPrinter printer = new POSPrinter();

		try
		{
			//Open Printer
			printer.open("CUSTOM TG2480H POS Printer USB Linux");
			printer.claim(1);
			printer.setDeviceEnabled(true);

			//Print a Text String
			printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Print Test");

			//Close Printer
			printer.setDeviceEnabled(false);
			printer.release();
			printer.close();
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

	private static void printJposSerial() {
		System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");     

		// instantiate a new jpos.POSPrinter object
		POSPrinter printer = new POSPrinter();

		try
		{
			//Open Printer
			printer.open("CUSTOM TG2480H POS Printer COM Linux");
			printer.claim(1);
			printer.setDeviceEnabled(true);

			//Print a Text String
			printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Print Test");

			//Close Printer
			printer.setDeviceEnabled(false);
			printer.release();
			printer.close();
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




	private static void printMethod1() throws PrintException, IOException {
		String defaultPrinter =	PrintServiceLookup.lookupDefaultPrintService().getName();
		System.out.println("Default printer: " + defaultPrinter);
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();

		// prints the famous hello world! plus a form feed
		InputStream is = new ByteArrayInputStream("hello world!\f".getBytes("UTF8"));

		PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));

		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(is, flavor, null);
		DocPrintJob job = service.createPrintJob();

		PrintJobWatcher pjw = new PrintJobWatcher(job);
		job.print(doc, pras);
		pjw.waitForDone();
		is.close();
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
		printMap.put("<referencia>","-----");
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

	public static boolean print(String form, Map<String,String> formData) {


		BufferedReader reader;
		FileWriter fw = null;
		try {
			fw = new FileWriter("./Form/" + form + "out.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Ptr.print IOException");
			e1.printStackTrace();
			logger.error(e1);
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			return false;
		}		

		try {
			reader = new BufferedReader(new FileReader("./Form/" + form + ".txt"));
			String line = reader.readLine();
			while (line != null) {
				for (Entry<String, String> entry : formData.entrySet()) {
					line = line.replace(entry.getKey() ,entry.getValue() );
				}

				fw.write(line + System.getProperty("line.separator"));
				//System.out.println(line);
				line = reader.readLine();

			}
			reader.close();
			fw.close();
		}
		catch( FileNotFoundException fe) {
			System.out.println("No se encontro el archivo [./Form/" + form + ".txt]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "FORM NOT FOUND");
			logger.error(fe);
			return false;


		} catch (IOException e) {
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "EXCEPTION ");
			e.printStackTrace();
			logger.error(e);
			return false;
		}



		// Input the file
		InputStream textStream = null; 
		try { 
			textStream = new FileInputStream("./Form/" + form + "out.txt"); 
		} catch (FileNotFoundException ffne) { 

			System.out.println(ffne.getMessage());
		} 
		if (textStream == null) { 
			return false; 
		}


		// CUPS Printing 


		try { 

			CupsClient cupsClient = new CupsClient("127.0.0.1", 631);




			URL printerURL = new URL("http://127.0.0.1:631/printers/CUSTOM_Engineering_TG2480-H");
			CupsPrinter cupsPrinter = cupsClient.getPrinter(printerURL);
			for(String media : cupsPrinter.getMediaSupported())
			{
				System.out.println("MEDIA [" + media + "]");
			}

			PrintJob printJob = new PrintJob.Builder(textStream).build();




			PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
			if(printRequestResult.isSuccessfulResult()) {
				System.out.println("Impresion OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			}
			else {
				System.out.println("Impresion FAIL");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
				return false;
			}


		}catch (Exception ignored){
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			if(ignored.getMessage() != null)
				System.out.println(ignored.getMessage());
			else
				ignored.printStackTrace();

			return false;

		}	


		return true;
	}

}

class PrintJobWatcher {
	boolean done = false;

	PrintJobWatcher(DocPrintJob job) {
		job.addPrintJobListener(new PrintJobAdapter() {
			public void printJobCanceled(PrintJobEvent pje) {
				System.out.println("...printJobCanceled");
				allDone();
			}
			public void printJobCompleted(PrintJobEvent pje) {
				System.out.println("...printJobCompleted");
				allDone();
			}
			public void printJobFailed(PrintJobEvent pje) {
				System.out.println("...printJobFailed");
				allDone();
			}
			public void printJobNoMoreEvents(PrintJobEvent pje) {
				System.out.println("...printJobNoMoreEvents");
				allDone();
			}
			void allDone() {
				synchronized (PrintJobWatcher.this) {
					done = true;
					System.out.println("Printing done ...");
					PrintJobWatcher.this.notify();
				}
			}
		});
	}
	public synchronized void waitForDone() {
		try {
			while (!done) {
				wait();
			}
		} catch (InterruptedException e) {
			System.out.println("waitForDone InterruptedException");
		}
	}
}
