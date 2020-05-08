package pentomino.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

/*
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import javax.print.attribute.standard.Sides;
*/


public class TicketPrint {

	public static void main(String[] args) {
		
		String currentDirectory = System.getProperty("user.dir");
	      System.out.println("The current working directory is " + currentDirectory);
		
		
		// Input the file
		InputStream textStream = null; 
		try { 
			textStream = new FileInputStream("file.TXT"); 
		} catch (FileNotFoundException ffne) { 
			
			System.out.println(ffne.getMessage());
		} 
		if (textStream == null) { 
		        return; 
		}
		
		/*
		if (service != null){
	        FileInputStream fis = new FileInputStream("c:/mytxt.txt");
	        Doc pdfDoc = new SimpleDoc(fis, new DocFlavor.INPUT_STREAM ("application/octet-stream"), null);
	        DocPrintJob printJob = service.createPrintJob();
	        printJob.print(pdfDoc, new HashPrintRequestAttributeSet());
	        fis.close();
	    }
		*/
		
		/*  CUPS Printing */
		
		CupsPrinter selectedPrinter = null;
		try { 
			
			CupsClient cupsClient = new CupsClient("127.0.0.1", 631);
			/*
			CupsPrinter cupsPrinter = cupsClient.getDefaultPrinter();
			InputStream inputStream = new FileInputStream("file.TXT");
			PrintJob printJob = new PrintJob.Builder(inputStream).build();
			
			
			PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
			*/
			
			/*
	        CupsClient client = new CupsClient("11.50.40.20", 631);
			//CupsClient client = new CupsClient();
	        */
	        
	        URL printerURL = new URL("http://127.0.0.1:631/printers/CUSTOM_Engineering_TG2480-H");
	        CupsPrinter cupsPrinter = cupsClient.getPrinter(printerURL);
	        PrintJob printJob = new PrintJob.Builder(textStream).build();
	        PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
	        if(printRequestResult.isSuccessfulResult())
	        	System.out.println("Impresion OK");
	        else
	        	System.out.println("Impresion FAIL");
	        /*
	       
	        
	        List<CupsPrinter> printers = client.getPrinters();
	        if (printers.size() == 0) {
	            throw new RuntimeException("Cant list Printer");
	        }
	       
	       for (CupsPrinter cupsPrinterIn : printers) {
	    	   System.out.println(cupsPrinterIn.getName());
	            if (cupsPrinterIn.getName().equals("CUSTOM_Engineering_TG2480-H")) {
	                selectedPrinter = cupsPrinterIn;
	            }
	        }
	       */
	    }catch (Exception ignored){
	        System.out.println("YA MAMOTO ---------------");
	    	ignored.printStackTrace();
	    	System.out.println("-------------------------");
	    }
	
		/*
		PrintJob printJob = new PrintJob.Builder(textStream).jobName("Jobname").build();
	    try {
			PrintRequestResult result = selectedPrinter.print(printJob);
			System.out.println("EXITO [" + result.isSuccessfulResult() + "]");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	    
	    
		/* IPP PRinting */
	/*
	URI printerURI = new URI("ipp://SERVER:631/printers/PRINTER_NAME");
	IppPrintService svc = new IppPrintService(printerURI);
	InputStream stream = new BufferedInputStream(new FileInputStream("image.epl"));
	DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	Doc myDoc = new SimpleDoc(stream, flavor, null);
	DocPrintJob job = svc.createPrintJob();
	job.print(myDoc, null);
		*/
		
		/* ------------------------------------ */
		
		//DocFlavor myFormat = DocFlavor.INPUT_STREAM.TEXT_HTML_US_ASCII;
		/*	
		
		DocFlavor myFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
				
		// Create a Doc
		Doc myDoc = new SimpleDoc(textStream, myFormat, null); 
		// Build a set of attributes
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet(); 
		aset.add(new Copies(1)); 
		//aset.add(MediaSize.ISO.C3);
		//aset.add(MediaSize.Other);
		aset.add(Sides.ONE_SIDED); 
		// discover the printers. that can print the format according to the
		// instructions in the attribute set
		//PrintService[] services = PrintServiceLookup.lookupPrintServices(myFormat, aset);
		
		PrintService[] services = PrintServiceLookup.lookupPrintServices(myFormat, new HashPrintRequestAttributeSet());
		// Create a print job from one of the print services
		
		System.out.println("Servicios encontrados [" + services.length + "]");
		
		if (services.length > 0) { 
			
			for(int i = 0; i < services.length; i++) {			
				System.out.println("Nombre [" + services[i].getName() + "]");				
							
				
				if(services[i].getName().contains("CUSTOM_Engineering_TG2480-H")) {
					DocPrintJob job = services[i].createPrintJob(); 
			        try {
			        	
			        	for (DocFlavor f : services[i].getSupportedDocFlavors()){
			                System.out.println("media type : "+f.getMediaType());
			                System.out.println("mime type : "+f.getMimeType());
			            }
			        	
			            job.print(myDoc, new HashPrintRequestAttributeSet()); 
			        } catch (Exception pe) {
			        	pe.printStackTrace();
			        }
				}
				
				
			}
			
		         
		}
		else {
			System.out.println("No se encontro impresora");
		}
		*/
		
	}
	
	
}
