package pentomino.flow;

import java.util.Timer;
import java.util.TimerTask;

import pentomino.core.devices.Afd;

public class JcmMonitor extends Thread {

public void run(){

	System.out.println("JcmMonitor running");
	
	Timer screenTimerDispense = new Timer();

	screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
		@Override
		public void run() {
			Afd.BroadcastFullStatus();
		}
	}, 120000,120000);
	
	
	boolean enEspera = true;
	
	while(enEspera) {
	
		System.out.println("en espera");	
		
		
		try {
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	System.out.println("JcmMonitor bygon verde y plaquitas");
	
   } 

}
