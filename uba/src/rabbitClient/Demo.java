package rabbitClient;

import com.google.gson.Gson;

public class Demo {
	
	private static Gson gson = new Gson();
	
	private static String exchange = "ex.agents.topic";
	private static String routingKey = "agent.event.eja";
	private static String routingKeyStaled = "";
	private static int staledTime = 300;
	
	/*EVENT HEADERS */
	/*
	 return new Dictionary<string, object> {
             {"acq-source-channel", "atm_blu"},
             {"acq-source-type", "event"},
             {"acq-channelId", "red-blu"}
         };
	 */
	
	
	 public static void main(String args[]){
	   try{
	      
		
			
		   WriteToJournal.JcmWriteToJournal("JCM_EVENT", 10, 20, "1", "2", "3");
			
			
		   
		  
	     // produce.publish();
	  
	      // Consume
	      /*
	      Receiver receive = new Receiver();
	      receive.receive();
	      */
	   }catch(Exception e){
	     e.printStackTrace();
	   }
	 }
	 
	}
