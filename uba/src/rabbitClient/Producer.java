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
         
   
   try{
       Connection conn = RabbitMQConnection.getConnection();
       if(conn != null){
         Channel channel = conn.createChannel();  
          
         props = new BasicProperties();
         map = new HashMap<String,Object>(); 
         map.put("acq-source-channel","atm_blu");
         map.put("acq-source-type","event");
         map.put("acq-channelId","red-blu");
         props = props.builder().headers(map).build();
         channel.basicPublish(exchange, routingKey,true, props, jsonMessage.getBytes());
         System.out.println(" Message Sent '" + jsonMessage + "'"); 
         
         channel.close();
         conn.close();
       }
   }catch(Exception e){
       e.printStackTrace();
   }
 }


 public void SendResponse(String message, String exchange, String routingKey,
		  HashMap<String,Object> headers, String correlationId, String replyToQueue) {
        
	   Map<String,Object> map = null;
	   BasicProperties props = null;	         
	   
	   System.out.println(" Message Sent '" + message + "'"); 
	   System.out.println(" exchange '" + exchange + "'"); 
	   System.out.println(" routingKey '" + routingKey + "'"); 
	   System.out.println(" correlationId '" + correlationId + "'"); 
	   System.out.println(" replyToQueue '" + replyToQueue + "'");
	   
	   try{
	       Connection conn = RabbitMQConnection.getConnection();
	       if(conn != null){
	         Channel channel = conn.createChannel();  
	          
	         props = new BasicProperties();
	         props = props.builder().build();
	         //props = props.builder().headers(map).build();
	         props = props.builder().correlationId(correlationId).build();
	         props = props.builder().replyTo(replyToQueue).build();
	         
	         channel.basicPublish(exchange, routingKey,true, props, message.getBytes());
	         
	         
	         System.out.println(" Message Sent '" + message + "'"); 
	         
	         channel.close();
	         conn.close();
	       }
	   }catch(Exception e){
	       e.printStackTrace();
	   }	 

     } 
}


