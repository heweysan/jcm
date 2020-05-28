package rabbitClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AuthenticationFailureException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnection {

	static Connection conn = null;

	public static Connection getConnection(){

		//System.out.println("RabbitMQConnection.Connection");	

		if(conn != null && conn.isOpen()) {
			//System.out.println("RabbitMQConnection.Connection reusing connection");
			return conn;
		}


		//Connection conn = null;

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("redblu_administrator");
		factory.setPassword("4dm1n1str4t0r");
		factory.setVirtualHost("vh_business");
		factory.setHost("11.50.0.7");
		factory.setConnectionTimeout(5000);
		factory.setPort(5672);
		factory.setAutomaticRecoveryEnabled(true); //TODO: HEWEY AQUI QUE ES

		try {
			conn = factory.newConnection();
		} 
		catch(AuthenticationFailureException afe) {		 
			String message = afe.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection AFE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection AFE ALGO EXTRA");
				afe.printStackTrace();
			}
		}	  
		catch (IOException | TimeoutException e) {
			System.out.println("RabbitMQConnection.getConnection IOE");
			String message = e.getMessage();
			if(message!= null)
				System.out.println("RabbitMQConnection.getConnection IOE [" + message + "]");
			else {
				System.out.println("RabbitMQConnection.getConnection IOE ALGO EXTRA");
				e.printStackTrace();
			}			  
		}      
		return conn;
	}






}