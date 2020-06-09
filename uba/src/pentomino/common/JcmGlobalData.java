package pentomino.common;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class JcmGlobalData {
	
	/***
	 * El maximo de dinero antes de que se vaya a cajita directo.
	 */
	public static int maxRecyclableCash = 0;
	
	public static int totalCashInRecycler1 = 0;
	public static int totalCashInRecycler2 = 0;
	
	public static int totalCashInRecyclers = 0;
	
	public static int rec1bill1Denom = 0;
	public static int rec1bill2Denom = 0;
	public static int rec2bill1Denom = 0;
	public static int rec2bill2Denom = 0;
		
	public static int rec1bill1Available = 0;
	public static int rec1bill2Available = 0;
	public static int rec2bill1Available = 0;
	public static int rec2bill2Available = 0;
	
	public static double dispenseAmount = 0;
	public static double dispenseChange = 0;
	
	public static double montoDispensar;
	
	public static double partialAmountDispensed = 0;
	
	/* Variables de control para saber si ya dispensaron todos los caseteros*/
	public static boolean jcm1cass1Dispensed = false;
	public static boolean jcm1cass2Dispensed = false;
	public static boolean jcm2cass1Dispensed = false;
	public static boolean jcm2cass2Dispensed = false;	
	
	
	public static SortedMap<Integer,Integer> availableBillsForRecycling = new TreeMap<Integer, Integer>(Collections.reverseOrder());
	
	public static SortedMap<Integer,Integer> denominateInfo = new TreeMap<Integer, Integer>(Collections.reverseOrder());
	
	public static boolean isDebug = false;
	
	public static String denominateInfoToString() {
		return "[20x" + denominateInfo.getOrDefault(20, 0) + "|50x"  + denominateInfo.getOrDefault(50, 0) + "|100x"  + denominateInfo.getOrDefault(100, 0) + "|200x"  + denominateInfo.getOrDefault(200, 0) + "|500x"  + denominateInfo.getOrDefault(500, 0) + "|1000x"  + denominateInfo.getOrDefault(1000, 0) + "]";
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
	}

}
