package rabbitClient;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Channel;


public class ConnectRabbit  {
	
	private String Server;
	private int Port;
	private String VirtualHost; 
	private String User;
	private String Password;
	private int Retries;
	
	public String getServer() {
		return Server;
	}
	public void setServer(String server) {
		Server = server;
	}
	public int getPort() {
		return Port;
	}
	public void setPort(int port) {
		Port = port;
	}
	public String getVirtualHost() {
		return VirtualHost;
	}
	public void setVirtualHost(String virtualHost) {
		VirtualHost = virtualHost;
	}
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public int getRetries() {
		return Retries;
	}
	public void setRetries(int retries) {
		Retries = retries;
	}
	
	
	public ConnectRabbit(String server, int port, String virtualHost, String user, String password, int retries) {
        Server = server;
        Port = port;
        VirtualHost = virtualHost;
        User = user;
        Password = password;
        Retries = retries;
    }
	
	public void OpenConnection(String hostName, int port, String username, String password, String virtualHost, Boolean recoveryEnabled, int connTimeout) throws IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setAutomaticRecoveryEnabled(true);		
		factory.setTopologyRecoveryEnabled(true);
		factory.setNetworkRecoveryInterval(5000);
		factory.setHost(hostName);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setVirtualHost(virtualHost);
		factory.setRequestedHeartbeat(10);
		factory.setConnectionTimeout(connTimeout);
				
		
		try (Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel()) {
			
			/*
			//creamos los headers
			MessageProperties props = MessagePropertiesBuilder.newInstance().setContentType(MessageProperties.CONTENT_TYPE_JSON).build();
			props.setHeader("headerKey1", "headerValue1");

			Message msg = new Message("{'body':'value1','body2':value2}".getBytes(), props);        

			
			
			rabbitTemplate.send("exchange.direct.one", new String(), msg);
			*/
			
			//BasicProperties basicProperties = new BasicProperties.Builder().headers(headers);
			
			
			/*		
			channel.queueDeclare(QUEUE_NAME,true, false, false, null);
			String message = "Hello World!";
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");
			*/
		}
		
		
		}

}
