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
	
	
	private static Connection sqlLiteConn = null;
	
	private static void connect() { 
			
		System.out.println("Config.connect");
        try { 
        	
        	if(sqlLiteConn != null && !sqlLiteConn.isClosed())
    			return;
           
            String url = "jdbc:sqlite:./Pentomino.Config.db3";
           
            sqlLiteConn = DriverManager.getConnection(url);           
            
            
        } catch (SQLException e) {
        	if(e.getMessage() != null)
        		System.out.println("Config.connect SQLException [" +  e.getMessage() + "]");
        	else{
        		e.printStackTrace();
        	}
                    	
        } finally {
           
        }
    }
	
	public static String GetPulsarParam(String param, String defValue) {
				
		if(PulsarParamHashmap.containsKey(param)) {
			return PulsarParamHashmap.get(param);
		}
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM PulsarParams WHERE Key = ?  COLLATE NOCASE;";		
		
		lock.lock();
		try {
		
			Config.connect();
			
			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){		
		            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            	            
	            PulsarParamHashmap.put(param,retVal);
	            
	            if (sqlLiteConn != null) {
	                sqlLiteConn.close();                
	            }
	        } catch (SQLException e) {
	        	if(e.getMessage() != null)
	        		System.out.println("Config.GetPulsarParam SQLException [" +  e.getMessage() + "]");
	        	else{
	        		e.printStackTrace();
	        	}
	        }
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.GetPulsarParam GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
		
		return retVal;
	}
	
	public static String GetDirective(String param, String defValue) {
				
		if(DirectiveParamHashmap.containsKey(param)) {
			return DirectiveParamHashmap.get(param);
		}
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM Directives WHERE Key = ?  COLLATE NOCASE;";
		
		lock.lock();
		try {
			Config.connect();
			
			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	    
	            ResultSet rs  = pstmt.executeQuery();
	    
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            
	            DirectiveParamHashmap.put(param,retVal);
	            
	            if (sqlLiteConn != null) {
	                sqlLiteConn.close();	                
	            }
	            	            
	        } catch (SQLException e) {
	        	if(e.getMessage() != null)
	        		System.out.println("Config.GetDirective SQLException [" +  e.getMessage() + "]");
	        	else{
	        		e.printStackTrace();
	        	}
	        	
	        }		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.GetDirective GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
		
		return retVal;
	}
	
	public static String GetPersistence(String param, String defValue) {
		
		if(PersistenceParamHashmap.containsKey(param)) {
			//System.out.println("Persistence param retrieved from Map");
			return PersistenceParamHashmap.get(param);
		}
		
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM Entries WHERE Key = ?  COLLATE NOCASE;";
		
		lock.lock();
		try {
			Config.connect();
		
			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            
	            PersistenceParamHashmap.put(param,retVal);
	            
	            if (sqlLiteConn != null) {
	                sqlLiteConn.close();
	                //System.out.println("Connection closed");
	            }
	            
	        } catch (SQLException e) {
	        	if(e.getMessage() != null)
	        		System.out.println("Config.GetPersistence SQLException [" +  e.getMessage() + "]");
	        	else{
	        		e.printStackTrace();
	        	}
	        }
		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.GetPersistence GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
		
	
		return retVal;
	}
	
	
	
}
