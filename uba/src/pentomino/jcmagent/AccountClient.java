package pentomino.jcmagent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import pentomino.config.Config;
import pentomino.rabbitqueuebroker.Credentials;
import pentomino.rabbitqueuebroker.RabbitMQConnection;

public class AccountClient {

	private static Credentials _credentials;
	private static String _myRoutingKey;
	private static String _atmName;
	private static boolean _listening = false;;

	public static String LoginAdminAccess(String user, String password) {

		String response = SendAndReceive("adminAccess", String.format("%s,%s", user, password));
		return response;
	}

	public static void Init() {

		System.out.println("AccountClient Init");
		
		_credentials = new Credentials();
		_credentials.HostName = Config.GetPulsarParam("AccountsHost","");           
		_credentials.Port = Integer.parseInt(Config.GetPulsarParam("AccountsPort",""));
		_credentials.UserName = Config.GetPulsarParam("AccountsUser","");
		_credentials.Password = Config.GetPulsarParam("AccountsPassword","");
		_credentials.VirtualHost = Config.GetPulsarParam("AccountsVH","");
		_atmName = Config.GetDirective("AtmId", "");;

		if (!_listening) {
			_myRoutingKey = Config.GetPulsarParam("AccountsRoutingKey","");

			String accountsExchange = Config.GetPulsarParam("AccountsExchange","");

			
			System.out.println("_credentials.HostName [" + _credentials.HostName + "]");
			System.out.println("_credentials.Port [" + _credentials.Port + "]");
			System.out.println("_credentials.UserName [" + _credentials.UserName + "]");
			System.out.println("_credentials.Password [" + _credentials.Password + "]");
			System.out.println("_credentials.VirtualHost [" + _credentials.VirtualHost + "]");
			System.out.println("_atmName [" + _atmName + "]");
			System.out.println("_myRoutingKey [" + _myRoutingKey + "]");
			System.out.println("accountsExchange [" + accountsExchange + "]");
			
			ListenForMessages(_atmName, accountsExchange,_myRoutingKey, _credentials);		
			
			/*
			new Thread(() => {
				Consumer.ListenForMessages(_atmName, Config.GetPulsarParam("AccountsExchange"),
						_myRoutingKey, _credentials);                    
			}).Start();

			 */

			_listening = true;
		}

		
	}

	
	 public static void ListenForMessages(String queue, String exchange, String routingKey, Credentials c) {
         Consume(queue, exchange, routingKey, c);
     }

	 public static void ListenForMessages2(String queue, String exchange, String routingKey, Credentials c) {
         Consume2(queue, exchange, routingKey, c);
     }

	private static String SendAndReceive(String operation, String message) {
		
		System.out.println("AccountClient SendAndReceive");
		
		Init();
		int totalSleep = 0;
		String correlationId = "ACC" + UUID.randomUUID().toString();

		Map<String,Object> headers = null;
		headers = new HashMap<String,Object>();
		headers.put("accounts-endpoint","red_blu");   
		headers.put("accounts-source-type","affiliation");

		/*
		new Thread(() => {
			Consumer.ListenForMessages(_atmName, AtmVariables.GetConfig("AccountsExchange"),
					_myRoutingKey, _credentials);
		}).Start();
		 */
		
		String accountsExchange = Config.GetPulsarParam("AccountsExchange","");
		
		ListenForMessages2(_atmName, accountsExchange,_myRoutingKey, _credentials);

		SendMessage(accountsExchange, Config.GetPulsarParam("AccountsQueue",""), message, _atmName, correlationId, "accounts", headers, operation, _credentials);

		_myRoutingKey = "accounts";       

		/*
		while (!Responses.ContainsKey(correlationId) && totalSleep < 60000) {
			totalSleep += 1500;
			Thread.Sleep(1500);
		}

		return Responses.ContainsKey(correlationId) ? Responses[correlationId] : null;
		*/
		
		return "";
	}

	public static boolean SendMessage(String exchange, String queue, String message, String replyToQueue, String correlationId, String routingKey, Map<String, Object> headers, String operationType, Credentials c) {
		try {

			Connection rabbitConn = RabbitMQConnection.getConnection(c);

			Channel channel = rabbitConn.createChannel();

			//QueueFactory.AddQueue(replyToQueue, exchange, null, c);

			///var endcodedMessage = Encoding.UTF8.GetBytes(message);

			BasicProperties props = new BasicProperties();
			if (headers == null) {
				System.out.println("HEaders vacios");
				headers = new HashMap<String,Object>();
			}

			/*
             props = new BasicProperties();
				props = props.builder().build();

				if(headers != null && !headers.isEmpty())
					props = props.builder().headers(map).build();


				//props = props.builder().headers(map).build();
				props = props.builder().correlationId(correlationId).build();
				props = props.builder().replyTo(replyToQueue).build();
			 */

			headers.put("clas-operation-type", operationType);

			props = props.builder().build();
			props = props.builder().headers(headers).build();
			props = props.builder().expiration("20000").build();
			props = props.builder().correlationId(correlationId).build();
			props = props.builder().replyTo(replyToQueue).build();

			//props.SetPersistent(true);

			//channel.basicPublish("ex.cm.topic", "cm.cashin.*",true, props, gson.toJson(requestMessage).getBytes());
			System.out.println("SendMessage [" + exchange + "] [" + routingKey + "] [" + message + "]");
			channel.basicPublish(exchange, routingKey,true, props, message.getBytes());

			return true;
		} catch (Exception e) {

			System.out.println("Exceptipn Men " + e.getMessage());
			/*
				using (var wtr = new StreamWriter("C:\\Pentomino\\Logs\\RabbitClient.txt", true))
				wtr.WriteLine("Producer exception: " + e.Message + Environment.NewLine + e.StackTrace);
			 */

			return false;

		}
	}

	private static void Consume(String queue, String exchange, String routingKey, Credentials c) {

		//var channel = QueueFactory.AddQueue(queue, exchange, null, c);

		//var subscription = new Subscription(channel, queue, false);
		
		System.out.println("AccountClient.Consume");

		
		Map<String,Object> map = null;
		BasicProperties props = null;
		Channel channel;
		try {

			System.out.println("AccountClient.Consume queue [" + queue + "] exchange [" + exchange + "] routingKey [" + routingKey + "]");
			
			Connection connection = RabbitMQConnection.getConnection(c);
			if(connection == null) {
				//TODO: HEWEY AQUI
				System.out.println("Como dijo Yamamoto: todo baila.");
				return;
			}

			channel = connection.createChannel();
			props = new BasicProperties();
			map = new HashMap<String,Object>(); 

			props = props.builder().headers(map).build();

			
			channel.queueDeclare(queue, true, false, false, new HashMap<String,Object>());



			DeliverCallback deliverCallback = (consumerTag, message) -> {
				String body = new String(message.getBody(), "UTF-8");

				String replyToQueue = message.getProperties().getReplyTo();
			};
			
			boolean autoAck = false;	
			
			
			channel.basicConsume(queue, autoAck, deliverCallback, consumerTag -> { });

		} catch (IOException ioe) {		
			System.out.println("DTAServer bailo con Bertha esto.");
			ioe.printStackTrace();
		}
	}


	private static void Consume2(String queue, String exchange, String routingKey, Credentials c) {

		//var channel = QueueFactory.AddQueue(queue, exchange, null, c);

		//var subscription = new Subscription(channel, queue, false);
		
		System.out.println("AccountClient.Consume2");

		
		Map<String,Object> map = null;
		BasicProperties props = null;
		Channel channel;
		try {

			Connection connection = RabbitMQConnection.getConnection2(c);
			if(connection == null) {
				//TODO: HEWEY AQUI
				System.out.println("Como dijo Yamamoto: todo baila.");
				return;
			}

			channel = connection.createChannel();
			props = new BasicProperties();
			map = new HashMap<String,Object>(); 

			props = props.builder().headers(map).build();

			channel.queueDeclare(queue, true, false, false, new HashMap<String,Object>());



			DeliverCallback deliverCallback = (consumerTag, message) -> {
				String body = new String(message.getBody(), "UTF-8");

				String replyToQueue = message.getProperties().getReplyTo();

				System.out.println("[DTAServer] Received '" + body + "'");


				/*
				String response = "";

				try {
					Producer myProd = new Producer(); 
					myProd.SendResponse(response, "", replyToQueue, null, message.getProperties().getCorrelationId(), replyToQueue);
				}
				catch(Exception e) {
					System.out.println("SendResponse Exception ");
					e.printStackTrace();
				}
				*/

			};
			
			boolean autoAck = false;	
			
			
			channel.basicConsume(queue, autoAck, deliverCallback, consumerTag -> { });

		} catch (IOException ioe) {		
			System.out.println("DTAServer bailo con Bertha esto.");
			ioe.printStackTrace();
		}
	}


	public void SetupRabbitListener(String queue, String exchange, String routingKey, Credentials c) {

		System.out.println("AccountClient.SetupRabbitListener");

	
		Map<String,Object> map = null;
		BasicProperties props = null;
		Channel channel;
		try {

			Connection connection = RabbitMQConnection.getConnection(c);
			if(connection == null) {
				//TODO: HEWEY AQUI
				System.out.println("Como dijo Yamamoto: todo baila.");
				return;
			}

			channel = connection.createChannel();
			props = new BasicProperties();
			map = new HashMap<String,Object>(); 

			props = props.builder().headers(map).build();

			channel.queueDeclare(queue, true, false, false, new HashMap<String,Object>());



			DeliverCallback deliverCallback = (consumerTag, message) -> {
				String body = new String(message.getBody(), "UTF-8");

				String replyToQueue = message.getProperties().getReplyTo();

				System.out.println("[DTAServer] Received '" + body + "'");


				/*
				String response = "";

				try {
					Producer myProd = new Producer(); 
					myProd.SendResponse(response, "", replyToQueue, null, message.getProperties().getCorrelationId(), replyToQueue);
				}
				catch(Exception e) {
					System.out.println("SendResponse Exception ");
					e.printStackTrace();
				}
				*/

			};
			
			boolean autoAck = false;	
			
			
			channel.basicConsume(queue, autoAck, deliverCallback, consumerTag -> { });

		} catch (IOException ioe) {		
			System.out.println("DTAServer bailo con Bertha esto.");
			ioe.printStackTrace();
		}

	}



}
