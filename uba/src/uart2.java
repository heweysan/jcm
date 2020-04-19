import java.io.*;
import java.util.*;
import javax.comm.*;
import gnu.io.*;

public class uart2 extends protocol implements Runnable, SerialPortEventListener{
	
	
	static Enumeration portList;
	static CommPortIdentifier portId;
	static SerialPort serialPort;
    static OutputStream outputStream;
    static InputStream inputStream;
    static String messageString = "Hello, world!\n";
    static int baud;
    
	Thread readThread;
    
    private byte[] bty = new byte[100];
    private int itm = 0;
    private int lng= 0; 
	  
	private enum state{ SYNC, LENGTH, DATA, CHK}
	private state flg1 = state.SYNC;
	
	uart2(){

	}
	
	public void run() {
	
		/*
		while(true){

			try {
				
				serialTx(uart2.msgmaster); 
				
				if(uart2.msgmaster[2] == 0x50 || uart2.msgmaster[2] == 17){ //eL 17 HAY QUE QUITARLO
					System.out.println("es 0x50...");
					this.id003_format((byte)5, (byte) 0x11, msgmaster); //STATUS_REQUEST					
				}
				
				Thread.sleep(30000); //original 200
			} catch (InterruptedException e) {System.out.println(e);}
		}
		*/
		System.out.println("uart2 run exit");
	}
	
	public void serialTx(byte[] msg) {		
        try {
            
            System.out.println(baitsToString("uart2 serialTx", msg));
            outputStream.write(msg,0,msg[1]);
        
        } catch (IOException e) {}
       
	}
	
	public void openPort (String prt){
		
		System.out.println("uart2 openPort [" + prt + "]" );
		uart2.portList =  CommPortIdentifier.getPortIdentifiers();
		
		while (uart2.portList.hasMoreElements()) {
			uart2.portId = (CommPortIdentifier) uart2.portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(prt)) {
                
                	// [0]          
                    try {
                        serialPort = (SerialPort) portId.open("srlport", 2000);
                    } 
                    catch (PortInUseException e) {
                    	System.out.println("PortInUseException");
                    }
                    
                    try {
                        inputStream = serialPort.getInputStream();
                    } 
                    catch (IOException e) {
                    	System.out.println(e);
                    }
                    
                    try {
                        outputStream = serialPort.getOutputStream();
                    } 
                    catch (IOException e) {
                    	
                    }
                    
                    serialPort.notifyOnDataAvailable(true);
                    try {
                        serialPort.setSerialPortParams(uart2.baud/*9600*/,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_EVEN);
                    } catch (UnsupportedCommOperationException e) {}
                    // [0]         
                    try {
            			serialPort.addEventListener(this);
            		} catch (TooManyListenersException e) {
            			e.printStackTrace();
            		}
                    
                    readThread = new Thread(this);
                    readThread.start();
                    
                }
            }
        }
	  
     }
	
	public void serialEvent(SerialPortEvent event){
		System.out.println("uart2 serialEvent");
	       switch(event.getEventType()) {
	        case SerialPortEvent.BI:
	        case SerialPortEvent.OE:
	        case SerialPortEvent.FE:
	        case SerialPortEvent.PE:
	        case SerialPortEvent.CD:
	        case SerialPortEvent.CTS:
	        case SerialPortEvent.DSR:
	        case SerialPortEvent.RI:
	        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	        	System.out.println("uart2 serialEvent  EMPTY o ESOS");
	            break;
	        case SerialPortEvent.DATA_AVAILABLE:
	        	System.out.println("uart2 serialEvent DATA_AVAILABLE");
	        	
	        	this.id003_format((byte)5, (byte) 0x50, jcmMessage,true);
	        	serialTx(uart2.jcmMessage); 
				
					        	
	        	
	            byte[] readBuffer = new byte[100];

	            try {
	                while (inputStream.available() > 0) {
	                    int numBytes = inputStream.read(readBuffer);
	                    System.out.println("uart2 numBytes [" + numBytes + "]");
	                    // [TEST]

	                    for(int j=0; j < numBytes ;j++){
	                    	
		                    if (flg1 == state.DATA) {
		                    	lng--;
		                    	bty[itm++] = readBuffer[j];
		                    	
		                    	if(lng <= 0){
		                    		flg1 = state.SYNC;
		                    		lng = 0;
		                    		itm = 0;
		                    		this.receiving(bty);
		                    		
		                    	}
		                    	continue;
		                    }
		                    
		                    if (flg1 == state.LENGTH) {	
		                    	flg1 = state.DATA;
		                    	bty[itm++] = readBuffer[j];
		                    	lng = (int)readBuffer[j] - 2;
		                    	continue;
		                    }
	                    	
	                    	if ((readBuffer[j] == (byte) 0xFC) && (flg1 == state.SYNC)) {
	                    		flg1 = state.LENGTH;
	                    		bty[itm++] = readBuffer[j];
	                    		continue;
	                    	}	                    	       	
	                    }
	                    // ![TEST]
	                    
	                }

	            } catch (IOException e) {System.out.println(e);}
	            break;
	        }
	}

	public static String baitsToString(String texto, byte[] baits) {
    	String result = texto;
    	for (byte theByte : baits){
    		result += " [" + Integer.toHexString(theByte) + "] ";
        }
    	return result;
    }
}
