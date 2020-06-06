package pentomino.core.devices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import pentomino.common.JcmGlobalData;
import pentomino.flow.CurrentUser;
import pentomino.flow.DispenseStatus;
import pentomino.flow.Flow;

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
	
	public static boolean validateDispense() {

		if(JcmGlobalData.isDebug) {
			System.out.println("validateDispense  [DEBUG]");
			
			Flow.jcms[0].contadores.Cass1Denom = 200;
			Flow.jcms[0].contadores.Cass2Denom = 100;
			Flow.jcms[1].contadores.Cass1Denom = 20;
			Flow.jcms[1].contadores.Cass2Denom = 50;
			
			
			
			JcmGlobalData.availableBillsForRecycling.put(20, 4);
			JcmGlobalData.availableBillsForRecycling.put(200, 1);
			JcmGlobalData.availableBillsForRecycling.put(50, 2);
			JcmGlobalData.availableBillsForRecycling.put(100, 3);	
			JcmGlobalData.montoDispensar = Flow.montoRetiro;
			if(Afd.denominateInfo(JcmGlobalData.montoDispensar)) {
				//TODO: Ahorita TODOS los cassettes deben ser diferentes...
				//Se puede dispensar

				//revisamos si hay cambio o no.
				if(JcmGlobalData.dispenseChange > 0){				//Dispensado parcial				
					System.out.println("HAY CAMBIO [" +JcmGlobalData.dispenseChange + "]" );
					CurrentUser.dispenseStatus = DispenseStatus.Partial;
				}
				else
					CurrentUser.dispenseStatus = DispenseStatus.Complete;
				
				//Seteamos los valores para cada casetero			
				Flow.jcms[0].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].contadores.Cass1Denom, 0);
				Flow.jcms[0].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].contadores.Cass2Denom, 0);
				Flow.jcms[1].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].contadores.Cass1Denom, 0);
				Flow.jcms[1].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].contadores.Cass2Denom, 0);

			}
			else {			
				CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
				return false;
			}
			
			return true;
		}

		if(Flow.montoRetiro < 20) {
			CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
			System.out.println("De origen no se puede dispensar [menor a 20]");
			return false;
		}        

		double sobrante = 0;
		if(Flow.montoRetiro < 40) {
			sobrante = Flow.montoRetiro - 20;
		}
		else {
			sobrante = Flow.montoRetiro % 10;
		}

		System.out.println("sobrante [" + sobrante + "]");

		if(sobrante > 0)
			CurrentUser.dispenseStatus = DispenseStatus.Partial;


		JcmGlobalData.montoDispensar = Flow.montoRetiro - sobrante;

		//Checamos los contadores actuales        
		Flow.actualizaContadoresRecicladores();       

		double disponible = JcmGlobalData.totalCashInRecyclers1 + JcmGlobalData.totalCashInRecyclers2;
		System.out.println("Disponible para dispensar [" + disponible + "]");

		//No hay dinero para dispensar
		if(disponible == 0) {
			CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}

		//Checamos que tenga algo de dinero.
		if(Flow.jcms[0].contadores.Cass1Available == 0 && Flow.jcms[0].contadores.Cass2Available == 0 && Flow.jcms[1].contadores.Cass1Available == 0 && Flow.jcms[1].contadores.Cass2Available == 0){
			System.out.println("No hay dinero en los caseteros para dispensar");
			CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}


		//Si es mas de lo que tenemos dispensamos todo lo que tenemos como parcial.
		if(Flow.montoRetiro > disponible) {			
			JcmGlobalData.montoDispensar = disponible;
			System.out.println("Retiro parcial mas dinero del que hay");
			CurrentUser.dispenseStatus = DispenseStatus.Partial;
			Flow.jcms[0].jcmCass1 = Flow.jcms[0].contadores.Cass1Available;
			Flow.jcms[0].jcmCass2 = Flow.jcms[0].contadores.Cass2Available;
			Flow.jcms[1].jcmCass1 = Flow.jcms[1].contadores.Cass1Available;
			Flow.jcms[1].jcmCass2 = Flow.jcms[1].contadores.Cass2Available;
			return true;			
		}		


		System.out.println("Solicitado [" + Flow.montoRetiro + "] disponible [" + disponible + "] sobrante [" + (Flow.montoRetiro - disponible) + "]");


		int iBuffer = 0;

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[0].contadores.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[0].contadores.Cass1Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass1Denom, Flow.jcms[0].contadores.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass1Denom, Flow.jcms[0].contadores.Cass1Available);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[0].contadores.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[0].contadores.Cass2Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass2Denom, Flow.jcms[0].contadores.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].contadores.Cass2Denom, Flow.jcms[0].contadores.Cass2Available + iBuffer);


		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[1].contadores.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[1].contadores.Cass1Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass1Denom, Flow.jcms[1].contadores.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass1Denom, Flow.jcms[1].contadores.Cass1Available + iBuffer);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[1].contadores.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[1].contadores.Cass2Available);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass2Denom, Flow.jcms[1].contadores.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].contadores.Cass2Denom, Flow.jcms[1].contadores.Cass2Available + iBuffer);


		if(Afd.denominateInfo(JcmGlobalData.montoDispensar)) {
			//TODO: Ahorita TODOS los cassettes deben ser diferentes...
			//Se puede dispensar

			//revisamos si hay cambio o no.
			if(JcmGlobalData.dispenseChange > 0){				//Dispensado parcial				
				System.out.println("HAY CAMBIO [" +JcmGlobalData.dispenseChange + "]" );
				CurrentUser.dispenseStatus = DispenseStatus.Partial;
			}
			else
				CurrentUser.dispenseStatus = DispenseStatus.Complete;

			//Seteamos los valores para cada casetero			
			Flow.jcms[0].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].contadores.Cass1Denom, 0);
			Flow.jcms[0].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].contadores.Cass2Denom, 0);
			Flow.jcms[1].jcmCass1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].contadores.Cass1Denom, 0);
			Flow.jcms[1].jcmCass2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].contadores.Cass2Denom, 0);

		}
		else {			
			CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
			return false;
		}

		System.out.println("jcm1cass1 [" + Flow.jcms[0].jcmCass1 + "] jcm1cass2 [" + Flow.jcms[0].jcmCass2 + "] jcm2cass1 [" + Flow.jcms[1].jcmCass1 + "]jcm2cass2 [" + Flow.jcms[1].jcmCass2 + "]" );

		return true;

	}
}
