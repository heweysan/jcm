package pentomino.jcmagent;

public class SimpleRabbitEnvironmentVariables {
	public String Command; 		
    public String Date;			
    public String File; 			
    public String Parameter; 	
    public String Extra;			

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

	public String getParameter() {
		return Parameter;
	}

	public void setParameter(String parameter) {
		Parameter = parameter;
	}

	public String getExtra() {
		return Extra;
	}

	public void setExtra(String extra) {
		Extra = extra;
	}

	public EnvironmentVariables Convert() {
        
    	EnvironmentVariables retVal = new EnvironmentVariables();
    	    	
    	retVal.Date = Date;
		retVal.File = File;
		retVal.Parameter = Parameter;
		retVal.Extra = Extra;
    			
    	return retVal;
        
    }
	
}
