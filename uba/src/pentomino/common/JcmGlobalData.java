package pentomino.common;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import pentomino.config.Config;

public class JcmGlobalData {
	
	public static boolean isDebug = false;
	
	/**
	 * Indica si esta inciando modo adminsitrativo para no redireccioanr si hay algun error de red
	 */
	public static boolean isAdmin = false;
	
	
	public static int totalCashInRecycler1 = 0;
	public static int totalCashInRecycler2 = 0;
	
	public static int totalCashInRecyclers = 0;
	
	public static String atmId = "";
	
	
	/*
	 * Las denominaciones para dispensar de cadad cajita 
	 */
	
	public static int rec1bill1Denom = 0;
	public static int rec1bill2Denom = 0;
	public static int rec2bill1Denom = 0;
	public static int rec2bill2Denom = 0;
		
	
	/*
	 * Cuantos billetes de cada denominacion tiene disponible 
	 */
	
	public static int rec1bill1Available = 0;
	public static int rec1bill2Available = 0;
	public static int rec2bill1Available = 0;
	public static int rec2bill2Available = 0;
	

	/**
	 * Lo suma de lo que ha ido dispensando.
	 */
	public static double partialAmountDispensed = 0;
	
	/* 
	 * Variables de control para saber si ya dispensaron todos los caseteros
	*/
	public static boolean jcm1cass1Dispensed = false;
	public static boolean jcm1cass2Dispensed = false;
	public static boolean jcm2cass1Dispensed = false;
	public static boolean jcm2cass2Dispensed = false;	
	
	
	/*
	 * Varaibels para saber si ya termino de hacer el flush de billetes
	 */
	public static boolean jcm1cass1Flushed = false;
	public static boolean jcm1cass2Flushed = false;
	public static boolean jcm2cass1Flushed = false;
	public static boolean jcm2cass2Flushed = false;
	
	public static SortedMap<Integer,Integer> availableBillsForRecycling = new TreeMap<Integer, Integer>(Collections.reverseOrder());
	
	public static SortedMap<Integer,Integer> denominateInfo = new TreeMap<Integer, Integer>(Collections.reverseOrder());
	
	//Estas varibales se usan para saber que denominaciones tiene cada "cassette" logico en cada JCM
	
		
	/**
	 * Se usa para saber que denominaciones tiene el JCM1
	 * Se usan dos por si el otro JCM tiene las mismas denominaciones
	 */
	public static Map<String,String> jcm1cassetteDataValues = new HashMap<String,String>(); 
	
	/**
	 * Se usa para saber que denominaciones tiene el JCM2
	 * Se usan dos por si el otro JCM tiene las mismas denominaciones
	 * <CasetteNum,Denominacion>
	 */
	public static Map<String,String> jcm2cassetteDataValues = new HashMap<String,String>(); 
	
	
	
	public static String getKey(Integer jcm, String denom) {
	   
		if(jcm == 0) {
			for (Entry<String, String> entry : jcm1cassetteDataValues.entrySet()) {
				//System.out.println("entry.getValue() [" + entry.getValue() + "]");
		        if (entry.getValue().equals(denom)) {
		            return entry.getKey();
		        }
		    }
		}
		if(jcm == 1) {
			for (Entry<String, String> entry : jcm2cassetteDataValues.entrySet()) {
		        if (entry.getValue().equals(denom)) {
		            return entry.getKey();
		        }
		    }
		}
		
	    return "";
	}
	
	public static String denominateInfoToString() {
		return "[20x" + denominateInfo.getOrDefault(20, 0) + "|50x"  + denominateInfo.getOrDefault(50, 0) + "|100x"  + denominateInfo.getOrDefault(100, 0) + "|200x"  + denominateInfo.getOrDefault(200, 0) + "|500x"  + denominateInfo.getOrDefault(500, 0) + "|1000x"  + denominateInfo.getOrDefault(1000, 0) + "]";
	}
	
	
	public static void main(String[] args) {
			
	}

	public static int getMaxRecyclableCash() {
		return Integer.parseInt(Config.GetDirective("maxRecyclableCash","0"));
	}

	public static void setMaxRecyclableCash(int maxRecyclableCash) {
	}
	
	
	/**
	 * Precarga variables de Config que sabemos vamos a usar regularmente.
	 * Cuando se actualiza una variable via manager se deben actualizar. TODO: Revisar que esto se haga
	 */
	public static void PreloadConfigVariables() {
		
		System.out.println("PreloadConfigVariables");
		
		atmId = Config.GetDirective("AtmId", "-----");
		rec1bill1Denom = Integer.parseInt(Config.GetDirective("Jcm1Denom1", "20"));
		rec1bill2Denom = Integer.parseInt(Config.GetDirective("Jcm1Denom2", "50"));
		rec2bill1Denom = Integer.parseInt(Config.GetDirective("Jcm2Denom1", "100"));
		rec2bill2Denom = Integer.parseInt(Config.GetDirective("Jcm2Denom2", "200"));
		setMaxRecyclableCash(Integer.parseInt(Config.GetDirective("maxRecyclableCash","0")));
		
		jcm1cassetteDataValues = new HashMap<String, String>();
		jcm2cassetteDataValues = new HashMap<String, String>();

		jcm1cassetteDataValues.put("1", Config.GetPersistence("Cassette1Value", "0"));
		jcm1cassetteDataValues.put("2", Config.GetPersistence("Cassette2Value", "0"));

		jcm2cassetteDataValues.put("3", Config.GetPersistence("Cassette3Value", "0"));
		jcm2cassetteDataValues.put("4", Config.GetPersistence("Cassette4Value", "0"));		
		
		System.out.println("rec1bill1Denom " + rec1bill1Denom);
		System.out.println("rec1bill2Denom " + rec1bill2Denom);
		System.out.println("rec2bill1Denom " + rec2bill1Denom);
		System.out.println("rec2bill2Denom " + rec2bill2Denom);
		
	}

}
