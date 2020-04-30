package rabbitClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import pentomino.config.Configuration;
 
public class Receiver {
  
 public void receive(){
   try{
       Connection conn = RabbitMQConnection.getConnection();
       if(conn != null){
         Channel channel = conn.createChannel();
        
         Consumer consumer1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" Message Received Queue 1 '" + message + "'");
            }
         };
         channel.basicConsume(HeaderExchange.QUEUE_NAME_1, true, consumer1);
        
         channel.close();
         conn.close();
       }
   }catch(Exception e){
      e.printStackTrace();
   }
   
   
 }
 
 
 public void SetupRabbitListener() {
	 
	 //TODO: Poner validacion de connection status
	 
	 //ListenForCommands();
	 
	 String exchange = Configuration.GetDirective("BusinessCommandTopic", "command.atm.topic");
	 String atmId = Configuration.GetDirective("AtmId", "");
	 
	 
	 /*
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
	        	         
	         channel.close();
	         conn.close();
	       }
	   }catch(Exception e){
	       e.printStackTrace();
	   }
	 */
	 
	 Map<String,Object> map = null;
	 BasicProperties props = null;
	Channel channel;
	try {
		
		
		/*
		  var exchange = Config.GetDirective("BusinessCommandTopic", "command.atm.topic");
            var topicQueue = Config.GetDirective("BusinessCommandTopicQueue", "dta.command." + atmId);
            var routingKeys = new[] { "command.dta." + atmId, "*.dta.broadcast" };
		 */
		
		Connection connection = RabbitMQConnection.getConnection();
		channel = connection.createChannel();
		props = new BasicProperties();
        map = new HashMap<String,Object>(); 
        map.put("command.dta.CI99XE0001","*.dta.broadcast");      
        props = props.builder().headers(map).build();
        
        channel.queueDeclare("dta.command.CI99XE0001", true, false, false, new HashMap<String,Object>());
		channel.queueBind("dta.command.CI99XE0001", "command.atm.topic", "command.dta.CI99XE0001");
		channel.queueBind("dta.command.CI99XE0001", "command.atm.topic", "*.dta.broadcast");
		
	     DeliverCallback deliverCallback = (consumerTag, message) -> {
	         String body = new String(message.getBody(), "UTF-8");
	         String replyToQueue = message.getProperties().getReplyTo();
	         
	         System.out.println(" [x] consumerTag '" + consumerTag + "'");	         
	         System.out.println(" [x] Received '" + body + "'");
	         System.out.println(" [x] replyToQueue '" + replyToQueue + "'");
	         
	         
	         /* ESTO SE AHCE EN OTRO LADO */
	         String response =  "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"CI99XE0001\", \"Files\":[\"javaDummyFile1.zip\",\"javaDummyFile2.txt\"]}}";
	         
	        Producer myProd = new Producer(); 
	        myProd.SendResponse(response, "", replyToQueue, null,
                     message.getProperties().getCorrelationId(), replyToQueue);
	         
	         
	     };
	     channel.basicConsume("dta.command.CI99XE0001", true, deliverCallback, consumerTag -> { });
	     
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	 
 }
 
 
 public void ListenForCommands() {
	 
	 String atmId = Configuration.GetDirective("AtmId", "");
	 String exchange = Configuration.GetDirective("BusinessCommandTopic", "command.atm.topic");
	 String topicQueue = Configuration.GetDirective("BusinessCommandTopicQueue", "dta.command." + atmId);
	 String routingKeys[] = new String[] { "command.dta." + atmId, "*.dta.broadcast" };
	 
	 
	 try{
	       Connection conn = RabbitMQConnection.getConnection();
	       if(conn != null){
	         Channel channel = conn.createChannel();
	        
	         Consumer consumer1 = new DefaultConsumer(channel) {
	            @Override
	            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	              String message = new String(body, "UTF-8");
	              System.out.println(" Message Received Queue 1 '" + message + "'");
	            }
	         };
	         channel.basicConsume(HeaderExchange.QUEUE_NAME_1, true, consumer1);
	        
	         channel.close();
	         conn.close();
	       }
	   }catch(Exception e){
	      e.printStackTrace();
	   }
	 
	 
 }
 
}