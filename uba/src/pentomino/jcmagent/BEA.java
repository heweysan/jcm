package pentomino.jcmagent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.google.gson.Gson;

import pentomino.config.Config;

public class BEA {

	private static String SessionId = "";

	//private static Gson gson = new Gson();

	/**
	 * 
	 * @param businessEvent
	 * @param inSession def(false)
	 * @param newSession def(false)
	 * @param attributes def("")
	 */
	public static void BusinessEvent(pentomino.common.BusinessEvent businessEvent, boolean inSession,
			boolean newSession, String attributes) {
		// inSession default = false;
		// newSession default = false;
		// attibutes default = "";

				
		
		String session = "";
		
		if (newSession){
			GetNewSessionId();
			session = SessionId;
		}
		

		if (inSession)
			session = SessionId;

		String data = "" + businessEvent.getBusinessEventName() + ";" + session + ";" + attributes;
		
		InvBroadcast(java.lang.System.currentTimeMillis(), data);

	}

	private static void GetNewSessionId() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String yyyyMMddHHmmss = dateFormat.format(new Date());
		SessionId = yyyyMMddHHmmss;
	}

	public static void InvBroadcast(long timestamp, String data) {

		String[] receivedValues = data.split(";");

		ArrayList<String> listOfString = new ArrayList<String>();
		
		System.out.println("tma [" + receivedValues.length + "]");
		
		for(int i = 0; i < receivedValues.length; i++)
			listOfString.add(receivedValues[i]);
		
		int faltantes = 4 - receivedValues.length;
		
		for(int i = 0; i < faltantes; i++)		
			listOfString.add("");
		
		
		
		AgentMessage agentMsg = new AgentMessage();

		agentMsg.Agent = "bea";
		agentMsg.Command = "add";
		agentMsg.Timestamp = timestamp;
		agentMsg.Values = listOfString;
		agentMsg.Id = java.util.UUID.randomUUID().toString();
		agentMsg.AtmId = Config.GetDirective("AtmId", "");		


		//System.out.println("MESSAGE VALUES[" + listOfString + "]");
		//System.out.println("BEA MESSAGE JSON[" + gson.toJson(agentMsg) + "]");

		AgentsQueue.bq.add(agentMsg);

	}

}
