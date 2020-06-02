package rabbitClient;

import com.google.gson.Gson;

import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.TransactionType;
import pentomino.jcmagent.DTAServer;
import pentomino.jcmagent.RaspiAgent;
import pentomino.jcmagent.SimpleRabbitEnvironmentVariablesContainer;

public class Demo {

	public static void main(String args[]) {
		System.out.println("Demo.main");
		
		try {
			/*
			
			DTAServer dtaServer = new DTAServer();
			
			String jsonString = "{\"data\":{\"Command\":\"LISTFILES\",\"Date\":\"Wed Apr 29 21:36:22 CDT 2020\",\"Parameter\":null,\"Extra\":\"\"}}";

			SimpleRabbitEnvironmentVariablesContainer otherEnvVars = new Gson().fromJson(jsonString,
					SimpleRabbitEnvironmentVariablesContainer.class);

			String response = dtaServer.ManageAgent(otherEnvVars.data.Command, otherEnvVars.data.Convert());
			System.out.println("response [" + response + "]");
			
			DtaListener myRec = new DtaListener();

			myRec.SetupRabbitListener();
			
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
