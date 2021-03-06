package pentomino.jcmagent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import pentomino.cashmanagement.Transactions;
import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.flow.Flow;
import rabbitClient.Producer;
import rabbitClient.RabbitMQConnection;

public class DTAServer {

	private static Gson gson = new Gson();

	private static final Logger logger = LogManager.getLogger(Transactions.class.getName());

	private static boolean flushing = false;
	
	public String ManageAgent(String function, EnvironmentVariables envVars) {

		System.out.println("DTAServer.ManageAgent");

		switch (function.toUpperCase()) {
		case "RESTARTATM":
			boolean busy = false;
			do {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}                
				busy = Config.GetPersistence("BoardStatus", "Busy").equalsIgnoreCase("Busy");
				System.out.println("RESTARTATM Busy[" + busy + "]");
			} while (busy);
			Flow.redirect(Flow.panelReinicio);
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
			if (envVars.Parameter.contains("HostName"))
				return ReceiveFile(envVars);
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
			Config.SetPersistence("ForceOoS", "true");
			System.out.println("GOOFFLINE");
			return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
		
		case "FLUSHJCM":
			
			if(flushing) {
				return "{\"data\":{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
			}
			
			boolean busyflush = false;
			do {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}                
				busyflush = Config.GetPersistence("BoardStatus", "Busy").equalsIgnoreCase("Busy");
				System.out.println("FLUSHJCM Busy[" + busyflush + "]");
			} while (busyflush);
						
			return FlushJcms();
		case "OPENSAFE":
			return OpenSafe();
		case "OPENCABINET":
			return OpenCabinet();
		case "TURNOFFALARM":
			return TurnOffAlarm();
			
			
		}

		return "Pentomino-ReturnValue: NOK";
	}

	private String OpenSafe() {
		System.out.println("OPENSAFE");
		
		boolean isBusy = false;
		do {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {					
				e.printStackTrace();
			}                
			isBusy = Config.GetPersistence("BoardStatus", "Busy").equalsIgnoreCase("Busy");
			System.out.println("OPENSAFE Busy[" + isBusy + "]");
		} while (isBusy);
		
		Flow.timerBoveda();
		Flow.isAdminTime = true;
		return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
	}
	
	
	private String OpenCabinet() {
		System.out.println("OPENCABINET");
		Flow.miTio.alarmOff();
		Flow.timerFascia();
		Flow.isAdminTime = true;
		
		
		return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
	}
	
	private String TurnOffAlarm() {
		System.out.println("TURNOFFALARM");
		Flow.miTio.alarmOff();
		
		return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
	}
	
	
	
	
	
	String mensaje;
	int montoBajado = 0;
	
	private String FlushJcms() {
		
		flushing = true;
		
		Flow.redirect(Flow.panelSplash);
				
		//Mandamos a fuera de linea en lo que esta cosa baja los billetes....

		mensaje = JcmGlobalData.rec1bill1Denom + "x" + JcmGlobalData.rec1bill1Available	+ ";" 
				+ JcmGlobalData.rec1bill2Denom + "x" + JcmGlobalData.rec1bill2Available + ";" 
				+ JcmGlobalData.rec2bill1Denom + "x" + JcmGlobalData.rec2bill1Available + ";" 
				+ JcmGlobalData.rec2bill2Denom + "x" + JcmGlobalData.rec2bill2Available;
	
		montoBajado = (JcmGlobalData.rec1bill1Denom * JcmGlobalData.rec1bill1Available)
				+ (JcmGlobalData.rec1bill2Denom * JcmGlobalData.rec1bill2Available) 
				+ (JcmGlobalData.rec2bill1Denom * JcmGlobalData.rec2bill1Available) 
				+ (JcmGlobalData.rec2bill2Denom * JcmGlobalData.rec2bill2Available);
		
		System.out.println("FLUSHJCM [" + montoBajado + "] " +  mensaje );
		logger.info("FLUSHJCM [" + montoBajado + "] " +  mensaje );		
		
		JcmGlobalData.jcm1cass1Flushed = false;
		JcmGlobalData.jcm1cass2Flushed = false;
		JcmGlobalData.jcm2cass1Flushed = false;
		JcmGlobalData.jcm2cass2Flushed = false;
		
		RaspiAgent.WriteToJournal("ADMIN",montoBajado ,0, "", "MANAGER", "FLUSHJCM Flushing " + mensaje, AccountType.None, TransactionType.Administrative);		
		
		//JCM 1
		// primero el inhibit (que siempre debe estar deshabilitado pero por si acaso)
		Flow.jcms[0].jcmMessage[3] = 0x01;
		Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);

		if(JcmGlobalData.rec1bill1Available > 0) {
			System.out.println("Bajando jcm1 cassete 1");
			Flow.jcms[0].currentOpertion = jcmOperation.CollectCass1;
			Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x1,Flow.jcms[0].jcmMessage);	
		}
		else{
			System.out.println("Nada que bajar de jcm1 cassete 1");
			JcmGlobalData.jcm1cass1Flushed = true;
			if(JcmGlobalData.rec1bill2Available > 0) {
				System.out.println("Bajando jcm1 cassete 2");
				Flow.jcms[0].currentOpertion = jcmOperation.CollectCass2;
				Flow.jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x2,Flow.jcms[0].jcmMessage);
			}
			else {
				System.out.println("Nada que bajar de jcm1 cassete 2");
				JcmGlobalData.jcm1cass2Flushed = true;
				Flow.jcms[0].currentOpertion = jcmOperation.None;
			}
		}


		//JCM 2
		
		// primero el inhibit (que siempre debe estar deshabilitado pero por si acaso)
		Flow.jcms[1].jcmMessage[3] = 0x01;
		Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);

		if(JcmGlobalData.rec2bill1Available > 0) {
			System.out.println("Bajando jcm2 cassete 1");
			Flow.jcms[1].currentOpertion = jcmOperation.CollectCass1;
			Flow.jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x1,Flow.jcms[1].jcmMessage);
		}
		else{
			System.out.println("Nada que bajar de jcm2 cassete 1");
			JcmGlobalData.jcm2cass1Flushed = true;
			if(JcmGlobalData.rec2bill2Available > 0) {
				System.out.println("Bajando jcm2 cassete 2");
				Flow.jcms[1].currentOpertion = jcmOperation.CollectCass2;
				Flow.jcms[1].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4b, (byte) 0x0, (byte) 0x2,Flow.jcms[1].jcmMessage);
			}
			else {
				System.out.println("Nada que bajar de jcm2 cassete 2");
				JcmGlobalData.jcm2cass2Flushed = true;
			}						
		}
		
		Timer flushTimer = new Timer();

		flushTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {			
				
				System.out.println("WAITING FLUSH ... " + JcmGlobalData.jcm1cass1Flushed  + " " +  JcmGlobalData.jcm1cass2Flushed  + " " +  JcmGlobalData.jcm2cass1Flushed  + " " +  JcmGlobalData.jcm2cass2Flushed);
				
				if(JcmGlobalData.jcm1cass1Flushed && JcmGlobalData.jcm1cass2Flushed && JcmGlobalData.jcm2cass1Flushed && JcmGlobalData.jcm2cass2Flushed) {
					
					System.out.println("FINISHED FLUSHING...");
					
					Flow.jcms[0].currentOpertion = jcmOperation.None;
					Flow.jcms[1].currentOpertion = jcmOperation.None;
					
					//Seteamos los caseteros a 0.
					System.out.println("Seteamos los caseteros a 0");					
					for(int i = 1; i < 5; i++) {
						Config.SetPersistence("Cassette" + i + "Original", "0");
						Config.SetPersistence("Cassette" + i + "Dispensed", "0");
						Config.SetPersistence("Cassette" + i + "Rejected", "0");
						Config.SetPersistence("Cassette" + i + "Total", "0");
					}
					
					RaspiAgent.WriteToJournal("ADMIN",montoBajado, 0, "", "MANAGER", "FLUSHJCM Flushed " + mensaje, AccountType.None, TransactionType.Administrative);
										
					//Tomamos de nuevo los valores de los casseetteesss de reciclado
					
					Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
							Flow.jcms[0].jcmMessage);
				
					Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
							Flow.jcms[1].jcmMessage);
					
					Flow.panelIdle.setBackground("./images/Scr7SinEfectivo.png");
					Flow.redirect(Flow.panelIdle);
					
					flushing = false;
					
					flushTimer.cancel();
					return;
				}
			}
		}, TimeUnit.SECONDS.toMillis(5),TimeUnit.SECONDS.toMillis(10)); 		
		
		
		return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
	}

	private String UploadFile(EnvironmentVariables envVars) {
		
		System.out.println("UploadFile");

		String[] array;
		Map<String,String> parameters = new HashMap<String,String>();

		System.out.println("-- " + envVars.Parameter);
		//if(envVars.Parameter.contains("�")) {
		array = envVars.Parameter.split(",");
		for (String data : array) {
			System.out.println("-->>" + data);
			String[] paramData = data.split("�");
			System.out.println("-->>>>" + paramData[0].trim() + " -- " + paramData[1].trim());
			parameters.put(paramData[0].trim(), paramData[1].trim());
		}
		//}

		String[] refId = envVars.Extra.split("�");

		String ftpServerIp = parameters.get("HostName");
		String ftpServerPort = parameters.get("port");
		String ftpUserName = parameters.get("username");
		String ftpPassword = parameters.get("password");
		String ftpPath = parameters.get("path");

		System.out.println(parameters.get("HostName") + " - " +parameters.get("port") + " - " +parameters.get("username") + " - " +parameters.get("password") + " - " +parameters.get("path") );

		SftpUtils sftpClient = new SftpUtils(ftpServerIp,ftpServerPort,ftpUserName,ftpPassword,ftpPath);	

		try {
			System.out.println("Conectando...");
			sftpClient.connect();
			System.out.println("...Conectado");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String file = parameters.get("SourceFile");
		System.out.println("Solicitud de upload para [" + file + "]");

		String[] folders = {"./downloads","./crashes","./logs" }; //Constants.DownloadsDir, Constants.CrashDir, Constants.LogsDir
		String foundFolder = "";

		boolean found = false;
		for (final String folder : folders) {
			File fileEntry = new File(folder + "/" +  file);
			System.out.println("File [" + folder + "/" +  file + "]");
			if (fileEntry.exists()) {
				System.out.println("Archivo encontrado");
				foundFolder = folder;
				found = true;
				break;					
			}
		}


		HashMap<String, Object> headers = new HashMap<String,Object>();
		headers.put("refId", refId[1]);


		//Version uno lo mandamos en vivo

		if(!found) { 			
			RespondCommand("{\"ReturnValue\":\"NOK:File not found\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}",headers);
		} 
		else {
			try {
				sftpClient.upload(foundFolder + "/" + file, ftpPath + "/" + envVars.File + ".filemanager");
				sftpClient.rename(ftpPath + "/" + envVars.File + ".filemanager", ftpPath + "/" + envVars.File);

				RespondCommand("{\"ReturnValue\":\"DONE\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}", headers);

				return "Pentomino-ReturnValue: OK";
			} catch (JSchException | SftpException e) {
				// TODO Auto-generated catch block

				RespondCommand("{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}", headers);

				e.printStackTrace();
				return "Pentomino-ReturnValue: NOK";
			}
		}	

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


		System.out.println("ReceiveFile");

		String[] array;
		Map<String,String> parameters = new HashMap<String,String>();

		System.out.println("-- " + envVars.Parameter);
		//if(envVars.Parameter.contains("�")) {
		array = envVars.Parameter.split(",");
		for (String data : array) {
			System.out.println("-->>" + data);
			String[] paramData = data.split("�");
			System.out.println("-->>>>" + paramData[0].trim() + " -- " + paramData[1].trim());
			parameters.put(paramData[0].trim(), paramData[1].trim());
		}
		//}

		String[] refId = envVars.Extra.split("�");

		String ftpServerIp = parameters.get("HostName");
		String ftpServerPort = parameters.get("port");
		String ftpUserName = parameters.get("username");
		String ftpPassword = parameters.get("password");
		String ftpPath = parameters.get("path");
		String destination = parameters.get("destFile");


		System.out.println(parameters.get("HostName") + " - " +parameters.get("port") + " - " +parameters.get("username") + " - " +parameters.get("password") + " - " +parameters.get("path") + " - " +parameters.get("destFile") );

		SftpUtils sftpClient = new SftpUtils(ftpServerIp,ftpServerPort,ftpUserName,ftpPassword,ftpPath);	

		try {
			System.out.println("Conectando...");
			sftpClient.connect();
			System.out.println("...Conectado");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String file =  envVars.File;
		System.out.println("Solicitud de download para [" + file + "]");

		HashMap<String, Object> headers = new HashMap<String,Object>();
		headers.put("refId", refId[1]);


		//Version uno lo mandamos en vivo


		try {
			sftpClient.download(ftpPath + "/" + envVars.File, "./downloads/" + destination);

			RespondCommand("{\"ReturnValue\":\"DONE\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\",\"Progress\":100\"}", headers);

			return "Pentomino-ReturnValue: OK";
		} catch (JSchException | SftpException e) {
			// TODO Auto-generated catch block

			RespondCommand("{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}", headers);

			e.printStackTrace();
			return "Pentomino-ReturnValue: NOK";
		}	

	}


	private String ListFiles() {
		System.out.println("DTAServer.ListFiles");

		String files = "";

		String[] folders = {"./downloads","./crashes","./logs" }; //Constants.DownloadsDir, Constants.CrashDir, Constants.LogsDir
		for (final String folder : folders) {
			System.out.println("folder [" + folder + "]");
			File folderEntry = new File(folder);

			if(folderEntry.exists()) {
				for (final File fileEntry : folderEntry.listFiles()) {
					if (fileEntry.isDirectory()) {
						continue;
					} else {
						files += ",\"" + fileEntry.getName() + "\"";
						System.out.println(fileEntry.getName());
					}
				}
			}
		}


		//Quitamos la coma del inicio
		if(files.length() > 1)
			files = files.substring(1);

		String response =  "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\""+ Config.GetDirective("AtmId", "") + "\", \"Files\":[" + files + "]}}";

		return response;        
	}


	private String SetConfigDirective(String keyValue) {
		System.out.println("SetConfigDIrective");

		try {
			if (keyValue.isEmpty())
				return "Pentomino-ReturnValue: NOK: NULL keyValue";

			String newKey = keyValue;
			newKey = newKey.replace("¬", "�");
			String key = newKey.split("�")[0];
			String value = newKey.split("�")[1];
			Config.SetDirective(key, value);

			return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

		} catch (Exception ex) {
			logger.error(ex);
			return "{\"data\":{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

		}
	}


	private String ExecuteFile(String parameter) {
		System.out.println("Parameter [" + parameter + "]");
		Runtime run = Runtime.getRuntime();
		String operationResult = "OK";
		try {
			run.exec("/home/pi/Desktop/Pentomino/downloads/" + parameter);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			operationResult = "EXCEPTION";
			e.printStackTrace();
		}
		
		String returnValue = "{\"data\":{\"ReturnValue\":\"" + operationResult + "\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

		return returnValue;
	
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

		boolean found = false;

		System.out.println("Solicitud de borrado para [" + file + "]");
		String operationResult = "NOT_FOUND";

		String[] folders = {"./downloads","./crashes","./logs" }; //Constants.DownloadsDir, Constants.CrashDir, Constants.LogsDir


		for (final String folder : folders) {
			File fileEntry = new File(folder + "/" +  file);
			System.out.println("File [" + folder + "/" +  file + "]");
			if (fileEntry.exists()) {
				System.out.println("Archivo encontrado");
				found = true;
				break;					
			}
		}


		if(found) {
			File folder = new File("./downloads/"+file);  
			found = folder.delete();
			if(found) {
				operationResult = "OK";
			}			
			else
				operationResult = "EXCEPTION";
		}

		String returnValue = "{\"data\":{\"ReturnValue\":\"" + operationResult + "\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

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
				
		return null;
	}


	private String SetPulsarConfig(String keyValue) {
		System.out.println("SetPulsarConfig");

		try {
			if (keyValue.isEmpty())
				return "Pentomino-ReturnValue: NOK: NULL keyValue";

			String newKey = keyValue;
			newKey = newKey.replace("¬", "�");
			String key = newKey.split("�")[0];
			String value = newKey.split("�")[1];
			Config.SetPulsarParameter(key, value);

			return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

		} catch (Exception ex) {
			logger.error(ex);
			return "{\"data\":{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

		}
	}


	private String DeleteConfigDirective(String keyValue) {
		try {

			if(Config.RemoveDirective(keyValue))
				return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
			else
				return "{\"data\":{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";

		} catch (Exception e) {             
			return "{\"data\":{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";             
		}
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

		return Config.GetAllDirectives();
	}


	private String CloseFlow() {
		// TODO Auto-generated method stub
		return null;
	}


	private String RestartAtm() {
		// TODO Auto-generated method stub

		System.out.println("RestartAtm");
		//String command = "sleep 5; reboot";
		String command = "shutdown -r +1";
		Runtime runtime = Runtime.getRuntime();		
		try {
			runtime.exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"data\":{\"ReturnValue\":\"NOK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
		}

		return "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";


	}


	private String GetPulsarConfigKeys() {

		return Config.GetAllPulsarParams();
	}


	public void findFileInFolder(final File folder) {
		System.out.println("Folder [" + folder.getName() + "]");
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				findFileInFolder(fileEntry);
			} else {
				System.out.println(fileEntry.getName());
			}
		}
	}


	public void listFilesForFolder(final File folder) {
		System.out.println("Folder [" + folder.getName() + "]");
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				System.out.println(fileEntry.getName());
			}
		}
	}



	public void SetupRabbitListener() {

		System.out.println("Receiver.SetupRabbitListener");

		//TODO: Poner validacion de connection status 

		String exchange = Config.GetDirective("BusinessCommandTopic", "command.atm.topic");		 
		String atmId = Config.GetDirective("AtmId", "");
		String topicQueue = "dta.command." + atmId;  //CI99XE0001

		Map<String,Object> map = null;
		BasicProperties props = null;
		Channel channel;
		try {
			
			Connection connection = RabbitMQConnection.getConnection();
			if(connection == null) {
				//TODO: HEWEY AQUI
				System.out.println("Como dijo Yamamoto: todo baila.");
				return;
			}

			channel = connection.createChannel();
			props = new BasicProperties();
			map = new HashMap<String,Object>(); 
			map.put("command.dta." + atmId,"*.dta.broadcast");      
			props = props.builder().headers(map).build();

			channel.queueDeclare(topicQueue, true, false, false, new HashMap<String,Object>());
			channel.queueBind(topicQueue, exchange, "command.dta." + atmId);
			channel.queueBind(topicQueue, exchange, "*.dta.broadcast");

			DeliverCallback deliverCallback = (consumerTag, message) -> {
				String body = new String(message.getBody(), "UTF-8");

				String replyToQueue = message.getProperties().getReplyTo();

				System.out.println("[DTAServer] Received '" + body + "'");

				List<String> dan = Arrays.asList("UPLOADFILE", "UPLOADLOGS", "RECEIVEFILE", "SENDFILE", "SENDLOGS");
				boolean contains = false;
				String comando = "";
				boolean fallo = false;

				try {
					RabbitEnvironmentVariablesContainer envVars = gson.fromJson(body, RabbitEnvironmentVariablesContainer.class);
					comando = envVars.getData().Command;
				}catch(Exception e) {
					fallo = true;
				}

				if(fallo) {				
					SimpleRabbitEnvironmentVariablesContainer envVars = gson.fromJson(body, SimpleRabbitEnvironmentVariablesContainer.class);
					comando = envVars.getData().Command;
				}

				contains = dan.contains(comando);				

				String response = "";


				if(contains) {
					//Received '{"data":{"Command":"SENDFILE","Date":"Thu Jun 04 16:29:06 CDT 2020","File":"5ed967a2602dcf8f1fcf3942"
					//,"Parameter":{"HostName":"11.50.0.7","port":22,"username":"filemanager","password":":s0pJG-Ex1eX","path":"retrieve/in"
					//,"SourceFile":"archivo1.txt"},"Extra":{"refId":"5ed967a2602dcf8f1fcf3943"}}}'
					RabbitEnvironmentVariablesContainer envVarsExtra = gson.fromJson(body, RabbitEnvironmentVariablesContainer.class);
					response = ManageAgent(comando, envVarsExtra.data.Convert());
					return;

				}
				else {
					//[DTAServer] Received '{"data":{"Command":"LISTFILES","Date":"Thu Jun 04 20:15:06 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"RESTARTATM","Date":"Fri Jun 05 13:35:42 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"GOOFFLINE","Date":"Fri Jun 05 13:39:16 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"CLOSEFLOW","Date":"Fri Jun 05 13:42:44 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"CLEANFLOWCACHE","Date":"Fri Jun 05 13:43:41 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"CLOSEPENTOMINO","Date":"Fri Jun 05 13:44:12 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"RUNUPDATE","Date":"Fri Jun 05 13:46:05 CDT 2020","Parameter":null,"Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"SETCONFIG","Date":"Fri Jun 05 13:50:26 CDT 2020","Parameter":"IgnoreInop�false","Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"SETCONFIG","Date":"Fri Jun 05 13:52:49 CDT 2020","Parameter":"directivaValor�mi valor neuvo","Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"DELETECONFIG","Date":"Fri Jun 05 13:54:23 CDT 2020","Parameter":"AtmId[CashManagement]","Extra":""}}'
					//[DTAServer] Received '{"data":{"Command":"SETPULSARCONFIG","Date":"Fri Jun 05 14:49:47 CDT 2020","Parameter":"TransacType�DEBUG","Extra":""}}'
					SimpleRabbitEnvironmentVariablesContainer responseMap = gson.fromJson(body, SimpleRabbitEnvironmentVariablesContainer.class);
					EnvironmentVariables envVar = responseMap.getData().Convert();

					response = ManageAgent(comando, envVar);

				}


				try {
					Producer myProd = new Producer(); 
					myProd.SendResponse(response, "", replyToQueue, null, message.getProperties().getCorrelationId(), replyToQueue);
				}
				catch(Exception e) {
					System.out.println("SendResponse Exception ");
					e.printStackTrace();
				}


			};
			channel.basicConsume("dta.command." + atmId, true, deliverCallback, consumerTag -> { });

		} catch (IOException ioe) {		
			System.out.println("DTAServer bailo con Bertha esto.");
			ioe.printStackTrace();
		}

	}

	public void RespondCommand(String returnValue, HashMap<String, Object> headers) {

		String atmId = Config.GetDirective("AtmId", null);
		if (atmId.isEmpty()) {
			try{
				logger.error("Couldn't listen to topic (AtmId not found);");
			} catch (Exception e) {            	
			}
			return;
		}

		String exchange = Config.GetDirective("BusinessAmqpReplyExchange", "command.atm.topic");

		try {

			if(headers.isEmpty())
				System.out.println("Headers vacios!!!!");


			Map<String,Object> msgHeaders = new HashMap<String, Object>();
			msgHeaders.put("agent", "dta");
			if (headers != null) {
				for (Map.Entry<String, Object> entry : headers.entrySet()) {
					System.out.println(entry.getKey() + ":" + entry.getValue());
					msgHeaders.put(entry.getKey(), entry.getValue());
				}               
			}
			String response = "{\"data\":" + returnValue + "}";

			Producer myProd = new Producer();           

			myProd.SendMessage(response, exchange, "dta.command.response", msgHeaders);	


		}
		catch (Exception e) {

			logger.error("Couldn't send response to rabbit", e);

		}
	}







}
