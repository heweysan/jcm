package rabbitClient;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Producer {


	private static String exchange = "ex.agents.topic";
	private static String routingKey = "agent.event.eja";


	public void publish(String jsonMessage){
		Map<String,Object> map = null;
		BasicProperties props = null;

		System.out.println("Producer.publish");	

		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

				props = new BasicProperties();
				map = new HashMap<String,Object>(); 
				map.put("acq-source-channel","atm_blu");
				map.put("acq-source-type","event");
				map.put("acq-channelId","red-blu");
				props = props.builder().headers(map).build();
				channel.basicPublish(exchange, routingKey,true, props, jsonMessage.getBytes());
				System.out.println(" Message Sent '" + jsonMessage + "'"); 

				channel.close();
				//rabbitConn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * @param message 
	 * @param exchange
	 * @param routingKey
	 * @param headers
	 * @param correlationId
	 * @param replyToQueue
	 */

	public void SendResponse(String message, String exchange, String routingKey,
			Map<String,String> headers, String correlationId, String replyToQueue) {

		System.out.println("Producer.SendResponse");	

		Map<String,Object> map = null;
		BasicProperties props = null;	         



		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

				props = new BasicProperties();
				props = props.builder().build();
				
				if(headers != null && !headers.isEmpty())
					props = props.builder().headers(map).build();
				
				
				//props = props.builder().headers(map).build();
				props = props.builder().correlationId(correlationId).build();
				props = props.builder().replyTo(replyToQueue).build();

				System.out.println(" Message Sent '" + message + "'"); 
				System.out.println(" exchange '" + exchange + "'"); 
				System.out.println(" routingKey '" + routingKey + "'"); 
				System.out.println(" correlationId '" + correlationId + "'"); 
				System.out.println(" replyToQueue '" + replyToQueue + "'");
				
				channel.basicPublish(exchange, routingKey,true, props, message.getBytes());	         

				System.out.println("Message Sent ... OK");

				channel.close();
				//rabbitConn.close();
			}
		}catch(Exception e){
			System.out.println("Producer.SendResponse Exception");
			e.printStackTrace();
		}	 

	} 
	
	public void SendMessage(String message, String exchange, String routingKey,Map<String,Object> headers) {

		System.out.println("Producer.SendMessage");	

		Map<String,Object> map = null;
		BasicProperties props = null;	         

		if(headers.isEmpty())
    		System.out.println("SendMessage headers vacios!!!!");
		
		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

				props = new BasicProperties();
				props = props.builder().build();
				
				//if(headers != null && !headers.isEmpty())
					props = props.builder().headers(map).build();
				
				
				props = props.builder().headers(headers).build();
				

				System.out.println(" Message Sent '" + message + "'"); 
				System.out.println(" exchange '" + exchange + "'"); 
				System.out.println(" routingKey '" + routingKey + "'"); 
				
				System.out.println("PROPS");
				for (Map.Entry<String, Object> entry : props.getHeaders().entrySet()) {
			        System.out.println("\t" + entry.getKey() + ":" + entry.getValue());
			    }
				
				
				channel.basicPublish(exchange, routingKey,true, props, message.getBytes());	         

				System.out.println("Message Sent ... OK");

				channel.close();
				//rabbitConn.close();
			}
		}catch(Exception e){
			System.out.println("Producer.SendMessage Exception");
			e.printStackTrace();
		}	 

	} 

}


