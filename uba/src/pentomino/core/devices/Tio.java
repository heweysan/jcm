package pentomino.core.devices;

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

import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.flow.EventListenerClass;
import pentomino.flow.MyEvent;
import pentomino.jcmagent.RaspiAgent;

public class Tio implements Runnable{
	
	
	// create gpio controller
    static GpioController gpio;
    
 // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
    static GpioPinDigitalInput boveda;
    static GpioPinDigitalInput fascia;
    static GpioPinDigitalOutput electroIman;
    static GpioPinDigitalOutput alarma;
    
    private static final Logger logger = LogManager.getLogger(Ptr.class.getName());
	
    public static void main(String[] args) {
		logger.debug("TIO MAIN");
		
		Tio miTio = new Tio();
		
		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	
		
	}
	
	public Tio() {
		
		System.out.println("TIO constructor");				
		
	}

	public boolean cierraBoveda() {
		
		 electroIman.low();
	     System.out.println("--> Electroiman ON");
		return true;
	}
	
	public boolean abreBoveda() {
		
		if(JcmGlobalData.isDebug) {
			EventListenerClass.fireMyEvent(new MyEvent("SafeOpen"));
		}
		else {
			electroIman.high();
		}
	    System.out.println("--> Electroiman OFF");
		return true;
	}
	
	
	public boolean cierraBoveda21() {
		
		 alarma.low();
	     System.out.println("--> Electroiman ON");
		return true;
	}
	
	public boolean abreBoveda21() {
		
		alarma.high();
	    System.out.println("--> Electroiman OFF");
		return true;
	}

	@Override
	public void run() {
		
		if(JcmGlobalData.isDebug) {
			logger.debug("Tio starting.... DEBUG");
			System.out.println("Tio starting.... DEBUG");   
			return;
		}
		
		logger.debug("Tio starting....");
		System.out.println("Tio starting....");
		
		gpio = GpioFactory.getInstance();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(gpio != null && gpio.isShutdown()) {
			System.out.println("TIO GPIO IS SHUTDOWN!");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gpio = GpioFactory.getInstance();
		}
		else {
			System.out.println("TIO GPIO UP");
		}
		
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
		
		boveda = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_26,PinPullResistance.PULL_UP);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    fascia = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_19, PinPullResistance.PULL_UP);
	    
	    try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
	    //El electroiman arranca encendido
		 electroIman = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_20, "ElectroIman", PinState.LOW);
		 try {
			Thread.sleep(500);
		 } catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		 }
		
		 //La alarma inicia apagada
		 alarma = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_21, "MyLED", PinState.HIGH);
		    
     
       // set shutdown state for this input pin
       boveda.setShutdownOptions(true);
       fascia.setShutdownOptions(true);
       
       // set shutdown state for this pin
       electroIman.setShutdownOptions(true, PinState.LOW,PinPullResistance.PULL_DOWN);
       alarma.setShutdownOptions(true,PinState.LOW);
       
       
       System.out.println("--> GPIO state should be: ON");

       try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
       
      
       System.out.println("alarma Name[" + alarma.getState().getName() + "] value[" + alarma.getState().getValue() + "]");
       
       
       //electroIman.low();       
       System.out.println("electroIman Name[" + electroIman.getState().getName() + "] value[" + electroIman.getState().getValue() + "]");
       
      
       
       
       
       // create and register gpio pin listener
       boveda.addListener(new GpioPinListenerDigital() {
           @Override
           public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        	   if(event.getState().isHigh()) { 
              		logger.info("BOVEDA ABIERTA");
              		System.out.println("BOVEDA ABIERTA");              		
              		RaspiAgent.Broadcast(DeviceEvent.AFD_SafeOpen,"");
              		EventListenerClass.fireMyEvent(new MyEvent("SafeOpen"));
              	}           	
              	else{
              		logger.info("BOVEDA CERRADA");
              		System.out.println("BOVEDA CERRADA");              		
              		RaspiAgent.Broadcast(DeviceEvent.AFD_SafeClosed,"");
              		EventListenerClass.fireMyEvent(new MyEvent("SafeClosed"));
              	}           	
           }

       });       
       

       // create and register gpio pin listener
       fascia.addListener(new GpioPinListenerDigital() {
           @Override
           public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
               // display pin state on console
        	   System.out.println(" --> GPIO FASCIA PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        	   if(event.getState().isHigh()) { 
           		logger.info("FASCIA ABIERTA");
           		System.out.println("FASCIA ABIERTA");
           		RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetOpen,"");
           		EventListenerClass.fireMyEvent(new MyEvent("CabinetOpen"));
           	}           	
           	else{
           		logger.info("FASCIA CERRADA");
           		System.out.println("FASCIA CERRADA");
           		RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetClosed,"");
           		EventListenerClass.fireMyEvent(new MyEvent("CabinetClosed"));
           	}           	
            
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
