package pentomino.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Config {

	private static Connection conn = null;
	
	private static void connect() { 
				
        try {        	
        	
        	if(conn != null && !conn.isClosed())
    			return;
           
            String url = "jdbc:sqlite:C:/Users/hewey/Desktop/Pentomino.Config.db3";
           
            conn = DriverManager.getConnection(url);           
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
           
        }
    }
	
	public static String GetPulsarParam(String param, String defValue) {
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM PulsarParams WHERE Key = ?  COLLATE NOCASE;";		
		
		Config.connect();
		
		try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		
		try {
            if (conn != null) {
                conn.close();                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
		
		System.out.println("param [" + param + "] value[" + retVal  + "]");
		
		return retVal;
	}
	
	public static String GetDirective(String param, String defValue) {
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM Directives WHERE Key = ?  COLLATE NOCASE;";
		
		Config.connect();
		
		try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	            
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		
		try {
            if (conn != null) {
                conn.close();
                //System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
		
		System.out.println("param [" + param + "] value[" + retVal  + "]");
		
		return retVal;
	}
	
	public static String GetPersistence(String param, String defValue) {
		
		String retVal = defValue;		
		String sql = "SELECT Value FROM Entries WHERE Key = ?  COLLATE NOCASE;";
		
		System.out.println(sql);
		Config.connect();
		
		try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            if(rs.isClosed())
	            	System.out.println("param [" + param + "] not found in DB setting defValue");
	            else
	            	retVal = rs.getString("Value");
	            	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		
		try {
            if (conn != null) {
                conn.close();
                //System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
		
		System.out.println("param [" + param + "] value[" + retVal  + "]");
		
		return retVal;
	}
	
	
	
}
