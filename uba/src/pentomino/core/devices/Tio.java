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

	
	/**
	 * Nos indica el estado de la boveda
	 */
	public static boolean safeOpen = false;
	

	/**
	 * Nos indica el estado del vastago
	 */
	public static boolean boltOpen = false;

	/**
	 * Nos indica el estado de la fascia
	 */
	public static boolean cabinetOpen = false;
	
	
	/**
	 * Nos indica el estado de la alarma
	 */
	public static boolean alarmOn = false;
	

	// create gpio controller
	static GpioController gpio;

	// provision gpio pin #02 as an input pin with its internal pull down resistor enabled
	static GpioPinDigitalInput boveda;
	static GpioPinDigitalInput fascia;
	static GpioPinDigitalOutput electroIman;
	static GpioPinDigitalOutput alarma;

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());

	public static void main(String[] args) {
		Tio miTio = new Tio();

		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	

	}

	public Tio() {
		System.out.println("TIO [running]");
	}

	public boolean abreElectroiman() {
		
		System.out.println("--> Electroiman ON");
		
		if(!JcmGlobalData.isDebug) 
			electroIman.low();
			
		boltOpen = true;
				
		return true;
	}

	public boolean cierraElectroiman() {
	
		System.out.println("--> Electroiman OFF");
		
		if(!JcmGlobalData.isDebug)
			electroIman.high();
	
		boltOpen = false;
		
		return true;
	}


	public boolean alarmOn() {
		System.out.println("--> Alarma ON");
		
		if(!JcmGlobalData.isDebug)
			alarma.low();
		
		alarmOn = true;
		
		return true;
	}

	public boolean alarmOff() {
		if(!JcmGlobalData.isDebug)
			alarma.high();
		
		alarmOn = false;
		System.out.println("--> Alarma OFF");
		return true;
	}

	@Override
	public void run() {

		logger.info("Tio [running]");
				
		if(JcmGlobalData.isDebug) {			
			return;
		}


		gpio = GpioFactory.getInstance();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}

		if(gpio != null && gpio.isShutdown()) {
			System.out.println("TIO GPIO IS SHUTDOWN!");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gpio = GpioFactory.getInstance();
		}
		else {
			System.out.println("TIO GPIO UP");
			logger.debug("TIO GPIO UP");
		}

		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));

		boveda = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_26,PinPullResistance.PULL_UP);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fascia = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_19, PinPullResistance.PULL_UP);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}

		//El electroiman arranca encendido
		electroIman = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_20, "ElectroIman", PinState.HIGH);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}

		//La alarma inicia apagada
		alarma = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_21, "MyLED", PinState.HIGH);


		// set shutdown state for this input pin
		boveda.setShutdownOptions(true);
		fascia.setShutdownOptions(true);

		// set shutdown state for this pin
		electroIman.setShutdownOptions(true, PinState.LOW,PinPullResistance.PULL_UP);
		alarma.setShutdownOptions(true,PinState.LOW);


		System.out.println("--> GPIO state should be: ON");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {			
			e1.printStackTrace();
		}



		System.out.println("alarma Name[" + alarma.getState().getName() + "] value[" + alarma.getState().getValue() + "]");
		      
		System.out.println("electroIman Name[" + electroIman.getState().getName() + "] value[" + electroIman.getState().getValue() + "]");



		/** 
		 * SENSOR DE LA BOVEDA
		 * Set de variable safeOpen a true/false
		 * Dispara evento SafeOpen/SafeClosed
		 * 
		 * create and register gpio pin listener
		 */
		boveda.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if(event.getState().isHigh()) { 
					logger.info("BOVEDA ABIERTA");
					System.out.println("BOVEDA ABIERTA");              		
					RaspiAgent.Broadcast(DeviceEvent.AFD_SafeOpen,"");					
					safeOpen = true;
					EventListenerClass.fireMyEvent(new MyEvent("SafeOpen"));
				}           	
				else{
					logger.info("BOVEDA CERRADA");
					System.out.println("BOVEDA CERRADA");              		
					RaspiAgent.Broadcast(DeviceEvent.AFD_SafeClosed,"");
					safeOpen = false;					
					EventListenerClass.fireMyEvent(new MyEvent("SafeClosed"));
				}           	
			}

		});       


		/** 
		 * SENSOR DE LA FASCIA
		 * Set de variable cabinetOpen a true/false
		 * Dispara evento CabinetOpen/CabinetClosed
		 * 
		 * create and register gpio pin listener
		 */
		fascia.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// display pin state on console
				System.out.println(" --> GPIO FASCIA PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				if(event.getState().isHigh()) { 
					logger.info("FASCIA ABIERTA");
					System.out.println("FASCIA ABIERTA");
					RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetOpen,"");
					cabinetOpen = true;
					EventListenerClass.fireMyEvent(new MyEvent("CabinetOpen"));
				}           	
				else{
					logger.info("FASCIA CERRADA");
					System.out.println("FASCIA CERRADA");
					RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetClosed,"");
					cabinetOpen = false;
					EventListenerClass.fireMyEvent(new MyEvent("CabinetClosed"));
				}           	

			}
		});     

		while(true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		// gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller


	}

}
