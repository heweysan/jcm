package pentomino.cashmanagement;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.cashmanagement.vo.ExceptionVO;
import pentomino.cashmanagement.vo.GenericMessageVO;
import pentomino.cashmanagement.vo.ResponseObjectVO;
import rabbitClient.RabbitMQConnection;



public class Transactions {


	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(Transactions.class.getName());

	private static Gson gson = new Gson();

	private Connection connection;

	public static void main(String args[]) throws IOException, InterruptedException{

		//ValidaUsuario("007007"); //007007		

		//BorraCashInOPs("CI01GL0001");		

		CashInOpVO myObj = new CashInOpVO();
		myObj.atmId = "CI01GL0001";
		myObj.amount = 20L;
		myObj.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
		myObj.operatorId = 7007;
		myObj.notesDetails = "1x20";

		//InsertaCashInOp(myObj);		

		//InsertaPreDeposito(new Deposito());
		
		
		DepositOpVO depositOpVO = new DepositOpVO();

		depositOpVO.atmId = "CI01GL0001"; //"CIXXGS0020";
		depositOpVO.amount = 20L;
		depositOpVO.b20 = 1;
		depositOpVO.b50 = 0;
		depositOpVO.b100 = 0;
		depositOpVO.b200 = 0;
		depositOpVO.b500 = 0;
		depositOpVO.b1000 = 0;
		depositOpVO.operatorId = 7007;
		depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
		depositOpVO.userName = "007007";
		
		ConfirmaDeposito(depositOpVO);

	}


	public static ResponseObjectVO InsertaCashInOp(CashInOpVO cashInOp) {

		System.out.println("\n--- InsertaCashInOp ---".toUpperCase());		

		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = cashInOp;

		ResponseObjectVO returnVO = new ResponseObjectVO();

		try{
			Connection conn = RabbitMQConnection.getConnection();
			if(conn != null){


				Channel channel = conn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","insert-cashin");         

				String replyQueueName = channel.queueDeclare().getQueue();

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.cashin.*",true, props, gson.toJson(requestMessage).getBytes());
				System.out.println("Sent     [" + gson.toJson(requestMessage) + "]"); 
									

				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {	});


				String result = response.take();

				if(!result.isEmpty()) {

					if(result != ""){
						Map responseMap = gson.fromJson(result, Map.class);
						returnVO.success = (Boolean) responseMap.get("success");
						returnVO.message = (String) responseMap.get("message");
						returnVO.exception = (ExceptionVO) responseMap.get("exception");
					}else{
						returnVO.success = false;
						returnVO.message = "Problemas al guardar el movimiento de cashin";
						returnVO.exception = null;
					}
				}else {
					returnVO.success = false;
					returnVO.message = "Problemas de comunicación con servicios de cashin";
					returnVO.exception = null;
				}

				channel.basicCancel(ctag);

				System.out.println("result   [" + result  + "]");
				System.out.println("returnVO [" + gson.toJson(returnVO)  + "]");

				channel.close();
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}		

		return returnVO;
	}

	public static ResponseObjectVO BorraCashInOPs(String atmId) {

		System.out.println("\n--- BorraCashInOPs ---".toUpperCase()); 

		
		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		String mensaje = "{\"data\":{\"atmId\":\"" + atmId + "\"}}";

		//GenericMessageVO requestMessage = new GenericMessageVO();
		ResponseObjectVO returnVO = new ResponseObjectVO();

		try{
			Connection conn = RabbitMQConnection.getConnection();
			if(conn != null){
				Channel channel = conn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","delete-cashin");         

				String replyQueueName = channel.queueDeclare().getQueue();
				
				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.cashin.*",true, props, mensaje.getBytes());
				System.out.println("Sent     [" + mensaje + "]"); 
				
				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});

				String result = response.take();
				
				if(!result.isEmpty()) {

					if(result != ""){
						Map responseMap = gson.fromJson(result, Map.class);
						returnVO.success = (Boolean) responseMap.get("success");
						returnVO.message = (String) responseMap.get("message");
						returnVO.exception = (ExceptionVO) responseMap.get("exception");
					}else{
						returnVO.success = false;
						returnVO.message = "Problemas al guardar el movimiento de cashin";
						returnVO.exception = null;
					}
				}else {
					returnVO.success = false;
					returnVO.message = "Problemas de comunicación con servicios de cashin";
					returnVO.exception = null;
				}

				channel.basicCancel(ctag);

				System.out.println("result   [" + result + "]");
				System.out.println("returnVO [" + gson.toJson(returnVO)  + "]");
				
				channel.close();
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return returnVO;
	}

	//Equivalente a /Deposito POST
	public static ResponseObjectVO InsertaPreDeposito(Deposito deposito) {

		System.out.println("\n--- InsertaPreDeposito ---".toUpperCase()); 

		if (logger.isDebugEnabled()) {
			logger.debug("InsertaPreDeposito(Deposito) - start"); //$NON-NLS-1$
		}	

		/* FRANKY */

		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		DepositOpVO depositOpVO = new DepositOpVO();
		ResponseObjectVO returnVO = new ResponseObjectVO();


		depositOpVO.atmId = "CI01GL0001"; //"CIXXGS0020";
		depositOpVO.amount = 20L;
		depositOpVO.b20 = 1;

		depositOpVO.b50 = 0;
		depositOpVO.b100 = 0;
		depositOpVO.b200 = 0;
		depositOpVO.b500 = 0;
		depositOpVO.b1000 = 0;

		depositOpVO.operatorId = 7007;
		depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
		depositOpVO.userName = "007007";

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = depositOpVO;


		try{
			Connection conn = RabbitMQConnection.getConnection();
			if(conn != null){
				Channel channel = conn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","insert-predeposit");         

				String replyQueueName = channel.queueDeclare().getQueue();
				//System.out.println("replyQueueName [" + replyQueueName + "]");

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.deposits.*",true, props, gson.toJson(requestMessage).getBytes());
				System.out.println("Sent     [" + gson.toJson(requestMessage) + "]"); 


				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);



				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});

				
				String result = response.take();

				if(!result.isEmpty()) {

					if(result != ""){
						Map responseMap = gson.fromJson(result, Map.class);
						returnVO.success = (Boolean) responseMap.get("success");
						returnVO.message = (String) responseMap.get("message");
						returnVO.exception = (ExceptionVO) responseMap.get("exception");
					}else{
						returnVO.success = false;
						returnVO.message = "Problemas al guardar el movimiento de cashin";
						returnVO.exception = null;
					}
				}else {
					returnVO.success = false;
					returnVO.message = "Problemas de comunicación con servicios de cashin";
					returnVO.exception = null;
				}
				
				channel.basicCancel(ctag);

				System.out.println("result   [" + result + "]");
				System.out.println("returnVO [" + gson.toJson(returnVO)  + "]");

				channel.close();
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}







		if (logger.isDebugEnabled()) {
			logger.debug("InsertaPreDeposito(Deposito) - end"); //$NON-NLS-1$
		}
		return returnVO;

	}


	//Equivalente a /Deposito DELETE
	public boolean RechazaDeposito(Deposito deposito) {
		if (logger.isDebugEnabled()) {
			logger.debug("RechazaDeposito(Deposito) - start"); //$NON-NLS-1$
		}

		if (logger.isDebugEnabled()) {
			logger.debug("RechazaDeposito(Deposito) - end"); //$NON-NLS-1$
		}
		return false;

	}

	public static CMUserVO ValidaUsuario(String idEmp) {

		System.out.println("\n--- ValidaUsuario ---".toUpperCase());

		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		String mensaje = "{\"data\":{\"numTarjeta\":\"" + idEmp + "\"}}";

		GenericMessageVO requestMessage = new GenericMessageVO();
		CMUserVO returnVO = new CMUserVO();    		        		

		try{

			Connection conn = RabbitMQConnection.getConnection();

			if(conn != null){
				Channel channel = conn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","validate-username");         

				String replyQueueName = channel.queueDeclare().getQueue();

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.auth.*",true, props, mensaje.getBytes());
				System.out.println("Sent     [" + mensaje + "]"); 
                                    

				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);	                      


				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});              	                      

				String result = response.take();            


				if(!result.isEmpty()) {
					if(result != ""){
						Map responseMap = gson.fromJson(result, Map.class);
						if(responseMap.containsKey("exception")){
							returnVO.isValid = false;
							returnVO.allowWithdrawals = false;
							returnVO.depositInfo = new ArrayList();
							returnVO.success = true;
							returnVO.profileName = null;
							returnVO.profileId = (int) 0L;
							returnVO.message = (String) responseMap.get("exception");
							ExceptionVO exception = new ExceptionVO();
							exception.Message = (String) responseMap.get("exception");
							exception.ClassName = Transactions.class.getName();
							returnVO.exception = exception;
							returnVO.totalDeposit = 0;
						}else{
							returnVO.isValid = true;
							returnVO.depositInfo = new ArrayList();;
							returnVO.success = true;
							returnVO.allowWithdrawals = (Boolean) responseMap.get("allowWithdrawals");
							returnVO.profileName = (String) responseMap.get("profileName");
							returnVO.profileId = (int) Float.parseFloat(responseMap.get("profileId").toString());
							returnVO.message = "Usuario no tiene depositos preparados.";
							returnVO.exception = null;
							returnVO.totalDeposit = 0;
						}

					}else{
						returnVO.isValid = false;
						returnVO.allowWithdrawals = false;
						returnVO.depositInfo = new ArrayList();
						returnVO.success = true;
						returnVO.profileName = null;
						returnVO.profileId = (int) 0L;
						returnVO.message = "Usuario no existe.";
						returnVO.exception = null;
						returnVO.totalDeposit = 0;
					}


				}else {
					returnVO.isValid = false;
					returnVO.allowWithdrawals = false;
					returnVO.depositInfo = new ArrayList();
					returnVO.success = true;
					returnVO.profileName = null;
					returnVO.profileId =  (int) 0L;
					returnVO.message = "Problemas de comunicación con servicios de usuarios";
					returnVO.exception = null;
					returnVO.totalDeposit = 0;
				}
				logger.info("returning ${JsonOutput.toJson(returnVO)}");


				System.out.println("result   [" + result + "]");  
				System.out.println("returnVO [" + gson.toJson(returnVO) + "]");                    


				channel.basicCancel(ctag);
				channel.close();
				conn.close();
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return returnVO;		




	}


	//Equivalente a /Deposito  PUT
	public static String ConfirmaDeposito(DepositOpVO depositOpVO) throws IOException, InterruptedException {

		System.out.println("\n--- ConfirmaDeposito ---".toUpperCase());
		/* FRANKY */

		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;		

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = depositOpVO;

		ResponseObjectVO returnVO = new ResponseObjectVO();

		try{
			Connection conn = RabbitMQConnection.getConnection();
			if(conn != null){
				Channel channel = conn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","insert-deposit");         

				String replyQueueName = channel.queueDeclare().getQueue();
				//System.out.println("replyQueueName [" + replyQueueName + "]");

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.deposits.*",true, props, gson.toJson(requestMessage).getBytes());
				System.out.println("Sent     [" + gson.toJson(requestMessage) + "]");

				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);



				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});

				//System.out.println("ctag ["+ ctag + "]");              

				String result = response.take();

				channel.basicCancel(ctag);

				System.out.println("result [" + result + "]");

				channel.close();
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return "";            

	}

	public void close() throws IOException {
		connection.close();
	}



}
