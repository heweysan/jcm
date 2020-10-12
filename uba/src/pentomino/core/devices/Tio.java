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

	static GpioPinDigitalInput boveda06;
	static GpioPinDigitalInput gabinete05;
	
	static GpioPinDigitalOutput perno20;
	static GpioPinDigitalOutput alarma21;

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());

	public static void main(String[] args) {
		Tio miTio = new Tio();

		Thread tioThread = new Thread(miTio, "Tio Thread");
		tioThread.start();	

	}

	public Tio() {
		System.out.println("[TIO] running");
	}

	public boolean abreElectroiman() {
		
		System.out.println("[TIO] Perno UP");
		
		if(!JcmGlobalData.isDebug) 
			perno20.low();
			
		boltOpen = true;
				
		return true;
	}

	public boolean cierraElectroiman() {
	
		System.out.println("[TIO] Perno DOWN");
		
		if(!JcmGlobalData.isDebug)
			perno20.high();
	
		boltOpen = false;
		
		return true;
	}


	public boolean alarmOn() {
			
		if(!JcmGlobalData.isDebug) {
			System.out.println("[TIO] Alarma ON NO DEBUG");
			alarma21.low();
		}
		else
			System.out.println("[TIO] Alarma ON");
		
		alarmOn = true;
		
		return true;
	}

	public boolean alarmOff() {
		if(!JcmGlobalData.isDebug)
			alarma21.high();
		
		alarmOn = false;
		System.out.println("[TIO] Alarma OFF");
		return true;
	}

	@Override
	public void run() {

		logger.info("[TIO] running");
				
		if(JcmGlobalData.isDebug) {			
			return;
		}		
		
		SetupGPIO();
		
		SetupAlarma();

		SetupPerno();
		
		SetupGabinete();
		
		SetupBoveda();
		
		while(true) {
			try {
				Thread.sleep(500);			
								
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}

	}

	void SetupGPIO() {
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
		gpio = GpioFactory.getInstance();

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}				
		
		if(gpio != null && gpio.isShutdown()) {
			System.out.println("[TIO] GPIO IS SHUTDOWN!");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gpio = GpioFactory.getInstance();
		}
		else {
			System.out.println("[TIO] GPIO UP");
			logger.debug("[TIO] GPIO UP");
		}
	}
	
	void SetupAlarma(){
		
		//La alarma inicia apagada
		alarma21 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_21, "Alarma", PinState.HIGH);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("[TIO] Alarma State[" + alarma21.getState().getValue() + "]");
		
	}
	
	void SetupPerno() {
		//El electroiman arranca encendido
		perno20 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_20, "Perno", PinState.HIGH);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}
		
		System.out.println("[TIO] Perno State[" + perno20.getState().getValue() + "]");
	}
	
	void SetupGabinete() {
		gabinete05 = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_05, PinPullResistance.PULL_UP);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {			
			e2.printStackTrace();
		}
		
		/** 
		 * SENSOR DE LA FASCIA
		 * Set de variable cabinetOpen a true/false
		 * Dispara evento CabinetOpen/CabinetClosed
		 * 
		 * create and register gpio pin listener
		 */
		gabinete05.removeAllListeners();
		gabinete05.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// display pin state on console
				System.out.println(" --> GPIO GABINETE PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				if(event.getState().isHigh()) { 
					logger.info("GABINETE ABIERTO");
					System.out.println("GABINETE ABIERTO");
					RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetOpen,"");
					cabinetOpen = true;
					EventListenerClass.fireMyEvent(new MyEvent("CabinetOpen"));
				}           	
				else{
					logger.info("GABINETE CERRADO");
					System.out.println("GABINETE CERRADO");
					RaspiAgent.Broadcast(DeviceEvent.EPP_CabinetClosed,"");
					cabinetOpen = false;
					EventListenerClass.fireMyEvent(new MyEvent("CabinetClosed"));
				}
			}
		});  
		
		System.out.println("[TIO] Gabiente State[" + gabinete05.getState().getValue() + "]");
		System.out.println("[TIO] Gabiente getPin -> " + gabinete05.getPin().getName() );
		System.out.println("[TIO] Gabiente getMode -> " + gabinete05.getMode().getName() );
		System.out.println("[TIO] Gabiente getMode -> " + gabinete05.getProvider().getName() );
	}
	
	void SetupBoveda() {
		boveda06 = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_06, PinPullResistance.PULL_UP);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		// set shutdown state for this input pin
		boveda06.setShutdownOptions(true);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {			
			e1.printStackTrace();
		}
		
		/** 
		 * SENSOR DE LA BOVEDA
		 * Set de variable safeOpen a true/false
		 * Dispara evento SafeOpen/SafeClosed
		 * 
		 * create and register gpio pin listener
		 */
		boveda06.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("[TIO] GPIO BOVEDA PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				if(event.getState().isHigh()) { 
					logger.info("[TIO] BOVEDA ABIERTA");
					System.out.println("[TIO] BOVEDA ABIERTA");              		
					RaspiAgent.Broadcast(DeviceEvent.AFD_SafeOpen,"");					
					safeOpen = true;
					EventListenerClass.fireMyEvent(new MyEvent("SafeOpen"));
				}           	
				else{
					logger.info("[TIO] BOVEDA CERRADA");
					System.out.println("[TIO] BOVEDA CERRADA");              		
					RaspiAgent.Broadcast(DeviceEvent.AFD_SafeClosed,"");
					safeOpen = false;					
					EventListenerClass.fireMyEvent(new MyEvent("SafeClosed"));
				}           	
			}

		});       
		
		System.out.println("[TIO] Boveda State[" + boveda06.getState().getValue() + "]");
	}
	
}
