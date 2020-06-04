package pentomino.flow;
/**
 * 
 */

import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;

/**
 * @author hewey
 *
 */
public class CurrentUser {

	public static String 		atmId ="";
	private static String 		loginUser = "";
	public static String 		loginPassword = "";
	public static int 			montoRetiro = 0;
	public static String 		tokenConfirmacion = "";
	public static int 			montoDepositado = 0;	
	public static String 		referencia = "";
	public static int 			loginAttempts = 0;
	public static int 			tokenAttempts = 0;
	public static PinpadMode 	pinpadMode = PinpadMode.None;
	public static String 		asteriscos = "";
	public static String 		token = "";
	public static jcmOperation 	currentOperation = jcmOperation.None;
	public static DispenseStatus dispenseStatus = DispenseStatus.None; 
	
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
		setLoginUser("");
		loginPassword = "";
		loginAttempts = 0;
		tokenAttempts = 0;
		tokenConfirmacion = "";
		asteriscos = "";
	}


	public static String getLoginUser() {
		return loginUser;
	}


	public static void setLoginUser(String loginUser) {
		CurrentUser.loginUser = loginUser;
	}
	

}
