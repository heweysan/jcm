package pentomino.jcmagent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class BEA {

	private static String SessionId = "";

	private static Gson gson = new Gson();

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
		// attibutes defautl = "";

		if (newSession)
			GetNewSessionId();

		String session = "";

		if (inSession)
			session = SessionId;

		String data = "" + businessEvent.getBusinessEventName() + ";" + session + ";" + attributes;
		
		System.out.println("data [" + data + "]");
				InvBroadcast(java.lang.System.currentTimeMillis(), data);



	}

	private static void GetNewSessionId() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String yyyyMMddHHmmss = dateFormat.format(new Date());
		SessionId = yyyyMMddHHmmss;
	}

	public static void InvBroadcast(long timestamp, String data) {

		List<String> values = Arrays.asList(data.split(";"));

		AgentMessage agentMsg = new AgentMessage();

		agentMsg.Agent = "bea";
		agentMsg.Command = "add";
		agentMsg.Timestamp = timestamp;
		agentMsg.Values = values;
		agentMsg.Id = java.util.UUID.randomUUID().toString();

		System.out.println(gson.toJson(agentMsg));

		AgentsQueue.bq.add(agentMsg);

	}

}
