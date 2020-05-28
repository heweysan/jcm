package pentomino.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

public class Afd {

	public static void main(String[] args) {
		
		
		JcmGlobalData.availableBillsForRecycling.put(20, 4);
		JcmGlobalData.availableBillsForRecycling.put(200, 1);
		JcmGlobalData.availableBillsForRecycling.put(50, 2);
		JcmGlobalData.availableBillsForRecycling.put(100, 3);	
		
        System.out.println(denominateInfo(730));
		
	}

	private static boolean dispenseMixFound = false;
	
	public static boolean denominateInfo(double montoSolicitado) {
		
		
		dispenseMixFound = false;
		
		List<Integer> bills = new ArrayList<Integer>();
        List<Integer> denominations = new ArrayList<Integer>();
        
        //Sacamos las denominaciones que podemos usar.
        
        
        for (SortedMap.Entry<Integer, Integer> entry : JcmGlobalData.availableBillsForRecycling.entrySet()) {           
            denominations.add(entry.getKey());
        }  
             
        
        if(montoSolicitado < 20) {
       	 System.out.println("De origen no se puede dispensar [menor a 20]");
       	 return false;
        }        
        
        double sobrante = 0;
        if(montoSolicitado < 40) {
       	 sobrante = montoSolicitado - 20;
        }
        else {
       	 sobrante = montoSolicitado % 10;
        }        
                
        double dispenseAmount = montoSolicitado - sobrante;
        
        JcmGlobalData.dispenseChange = sobrante;
        JcmGlobalData.dispenseAmount = dispenseAmount;
        
        System.out.println("Intento de dispensado por [" + dispenseAmount + "] Sobrante[" + sobrante + "]");
        
		return billCombinations(bills, denominations, 0, 0, dispenseAmount);
	}
	
	public static boolean billCombinations(List<Integer> bills, List<Integer> denomination, int highest, double sum, double dispenseAmount) {
		
		if(dispenseMixFound)
			return true;
		
        if (sum == dispenseAmount) {        	
        	dispenseMixFound = Display(bills, denomination);            
            return dispenseMixFound;
        }
        
        if (sum > dispenseAmount) {         	
            return false;
        }      
        
        for (Integer billValue : denomination) {
            if (billValue >= highest) {                        
            	List<Integer> copy = new ArrayList<Integer>(bills);
                copy.add(billValue);               
                billCombinations(copy, denomination, billValue, sum + billValue, dispenseAmount);
            }
        }
        return dispenseMixFound;
	}
	
	
	static boolean Display(List<Integer> notes, List<Integer> amounts){
		//
		
		JcmGlobalData.denominateInfo.clear();
		
		for (Integer amount : amounts) {        
			int count = Collections.frequency(notes, amount);		
			
			if(count > JcmGlobalData.availableBillsForRecycling.get(amount)) {				
				return false;
			}
			JcmGlobalData.denominateInfo.put(amount,count);
            
        }
		System.out.println("---- O C U R R E N C I A ----");
		System.out.println(JcmGlobalData.denominateInfo);
		System.out.println("------------------------------");		
		return true;
    }
}
