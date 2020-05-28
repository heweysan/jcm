package pentomino.common;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class JcmGlobalData {
	
	/***
	 * El maximo de dinero antes de que se vaya a cajita directo.
	 */
	public static int maxRecyclableChash = 0;
	public static int totalCashInRecyclers1 = 0;
	public static int totalCashInRecyclers2 = 0;
	
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
	
	public static SortedMap<Integer,Integer> availableBillsForRecycling = new TreeMap<Integer, Integer>(Collections.reverseOrder());
	
	public static SortedMap<Integer,Integer> denominateInfo = new TreeMap<Integer, Integer>(Collections.reverseOrder());
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
	}

}
