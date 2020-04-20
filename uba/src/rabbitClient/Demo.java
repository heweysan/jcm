package rabbitClient;



import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.DeviceType;
import pentomino.common.TransactionType;
import pentomino.config.Configuration;
import pentomino.jcmagent.RaspiAgent;

public class Demo {
	
	/*
	private static String exchange = "ex.agents.topic";
	private static String routingKey = "agent.event.eja";
	private static String routingKeyStaled = "";
	private static int staledTime = 300;
	*/
	
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
			 
			 System.out.println(DeviceEvent.DEP_CashInEndOk.getNumVal());
			 RaspiAgent.WriteToJournal("JCM_EVENT", 0, 0, "", "", "Prueba desde la frambuesita",AccountType.Administrative,TransactionType.CashManagement);
		   
			 RaspiAgent.Broadcast(DeviceEvent.DEP_CashInEndOk,"12321");		   
		     
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 
}
