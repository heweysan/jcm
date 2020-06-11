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

	public static String 			atmId ="";
	public static String 			loginUser = "";
	public static String 			loginPassword = "";
	public static String 			tokenConfirmacion = "";
	public static String 			reference = "";
	public static int 				loginAttempts = 0;
	public static int 				tokenAttempts = 0;
	public static PinpadMode 		pinpadMode = PinpadMode.None;
	public static String 			asteriscos = "";
	public static String 			token = "";
	public static jcmOperation 		currentOperation = jcmOperation.None;
	public static DispenseStatus 	dispenseStatus = DispenseStatus.None; 
	public static String			movementId = "";	
	public static String			BoardStatus = "Available";
	
	/**
	 * De cuanto es el retiro, no necesariamente lo que se va  apoder dispensar
	 */
	public static double 	 		WithdrawalRequested = 0;
	/**
	 * Cuanto es lo que realmente se va a dispensar
	 */
	public static double  			WithdrawalDispense = 0;
	/**
	 * El sobrante del dispensado. 
	 * Es decir la diferencia entre lo que pidio y lo que se puede dispensar.
	 * Por ejemplo pidio 1500 solo toenemos 1000, el cambio es 500.
	 */
	public static double  			WithdrawalChange = 0;
	
	public static int 				totalAmountInserted = 0;
	
	
	public static void resetUserData() {
		pinpadMode = PinpadMode.None;
		movementId = "";
		WithdrawalRequested = 0;
		WithdrawalDispense = 0;
		totalAmountInserted = 0;
		cleanPinpadData();		
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
		asteriscos = "";
	}


}
