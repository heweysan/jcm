package pentomino.flow;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import pentomino.core.devices.Afd;
import pentomino.core.devices.Ptr;

public class JcmMonitor extends Thread {

public void run(){

	System.out.println("JcmMonitor [running]");
	
	Ptr.initializeCupsClient();
	
	Timer screenTimerDispense = new Timer();

	screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
		@Override
		public void run() {			
			Afd.BroadcastFullStatus();
			Ptr.BroadcastFullStatus();
		}
	}, TimeUnit.SECONDS.toMillis(30),TimeUnit.MINUTES.toMillis(2)); 
	
   } 

}
