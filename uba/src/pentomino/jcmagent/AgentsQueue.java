package pentomino.jcmagent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import pentomino.common.AccountType;
import pentomino.common.CryptUtils;
import pentomino.common.DeviceEvent;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import rabbitClient.CmListener;
import rabbitClient.RabbitMQConnection;
import rabbitClient.DtaListener;


public class AgentsQueue  implements Runnable{

	private static Gson gson = new Gson();
		
	public static BlockingQueue<AgentMessage> bq = new LinkedBlockingQueue<AgentMessage>();
	
	private volatile boolean terminateRequested;
	

	public AgentsQueue(BlockingQueue<AgentMessage> myQueue) {
		System.out.println("AgentsQueue constructor.");
		bq = myQueue;
	}
	
	public AgentsQueue() {
		System.out.println("AgentsQueue constructor.");
		
	}
	
	
	public static void main(String args[]) {

		try {

			//DTAServer dtaServer = new DTAServer();

			/*
			String jsonString = "{\"data\":{\"Command\":\"LISTFILES\",\"Date\":\"Wed Apr 29 21:36:22 CDT 2020\",\"Parameter\":null,\"Extra\":\"\"}}";

			SimpleRabbitEnvironmentVariablesContainer otherEnvVars = new Gson().fromJson(jsonString,
					SimpleRabbitEnvironmentVariablesContainer.class);

			String response = dtaServer.ManageAgent(otherEnvVars.data.Command, otherEnvVars.data.Convert());
			System.out.println(response);
			*/

			
			DtaListener myRec = new DtaListener();
			myRec.SetupRabbitListener();
			
			 /*
			CmListener myCmListener = new CmListener();
			myCmListener.SetupRabbitListener();
			*/
			
			

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
		
	//System.out.println("AgentsQueue.SendCommandToRabbit");	
	
	//String atmId = Configuration.GetDirective("AtmId", "");
		
		
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
              
              System.out.println(" Message Sent '" + gson.toJson(payload) + "'"); 
              
              channel.close();
              //rabbitConn.close();
            }
            else {
            	//NO HAY RABBIT CREAMOS EL ARCHIVO DE QUEUE
            	CryptUtils.SaveEntry(payload);
            	
            }
        }catch(Exception e){
        	System.out.println("AQUI MAMAO");
            e.printStackTrace();
        }       
        
       
	}
	
	
}
