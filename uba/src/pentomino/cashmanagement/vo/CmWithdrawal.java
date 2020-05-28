package pentomino.cashmanagement.vo;

public class CmWithdrawal {

    public String atmId;
    public int operatorId;
    public String password;
    public double amount;
    public String token;
    public long operationDateTimeMilliseconds;
    public String reference;
    public String operationType = "FLEXPOS_WITHDRAWAL";
	
}
