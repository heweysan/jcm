package pentomino.jcmagent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import pentomino.common.CryptUtils;
import pentomino.config.Config;
import rabbitClient.DtaListener;
import rabbitClient.RabbitMQConnection;


public class AgentsQueue  implements Runnable{

	private static Gson gson = new Gson();
		
	public static BlockingQueue<AgentMessage> bq = new LinkedBlockingQueue<AgentMessage>();
	
	private volatile boolean terminateRequested;
	
	/*
	public AgentsQueue(BlockingQueue<AgentMessage> myQueue) {
		System.out.println("AgentsQueue starting...");
		bq = myQueue;
	}
	*/
	
	public AgentsQueue() {
		System.out.println("AgentsQueue constructor.");
		
	}
	
	
	public static void main(String args[]) {
		try {			
			DtaListener myRec = new DtaListener();
			myRec.SetupRabbitListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("AgentsQueue run");
		
		 try {
		      while (!terminateRequested) {
		    	  //System.out.println("AgentsQueue Esperando...");
		    	  SendCommandToRabbit(bq.take());		       
		      }
		    } catch (InterruptedException ex) {
		      Thread.currentThread().interrupt();
		    }
		
	}
	
	
private static void SendCommandToRabbit(AgentMessage payload) {
		

		
		//TODO: Todo el mecanismo por si no esta disponoble rabbit y el queue, etc.
		
		String exchange = Config.GetDirective("BusinessAmqpExchange", null);
		
		String routingKey = Config.GetDirective(String.format("%sEventRoutingKey", payload.Agent),
				String.format("agent.event.%s",payload.Agent.toLowerCase()));
			
		
		String routingKeyStaled = "";  
		if (payload.Agent.equalsIgnoreCase("hma"))
            routingKeyStaled = Config.GetDirective(String.format("Staled%sEventRoutingKey", payload.Agent),
            		String.format("agent.event.%s.staled", payload.Agent.toLowerCase()));
        else
            routingKeyStaled = Config.GetDirective(String.format("Staled%sEventRoutingKey", payload.Agent),"");
        
        String staledTime = Config.GetDirective("HmaMsgStaledSeconds", "300");
		
     // Publish
        
        Map<String,Object> map = null;
        BasicProperties props = null;
              
        
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
              channel.basicPublish(exchange, routingKey,true, props, gson.toJson(payload).getBytes());              
                             
              channel.close();
             
            }
            else {
            	//NO HAY RABBIT CREAMOS EL ARCHIVO DE QUEUE
            	CryptUtils.SaveEntry(payload);
            	
            }
        }catch(Exception e){
        	System.out.println("AgentsQueue.SendCommandToRabbit EXCEPTION");
            e.printStackTrace();
        }       
        
       
	}
	
	
}
