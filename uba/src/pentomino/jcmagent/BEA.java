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

		/// <summary>Broadcasts an event through the XFS bus, acting on behalf of the
		/// sender. Used for device emulation layer</summary>
		/// <param name="device">Device to emulate. (AFD, PTR, EPP, PIN, TIO, DEVICEBUS,
		/// Unknown, etc)</param>
		/// <param name="evt">Event to emulate</param>
		/// <param name="timestamp">Timestamp. Usually set to DateTime.Now</param>
		/// <param name="data">Event data. Set to string.Empty if unused</param>
		// InvBroadcast("DEVICEBUS", "BusinessEvent", DateTime.Now,
		/// string.Format("{0};{1};{2};", businessEvent, inSession ? SessionId : "",
		/// attributes));
		// InvBroadcast("DEVICEBUS", "BusinessEvent", DateTime.Now,
		/// string.Format("{0};{1};{2};", businessEvent, inSession ? SessionId : "",
		/// attributes));
		// public static void InvBroadcast(string device, string evt, System.DateTime
		/// timestamp, string data)

	}

	private static void GetNewSessionId() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date hoy = new Date();
		String yyyyMMddHHmmss = dateFormat.format(hoy);
		System.out.println("yyyyMMddHHmmss [" + yyyyMMddHHmmss + "]");

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
