package pentomino.common;

public enum BusinessEvent {

	SessionStart("SessionStart"),
	SessionEnd("SessionEnd"),
	AccessMediaRead("AccessMediaRead"),
	AffiliationStatus("AffiliationStatus"),
	PinRead("PinRead"),
	TransactionSelected("TransactionSelected"),
	TransactionRequestSent("TransactionRequestSent"),
	TransactionResponseReceived("TransactionResponseReceived"),
	AdministrativeOperationStarted("AdministrativeOperationStarted"),
	AdministrativeOperatonEnded("AdministrativeOperatonEnded"),
	DepositStart("DepositStart"),
	DepositEnd("DepositStart"),
	CashCollectionException("CashCollectionException"),
	CashCollectionEnded("CashCollectionEnded"),
	ReverseFail("ReverseFail");
	
		  
	  private final String businessEventName;
	 
	  BusinessEvent(String businessEvent) {
	    this.businessEventName = businessEvent;
	  }
	  
	  public String getBusinessEventName() {
	    return this.businessEventName;
	  }

}
