import java.io.*;
import java.util.*;
import javax.comm.*;
import gnu.io.*;
import java.text.Format;

public class uart extends protocol implements Runnable, SerialPortEventListener{
	
	
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
	
	private byte lastMsg = 0x0;
	
	uart(){

	}
	
	public void run() {
	
		while(true){
			try {
				serialTx(uart.jcmMessage); 
				
				if(uart.jcmMessage[2] == ACK){
					this.id003_format((byte)5, STATUS_REQUEST, jcmMessage,true); //STATUS_REQUEST
				}
				Thread.sleep(200); //original 200
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}
	
	public void serialTx(byte[] msg) {		
        try {
            if(msg[2] != 0x11)
            	System.out.println(baitsToString("uart->serialTx", msg));
            
            outputStream.write(msg,0,msg[1]);
        
        } catch (IOException e) {
        	e.printStackTrace();
        }
       
	}
	
	public void openPort (String prt){
		
		System.out.println("uart->openPort [" + prt + "]" );
		uart.portList =  CommPortIdentifier.getPortIdentifiers();
		
		while (uart.portList.hasMoreElements()) {
			uart.portId = (CommPortIdentifier) uart.portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(prt)) {
                
                	// [0]          
                    try {
                    	System.out.println("Abriendo puerto");
                        serialPort = (SerialPort) portId.open("srlport", 2000);
                    } 
                    catch (PortInUseException e) {
                    	System.out.println("PortInUseException");
                    }
                    
                    try {
                    	System.out.println("getInputstream");
                        inputStream = serialPort.getInputStream();
                    } 
                    catch (IOException e) {
                    	System.out.println(e);
                    }
                    
                    try {
                    	System.out.println("getOutputStream");
                        outputStream = serialPort.getOutputStream();
                    } 
                    catch (IOException e) {
                    	e.printStackTrace();
                    }
                    
                    serialPort.notifyOnDataAvailable(true);
                    try {
                    	System.out.println("Setting Port Params");
                        serialPort.setSerialPortParams(uart.baud/*9600*/,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_EVEN);
                    } catch (UnsupportedCommOperationException e) {
                    	e.printStackTrace();
                    }
                    // [0]         
                    try {
                    	System.out.println("adding event listener");
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
			//System.out.println("serialEvent");
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
	            break;
	        case SerialPortEvent.DATA_AVAILABLE:
	            byte[] readBuffer = new byte[100];

	            try {
	                while (inputStream.available() > 0) {
	                    int numBytes = inputStream.read(readBuffer);
	                    
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

	public void reiniciar() {
		System.out.println("uart reiniciar");
		this.id003_format((byte)5, (byte) 0x40, jcmMessage,true);		
	}
	
	
	public static String baitsToString(String texto, byte[] baits) {
    	String result = texto;
    	for (byte theByte : baits){
    		result += " [" + Integer.toHexString(theByte) + "] ";
        }
    	return result;
    }

	public void reciclar() {
		System.out.println("uart reciclar");
		uart.jcmMessage[2] = (byte) 0XFC;
	}

	public void estatus() {
		System.out.println("uart estatus");
		uart.jcmMessage[2] = 0X11;
		
	}

	public void statusRequest() {
		// TODO Auto-generated method stub
		
	}
}
