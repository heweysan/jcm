package pentomino.cashmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pentomino.cashmanagement.vo.CmMessageRequest;
import pentomino.flow.Flow;
import rabbitClient.CmListener;

public class CmQueue implements Runnable{

	private static final Logger logger = LogManager.getLogger(Flow.class.getName());

	public static LinkedList<CmMessageRequest> queueList = new LinkedList<CmMessageRequest>();

	static ReentrantLock lock = new ReentrantLock();

	private static Connection sqlLiteConn = null;	


	@Override
	public void run() {
		logger.info("CmQueue [running]");
		System.out.println("CmQueue [running]");

		//Revisamos si hay retiros pendientes
		GetPendingWithdrawals();

		try {		
			CmListener myCmListener = new CmListener();
			myCmListener.SetupRabbitListener();				

		} catch (Exception e) {
			logger.error(e);
			System.out.println("CmQueue.run EXCEPTION");
			e.printStackTrace();
		}

		Timer screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Pending Widthrawals [" + queueList.size() + "]");
			}
		}, TimeUnit.SECONDS.toMillis(10),TimeUnit.MINUTES.toMillis(1));

	}


	private static void connect() { 

		try { 

			if(sqlLiteConn != null && !sqlLiteConn.isClosed())
				return;

			String url = "jdbc:sqlite:./Pentomino.CashManagement.db3";

			sqlLiteConn = DriverManager.getConnection(url);           

		} catch (SQLException e) {
			if(e.getMessage() != null)
				System.out.println("CmQueue.connect SQLException [" +  e.getMessage() + "]");
			else{
				e.printStackTrace();
			}

		} finally {

		}
	}

	public static void GetPendingWithdrawals() {


		String sql = "SELECT * FROM withdrawals WHERE cashedout = 0  COLLATE NOCASE;";

		lock.lock();
		try {
			connect();

			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){

				ResultSet rs  = pstmt.executeQuery();

				if(rs.isClosed())
					System.out.println("No pending Withdrawals found in DB");
				else {
					while (rs.next()) {	            		
						CmMessageRequest myMessage = new CmMessageRequest();
						myMessage.cashedout = rs.getInt("cashedout");
						myMessage.amount = rs.getDouble("amount");
						myMessage.token = rs.getString("token");
						myMessage.reference = rs.getString("reference");	
						queueList.add(myMessage);
					}
				}	            

				if (sqlLiteConn != null) {
					sqlLiteConn.close();	                
				}

			} catch (SQLException e) {
				if(e.getMessage() != null)
					System.out.println("CmQueue.GetPendingWithdrawals SQLException [" +  e.getMessage() + "]");
				else{
					e.printStackTrace();
				}	        	
			}		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("CmQueue.GetPendingWithdrawals GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}

	}


	public static void InsertPendingWithdrawal(CmMessageRequest myMessage) {	


		String sql = "insert into withdrawals (reference, amount, token, cashedout) values (?,?,?,?);";

		lock.lock();
		try {
			connect();

			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){

				// set the value
				pstmt.setString(1,myMessage.reference);
				pstmt.setDouble(2,myMessage.amount);
				pstmt.setString(3,myMessage.token);
				pstmt.setInt(4,0);

				pstmt.executeUpdate();
				
				if (sqlLiteConn != null) {
					sqlLiteConn.close();	                
				}

			} catch (SQLException e) {
				if(e.getMessage() != null)
					System.out.println("CmQueue.InsertPendingWithdrawal SQLException [" +  e.getMessage() + "]");
				else{
					e.printStackTrace();
				}	        	
			}		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("CmQueue.InsertPendingWithdrawal GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
	}

	public static void ClosePendingWithdrawal(String reference) {	

		System.out.println("ClosePendingWithdrawal [" + reference + "]");


		String sql = "update withdrawals set cashedout = 1 where reference = ?;";

		lock.lock();
		try {
			connect();

			try (PreparedStatement pstmt  = sqlLiteConn.prepareStatement(sql)){

				// set the value
				pstmt.setString(1,reference);

				pstmt.executeUpdate();

				if (sqlLiteConn != null) {
					sqlLiteConn.close();	                
				}

			} catch (SQLException e) {
				if(e.getMessage() != null)
					System.out.println("CmQueue.ClosePendingWithdrawal SQLException [" +  e.getMessage() + "]");
				else{
					e.printStackTrace();
				}	        	
			}		
		}catch(Exception ge) {
			if(ge.getMessage() != null)
				System.out.println("CmQueue.ClosePendingWithdrawal GENERAL EXCEPTION [" +  ge.getMessage() + "]");
			else{
				ge.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
	}




	public static void addPendingWithdrawal(CmMessageRequest myMsg) {

		queueList.add(myMsg);
		InsertPendingWithdrawal(myMsg);


	}


}
