package pentomino.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class Config {

	private static HashMap<String, String> PulsarParamHashmap = new HashMap<String, String>();
	private static HashMap<String, String> PersistenceParamHashmap = new HashMap<String, String>();
	private static HashMap<String, String> DirectiveParamHashmap = new HashMap<String, String>();
	
	static ReentrantLock lock = new ReentrantLock();
	
	
	private static Connection conn = null;
	
	private static void connect() { 
				
        try { 
        	
        	if(conn != null && !conn.isClosed())
    			return;
           
            String url = "jdbc:sqlite:./Pentomino.Config.db3";
           
            conn = DriverManager.getConnection(url);           
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
           
        }
    }
	
	public static String GetPulsarParam(String param, String defValue) {
				
		if(PulsarParamHashmap.containsKey(param)) {			
			System.out.println("Pulsar param retrieved from Map");
			return PulsarParamHashmap.get(param);
		}
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM PulsarParams WHERE Key = ?  COLLATE NOCASE;";		
		
		lock.lock();
		try {
		
			Config.connect();
			
			try (PreparedStatement pstmt  = conn.prepareStatement(sql)){		
		            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            	            
	            PulsarParamHashmap.put(param,retVal);
	            
	            if (conn != null) {
	                conn.close();                
	            }
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		}catch(Exception ge) {
			ge.printStackTrace();
		}
		finally{
			lock.unlock();
		}
		
		System.out.println("param [" + param + "] value[" + retVal  + "]");
		
		return retVal;
	}
	
	public static String GetDirective(String param, String defValue) {
		
		
		if(DirectiveParamHashmap.containsKey(param)) {
			System.out.println("Directive param retrieved from Map");
			return DirectiveParamHashmap.get(param);
		}
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM Directives WHERE Key = ?  COLLATE NOCASE;";
		
		lock.lock();
		try {
			Config.connect();
		
			try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	            
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            
	            DirectiveParamHashmap.put(param,retVal);
	            
	            if (conn != null) {
	                conn.close();
	                System.out.println("Connection closed");
	            }
	            	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }		
		}catch(Exception ge) {
			ge.printStackTrace();
		}
		finally{
			lock.unlock();
		}
		
		System.out.println("param [" + param + "] value[" + retVal  + "]");
		
		return retVal;
	}
	
	public static String GetPersistence(String param, String defValue) {
		
		if(PersistenceParamHashmap.containsKey(param)) {
			System.out.println("Persistence param retrieved from Map");
			return PersistenceParamHashmap.get(param);
		}
		
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM Entries WHERE Key = ?  COLLATE NOCASE;";
		
		lock.lock();
		try {
			Config.connect();
		
			try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            
	            PersistenceParamHashmap.put(param,retVal);
	            
	            if (conn != null) {
	                conn.close();
	                //System.out.println("Connection closed");
	            }
	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		
		}catch(Exception ge) {
			ge.printStackTrace();
		}
		finally{
			lock.unlock();
		}
		
		System.out.println("param [" + param + "] value[" + retVal  + "]");
		
		
		
		return retVal;
	}
	
	
	
}
