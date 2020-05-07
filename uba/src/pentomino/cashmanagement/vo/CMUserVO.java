package pentomino.cashmanagement.vo;

import java.io.Serializable;
import java.util.List;

public class CMUserVO implements Serializable {
	
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Boolean success;
	
	 public String message;
	
	 public Boolean isValid;
	
	 public List depositInfo;
	
	 public String profileName;
	
	 public int profileId;
	
	 public ExceptionVO exception;
	
	 public Integer totalDeposit;
	
	 public Boolean allowWithdrawals;

}
