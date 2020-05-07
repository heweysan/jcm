package rabbitClient;

import com.google.gson.Gson;

import pentomino.cashmanagement.Transactions;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.TransactionType;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;
import pentomino.jcmagent.SimpleRabbitEnvironmentVariablesContainer;

public class Demo {	
	
	public static void main(String args[]){
		 
		 try{
			 DTAServer dtaServer = new DTAServer();
			 
			String jsonString = "{\"data\":{\"Command\":\"LISTFILES\",\"Date\":\"Wed Apr 29 21:36:22 CDT 2020\",\"Parameter\":null,\"Extra\":\"\"}}";
			
			SimpleRabbitEnvironmentVariablesContainer otherEnvVars =  new Gson().fromJson(jsonString, SimpleRabbitEnvironmentVariablesContainer.class);		 
						
			String response = dtaServer.ManageAgent(otherEnvVars.data.Command, otherEnvVars.data.Convert());
			System.out.println(response);
			
			Transactions cmTrans = new Transactions();
			
			
			
			
			 RaspiAgent.WriteToJournal("JCM_EVENT", 0, 0, "", "", "Prueba desde la frambuesita 2",AccountType.Administrative,TransactionType.CashManagement);
		   
			 RaspiAgent.Broadcast(DeviceEvent.DEP_CashInEndOk,"Monto Incorrecto Java Jcm");		   
			 
			 Receiver myRec = new Receiver();
			 
			 myRec.SetupRabbitListener();
			
			 
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 
}
