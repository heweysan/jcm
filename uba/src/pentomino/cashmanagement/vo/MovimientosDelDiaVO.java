package pentomino.cashmanagement.vo;

import java.util.List;

public class MovimientosDelDiaVO {
	public String message;
	public boolean success;
	public String atm;
	
	public Exception exception;
	
	public long date;
	public String depositId;
	public String withdrawalId;
	public double requestingUser;
	public int totalDeposits;
	public long depositsAmount;
	public int totalWithdrawals;
	public long withdrawalsAmount;
	public long totalCollections;
	public long collectionsAmount;	
	public List<DepositoDelDia> depositsDetail;
	public List<DepositoDelDia> withdrawalsDetail;
}


class Exception{
	public String className;
	public String message;

}