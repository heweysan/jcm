package rabbitClient;

import java.util.HashMap;
import java.util.Map;
 
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
//import com.rabbitmq.helper.ExchangeType;
 
public class HeaderExchange {
  
 public static String EXCHANGE_NAME = "header-exchange";
 public static String QUEUE_NAME_1 = "header-queue-1";
 public static String QUEUE_NAME_2 = "header-queue-2";
 public static String QUEUE_NAME_3 = "header-queue-3";
 
	private static String exchange = "ex.agents.topic";
	private static String routingKey = "agent.event.eja";
	private static String routingKeyStaled = "";
	private static int staledTime = 300;
  
 public static String ROUTING_KEY = "";
  
 public void createExchangeAndQueue(){
   Map<String,Object> map = null; 
   try{
      Connection conn = RabbitMQConnection.getConnection();
      if(conn != null){
        Channel channel = conn.createChannel(); 
  
        channel.exchangeDeclare(exchange, ExchangeType.HEADER.getExchangeName(), true);
        /*
        return new Dictionary<string, object> {
            {"acq-source-channel", "atm_blu"},
            {"acq-source-type", "event"},
            {"acq-channelId", "red-blu"}
        };        
        */
        
        map = new HashMap<String,Object>();   
        map.put("acq-source-channel","atm_blu");
        map.put("acq-source-type","event");
        map.put("acq-channelId","red-blu");
        channel.queueDeclare(QUEUE_NAME_1, true, false, false, null);
        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, ROUTING_KEY ,map);
        
        
        /*
        // First Queue 
        map = new HashMap<String,Object>();   
        map.put("x-match","any");
        map.put("First","A");
        map.put("Fourth","D");
        channel.queueDeclare(QUEUE_NAME_1, true, false, false, null);
        channel.queueBind(QUEUE_NAME_1, EXCHANGE_NAME, ROUTING_KEY ,map);
  
        // Second Queue 
        map = new HashMap<String,Object>();   
        map.put("x-match","any");
        map.put("Fourth","D");
        map.put("Third","C");
        channel.queueDeclare(QUEUE_NAME_2, true, false, false, null);
        channel.queueBind(QUEUE_NAME_2, EXCHANGE_NAME, ROUTING_KEY ,map);
  
        // Third Queue 
        map = new HashMap<String,Object>();   
        map.put("x-match","all");
        map.put("First","A");
        map.put("Third","C"); 
        channel.queueDeclare(QUEUE_NAME_3, true, false, false, null);
        channel.queueBind(QUEUE_NAME_3, EXCHANGE_NAME, ROUTING_KEY ,map);
        */
        channel.close();
        conn.close();
      }
   }catch(Exception e){
        e.printStackTrace();
   }
 }
 
}
