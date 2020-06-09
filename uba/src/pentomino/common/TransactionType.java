package pentomino.common;

public enum TransactionType {
	 Withdrawal {
		@Override
		public
		String getName() {
			
			return  "Withdrawal";
		}
	},
     BalanceCheck {
		@Override
		public
		String getName() {
			
			return  "BalanceCheck";
		}
	},
     PinChange {
		@Override
		public
		String getName() {
			
			return  "PinChange";
		}
	},
     PhoneSale {
		@Override
		public
		String getName() {
			
			return  "PhoneSale";
		}
	},
     GenericSale {
		@Override
		public
		String getName() {
			
			return  "GenericSale";
		}
	},
     ServicePayment {
		@Override
		public
		String getName() {
			
			return  "ServicePayment";
		}
	},
     Replenishment {
		@Override
		public
		String getName() {
			
			return  "Replenishment";
		}
	},
     Administrative {
		@Override
		public
		String getName() {
			
			return  "Administrative";
		}
	},
     ControlMessage {
		@Override
		public
		String getName() {
			
			return  "ControlMessage";
		}
	},
     CashManagement {
		@Override
		public
		String getName() {
			// TODO Auto-generated method stub
			return  "CashManagement";
		}
	},
     Rollback {
		@Override
		public
		String getName() {
			// TODO Auto-generated method stub
			return  "Rollback";
		}
	},
     Reverse {
		@Override
		public
		String getName() {
			// TODO Auto-generated method stub
			return  "Reverse";
		}
	},
     PartialReverse {
		@Override
		public
		String getName() {
			// TODO Auto-generated method stub
			return  "PartialReverse";
		}
	},
     Exception {
		@Override
		public
		String getName() {
			// TODO Auto-generated method stub
			return  "Exception";
		}
	},
     Other {
		@Override
		public
		String getName() {
			
			return  "Other";
		}
	},
     None{
		    @Override
			public
		    String getName() {
		        return "None";
		    }
		};

	public abstract String getName();
	
}
