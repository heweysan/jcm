package pentomino.jcmagent;

import java.util.Map;

public class RabbitEnvironmentVariables {

	public String Command; 		
	public String Date;			
	public String File; 			
	public Map<String,String> Parameter; 	
	public Map<String,String> Extra;			

	public String getCommand() {
		return Command;
	}

	public void setCommand(String command) {
		Command = command;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getFile() {
		return File;
	}

	public void setFile(String file) {
		File = file;
	}



	public EnvironmentVariables Convert() {

		EnvironmentVariables retVal = new EnvironmentVariables();

		retVal.Date = Date;
		retVal.File = File;

		retVal.Parameter = convertWithIteration(Parameter);

		retVal.Extra = convertWithIteration(Extra);

		return retVal;

	}	

	public String convertWithIteration(Map<String, ?> map) {
		StringBuilder mapAsString = new StringBuilder("");
		for (String key : map.keySet()) {
			mapAsString.append(key + "¬" + map.get(key) + ", ");
		}
		mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("");

		//System.out.println("mapAsString [" + mapAsString + "]");

		return mapAsString.toString();
	}

}
