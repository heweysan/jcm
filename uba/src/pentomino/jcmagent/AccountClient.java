package pentomino.jcmagent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import pentomino.config.Config;
import pentomino.flow.gui.admin.AdminLogin;


public class AccountClient {

	private static String replyQueueName = "";
	private static String correlationId = "";

	private static Gson gson = new Gson();

	static Connection conn = null;

	private static CountDownLatch loginLatch = new CountDownLatch (1);
	private static String returnMessage = "";

	public String LoginAdminAccess(String user, String password) {
		
		AdminLogin myLogin = new AdminLogin();
		myLogin.username = user;
		myLogin.password = password;

		returnMessage = "0";		
		
		SendAndReceive("adminAccess", gson.toJson(myLogin));
		
		System.out.println("LoginAdminAccess returnMessage [" + returnMessage + "]");
		return returnMessage;
	}

	private boolean SendAndReceive(String operation, String message) {

		System.out.println("AccountClient SendAndReceive");

		String atmId = Config.GetDirective("AtmId", "");
		String hostname = Config.GetPulsarParam("AccountsHost",""); 
		int port = Integer.parseInt(Config.GetPulsarParam("AccountsPort",""));
		String username = Config.GetPulsarParam("AccountsUser","");
		String password = Config.GetPulsarParam("AccountsPassword","");
		String virtualhost = Config.GetPulsarParam("AccountsVH","");		
			
		try {
			ConfigureConnectionOrig(hostname, port, username, password, virtualhost, atmId, message);
		} catch (IOException | TimeoutException | InterruptedException e) {
			
			e.printStackTrace();
			return false;
		}		
		
		return true;
	}

	
	private static boolean GetConnection(String hostName, int port, String username, String password, String virtualHost, String queue) {
		
		System.out.println("GetConnection");
		
		if(conn != null && conn.isOpen()) {		
			System.out.println("OPEN!");
			return true;
		}
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setVirtualHost(virtualHost);
		factory.setHost(hostName);
		factory.setConnectionTimeout(5000);
		factory.setPort(port);
		factory.setAutomaticRecoveryEnabled(false);
		factory.setTopologyRecoveryEnabled(false);
		factory.setNetworkRecoveryInterval(20000);
		factory.setRequestedHeartbeat(10);


		try {
			conn = factory.newConnection();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	
	private void ConfigureConnectionOrig(String hostName, int port, String username, String password, String virtualHost, String queue, String requestMessage) throws IOException, TimeoutException, InterruptedException {

		System.out.println("ConfigureConnection");
				
		GetConnection(hostName, port, username, password, virtualHost, queue);

		Channel channel = conn.createChannel();

		/*
	 	queue - the name of the queue    		
	    durable - true if we are declaring a durable queue (the queue will survive a server restart)
	    exclusive - true if we are declaring an exclusive queue
	    autoDelete - true if we are declaring an autodelete queue (server will delete it when no longer in use)
	    arguments - other properties (construction arguments) for the queue 
		 */

		
		replyQueueName = queue;
		
		loginLatch = new CountDownLatch (1);
		
		channel.queueDeclare(queue, true, false, false, new HashMap<String,Object>());
		channel.basicQos(0,20,false);


		correlationId = UUID.randomUUID().toString();

		System.out.println("ConfigureConnection basicConsume");

		DeliverCallback deliverCallback = (consumerTag, message) -> {
			String body = new String(message.getBody(), "UTF-8");

			System.out.println(" [Account Client Validation] Received [" + body + "] correlationId sent [" + correlationId + "] correlationId received [" + message.getProperties().getCorrelationId() + "]" );


			if(correlationId.equalsIgnoreCase(message.getProperties().getCorrelationId())) {
				System.out.println("SI LLEGO CARNALIN [" + body + "]");
				channel.basicAck( message.getEnvelope().getDeliveryTag(), false);							
				returnMessage = body;
				System.out.println("ConfigureConnectionOrig returnMessage [" + returnMessage + "]");				
				conn.close();
				loginLatch.countDown();				
			}

		};		

		channel.basicConsume(replyQueueName, false, deliverCallback, consumerTag -> { });	
		
		Map<String,Object> map = null;
		map = new HashMap<String,Object>();
		map.put("accounts-endpoint","red_blu");
		map.put("accounts-source-type","accountClient");
		map.put("operation-type","adminAccess");


		AMQP.BasicProperties props = new AMQP.BasicProperties
				.Builder()
				.correlationId(correlationId)
				.replyTo(replyQueueName)
				.headers(map)
				.build();

		String routingKey = "pentomino.affiliations.atms";



		try {
			System.out.println("ConfigureConnection basicPublish [" + requestMessage + "]");
			String AccountsExchange = Config.GetPulsarParam("AccountsExchange","");
			System.out.println("AccountsExchange [" + AccountsExchange + "]");
			channel.basicPublish(AccountsExchange,routingKey,true, props, requestMessage.getBytes());
			
			try {
				if (loginLatch.await (15L, TimeUnit.SECONDS)) {
					System.out.println("SALIENDO COOL");					
				}
				else
				{
					System.out.println("SALIENDO TIMEOUT");
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}



}
