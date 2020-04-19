package rabbitClient;

import java.util.ArrayList;
import java.util.List;

public class ServerEntry {

		
	private String Agent;
	
	private String Command;
	
	private String Data;
	
	private String DeviceType;
	
	private String Event;
	
	private String Id;
	
	private long Timestamp;
	
	private String AtmId;
	
	private List<String> Values = new ArrayList<String>();
	
	public String getAgent() {
		return Agent;
	}

	public void setAgent(String agent) {
		Agent = agent;
	}

	public String getCommand() {
		return Command;
	}

	public void setCommand(String command) {
		Command = command;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}

	public String getDeviceType() {
		return DeviceType;
	}

	public void setDeviceType(String deviceType) {
		DeviceType = deviceType;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public long getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(long timestamp) {
		Timestamp = timestamp;
	}

	public List<String> getValues() {
		return Values;
	}

	public void setValues(List<String> values) {
		Values = values;
	}
	
	public String getAtmId() {
		return AtmId;
	}

	public void setAtmId(String atmId) {
		AtmId = atmId;
	}

	

	
	
}
