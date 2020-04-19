import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Config {

	private static Connection conn = null;
	
	public static void connect() {
        
		if(conn != null)
			return;
		
        try {
            // db parameters  C:\Users\hewey\Desktop\Glider
            String url = "jdbc:sqlite:C:/Users/hewey/Desktop/Pentomino.Config.db3";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            /*
        	try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }*/
        }
    }
	
	public String getPulsarParam(String param) {
		
		String retVal = "";
		
		String sql = "SELECT Value FROM PulsarParams WHERE Key = ?  COLLATE NOCASE;";
		
		System.out.println(sql);
		Config.connect();
		
		try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
	            
				// set the value
	            pstmt.setString(1,param);
	           
	            ResultSet rs  = pstmt.executeQuery();
	           
	            System.out.println(rs.getString("Value") + "\t");
	            	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
		
		try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
		
		return retVal;
	}
	
	
	
}
