package pentomino.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import pentomino.jcmagent.AgentsQueue;

public class Ptr {

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());
	
	public static void main(String[] args) {
		
		
		//Queue<String> myqueue = new LinkedList<String>();
		//Queue<String> mq2 =  (Queue<String>) Collections.synchronizedList(new LinkedList<String>());
		
		BlockingQueue<String> myqueue = new LinkedBlockingQueue<String>();
		
		String currentDirectory = System.getProperty("user.dir");
	    System.out.println("The current working directory is " + currentDirectory);
		
	    logger.debug("PTR MAIN");      
	     
	    final AgentsQueue miQueue = new AgentsQueue();
		Thread miQueueThread = new Thread(miQueue, "miQueueThread");
		miQueueThread.start();
    
		
		//miQueue.bq.add("AAA");
		/*
		myqueue.add("1");
		myqueue.add("2");
		myqueue.add("3");
		myqueue.add("4");
		myqueue.add("5");
		myqueue.add("6");
		*/	
		
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
