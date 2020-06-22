package pentomino.core.devices;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

import jpos.util.JposPropertiesConst;



public class JPos {
	

	    /**
	     * @param args the command line arguments
	     */
		 public static void main(String[] args) {

	     System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");     

	     // instantiate a new jpos.POSPrinter object
	     POSPrinter printer = new POSPrinter();

	     try
	     {
	        //Open Printer
	        printer.open("CUSTOM KUBE POS Printer USB");
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

	     System.exit(0);
	 }



}
