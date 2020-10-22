package pentomino.cashmanagement.vo;

import java.util.List;

public class DepositosDelDiaVO {
	public String message;
	public boolean success;
	public String atm;
	
	public long date;
	public String depositId;
	public String withdrawalId;
	public String requestingUser;
	public int totalDeposits;
	public long depositsAmount;
	public int totalWithdrawals;
	public long withdrawalsAmount;
	public long totalCollections;
	public long collectionsAmount;
	
	public List<DepositoDelDia> depositsDetail;
}
