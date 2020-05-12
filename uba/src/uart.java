
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.*;
import java.util.*;
import gnu.io.*;
import gnu.io.SerialPortEventListener;

public class uart extends protocol implements Runnable, SerialPortEventListener{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(uart.class.getName());
	
	
	@SuppressWarnings("rawtypes")
	static Enumeration portList;
	public CommPortIdentifier portId;
	public SerialPort serialPort;
	public OutputStream outputStream;
	public InputStream inputStream;
	public int baud;
	public String port;
	public int id = -1;
	

	Thread readThread;
    
    private byte[] bty = new byte[100];
    private int itm = 0;
    private int lng= 0; 
	  
	private enum state{ SYNC, LENGTH, DATA, CHK}
	private state flg1 = state.SYNC;
	
	//private byte lastMsg = 0x0;
	private byte lastTx = 0x0;
	
	
	uart(){
				
		
	}
	
	uart(int identificador){
		System.out.println("uart constructor [" + identificador + "]");
		jcmId = identificador;
	}
	
	public void run() {
	
		while(true){
			try {
				serialTx(jcmMessage); 
				
				if(jcmMessage[2] == ACK){
					this.id003_format((byte)5, STATUS_REQUEST, jcmMessage,true); //0X11
				}
				Thread.sleep(200);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}
	
	public int tempCont = 0;
	
	public void serialTx(byte[] msg) {		
        try {
        
            if(msg[2] != lastTx) {
            	lastTx = msg[2];
            	System.out.println(baitsToString("\n[" + id + "] uart->serialTx", msg));
            }
            else {
            	tempCont++;
            	if(tempCont == 10) {
            		System.out.print(".");
            		tempCont = 0;
            	}
            }
            	
            
            outputStream.write(msg,0,msg[1]);
        
        } catch (IOException e) {
        	e.printStackTrace();
        }
       
	}
	
	public void openPort (String prt){
		
		System.out.println("uart->openPort [" + prt + "]" );
		portList =  CommPortIdentifier.getPortIdentifiers();
		
		while (portList.hasMoreElements()) {
			//uart.portId = (CommPortIdentifier) uart.portList.nextElement();
			portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(prt)) {                
                	         
                    try {
                    	System.out.println("[" + id + "] Abriendo puerto");
                        serialPort = (SerialPort) portId.open("srlport", 2000);
                    } 
                    catch (PortInUseException e) {
                    	System.out.println("[" + id + "] PortInUseException");
                    }
                    
                    try {
                    	System.out.println("[" + id + "] getInputstream");
                        inputStream = serialPort.getInputStream();
                    } 
                    catch (IOException e) {
                    	System.out.println(e);
                    }
                    
                    try {
                    	System.out.println("[" + id + "] getOutputStream");
                        outputStream = serialPort.getOutputStream();
                    } 
                    catch (IOException e) {
                    	e.printStackTrace();
                    }
                    
                    serialPort.notifyOnDataAvailable(true);
                    try {
                    	System.out.println("[" + id + "] Setting Port Params");
                        serialPort.setSerialPortParams(baud/*uart.baud*//*9600*/,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_EVEN);
                    } catch (UnsupportedCommOperationException e) {
                    	e.printStackTrace();
                    }
                           
                    try {
                    	System.out.println("[" + id + "] adding event listener");
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
		System.out.println("[" + id + "] uart reiniciar");
		this.id003_format((byte)5, (byte) 0x40, jcmMessage,true);		
	}
	
	
	public String baitsToString(String texto, byte[] baits) {
    	String result = texto;
    	for (byte theByte : baits){
    		result += " [" + Integer.toHexString(theByte) + "] ";
        }
    	return result;
    }

	public void reciclar() {
		System.out.println("[" + id + "] uart reciclar");
		jcmMessage[2] = (byte) 0XFC;
	}

	public void estatus() {
		System.out.println("[" + id + "] uart estatus");
		jcmMessage[2] = 0X11;
		
	}

	public void statusRequest() {
		// TODO Auto-generated method stub
		
	}
}
