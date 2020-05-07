package pentomino.jcmagent;

public class EnvironmentVariables {
	
	public String Date;
    public String File;
    public String Parameter;
    public String Uri;
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

	public String getUri() {
		return Uri;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public String getExtra() {
		return Extra;
	}

	public void setExtra(String extra) {
		Extra = extra;
	}

	public String Extra;

    public EnvironmentVariables() { }
	

}
