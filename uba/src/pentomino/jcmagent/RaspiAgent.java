package pentomino.jcmagent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.TransactionType;
import pentomino.config.Config;

public class RaspiAgent {

	private static Gson gson = new Gson();
	
	/**
	The Desciption of the method to explain what the method does
	@param the parameters used by the method
	@return the value returned by the method
	@throws what kind of exception does this method throw
	*/
	public static void WriteToJournal(String evt, double amount, double available, String authId, String accountId, String extraData,AccountType acctType, TransactionType type) {
		
		String atmId = Config.GetDirective("AtmId", "");
		
		WriteToJournal(evt, amount, 0, authId, "", accountId, "0", "MXN", "",  "",
				atmId, extraData, acctType, type,"", "", 0, "");
			
	}
	
	/**
	The Desciption of the method to explain what the method does
	@param the parameters used by the method
	@return the value returned by the method
	@throws what kind of exception does this method throw
	*/
	private static void WriteToJournal( String evt, double amount, double available, String authId, String cardNumber
										, String accountId, String surcharge, String denomination, String arqc, String arpc
										, String switchAtmId, String extraData, AccountType acctType, TransactionType type, String aquirer
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
 			
        AgentMessage agentMsg = new AgentMessage(); //CIXXGS0020 
        
        agentMsg.Agent = "eja";
		agentMsg.Data = extraData;
		agentMsg.DeviceType = "JCM";
		agentMsg.Event = evt;
		agentMsg.Id = java.util.UUID.randomUUID().toString();
		agentMsg.Timestamp = java.lang.System.currentTimeMillis();
		agentMsg.Values = values;
		agentMsg.Command = "add";
		agentMsg.AtmId = switchAtmId;
		
		System.out.println(gson.toJson(agentMsg));
		
		AgentsQueue.bq.add(agentMsg);			
		
	}
	
	public static void Broadcast(DeviceEvent fullEvt, String data) {		
		
		String str = fullEvt.toString();

		List<String> list = Arrays.asList(str.split("_"));
		System.out.println(list);
		 
		AgentMessage agentMsg = new AgentMessage(); //CIXXGS0020 
	        
        agentMsg.Agent = "hma";
        agentMsg.Command = "add";
        agentMsg.DeviceType = list.get(0);
        agentMsg.Event = list.get(1);
		agentMsg.Id = java.util.UUID.randomUUID().toString();
		agentMsg.Timestamp = java.lang.System.currentTimeMillis();
		agentMsg.Data = data;
		agentMsg.AtmId = Config.GetDirective("AtmId", "");
		
		System.out.println(gson.toJson(agentMsg));
		
		AgentsQueue.bq.add(agentMsg);
		
	}
	
	
}
