package pentomino.common;

public enum AccountType {
	Savings {
		@Override
		public String getName() {

			return "Savings";
		}
	},
	Checkings {
		@Override
		public String getName() {

			return "Checkings";
		}
	},
	Credit {
		@Override
		public String getName() {

			return "Credit";
		}
	},
	Debit {
		@Override
		public String getName() {

			return "Debit";
		}
	},
	Other {
		@Override
		public String getName() {

			return "Other";
		}
	},
	International {
		@Override
		public String getName() {

			return "International";
		}
	},
	Administrative {
		@Override
		public String getName() {

			return "Administrative";
		}
	}    ,
	Internal
	{
		@Override
		public String getName() {

			return "Internal";
		}
	},
	None{
		@Override
		public String getName() {

			return "None";
		}
	};

	public abstract String getName();
}
