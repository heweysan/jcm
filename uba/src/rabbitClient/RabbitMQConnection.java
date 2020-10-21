package rabbitClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.AuthenticationFailureException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import pentomino.config.Config;
import pentomino.flow.Flow;

public class RabbitMQConnection {

	static Connection conn = null;

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());
	
	public static Connection getConnection(){
	
		if(conn != null && conn.isOpen()) {			
			return conn;
		}

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("redblu_administrator");
		factory.setPassword("4dm1n1str4t0r");
		factory.setVirtualHost("vh_business");
		factory.setHost(Config.GetPulsarParam("HostRabbit", "192.168.0.1"));
		factory.setConnectionTimeout(5000);
		factory.setPort(5672);
		factory.setAutomaticRecoveryEnabled(true); 

		try {
			conn = factory.newConnection();
		} 
		catch(AuthenticationFailureException afe) {
			logger.error(afe);
			String message = afe.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection AFE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection AFE");
				afe.printStackTrace();
			}
		}	  
		catch (IOException | TimeoutException e) {
			logger.error(e);			
			String message = e.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection IOE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection IOE");
				e.printStackTrace();
			}			  
		}      
		return conn;
	}






}