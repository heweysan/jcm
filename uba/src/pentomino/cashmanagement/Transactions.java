package pentomino.cashmanagement;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import pentomino.cashmanagement.vo.MovimientosDelDiaRequest;
import pentomino.cashmanagement.vo.MovimientosDelDiaVO;
import pentomino.cashmanagement.vo.ExceptionVO;
import pentomino.cashmanagement.vo.GenericMessageVO;
import pentomino.cashmanagement.vo.ResponseObjectVO;
import pentomino.config.Config;
import pentomino.core.devices.Ptr;
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


	public static void TraeMovimientosDelDia() throws ParseException  {

		System.out.println("\n--- TraeMovimientosDelDia ---".toUpperCase());

		String corrId = UUID.randomUUID().toString();

		MovimientosDelDiaRequest dddVO = new MovimientosDelDiaRequest();
		
		dddVO.atmId = Config.GetDirective("AtmId", "");
		dddVO.operatorId =  7007;//Integer.parseInt(CurrentUser.loginUser);
		
		
		String myDate = "2020/10/14 00:01:00"; //"2020/10/14 00:01:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = sdf.parse(myDate);
		long millis = date.getTime();
		
		dddVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
		dddVO.operationDateTimeMilliseconds = millis;
		
		/* 
		 	CON ERROR		 
			{"success":false,"message":"Error al obtener consecutivo en CM-Auth","exception":{"message":"Error al obtener consecutivo en CM-Auth","className":"ReportssHandler"}}
			
			SIN DATOS
			{"message":"Éxito","success":true,"atm":"WM01AT0001","date":1603342860000,"depositId":"06818909","withdrawalId":"06818909","requestingUser":7007,"totalDeposits":0,"depositsAmount":0,"totalWithdrawals":0,"withdrawalsAmount":0,"totalCollections":0,"collectionsAmount":0,"depositsDetail":[],"withdrawalsDetail":[]}
			
			CON DATOS
			{"message":"Éxito","success":true,"atm":"WM01AT0001","date":1602651660000,"depositId":"06828909","withdrawalId":"06828909","requestingUser":7007,"totalDeposits":21,"depositsAmount":4830.0,"totalWithdrawals":0,"withdrawalsAmount":0,"totalCollections":0,"collectionsAmount":0,"depositsDetail":[{"amount":510.0,"cashier":"007007","datetime":1602721289767},{"amount":50.0,"cashier":"007007","datetime":1602720842102},{"amount":50.0,"cashier":"007007","datetime":1602720476405},{"amount":50.0,"cashier":"007007","datetime":1602719144395},{"amount":20.0,"cashier":"007007","datetime":1602717685941},{"amount":20.0,"cashier":"007007","datetime":1602717043609},{"amount":370.0,"cashier":"007007","datetime":1602716507011},{"amount":370.0,"cashier":"007007","datetime":1602713187438},{"amount":440.0,"cashier":"007007","datetime":1602711919590},{"amount":300.0,"cashier":"007007","datetime":1602710512597},{"amount":50.0,"cashier":"007007","datetime":1602709756080},{"amount":50.0,"cashier":"007007","datetime":1602708981049},{"amount":70.0,"cashier":"007007","datetime":1602706198901},{"amount":750.0,"cashier":"007007","datetime":1602705864402},{"amount":20.0,"cashier":"007007","datetime":1602705687786},{"amount":720.0,"cashier":"007007","datetime":1602705071465},{"amount":500.0,"cashier":"007007","datetime":1602702876140},{"amount":20.0,"cashier":"003805","datetime":1602701591758},{"amount":150.0,"cashier":"007007","datetime":1602701333900},{"amount":200.0,"cashier":"007007","datetime":1602700839995},{"amount":120.0,"cashier":"007007","datetime":1602699429813}],"withdrawalsDetail":[]}
		*/
		GenericMessageVO requestMessage = new GenericMessageVO();
		requestMessage.data = dddVO;
		
		Map<String,Object> map = null;
		
		MovimientosDelDiaVO returnVO = new MovimientosDelDiaVO();    		        		

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
				System.out.println("---> [" + gson.toJson(requestMessage) + "]");				
				
				
				//Esperamos la respuesta
				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);	                     

				System.out.println("[1]");
				String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
					if (delivery.getProperties().getCorrelationId().equals(corrId)) {
						response.offer(new String(delivery.getBody(), "UTF-8"));
					}
				}, consumerTag -> {
				});              	                      

						
				
				String result = response.take();     				
			
				System.out.println("<--- [" + result + "]");

				if(!result.isEmpty() || result != "") {
					returnVO = gson.fromJson(result, MovimientosDelDiaVO.class);	
					System.out.println("succes [" + returnVO.success + "]");
						
					if(!returnVO.success){							
						//TODO: Mandar el broadcasta a journal
						logger.error("No se pudieron obtener los datos de los movimientos [" + returnVO.message + "]");
					}
					//Mandamo imprimir el tique
					Ptr.ptrMovimientosDelDia(returnVO);

				}else {
					//No se pudo traer la info imprimos ticket de error
					//TODO: Mandar el broadcasta a journal
					logger.error("No se pudieron obtener los datos de los movimientos respuesta vacia");
				}
				
				System.out.println("returnVO [" + gson.toJson(returnVO) + "]");

				channel.basicCancel(ctag);
				channel.close();

			}
		}catch(Exception e){
			System.out.println("TraeMovimientosDelDia [EXCEPTION]");
			e.printStackTrace();
		}

		
	}


	public void close() throws IOException {
		connection.close();
	}



}
