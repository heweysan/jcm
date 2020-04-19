package rabbitClient;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import pentomino.common.*;


public class WriteToJournal {

	
	private static Gson gson = new Gson();
	
	public static void JcmWriteToJournal(String evt, double amount, double available, String authId, String cardNumber, String accountId) {
		
		/*  EjEMPLO DESDE FLOW
			JcmWriteToJournal("Withdrawal", 0, 0,"","CardNumber", "", "0", "MXN", "",  "",
					"FullAtmId", "Withdrawal PresentFailed Timeout", AccountType.Checkings, pentomino.common.TransactionType.ControlMessage
					, "", "Timeout", 1006, "");
		*/
		
		JcmWriteToJournal(evt, 0, 0, authId, cardNumber, accountId, "0", "MXN", "",  "",
					"FullAtmId", "Withdrawal PresentFailed Timeout", AccountType.Administrative, TransactionType.ControlMessage
					,"", "", 0, "");
			
	}
	
	public static void JcmWriteToJournal( String evt, double amount, double available, String authId, String cardNumber, String accountId
										, String surcharge, String denomination, String arqc, String arpc, String switchAtmId
										, String extraData, AccountType acctType, TransactionType type, String aquirer
										, String errorCode, int responseCode, String switchAuthId) {
	
		
		//Quitamos todos los ; de los strings ya que si no los toma como separador y el evento no se guarda en EJA LOG
        if(evt != null && !evt.isEmpty()) { evt = evt.replace(";", ","); } else { evt = ""; } ;
        if(authId != null && !authId.isEmpty())  { authId = authId.replace(";", ","); } else { authId = ""; };
        if(cardNumber != null && !cardNumber.isEmpty()) { cardNumber = cardNumber.replace(";", ","); } else { cardNumber = ""; };
        if(accountId != null && !accountId.isEmpty()){ accountId = accountId.replace(";", ","); } else { accountId = ""; } ;
        
        String cardNumberMasked = "";
        String cardHash = "";
        String cardCrypto = "";
        String terminalCaps = "";  	//TODO: Revisar que valor va para jcm
        String flagCode = "";		//TODO: Revisar que valor va para jcm
        String posMode = "";		//TODO: Revisar que valor va para jcm
        
        List<String> values = new ArrayList<>(); 
        long cter = 0;
        
        //El orden de envio es:
        //  evt, amount, available, authId, cardNumberMasked, accountId, extraData, surcharge, denomination, arqc
        //, arpc, acctType, type, aquirer, errorCode, flagCode, terminalCaps, posMode, cter, responseCode
        //, switchAtmId, switchAuthId, cardHash, cardCrypto
        
        
        values.add(evt);
        values.add(Double.toString(amount));
        values.add(Double.toString(available));
        values.add(authId);
        values.add(cardNumberMasked);
        values.add(accountId);
        values.add(extraData);        
        values.add(surcharge);
        values.add(denomination);
        values.add(arqc);
        
        values.add(arpc);
        values.add(Integer.toString(acctType.ordinal()));
        values.add(Integer.toString(type.ordinal()));
        values.add(aquirer);
        values.add(errorCode);
        values.add(flagCode);
        values.add(terminalCaps);
        values.add(posMode);        
        values.add(Long.toString(cter));
        values.add(Integer.toString(responseCode));
        
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
