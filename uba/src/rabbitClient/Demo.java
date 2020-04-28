package rabbitClient;

import pentomino.common.DeviceEvent;
import pentomino.jcmagent.RaspiAgent;

public class Demo {	
	
	 public static void main(String args[]){
		 
		 try{
			 
			 System.out.println(DeviceEvent.DEP_CashInEndOk.getNumVal());
			 //RaspiAgent.WriteToJournal("JCM_EVENT", 0, 0, "", "", "Prueba desde la frambuesita 2",AccountType.Administrative,TransactionType.CashManagement);
		   
			 RaspiAgent.Broadcast(DeviceEvent.DEP_CashInEndOk,"Monto Incorrecto Java Jcm");		   
		     
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 
}
