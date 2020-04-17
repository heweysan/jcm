package rabbitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class WriteToJournal {

	
	private static Gson gson = new Gson();
	
	public static void JcmWriteToJournal(String evt, double amount, double available, String authId, String cardNumber, String accountId) {
	
		String switchAtmId = "";
		String extraData = "ENVIADO DESDE JAVA";
		String switchAuthId = "";
	
		
		//Quitamos todos los ; de los strings ya que si no los toma como separador y el evento no se guarda en EJA LOG
        if(evt != null && !evt.isEmpty()) { evt = evt.replace(";", ","); } else { evt = ""; } ;
        if(authId != null && !authId.isEmpty())  { authId = authId.replace(";", ","); } else { authId = ""; };
        if(cardNumber != null && !cardNumber.isEmpty()) { cardNumber = cardNumber.replace(";", ","); } else { cardNumber = ""; };
        if(accountId != null && !accountId.isEmpty()){ accountId = accountId.replace(";", ","); } else { accountId = ""; } ;
        
        String cardNumberMasked = "";
        String cardHash = "";
        String cardCrypto = "";     
        
        List<String> values = new ArrayList<>(); 
        
        values.add(evt);
        values.add(Double.toString(amount));
        values.add(Double.toString(available));
        values.add(authId);
        values.add(cardNumberMasked);
        values.add(accountId);
        values.add(extraData);
        values.add(switchAtmId);
        values.add(switchAuthId);
        values.add(cardHash);
        values.add(cardCrypto);

        ServerEntry myEntry = new ServerEntry();
		myEntry.setAgent("eja");
		myEntry.setCommand("add");
		myEntry.setTimestamp(java.lang.System.currentTimeMillis());
		myEntry.setValues(values);
		myEntry.setId(java.util.UUID.randomUUID().toString());
		//Esto para este caso van vacios
		myEntry.setData("DATA");
		myEntry.setDeviceType("MY DEVICE");
		myEntry.setEvent("MY HAPPY EVENT");
		myEntry.setAtmId("CIXXGS0020");
		
		
		AgentMessage agentMsg = new AgentMessage("CIXXGS0020", myEntry); //CIXXGS0020
		
		System.out.println(gson.toJson(agentMsg));
		
		// Publish
	    Producer produce = new Producer();
	    
	    produce.publish(gson.toJson(agentMsg));
	}
	
	
	
}
