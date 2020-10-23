package pentomino.cashmanagement;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovimientosDelDiaTask extends TimerTask {
     
	private static final Logger logger = LogManager.getLogger(MovimientosDelDiaTask.class.getName());
	
    @Override
    public void run() {
    	
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    	LocalDateTime now = LocalDateTime.now();  
    	System.out.println(dtf.format(now) + " Imprimimos reporte diario de movimientos");    
    	Transactions.TraeMovimientosDelDia();
    }
    
    
    public static void iniciaTarea(){
    	
    	System.out.println("MovimientosDelDiaTask [Inicializando]");
		logger.info("MovimientosDelDiaTask [Inicialiando]");
    	
    	long delay = TimeUnit.SECONDS.toMillis(10);
    	MovimientosDelDiaTask task = new MovimientosDelDiaTask();
        Timer timer = new Timer();
       
        LocalTime now = LocalTime.now();
        LocalTime executionTime = LocalTime.of(23, 59, 0);
            
        if(now.isBefore(executionTime)){       	
        	//Aun no es la hora, fijamos la primera corrida a esa hora.
        	delay = ChronoUnit.MILLIS.between(LocalTime.now(), executionTime);        	
        }
        else {
        	System.out.println("MovimientosDelDiaTask ya paso la hora, mandamos imprimir el reporte");
    		logger.info("MovimientosDelDiaTask ya paso la hora, mandamos imprimir el reporte");
        }
        
        System.out.println("delay " + delay);
        timer.schedule(task,delay,TimeUnit.DAYS.toMillis(1));
    }
}
