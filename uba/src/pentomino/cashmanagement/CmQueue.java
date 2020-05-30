package pentomino.cashmanagement;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import pentomino.cashmanagement.vo.CmMessageRequest;
import rabbitClient.CmListener;

public class CmQueue implements Runnable{

	
	public static LinkedList<CmMessageRequest> queueList = new LinkedList<CmMessageRequest>();
	
	public static void main(String[] args) {
		try {		
			CmListener myCmListener = new CmListener();
			myCmListener.SetupRabbitListener();				

		} catch (Exception e) {
			System.out.println("CmQueue.main EXCEPTION");
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		System.out.println("CmQueue run");
		
		try {		
			CmListener myCmListener = new CmListener();
			myCmListener.SetupRabbitListener();				

		} catch (Exception e) {
			System.out.println("CmQueue.run EXCEPTION");
			e.printStackTrace();
		}
		
		Timer screenTimerDispense = new Timer();
		
		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Tick..." + queueList.size());
			}
		}, 10000,30000);
		
	}

}
