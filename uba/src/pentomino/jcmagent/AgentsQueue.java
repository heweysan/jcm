package pentomino.jcmagent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import pentomino.common.CryptUtils;
import pentomino.config.Config;
import pentomino.flow.Flow;
import rabbitClient.DtaListener;
import rabbitClient.RabbitMQConnection;


public class AgentsQueue  implements Runnable{

	private static Gson gson = new Gson();

	public static BlockingQueue<AgentMessage> bq = new LinkedBlockingQueue<AgentMessage>();

	private volatile boolean terminateRequested;

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());


	public AgentsQueue() {		
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
		System.out.println("AgentsQueue [running]");
		logger.info("AgentsQueue [running]");

		try {
			while (!terminateRequested) {				
				SendCommandToRabbit(bq.take());		       
			}
		} catch (InterruptedException ex) {
			System.out.println("[AgentsQueue] Exception " + ex.getMessage());
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

		long staledTime = Long.parseLong(Config.GetDirective("HmaMsgStaledSeconds", "300"));

		long timeStamp = payload.Timestamp;
		long now = java.lang.System.currentTimeMillis();
		if(((now - timeStamp) > (staledTime * 1000)) && !routingKeyStaled.isEmpty()){
			System.out.println("Nos vamos por STALED");
			routingKey = routingKeyStaled;
		}


		// Publish

		Map<String,Object> map = null;
		BasicProperties props = null;
		boolean enQueue = false;

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
				enQueue = true;

			}
		}catch(Exception e){
			System.out.println("AgentsQueue.SendCommandToRabbit EXCEPTION");
			e.printStackTrace();
			//NO HAY RABBIT CREAMOS EL ARCHIVO DE QUEUE
			if(!enQueue)  //Para no duplicarlo			
				CryptUtils.SaveEntry(payload);
		}       


	}


}
