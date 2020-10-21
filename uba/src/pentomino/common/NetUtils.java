package pentomino.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pentomino.config.Config;
import pentomino.flow.Flow;

public class NetUtils {

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());
	
	public static boolean netIsAvailable() { 
		try {			
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(Config.GetPulsarParam("HostRabbit", "192.168.0.1"), 5672), 5000);			
			socket.close();			
			JcmGlobalData.netIsAvailable = true;
			return true;
		} catch (UnknownHostException e) {		
		} catch (IOException e) {		
		}
		logger.warn("netIsAvailable false");
		System.out.println("netIsAvailable false");
		JcmGlobalData.netIsAvailable = false;
		return false;
	}
	
}
