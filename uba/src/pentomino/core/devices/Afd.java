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

		CurrentUser.WithdrawalChange 	= sobrante;
		CurrentUser.WithdrawalDispense 	= dispenseAmount;

		System.out.println("Intento de dispensado por [" + dispenseAmount + "] Sobrante[" + sobrante + "]");

		//Aqui ya se que si tiene mas dinero del que pidio. Veamos cuanto le podemos dar si es que no hay ninguna combnacion con los billetes
		if(billCombinations(bills, denominations, 0, 0, dispenseAmount))
			return true;
		
		//Nos vamos a la combinacion de mayor a menor
		return false;
		
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

		
		if(CurrentUser.WithdrawalRequested < 20) {
			CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
			System.out.println("De origen no se puede dispensar [menor a 20]");
			return false;
		}        

		double sobrante = 0;
		if(CurrentUser.WithdrawalRequested < 40) {
			//Si es menor a 40 le restamos 20 para saber cuanto cambio queda
			sobrante = CurrentUser.WithdrawalRequested - 20;
		}
		else {
			sobrante = CurrentUser.WithdrawalRequested % 10;
		}

		System.out.println("sobrante [" + sobrante + "]");

		if(sobrante > 0)
			CurrentUser.dispenseStatus = DispenseStatus.Partial;


		//Se va a dispensar lo que solicito menos el sobrante.
		CurrentUser.WithdrawalDispense = CurrentUser.WithdrawalRequested - sobrante;
		CurrentUser.WithdrawalChange = sobrante;

		//Checamos los contadores actuales        
		Flow.actualizaContadoresRecicladores();       

		double disponible = JcmGlobalData.totalCashInRecycler1 + JcmGlobalData.totalCashInRecycler2;
		System.out.println("Disponible para dispensar [" + disponible + "]");

		//No hay dinero para dispensar
		if(disponible == 0) {
			CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}

		//Checamos que tenga algo de dinero.
		if(Flow.jcms[0].billCounters.Cass1Available == 0 && Flow.jcms[0].billCounters.Cass2Available == 0 && Flow.jcms[1].billCounters.Cass1Available == 0 && Flow.jcms[1].billCounters.Cass2Available == 0){
			System.out.println("No hay dinero en los caseteros para dispensar");
			CurrentUser.dispenseStatus = DispenseStatus.NoMoney;
			return false;
		}


		//Si es mas de lo que tenemos dispensamos todo lo que tenemos como parcial.
		if(CurrentUser.WithdrawalRequested > disponible) {			
			
			System.out.println("Retiro parcial mas dinero del que hay");
			CurrentUser.WithdrawalDispense = disponible;
			CurrentUser.WithdrawalChange = CurrentUser.WithdrawalRequested - disponible;			
			CurrentUser.dispenseStatus = DispenseStatus.Partial;
			
			JcmGlobalData.denominateInfo.clear();
			JcmGlobalData.denominateInfo.put(Flow.jcms[0].billCounters.Cass1Denom,Flow.jcms[0].billCounters.Cass1Available);
			JcmGlobalData.denominateInfo.put(Flow.jcms[0].billCounters.Cass2Denom,Flow.jcms[0].billCounters.Cass2Available);
			JcmGlobalData.denominateInfo.put(Flow.jcms[1].billCounters.Cass1Denom,Flow.jcms[1].billCounters.Cass1Available);
			JcmGlobalData.denominateInfo.put(Flow.jcms[1].billCounters.Cass2Denom,Flow.jcms[1].billCounters.Cass2Available);
			
			Flow.jcms[0].billsToDispenseFromCassette1 = Flow.jcms[0].billCounters.Cass1Available;
			Flow.jcms[0].billsToDispenseFromCassette2 = Flow.jcms[0].billCounters.Cass2Available;
			Flow.jcms[1].billsToDispenseFromCassette1 = Flow.jcms[1].billCounters.Cass1Available;
			Flow.jcms[1].billsToDispenseFromCassette2 = Flow.jcms[1].billCounters.Cass2Available;

			System.out.println("Solicitado [" + CurrentUser.WithdrawalRequested + "] disponible [" + disponible + "] sobrante [" + (CurrentUser.WithdrawalRequested - disponible) + "]");

			return true;			
		}		


		System.out.println("Solicitado [" + CurrentUser.WithdrawalRequested + "] disponible [" + disponible + "] sobrante [" + (CurrentUser.WithdrawalRequested - disponible) + "]");


		int iBuffer = 0;

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[0].billCounters.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[0].billCounters.Cass1Denom);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].billCounters.Cass1Denom, Flow.jcms[0].billCounters.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].billCounters.Cass1Denom, Flow.jcms[0].billCounters.Cass1Available);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[0].billCounters.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[0].billCounters.Cass2Denom);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].billCounters.Cass2Denom, Flow.jcms[0].billCounters.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[0].billCounters.Cass2Denom, Flow.jcms[0].billCounters.Cass2Available + iBuffer);


		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[1].billCounters.Cass1Denom))	{
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[1].billCounters.Cass1Denom);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].billCounters.Cass1Denom, Flow.jcms[1].billCounters.Cass1Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].billCounters.Cass1Denom, Flow.jcms[1].billCounters.Cass1Available + iBuffer);

		if(JcmGlobalData.availableBillsForRecycling.containsKey(Flow.jcms[1].billCounters.Cass2Denom))	{		
			iBuffer = JcmGlobalData.availableBillsForRecycling.get(Flow.jcms[1].billCounters.Cass2Denom);
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].billCounters.Cass2Denom, Flow.jcms[1].billCounters.Cass2Available + iBuffer);
		}
		else
			JcmGlobalData.availableBillsForRecycling.put(Flow.jcms[1].billCounters.Cass2Denom, Flow.jcms[1].billCounters.Cass2Available + iBuffer);


		//Sacamos la tabla de dispensado para el monto solicitado
		if(Afd.denominateInfo(CurrentUser.WithdrawalDispense)) {
			//TODO: Ahorita TODOS los cassettes deben ser diferentes...
			//Se puede dispensar

			//revisamos si hay cambio o no.
			if(CurrentUser.WithdrawalChange > 0){				//Dispensado parcial				
				System.out.println("HAY CAMBIO [" + CurrentUser.WithdrawalChange + "]" );
				CurrentUser.dispenseStatus = DispenseStatus.Partial;
			}
			else
				CurrentUser.dispenseStatus = DispenseStatus.Complete;

			//Seteamos los valores para cada casetero			
			Flow.jcms[0].billsToDispenseFromCassette1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].billCounters.Cass1Denom, 0);
			Flow.jcms[0].billsToDispenseFromCassette2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[0].billCounters.Cass2Denom, 0);
			Flow.jcms[1].billsToDispenseFromCassette1 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].billCounters.Cass1Denom, 0);
			Flow.jcms[1].billsToDispenseFromCassette2 = JcmGlobalData.denominateInfo.getOrDefault(Flow.jcms[1].billCounters.Cass2Denom, 0);

		}
		else {			
			
			//No hubo una denominacion directa, 
			
			CurrentUser.dispenseStatus = DispenseStatus.NotDispensable;
			return false;
		}

		System.out.println("jcm1cass1 [" + Flow.jcms[0].billsToDispenseFromCassette1 + "] jcm1cass2 [" + Flow.jcms[0].billsToDispenseFromCassette2 + "] jcm2cass1 [" + Flow.jcms[1].billsToDispenseFromCassette1 + "]jcm2cass2 [" + Flow.jcms[1].billsToDispenseFromCassette2 + "]" );

		return true;

	}
}
