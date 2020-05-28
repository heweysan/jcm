package pentomino.cashmanagement;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pentomino.cashmanagement.vo.CmMessageRequest;
import rabbitClient.CmListener;

public class CmQueue implements Runnable{

	
	public static LinkedList<CmMessageRequest> queueList = new LinkedList<CmMessageRequest>();
	
	private volatile boolean terminateRequested;
	
	public static void main(String[] args) {


		try {		
			CmListener myCmListener = new CmListener();
			myCmListener.SetupRabbitListener();				

		} catch (Exception e) {
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
			e.printStackTrace();
		}
		
		Timer screenTimerDispense = new Timer();
		
		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Tick..." + queueList.size());
				/*
				if(!al.isEmpty()) {
					System.out.println("Llego mensaje carnal");
				}
				*/
				
			}
		}, 10000,20000);
		
	}

}
