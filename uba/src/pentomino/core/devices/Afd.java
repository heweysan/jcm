package pentomino.core.devices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.DispenseStatus;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;

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

	public static void UpdateCurrentCountRequest() {

		Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,Flow.jcms[0].jcmMessage);

		Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,Flow.jcms[1].jcmMessage);
	}
	
	public static void BroadcastFullStatus() {
				
		String fullStatus = ""; 		//Cassette1-MXN-1133-1203%Cassette2-MXN-88-122%Cassette3-MXN-261-340%Cassette4-MXN-0-0
		String cashUnitAmounts = "";  	//Cassette1-500;Cassette2-100;Cassette3-200;Cassette4-500
		String cashUnitStatus = "";		//3-OK-0;4-OK-0;5-OK-0;6-EMPTY-0  id-STATUS-rejected
		
		String currency = "MXN";	
		
		for (int j = 1; j < 5; j++) { 
			int originalValue  = Integer.parseInt(Config.GetPersistence("Cassette" + j + "Original","0"));
			int dispensedValue = Integer.parseInt(Config.GetPersistence("Cassette" + j + "Dispensed","0"));
			int actual = originalValue - dispensedValue;
			String denom  = Config.GetPersistence("Cassette" + j + "Value","0");
			
			fullStatus+="Cassette" + j + "-" + currency + "-" + actual + "-" + originalValue + "%";
			cashUnitAmounts += "Cassette" + j + "-" + denom + ";";
			
			
			
		}
		
		fullStatus = fullStatus.substring(0,fullStatus.length()-1);
		cashUnitAmounts = cashUnitAmounts.substring(0,cashUnitAmounts.length()-1);
		
		System.out.println("fullStatus [" + fullStatus + "]");		
		RaspiAgent.Broadcast(DeviceEvent.AFD_FullStatus, fullStatus);
		
		System.out.println("cashUnitAmounts [" + cashUnitAmounts + "]");
		RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitAmounts, cashUnitAmounts);
		
		
		JcmGlobalData.jcm1cassetteDataValues.put("1", Config.GetPersistence("Cassette1Value", "0"));
		JcmGlobalData.jcm1cassetteDataValues.put("2", Config.GetPersistence("Cassette2Value", "0"));

		JcmGlobalData.jcm2cassetteDataValues.put("3", Config.GetPersistence("Cassette3Value", "0"));
		JcmGlobalData.jcm2cassetteDataValues.put("4", Config.GetPersistence("Cassette4Value", "0"));
		
		JcmGlobalData.jcm1cassetteDataValues.get("1");
		
		if(JcmGlobalData.rec1bill1Available > 0)		
			cashUnitStatus += "1-OK-0";
		else
			cashUnitStatus += "1-EMPTY-0";
		
		if(JcmGlobalData.rec1bill2Available > 0)		
			cashUnitStatus += ";2-OK-0";
		else
			cashUnitStatus += ";2-EMPTY-0";
		
		if(JcmGlobalData.rec2bill1Available > 0)		
			cashUnitStatus += ";3-OK-0";
		else
			cashUnitStatus += ";3-EMPTY-0";
		
		if(JcmGlobalData.rec2bill2Available > 0)		
			cashUnitStatus += ";4-OK-0";
		else
			cashUnitStatus += ";4-EMPTY-0";
		
		System.out.println("cashUnitStatus [" + cashUnitStatus + "]");
		RaspiAgent.Broadcast(DeviceEvent.AFD_CashUnitStatus, cashUnitStatus);
		
				
	}	

}
