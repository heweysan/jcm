package pentomino.jcmagent;

import java.util.List;

public class AgentMessage {

    public String Agent;
    public String Data;
    public String DeviceType;
    public String Command;
    public String Event;
    public String Id;
    public long Timestamp;
    public List<String> Values;
    public String AtmId;
    
	
    
    
    public AgentMessage(String atmId, ServerEntry entry)
    {
        if (entry != null)
        {
            Agent = entry.getAgent();
            Data = entry.getData();
            DeviceType = entry.getDeviceType();
            Event = entry.getEvent();
            Id = entry.getId();
            Timestamp = entry.getTimestamp();
            Values = entry.getValues();
            Command = entry.getCommand();
            AtmId = atmId;
        }        
    }



	public AgentMessage() {
		// TODO Auto-generated constructor stub
	}
}
