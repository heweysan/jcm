package pentomino.cashmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.cashmanagement.vo.CashInOpVO;
import pentomino.cashmanagement.vo.CmReverse;
import pentomino.cashmanagement.vo.CmWithdrawal;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.cashmanagement.vo.DepositoDelDia;
import pentomino.cashmanagement.vo.DepositosDelDiaRequest;
import pentomino.cashmanagement.vo.DepositosDelDiaVO;
import pentomino.cashmanagement.vo.ExceptionVO;
import pentomino.cashmanagement.vo.GenericMessageVO;
import pentomino.cashmanagement.vo.ResponseObjectVO;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import rabbitClient.RabbitMQConnection;


public class Transactions {

	private static final Logger logger = LogManager.getLogger(Transactions.class.getName());

	private static Gson gson = new Gson();

	private Connection connection;



	public static ResponseObjectVO InsertaCashInOp(CashInOpVO cashInOp) {

		System.out.println("\n--- InsertaCashInOp ---".toUpperCase());		

		String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = cashInOp;

		ResponseObjectVO returnVO = new ResponseObjectVO();

		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){


				Channel channel = rabbitConn.createChannel();  

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
				//System.out.println("Sent     [" + gson.toJson(requestMessage) + "]"); 


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
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
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

				//System.out.println("result   [" + result  + "]");
				//System.out.println("returnVO [" + gson.toJson(returnVO)  + "]");

				channel.close();
				//rabbitConn.close();
			}
		}catch(Exception e){
			System.out.println("InsertaCashInOp");
			e.printStackTrace();
		}		

		return returnVO;
	}

	public static ResponseObjectVO BorraCashInOPs(String atmId) {

		System.out.println("\n--- BorraCashInOPs ---".toUpperCase()); 


		String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		String mensaje = "{\"data\":{\"atmId\":\"" + atmId + "\"}}";

		ResponseObjectVO returnVO = new ResponseObjectVO();

		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

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
				//System.out.println("Sent     [" + mensaje + "]"); 

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
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
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

				//System.out.println("result   [" + result + "]");
				//System.out.println("returnVO [" + gson.toJson(returnVO)  + "]");

				channel.close();
				//rabbitConn.close();
			}
		}catch(Exception e){
			System.out.println("BorraCashInOPs");
			e.printStackTrace();
		}

		return returnVO;
	}

	//Equivalente a /Deposito POST
	public static ResponseObjectVO InsertaPreDeposito(Deposito deposito) {

		System.out.println("\n--- InsertaPreDeposito ---".toUpperCase()); 

		String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		DepositOpVO depositOpVO = new DepositOpVO();
		ResponseObjectVO returnVO = new ResponseObjectVO();

		String atmId = Config.GetDirective("AtmId", "");

		depositOpVO.atmId = atmId; 
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
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

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
				//System.out.println("Sent     [" + gson.toJson(requestMessage) + "]"); 


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
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
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

				//System.out.println("result   [" + result + "]");
				//System.out.println("returnVO [" + gson.toJson(returnVO)  + "]");

				channel.close();
				//rabbitConn.close();
			}
		}catch(Exception e){
			System.out.println("InsertaPreDeposito");
			e.printStackTrace();			
		}


		return returnVO;

	}


	//Equivalente a /Deposito DELETE
	public boolean RechazaDeposito(Deposito deposito) {
		return false;

	}

	public static CMUserVO ValidaUsuario(String idEmp) {

		System.out.println("\n--- ValidaUsuario ---".toUpperCase());

		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		String mensaje = "{\"data\":{\"numTarjeta\":\"" + idEmp + "\"}}";

		CMUserVO returnVO = new CMUserVO();    		        		

		try{

			Connection rabbitConn = RabbitMQConnection.getConnection();

			if(rabbitConn == null) {
				//NO SE PUDO CONECTAR O CREDENCIALES INCORRECTA, ALGO MALO PASO
				returnVO.isValid = true;
				returnVO.allowWithdrawals = false;
				returnVO.depositInfo = new ArrayList<Object>();
				returnVO.success = false;
				returnVO.profileName = null;
				returnVO.profileId = (int) 0L;
				returnVO.message = "Error de conexion";
				returnVO.exception = null;
				returnVO.totalDeposit = 0;
			}
			else{
				Channel channel = rabbitConn.createChannel();  

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
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
						if(responseMap.containsKey("exception")){
							returnVO.isValid = false;
							returnVO.allowWithdrawals = false;
							returnVO.depositInfo = new ArrayList<Object>();
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
							returnVO.depositInfo = new ArrayList<Object>();;
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
						returnVO.depositInfo = new ArrayList<Object>();
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
					returnVO.depositInfo = new ArrayList<Object>();
					returnVO.success = true;
					returnVO.profileName = null;
					returnVO.profileId =  (int) 0L;
					returnVO.message = "Problemas de comunicación con servicios de usuarios";
					returnVO.exception = null;
					returnVO.totalDeposit = 0;
				}
				logger.info("returning ${JsonOutput.toJson(returnVO)}");

				System.out.println("returnVO [" + gson.toJson(returnVO) + "]");

				channel.basicCancel(ctag);
				channel.close();

			}
		}catch(Exception e){
			System.out.println("ValidaUsuario [EXCEPTION]");
			e.printStackTrace();
		}

		return returnVO;
	}
	
	public static CMUserVO ValidaUsuarioPassword(String idEmp, String password) {

		System.out.println("\n--- ValidaUsuarioPassword ---".toUpperCase());

		final String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;

		String mensaje = "{\"data\":{\"numTarjeta\":\"" + idEmp + "\",\"password\":\"" + password + "\"}}";

		CMUserVO returnVO = new CMUserVO();    		        		

		try{

			Connection rabbitConn = RabbitMQConnection.getConnection();

			if(rabbitConn == null) {
				//NO SE PUDO CONECTAR O CREDENCIALES INCORRECTA, ALGO MALO PASO
				returnVO.isValid = false;
				returnVO.allowWithdrawals = false;
				returnVO.depositInfo = new ArrayList<Object>();
				returnVO.success = false;
				returnVO.profileName = null;
				returnVO.profileId = (int) 0L;
				returnVO.message = "Error de conexion";
				returnVO.exception = null;
				returnVO.totalDeposit = 0;
			}
			else{
				Channel channel = rabbitConn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","validate-username-password");         

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
				//RESPUESTA ERRONEA  
				//{"exception":"Password Erroneo"}
				//RESPUESTA CORRECTA
				//{"username":"007007","profileId":0,"profileName":"","authorities":[],"allowWithdrawals":true}
				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);	                      


				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});              	                      

				String result = response.take();            

				System.out.println("result [" + result + "]");

				if(!result.isEmpty()) {
					if(result != ""){
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
						System.out.println("isValid " + (String) responseMap.get("isValid"));
						
						if(responseMap.containsKey("exception")){
							returnVO.isValid = false;
							returnVO.allowWithdrawals = false;
							returnVO.depositInfo = new ArrayList<Object>();
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
							returnVO.depositInfo = new ArrayList<Object>();;
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
						returnVO.depositInfo = new ArrayList<Object>();
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
					returnVO.depositInfo = new ArrayList<Object>();
					returnVO.success = true;
					returnVO.profileName = null;
					returnVO.profileId =  (int) 0L;
					returnVO.message = "Problemas de comunicación con servicios de usuarios";
					returnVO.exception = null;
					returnVO.totalDeposit = 0;
				}
				logger.info("returning ${JsonOutput.toJson(returnVO)}");

				System.out.println("returnVO [" + gson.toJson(returnVO) + "]");

				channel.basicCancel(ctag);
				channel.close();

			}
		}catch(Exception e){
			System.out.println("ValidaUsuario [EXCEPTION]");
			e.printStackTrace();
		}

		return returnVO;
	}

	//Equivalente a /Deposito  PUT
	public static String ConfirmaDeposito(DepositOpVO depositOpVO)  {

		System.out.println("\n--- ConfirmaDeposito ---".toUpperCase());

		String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;		

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = depositOpVO;
		
		String retData = "";

		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","insert-deposit");         

				String replyQueueName = channel.queueDeclare().getQueue();

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.deposits.*",true, props, gson.toJson(requestMessage).getBytes());
				//System.out.println("Sent [" + gson.toJson(requestMessage) + "]");

				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});

				
				String result = response.take();
				System.out.println("ConfirmaDeposito result [" + result + "]");
			
				if(!result.isEmpty()) {
					if(result != ""){
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
						if(responseMap.containsKey("exception")){			
							System.out.println("TENGO [" + responseMap.get("message") + "]");
							retData = "";
						}else{
							if(responseMap.containsKey("success")) {								
								CurrentUser.movementId = (String) responseMap.get("value");
								 retData = CurrentUser.movementId;
							}
						}

					}else{
						retData = "";
					}

				}else {
					retData = "";
				}	
			
			
				channel.basicCancel(ctag);

				channel.close();

			}
		}catch(Exception e){
			System.out.println("ConfirmaDeposito");
			e.printStackTrace();
		}

		return retData;            

	}


	//Equivalente a /Deposito  PUT
	public static boolean ConfirmaRetiro(CmWithdrawal cmWithdrawalVo)  {

		System.out.println("\n--- ConfirmaRetiro ---".toUpperCase());

		boolean retData = false;

		String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;		

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = cmWithdrawalVo;			

		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","process-withdrawal");         

				String replyQueueName = channel.queueDeclare().getQueue();

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.withdrawals.*",true, props, gson.toJson(requestMessage).getBytes());
				System.out.println("ConfirmaRetiro Sent [" + gson.toJson(requestMessage) + "]");

				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});


				String result = response.take();

				channel.basicCancel(ctag);

				//ConfirmaRetiro result [{"message":"Éxito","success":true,"value":"733661"}]
				System.out.println("ConfirmaRetiro result [" + result + "]");

				if(!result.isEmpty()) {
					if(result != ""){
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
						if(responseMap.containsKey("exception")){			
							System.out.println("TENGO [" + responseMap.get("message") + "]");
							if( responseMap.get("message").toString().equalsIgnoreCase("Invalid Reference Data")) {
								//Ponemos como cashedout el retiro pues ya se cobró.
								CmQueue.queueList.removeFirst();
								CmQueue.ClosePendingWithdrawal(cmWithdrawalVo.reference);
							}

							return false;
						}else{
							if(responseMap.containsKey("success")) {
								retData =  (Boolean) responseMap.get("success");
								CurrentUser.movementId = (String) responseMap.get("value");
							}
						}

					}else{
						retData = false;
					}

				}else {
					retData = false;
				}					

				channel.close();					

			}
		}catch(Exception e){
			System.out.println("ConfirmaRetiro Exception");				
			e.printStackTrace();
			retData = false;
		}

		return retData;            

	}

	//REVERSO
	public static boolean WithdrawalReverse(CmReverse cmReverseVo)  {

		System.out.println("\n--- WithdrawalReverse ---".toUpperCase());

		boolean retData = false;

		String corrId = UUID.randomUUID().toString();

		Map<String,Object> map = null;		

		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = cmReverseVo;			

		try{
			Connection rabbitConn = RabbitMQConnection.getConnection();
			if(rabbitConn != null){
				Channel channel = rabbitConn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","process-reverse");         

				String replyQueueName = channel.queueDeclare().getQueue();

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();

				channel.basicPublish("ex.cm.topic", "cm.withdrawals.*",true, props, gson.toJson(requestMessage).getBytes());
				//System.out.println("Sent [" + gson.toJson(requestMessage) + "]");

				//Esperamos la respuesta

				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});


				String result = response.take();

				channel.basicCancel(ctag);

				//System.out.println("result [" + result + "]");

				if(!result.isEmpty()) {
					if(result != ""){
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
						if(responseMap.containsKey("exception")){			
														return false;
						}else{
							if(responseMap.containsKey("success")) {
								retData =  (Boolean) responseMap.get("success");
							}
						}

					}else{
						retData = false;
					}

				}else {
					retData = false;
				}					

				channel.close();					

			}
		}catch(Exception e){
			System.out.println("WithdrawalReverse Exception");				
			e.printStackTrace();
			retData = false;
		}

		return retData;            

	}


	public static void TraeDepositosDelDia()  {

		System.out.println("\n--- TraeDepositosDelDia ---".toUpperCase());

		String corrId = UUID.randomUUID().toString();

		DepositosDelDiaRequest dddVO = new DepositosDelDiaRequest();
		
		dddVO.atmId = Config.GetDirective("AtmId", "");
		dddVO.operatorId =   7007;//Integer.parseInt(CurrentUser.loginUser);
		dddVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
		
		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = dddVO;
		
		Map<String,Object> map = null;
		
		DepositosDelDiaVO returnVO = new DepositosDelDiaVO();    		        		

		try{

			Connection rabbitConn = RabbitMQConnection.getConnection();

			if(rabbitConn == null) {
				//NO SE PUDO CONECTAR IMPRIMIMOS TICKET DE ERROR
				
			}
			else{
				Channel channel = rabbitConn.createChannel();  

				map = new HashMap<String,Object>(); 
				map.put("operation-type","daily-report");         

				String replyQueueName = channel.queueDeclare().getQueue();

				AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.headers(map)
						.build();
				
				channel.basicPublish("ex.cm.topic", "cm.reports.*",true, props, gson.toJson(requestMessage).getBytes());
				System.out.println("Sent [" + gson.toJson(requestMessage) + "]");


				
				
				
				//Esperamos la respuesta
				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);	                     

				System.out.println("[1]");
				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});              	                      

				System.out.println("[2]");
				
				
				String result = response.take();     
				
				System.out.println("[3]");

				System.out.println("result [" + result + "]");

				if(!result.isEmpty()) {
					if(result != ""){
						Map<?, ?> responseMap = gson.fromJson(result, Map.class);
						System.out.println("isValid " + (String) responseMap.get("isValid"));
						
						if(responseMap.containsKey("exception")){
							
							returnVO.message = (String) responseMap.get("exception");
													
							
						}else{
							returnVO.success = true;
							returnVO.atm =(String) responseMap.get("atm"); 
							returnVO.date = (long) Float.parseFloat(responseMap.get("date").toString());		
							returnVO.depositId =(String) responseMap.get("depositId");
							returnVO.withdrawalId =(String) responseMap.get("withdrawalId");							
							returnVO.requestingUser =(String) responseMap.get("requestingUser");
							
							returnVO.totalDeposits = (int) Float.parseFloat(responseMap.get("totalDeposits").toString());
							returnVO.depositsAmount = (long) Float.parseFloat(responseMap.get("depositsAmount").toString());
							returnVO.totalWithdrawals = (int) Float.parseFloat(responseMap.get("totalWithdrawals").toString());
							returnVO.withdrawalsAmount = (long) Float.parseFloat(responseMap.get("withdrawalsAmount").toString());
							returnVO.totalCollections = (long) Float.parseFloat(responseMap.get("datotalCollectionste").toString());
							returnVO.collectionsAmount = (long) Float.parseFloat(responseMap.get("collectionsAmount").toString());
							
							DepositoDelDia[] mcArray =  gson.fromJson((String) responseMap.get("depositsDetail"), DepositoDelDia[].class);
														
							returnVO.depositsDetail = Arrays.asList(mcArray);
							

						}

					}else{
						//No se pudo traer la info imprimos ticket de error
					}

				}else {
					//No se pudo traer la info imprimos ticket de error
				}
				logger.info("returning ${JsonOutput.toJson(returnVO)}");

				System.out.println("returnVO [" + gson.toJson(returnVO) + "]");

				channel.basicCancel(ctag);
				channel.close();

			}
		}catch(Exception e){
			System.out.println("ValidaUsuario [EXCEPTION]");
			e.printStackTrace();
		}

		
	}


	public void close() throws IOException {
		connection.close();
	}



}
