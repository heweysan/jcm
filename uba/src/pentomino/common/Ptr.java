package pentomino.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

public class Ptr {

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());
	
	public static void main(String[] args) {
		
		
		String currentDirectory = System.getProperty("user.dir");
	    System.out.println("The current working directory is " + currentDirectory);
		
	    logger.debug("PTR MAIN");      
	     
	    Tio miTio = new Tio();
	    miTio.abreBoveda();
	    
		
		
		
	}
	
	public static boolean print(String data) {
		// Input the file
				InputStream textStream = null; 
				try { 
					textStream = new FileInputStream("file.TXT"); 
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
			        PrintJob printJob = new PrintJob.Builder(textStream).build();
			        PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
			        if(printRequestResult.isSuccessfulResult())
			        	System.out.println("Impresion OK");
			        else
			        	System.out.println("Impresion FAIL");

			    }catch (Exception ignored){
			        System.out.println("YA MAMOTO ---------------");
			    	ignored.printStackTrace();
			    	System.out.println("-------------------------");
			    }	
				
				
				return true;
	}
	
}
