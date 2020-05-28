package pentomino.common;

import java.io.BufferedReader;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.jcmagent.RaspiAgent;

public class Ptr {

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	
	
	public static void main(String[] args) {		
	}
	
	
	public static boolean printDeposit(DepositOpVO depositOpVO) {
		
		System.out.println("Prt.printDeposit"); 
		
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
		printMap.put("<referencia>","13579");
		printMap.put("<operacion>","23");
		printMap.put("<usuario>",depositOpVO.userName);		
		
		print("deposito",printMap);
		
		return true;
		
	}
	
	public static boolean printDispense(double montoRetiro, String currentUser) {
		
		System.out.println("Prt.printDispense"); 
		
		Date date = new Date(); 
		
		currencyFormat.setMaximumFractionDigits(0);

		Map<String,String> printMap = new HashMap<String,String>();
		printMap.put("<fecha>",String.format("%1$-15s",dateFormat.format(date)));
		printMap.put("<hora>",String.format("%1$-15s",timeFormat.format(date)));
		printMap.put("<monto>",currencyFormat.format(montoRetiro));		
		printMap.put("<referencia>","13579");
		printMap.put("<operacion>","23");
		printMap.put("<usuario>",currentUser);		
		
		print("retiro",printMap);
		
		return true;
		
	}
	
	public static boolean print(String form, Map<String,String> formData) {
		
		
		BufferedReader reader;
		FileWriter fw = null;
		try {
			fw = new FileWriter(form + "out.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
		
		
		
		try {
			reader = new BufferedReader(new FileReader(form + ".txt"));
			String line = reader.readLine();
			while (line != null) {
				for (Entry<String, String> entry : formData.entrySet()) {
			        line = line.replace(entry.getKey() ,entry.getValue() );
			    }
				
				fw.write(line + System.getProperty("line.separator"));
				System.out.println(line);
				line = reader.readLine();
				
			}
			reader.close();
		}
		catch( FileNotFoundException fe) {
			System.out.println("No se encontro el archivo [" + form + ".txt]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			return false;
		
		} catch (IOException e) {
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			e.printStackTrace();
			return false;
		}
		
		try {
			fw.close();
		} catch (IOException e) {
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			e.printStackTrace();
			return false;
		}
		
		
				
		// Input the file
				InputStream textStream = null; 
				try { 
					textStream = new FileInputStream(form + "out.txt"); 
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
			        }

			        
			    }catch (Exception ignored){
			    	RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			        if(ignored.getMessage() != null)
			        	System.out.println(ignored.getMessage());
			        else
			        	ignored.printStackTrace();
			    	
			    }	
				
				
				return true;
	}
	
}
