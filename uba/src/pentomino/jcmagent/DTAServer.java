package pentomino.jcmagent;

import java.io.File;

import pentomino.config.Config;

public class DTAServer {

	
	 public String ManageAgent(String function, EnvironmentVariables envVars) {
         //Logger.DebugFormat("Received DTA function: {0}", function);
		 System.out.println("DTAServer.ManageAgent");

         switch (function.toUpperCase()) {
             case "RESTARTATM":
                 return RestartAtm();

             case "CLOSEFLOW":
                 return CloseFlow();

             case "CLOSEPENTOMINO":
                 return ClosePentomino();

             case "COUNTQUEUEFILES":
                 return CountQueueFiles();

             case "RUNUPDATE":
                 return RunUpdate();

             case "CLEANFLOWCACHE":
                 return CleanFlowCache();

             case "UPLOADFILE":
             case "SENDFILE":
                 return UploadFile(envVars);

             case "UPLOADLOGS":
             case "SENDLOGS":
                 return UploadLogs(envVars);

             case "UPDATEEVENTS":
                 return UpdateEvents(envVars.Parameter);

             case "RECEIVEFILE":
                 /*if (envVars.Parameter.Contains("HostName"))
                     return ReceiveFile(envVars);*/
                 return ReceiveFile(envVars.File);

             case "APPENDFILE":
                 //return AppendFile(envVars.File, envVars.Request.InputStream, envVars.Extra);
            	   return "";
             case "LISTFILES":
                 return ListFiles();

             case "ENDFILE":
                 return EndFile(envVars.File, envVars.Parameter);

             case "DELETEFILE":
                 return DeleteFile(envVars.Parameter);

             case "UNZIPFILE":
                 return UnzipFile(envVars.Parameter);

             case "INSTALLFLOW":
                 return InstallFlow(envVars.Parameter);

             case "EXECUTEFILE":
                 return ExecuteFile(envVars.Parameter);

             case "SETCONFIG":
                 return SetConfigDirective(envVars.Parameter);

             case "SETPULSARCONFIG":
                 return SetPulsarConfig(envVars.Parameter);

             case "DELETECONFIG":
                 return DeleteConfigDirective(envVars.Parameter);

             case "DELETEPULSARCONFIG":
                 return DeletePulsarConfig(envVars.Parameter);

             case "GETCONFIG":
                 return GetConfigDirective(envVars.Parameter);

             case "GETPULSARCONFIGKEYS":
                 return GetPulsarConfigKeys();

             case "GETCONFIGKEYS":
                 return GetConfigDirectivesKeys();

             case "GOOFFLINE":
                 //TODO: REVISAR Config.SetPersistence("ForceOoS", true);
            	 
                 return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
                 
         }

         return "Pentomino-ReturnValue: NOK";
     }
	 
	 
	 private String UploadFile(EnvironmentVariables envVars) {
		// TODO Auto-generated method stub
		return null;
	}


	private String UploadLogs(EnvironmentVariables envVars) {
		// TODO Auto-generated method stub
		return null;
	}


	private String ReceiveFile(String file) {
		// TODO Auto-generated method stub
		return null;
	}


	private String ReceiveFile(EnvironmentVariables envVars) {
		// TODO Auto-generated method stub
		return null;
	}


	private String ListFiles() {
		System.out.println("DTAServer.ListFiles");
		// TODO Auto-generated method stub
		/* ESTO SE HACE EN OTRO LADO */
        String response =  "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"CI99XE0001\", \"Files\":[\"javaDummyFile1.zip\",\"javaDummyFile2.txt\"]}}";
        
        return response;        
	}


	private String SetConfigDirective(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String ExecuteFile(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String InstallFlow(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String UnzipFile(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String DeleteFile(String file) {
				
		String returnValue = "NOT_FOUND";

        //string[] folders = {Constants.DownloadsDir, Constants.CrashDir, Constants.LogsDir};

		final File folder = new File("/home/you/Desktop");
		listFilesForFolder(folder);

		File tempFile = new File("c:/temp/temp.txt");
		boolean exists = tempFile.exists();		
		
		return returnValue;
	}


	private String EndFile(String file, String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String CleanFlowCache() {
		// TODO Auto-generated method stub
		return null;
	}


	private String RunUpdate() {
		// TODO Auto-generated method stub
		return null;
	}


	private String CountQueueFiles() {
		// TODO Auto-generated method stub
		return null;
	}


	private String ClosePentomino() {
		// TODO Auto-generated method stub
		return null;
	}


	private String UpdateEvents(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String SetPulsarConfig(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String DeleteConfigDirective(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String DeletePulsarConfig(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String GetConfigDirective(String parameter) {
		// TODO Auto-generated method stub
		return null;
	}


	private String GetConfigDirectivesKeys() {
		// TODO Auto-generated method stub
		return null;
	}


	private String CloseFlow() {
		// TODO Auto-generated method stub
		return null;
	}


	private String RestartAtm() {
		// TODO Auto-generated method stub
		return null;
	}


	private String GetPulsarConfigKeys() {
		// TODO Auto-generated method stub
		return null;
	}


	

	
	public void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	        }
	    }
	}

	
	
	
	
	
	
	
	
}
