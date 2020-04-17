package rabbitClient;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
 
public class Receiver {
  
 public void receive(){
   try{
       Connection conn = RabbitMQConnection.getConnection();
       if(conn != null){
         Channel channel = conn.createChannel();
         // Consumer reading from queue 1
         Consumer consumer1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" Message Received Queue 1 '" + message + "'");
            }
         };
         channel.basicConsume(HeaderExchange.QUEUE_NAME_1, true, consumer1);
  
         // Consumer reading from queue 2 
         Consumer consumer2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" Message Received Queue 2 '" + message + "'");
            }
         };
         channel.basicConsume(HeaderExchange.QUEUE_NAME_2, true, consumer2);
  
         // Consumer reading from queue 3 
         Consumer consumer3 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
              String message = new String(body, "UTF-8");
              System.out.println(" Message Received Queue 3 '" + message + "'");
            }
         };
         channel.basicConsume(HeaderExchange.QUEUE_NAME_3, true, consumer3);
         channel.close();
         conn.close();
       }
   }catch(Exception e){
      e.printStackTrace();
   }
 }
 
}