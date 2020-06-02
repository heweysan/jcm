package pentomino.flow;
/**
 * 
 */

import pentomino.common.PinpadMode;

/**
 * @author hewey
 *
 */
public class CurrentUser {

	static String 		atmId ="";
	static String 		loginUser = "";
	static String 		loginPassword = "";
	static int 			montoRetiro = 0;
	static String 		tokenConfirmacion = "";
	static int 			montoDepositado = 0;	
	static String 		referencia = "";
	static int 			loginAttempts = 0;
	static int 			tokenAttempts = 0;
	static PinpadMode 	pinpadMode = PinpadMode.None;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	public static void cleanPinpadData(PinpadMode ppMode) {
		pinpadMode = ppMode;
		cleanPinpadData();		
	}
	
	public static void cleanPinpadData() {
		loginUser = "";
		loginPassword = "";
		loginAttempts = 0;
		tokenAttempts = 0;
		tokenConfirmacion = "";
	}
	

}
