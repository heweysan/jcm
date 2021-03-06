package rabbitClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.vo.CmMessageRequest;
import pentomino.config.Config;
import pentomino.flow.EventListenerClass;
import pentomino.flow.MyEvent;


public class CmListener {

	/*
	Todos van al vh: vh_busness con:

	exchange:                "ex.cm.topic"
	routingKey:              "cm.withdrawals.*"

	Mensaje de Notificaci�n
	{	
	    "reference":"5ec4801d3bb2f825e3daf0fa", 
	    "amount":22000, 
	    "token":"123456"	
	}

	Para recibirlo yo creo que una buena nomenclatura ser�a:

	cm.events.CI01GL0001


	Contrato para retiros
	props.headers.put('operation-type','process-withdrawal')

	{
	    "atmId": "C IXXGS0020" ,
	    "operatorId": 7007,
	    "password": "p4ssw0rd",
	    "amount": 22000,
	    "token": 123456,
	    "operationDateTimeMilliseconds":1586844501395,
	    "reference": "5ec389a73bb2f875843672c6",
	    "operationType": "FLEXPOS_WITHDRAWAL"
	}

	Response: {"message":"�xito","success":true,"value":"01960526"}

	Contrato para reversos
	props.headers.put('operation-type','process-reverse')

	{
	    "atmId": "C IXXGS0020" ,
	    "operatorId": 7007,
	    "password": "5123",
	    "amount": 22000,
	    "operationDateTimeMilliseconds":1586844501395,
	    "movementId": "01870526",
	    "operationType": "FLEXPOS_WITHDRAWAL"
	}

	Response: {"message":"�xito","value":"01970526","success":true}
	 */
	private static Gson gson = new Gson();

	public void SetupRabbitListener() {

		//System.out.println("CmListener.SetupRabbitListener");

		String atmId = Config.GetDirective("AtmId", "");
		
		String topicQueue = "cm.events." + atmId;

		Channel channel;
		try {

			Connection connection = RabbitMQConnection.getConnection();

			if(connection == null) {
				return;
			}


			channel = connection.createChannel();

			/*
			 	queue - the name of the queue    		
			    durable - true if we are declaring a durable queue (the queue will survive a server restart)
			    exclusive - true if we are declaring an exclusive queue
			    autoDelete - true if we are declaring an autodelete queue (server will delete it when no longer in use)
			    arguments - other properties (construction arguments) for the queue 
			 */

			boolean durable = false;
			boolean exclusive = false;
			boolean autoDelete = true;


			channel.queueDeclare(topicQueue, durable, exclusive, autoDelete, new HashMap<String,Object>());			


			DeliverCallback deliverCallback = (consumerTag, message) -> {
				/*
		    	 [x] consumerTag 'amq.ctag-AIOgOkTvOx71bWIbf01e4Q'
		    	 [x] Received '{"reference":"5ecda1f6602dcfd2fc2b0fa7","amount":1230,"token":"357654"}'
		    	 [x] replyToQueue 'null'
				 */
				String body = new String(message.getBody(), "UTF-8");
				String replyToQueue = message.getProperties().getReplyTo();

				System.out.println("consumerTag [" + consumerTag + "]");	         
				System.out.println("Received [" + body + "]");
				System.out.println("replyToQueue [" + replyToQueue + "]");	         

				Map<?, ?> responseMap = gson.fromJson(body, Map.class);
				CmMessageRequest myMsg = new CmMessageRequest();
				myMsg.amount = (double) responseMap.get("amount");
				myMsg.token = (String) responseMap.get("token");
				myMsg.reference = (String) responseMap.get("reference");
				myMsg.cashedout = 0;

				CmQueue.addPendingWithdrawal(myMsg);

				EventListenerClass.fireMyEvent(new MyEvent("widthdrawalRequest")); //Peticion de retiro

			};		

			channel.basicConsume(topicQueue, true, deliverCallback, consumerTag -> { });

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



}
