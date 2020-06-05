package rabbitClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.DeliverCallback;

import pentomino.config.Config;
import pentomino.jcmagent.DTAServer;
 
public class DtaListener {
  
 
 public void SetupRabbitListener() {
	 
	 System.out.println("DtaListener.SetupRabbitListener");
	 
	 //TODO: Poner validacion de connection status 
	 
	 String exchange = Config.GetDirective("BusinessCommandTopic", "command.atm.topic");
	 String atmId = Config.GetDirective("AtmId", "");
	 String topicQueue = "dta.command." + atmId;  //CI99XE0001
	 
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
        map.put("command.dta." + atmId,"*.dta.broadcast");      
        props = props.builder().headers(map).build();
        
        channel.queueDeclare(topicQueue, true, false, false, new HashMap<String,Object>());
        
        /* var routingKeys = new[] { "command.dta." + atmId, "*.dta.broadcast" }; */
		channel.queueBind(topicQueue, exchange, "command.dta." + atmId);
		channel.queueBind(topicQueue, exchange, "*.dta.broadcast");
		
	     DeliverCallback deliverCallback = (consumerTag, message) -> {
	    	 
	    	 System.out.println("DtaListener message received");
	    	 
	         String body = new String(message.getBody(), "UTF-8");
	         String replyToQueue = message.getProperties().getReplyTo();
	         
	         System.out.println(" [x] consumerTag '" + consumerTag + "'");	         
	         System.out.println(" [x] Received '" + body + "'");
	         System.out.println(" [x] replyToQueue '" + replyToQueue + "'");	         
	         	         
	         /* ESTO SE HACE EN OTRO LADO */
	         String response =  "{\"data\":{\"ReturnValue\":\"OK\", \"AtmId\":\"CIXXGS0020\", \"Files\":[\"javaDummyFile1.zip\",\"javaDummyFile2.txt\"]}}";
	         
	         Producer myProd = new Producer(); 
	         myProd.SendResponse(response, "", replyToQueue, null, message.getProperties().getCorrelationId(), replyToQueue);         
	         
	     };
	     channel.basicConsume("dta.command." + atmId, true, deliverCallback, consumerTag -> { });
	     
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("DtaListener.SetupRabbitListener exception");
		e.printStackTrace();
	}
	 
 }
 
  
}