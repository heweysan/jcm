package rabbitClient;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
 
public class RabbitMQConnection {
  
  public static Connection getConnection(){
    Connection conn = null;
    try{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("redblu_administrator");
        factory.setPassword("4dm1n1str4t0r");
        factory.setVirtualHost("vh_business");
        factory.setHost("11.50.0.7");
        factory.setPort(5672);
        conn = factory.newConnection();
        
    }catch(Exception e){
        e.printStackTrace();
    }
    return conn;
  }
 
  

  
  
  
}