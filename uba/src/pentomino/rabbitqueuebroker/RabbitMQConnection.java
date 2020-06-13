package pentomino.rabbitqueuebroker;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AuthenticationFailureException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnection {

	static Connection conn = null;
	static Connection conn2 = null;

	/**
	 * 
	 * @param queueName
	 * @param exchange
	 * @param arguments
	 * @param c
	 * @return Connection
	 */
	public static Connection getConnection( Credentials c){		

		if(conn != null && conn.isOpen()) {			
			return conn;
		}
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(c.UserName);
		factory.setPassword(c.Password);
		factory.setVirtualHost(c.VirtualHost);
		factory.setHost(c.HostName);
		factory.setConnectionTimeout(5000);
		factory.setPort(c.Port);
		factory.setAutomaticRecoveryEnabled(true); 
		
		try {
			conn = factory.newConnection();
		} 
		catch(AuthenticationFailureException afe) {		 
			String message = afe.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection AFE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection AFE");
				afe.printStackTrace();
			}
		}	  
		catch (IOException | TimeoutException e) {
			System.out.println("RabbitMQConnection.getConnection IOE");
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

	public static Connection getConnection2( Credentials c){		

		if(conn2 != null && conn2.isOpen()) {			
			return conn2;
		}
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(c.UserName);
		factory.setPassword(c.Password);
		factory.setVirtualHost(c.VirtualHost);
		factory.setHost(c.HostName);
		factory.setConnectionTimeout(5000);
		factory.setPort(c.Port);
		factory.setAutomaticRecoveryEnabled(true); 

		try {
			conn2 = factory.newConnection();
		} 
		catch(AuthenticationFailureException afe) {		 
			String message = afe.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection AFE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection AFE");
				afe.printStackTrace();
			}
		}	  
		catch (IOException | TimeoutException e) {
			System.out.println("RabbitMQConnection.getConnection IOE");
			String message = e.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection IOE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection IOE");
				e.printStackTrace();
			}			  
		}      
		return conn2;
	}




}