package pentomino.config;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class Config {

	private static HashMap<String, String> PulsarParamHashmap = new HashMap<String, String>();
	private static HashMap<String, String> PersistenceParamHashmap = new HashMap<String, String>();
	private static HashMap<String, String> DirectiveParamHashmap = new HashMap<String, String>();

	static ReentrantLock lock = new ReentrantLock();

	private static Connection sqlLiteConn = null;

	public static void main(String[] args) {


	}	

	private static void connect() { 

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

	public static void SetPersistence(String key, String value) {

		String sql = "";
		String id = "";
		sql = "select id from entries where key = ? COLLATE NOCASE limit 1;";

		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  


		lock.lock();
		try {
			Config.connect();


			try {

				PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql);  
				// key
				pstmt.setString(1,key);

				ResultSet rs  = pstmt.executeQuery();

				if(rs.isClosed()) {
					//No existe, hay que ingresarlo
					System.out.println("param [" + key + "] not found in DB inserting");

					sql = "insert into entries (Key, Value, Updated) values (?,?,?);";

					System.out.println("sql [" + sql + "]");

					pstmt.close();

					System.out.println("insert 1");
					pstmt = sqlLiteConn.prepareStatement(sql);
					System.out.println("insert 2");
					pstmt.clearParameters();
					// value
					pstmt.setString(1,key);

					// id
					pstmt.setString(2,value);

					pstmt.setString(3, dateFormat.format(date));
					System.out.println("insert 3");

					int res  = pstmt.executeUpdate();

					System.out.println("insert [" + res + "]");

					PersistenceParamHashmap.put(key,value);

					if (sqlLiteConn != null) {
						sqlLiteConn.close();	                
					}

				}
				else {
					//Hacemos el update del dato.
					System.out.println("param [" + key + "] found in DB updating");
					id = rs.getString("id");
					sql = "update entries set Value = ?, Updated = ? WHERE id = ?  COLLATE NOCASE;";
					pstmt.close();
					pstmt = sqlLiteConn.prepareStatement(sql);
					pstmt.clearParameters();

					// value
					pstmt.setString(1,value);

					// value
					pstmt.setString(2,dateFormat.format(date));

					// id
					pstmt.setString(3,id);
					rs.close();
					int res  = pstmt.executeUpdate();

					System.out.println("update [" + res + "]");

					PersistenceParamHashmap.put(key,value);

					if (sqlLiteConn != null) {
						sqlLiteConn.close();	                
					}
				}

			} catch (SQLException e) {
				if(e.getMessage() != null)
					System.out.println("Config.SetPersistence SQLException [" +  e.getMessage() + "]");
				else{
					e.printStackTrace();
				}	        	
			}		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.SetPersistence GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
	}	

	public static void SetDirective(String key, String value) {

		String sql = "";
		String id = "";
		sql = "select id from directives where key = ? COLLATE NOCASE limit 1;";

		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  


		lock.lock();
		try {
			Config.connect();


			try {

				PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql);  
				// key
				pstmt.setString(1,key);

				ResultSet rs  = pstmt.executeQuery();

				if(rs.isClosed()) {
					//No existe, hay que ingresarlo
					System.out.println("param [" + key + "] not found in DB inserting");

					sql = "insert into directives (Key, Value, Updated) values (?,?,?);";

					System.out.println("sql [" + sql + "]");

					pstmt.close();

					System.out.println("insert 1");
					pstmt = sqlLiteConn.prepareStatement(sql);
					System.out.println("insert 2");
					pstmt.clearParameters();
					// value
					pstmt.setString(1,key);

					// id
					pstmt.setString(2,value);

					pstmt.setString(3, dateFormat.format(date));
					System.out.println("insert 3");

					int res  = pstmt.executeUpdate();

					System.out.println("insert [" + res + "]");

					DirectiveParamHashmap.put(key,value);

					if (sqlLiteConn != null) {
						sqlLiteConn.close();	                
					}

				}
				else {
					//Hacemos el update del dato.
					System.out.println("param [" + key + "] found in DB updating");
					id = rs.getString("id");
					sql = "update Directives set Value = ?, Updated = ? WHERE id = ?  COLLATE NOCASE;";
					pstmt.close();
					pstmt = sqlLiteConn.prepareStatement(sql);
					pstmt.clearParameters();

					// value
					pstmt.setString(1,value);

					// value
					pstmt.setString(2,dateFormat.format(date));

					// id
					pstmt.setString(3,id);
					rs.close();
					int res  = pstmt.executeUpdate();

					System.out.println("update [" + res + "]");

					DirectiveParamHashmap.put(key,value);

					if (sqlLiteConn != null) {
						sqlLiteConn.close();	                
					}
				}

			} catch (SQLException e) {
				if(e.getMessage() != null)
					System.out.println("Config.SetDirective SQLException [" +  e.getMessage() + "]");
				else{
					e.printStackTrace();
				}	        	
			}		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.SetDirective GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	public static String GetAllDirectives() {
		
		String sql = "select key, value from directives order by key;";
		String ret = "";
		
		lock.lock();
		try {
			connect();
			
			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){
	    
	            ResultSet rs  = pstmt.executeQuery();
	    
	            if(rs.isClosed())
	            	System.out.println("No Directives found in DB !!!");
	            else {
	            	while (rs.next()) {	            		
	            		ret += "{\"Key\":\"" + rs.getString("key") +"\", \"Value\" : \"" + rs.getString("value") + "\"},";	            		
	            	}
	            }	            
	            
	            if (sqlLiteConn != null) {
	                sqlLiteConn.close();	                
	            }
	            	            
	        } catch (SQLException e) {
	        	if(e.getMessage() != null)
	        		System.out.println("Config.GetAllDirectives SQLException [" +  e.getMessage() + "]");
	        	else{
	        		e.printStackTrace();
	        	}	        	
	        }		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.GetAllDirectives GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
		
		ret = ret.substring(0, ret.length() -1);
		
		ret = "{\"Directives\" :[" + ret + "]}";
		
		byte[] textAsBytes = ret.getBytes(StandardCharsets.UTF_8);
		
		
		String retValE = Base64.getEncoder().encodeToString(textAsBytes);
		
        
        String retVal =  "{\"data\":{\"ReturnValue\":\"" + retValE + "\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
		
		
		return retVal;
		
		 
		
	}
	
public static String GetAllPulsarParams() {
		
		String sql = "select key, value from PulsarParams order by key;";
		String ret = "";
		
		lock.lock();
		try {
			connect();
			
			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){
	    
	            ResultSet rs  = pstmt.executeQuery();
	    
	            if(rs.isClosed())
	            	System.out.println("No PulsarParams found in DB !!!");
	            else {
	            	while (rs.next()) {	            		
	            		ret += "{\"Key\":\"" + rs.getString("key") +"\", \"Value\" : \"" + rs.getString("value") + "\"},";	            		
	            	}
	            }	            
	            
	            if (sqlLiteConn != null) {
	                sqlLiteConn.close();	                
	            }
	            	            
	        } catch (SQLException e) {
	        	if(e.getMessage() != null)
	        		System.out.println("Config.GetAllPulsarParams SQLException [" +  e.getMessage() + "]");
	        	else{
	        		e.printStackTrace();
	        	}	        	
	        }		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("Config.GetAllPulsarParams GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
		
		ret = ret.substring(0, ret.length() -1);
		
		ret = "{\"PulsarParams\" :[" + ret + "]}";
		
		byte[] textAsBytes = ret.getBytes(StandardCharsets.UTF_8);
		
		
		String retValE = Base64.getEncoder().encodeToString(textAsBytes);
		
        
        String retVal =  "{\"data\":{\"ReturnValue\":\"" + retValE + "\", \"AtmId\":\"" + Config.GetDirective("AtmId", null) + "\"}}";
		
		
		return retVal;
		
		 
		
	}
	
}
