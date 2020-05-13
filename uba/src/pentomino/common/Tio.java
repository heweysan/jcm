package pentomino.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import pentomino.jcmagent.RaspiAgent;

public class Tio implements Runnable{
	
	
	// create gpio controller
    final static GpioController gpio = GpioFactory.getInstance();
    
 // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
    final static GpioPinDigitalInput boveda = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_26,PinPullResistance.PULL_UP);
    final static GpioPinDigitalInput fascia = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_19, PinPullResistance.PULL_UP);
    static GpioPinDigitalOutput electroIman;
    static GpioPinDigitalOutput pin21;
    
    
    private static final Logger logger = LogManager.getLogger(Ptr.class.getName());
	
    public static void main(String[] args) {
		logger.debug("TIO MAIN");
		
		Tio miTio = new Tio();
		
		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	
		
	}
	
	public Tio() {
		
		System.out.println("TIO constructor");
		
		//gpio.shutdown();		
		
	}

	public boolean cierraBoveda() {
		
		 electroIman.low();
	     System.out.println("--> Electroinam ON");
		return true;
	}
	
	public boolean abreBoveda() {
		
		electroIman.high();
	    System.out.println("--> Electroinam OFF");
		return true;
	}
	
	
	public boolean cierraBoveda21() {
		
		 pin21.low();
	     System.out.println("--> Electroinam ON");
		return true;
	}
	
	public boolean abreBoveda21() {
		
		pin21.high();
	    System.out.println("--> Electroinam OFF");
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("TIO RUN");
       
		logger.debug("Tio starting....");
		
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
	     
		
		 electroIman = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_20, "ElectroIman", PinState.HIGH);
		 try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 pin21 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_21, "MyLED", PinState.HIGH);
		    
     
       // set shutdown state for this input pin
       boveda.setShutdownOptions(true);
       fascia.setShutdownOptions(true);
       
       // set shutdown state for this pin
       electroIman.setShutdownOptions(true, PinState.LOW);
       pin21.setShutdownOptions(true,PinState.LOW);
       
       
       System.out.println("--> GPIO state should be: ON");

       try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
       
       pin21.low();
       
       
       System.out.println("pin21 Name[" + pin21.getState().getName() + "] value[" + pin21.getState().getValue() + "]");
       
       
       electroIman.low();
       
       
       System.out.println("electroIman Name[" + pin21.getState().getName() + "] value[" + electroIman.getState().getValue() + "]");
       
       System.out.println("\n--> Electroiman ON");
       
       
       
       // create and register gpio pin listener
       boveda.addListener(new GpioPinListenerDigital() {
           @Override
           public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        	   if(event.getState().isHigh()) { 
              		logger.info("BOVEDA ABIERTA");
              		System.out.println("BOVEDA ABIERTA");              		
              		RaspiAgent.Broadcast(DeviceEvent.AFD_SafeOpen,"");	
              	}           	
              	else{
              		logger.info("BOVEDA CERRADA");
              		System.out.println("BOVEDA CERRADA");              		
              		RaspiAgent.Broadcast(DeviceEvent.AFD_SafeClosed,"");	
              	}           	
           }

       });       
       

       // create and register gpio pin listener
       fascia.addListener(new GpioPinListenerDigital() {
           @Override
           public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
               // display pin state on console
           	if(event.getState().isHigh()) { 
           		logger.info("FASCIA ABIERTA");
           		System.out.println("FASCIA ABIERTA");
           		RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetOpen,"");
           	}           	
           	else{
           		logger.info("FASCIA CERRADA");
           		System.out.println("FASCIA CERRADA");
           		RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetClosed,"");
           	}           	
            //System.out.println(" --> GPIO FASCIA PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
           }
       });       
      
       while(true) {
           try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       } 
	       // stop all GPIO activity/threads by shutting down the GPIO controller
	       // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
	       // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
      
		
	}

}
