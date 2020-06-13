package pentomino.jcmagent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import pentomino.config.Config;
import pentomino.flow.gui.admin.AdminLogin;
import pentomino.rabbitqueuebroker.Credentials;
import pentomino.rabbitqueuebroker.RabbitMQConnection;

public class AccountClient {

	private static String _myRoutingKey;
	private static String _atmName;
	private static boolean _listening = false;;
	private static String replyQueueName = "";
	private static String correlationId = "";

	private static Gson gson = new Gson();
	
	static Connection conn = null;
	
	private final static CountDownLatch loginLatch = new CountDownLatch (1);

	private static boolean callbackResults;
	
	public static String LoginAdminAccess(String user, String password) {

		AdminLogin myLogin = new AdminLogin();
		myLogin.username = user;
		myLogin.password = password;
		
		String requestMessage = gson.toJson(gson.toJson(myLogin));		
		
		String response = SendAndReceive("adminAccess", requestMessage);
		
		callbackResults = true;
        loginLatch.countDown ();
		
		return response;
	}

	



	private static String SendAndReceive(String operation, String message) {

		System.out.println("AccountClient SendAndReceive");

		String atmId = Config.GetDirective("AtmId", "");
		String hostname = Config.GetPulsarParam("AccountsHost",""); 
		int port = Integer.parseInt(Config.GetPulsarParam("AccountsPort",""));
		String username = Config.GetPulsarParam("AccountsUser","");
		String password = Config.GetPulsarParam("AccountsPassword","");
		String virtualhost = Config.GetPulsarParam("AccountsVH","");
		String exchange = Config.GetPulsarParam("AccountsExchange","");
		int timeout = 15000;

		try {
			ConfigureConnection(hostname, port, username, password, virtualhost, atmId, message);
		} catch (IOException | TimeoutException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		Worker first = new Worker(7000, loginLatch, "WORKER-1"); 
		
		first.start();
		
		try {
			if (loginLatch.await (5L, TimeUnit.SECONDS)) {
				System.out.println("SALIENDO COOL");
			    return "OK";
			}
			else
			{
				System.out.println("SALIENDO TIMEOUT");
			    return "0"; // Timeout exceeded
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
		
		return "0";
	}


	private static Connection ConfigureConnection(String hostName, int port, String username, String password, String virtualHost, String queue, String requestMessage) throws IOException, TimeoutException, InterruptedException {

		System.out.println("ConfigureConnection");
		
		if(conn != null && conn.isOpen()) {		
			System.out.println("OPEN!");
			return conn;
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

		
		conn = factory.newConnection();
		
		
		Channel channel = conn.createChannel();
		
		/*
	 	queue - the name of the queue    		
	    durable - true if we are declaring a durable queue (the queue will survive a server restart)
	    exclusive - true if we are declaring an exclusive queue
	    autoDelete - true if we are declaring an autodelete queue (server will delete it when no longer in use)
	    arguments - other properties (construction arguments) for the queue 
		 */

		replyQueueName = queue;
		
		channel.queueDeclare(queue, true, false, false, new HashMap<String,Object>());
		channel.basicQos(0,100,false);
		 
		final BlockingQueue<String> response = new ArrayBlockingQueue<>(5);
		
		correlationId = UUID.randomUUID().toString();

		System.out.println("ConfigureConnection basicConsume");
		
		
		DeliverCallback deliverCallback = (consumerTag, message) -> {
			String body = new String(message.getBody(), "UTF-8");
			String replyToQueue = message.getProperties().getReplyTo();
			
			
			
			System.out.println("[Account Cient Validation] Received [" + body + "] replyToQueue [" + replyToQueue + "]" );
			System.out.println("[Account Cient Validation] correlationId sent [" + correlationId + "] correlationId received [" + message.getProperties().getCorrelationId() + "]" );
			//message.getProperties().getCorrelationId()
			//ea.BasicProperties.CorrelationId == correlationId
			
				if(correlationId.equalsIgnoreCase(message.getProperties().getCorrelationId())) {
					System.out.println("SI LLEGO CARNALIN");
						channel.basicAck( message.getEnvelope().getDeliveryTag(), false);
	                    conn.close();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return conn;
	}

	
	// A class to represent threads for which 
	// the main thread waits. 
	static class Worker extends Thread 
	{ 
	    private int delay; 
	    private CountDownLatch latch; 
	  
	    public Worker(int delay, CountDownLatch latch, String name) 
	    { 
	        super(name); 
	        this.delay = delay; 
	        this.latch = latch; 
	    } 
	  
	    @Override
	    public void run() 
	    { 
	        try
	        { 
	            Thread.sleep(delay); 
	            latch.countDown(); 
	            System.out.println(Thread.currentThread().getName() 
	                            + " finished"); 
	        } 
	        catch (InterruptedException e) 
	        { 
	            e.printStackTrace(); 
	        } 
	    } 
	} 


}
