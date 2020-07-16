package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.jcmagent.RaspiAgent;

public class PanelDispense extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Timer screenTimerDispense = new Timer();

	public static JLabel lblRetiraBilletesMontoDispensar = new JLabel("$0");
	
	public static boolean dispenseError = false;

	/**
	 * @wbp.parser.constructor
	 */

	public PanelDispense(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	


	@Override
	public void ContentPanel() {
		lblRetiraBilletesMontoDispensar.setHorizontalAlignment(SwingConstants.LEFT);
		lblRetiraBilletesMontoDispensar.setFont(new Font("Tahoma", Font.PLAIN, 55));
		lblRetiraBilletesMontoDispensar.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensar.setBounds(1200, 939, 695, 83); // 667, 600, 622, 92          667, 940, 622, 92
		add(lblRetiraBilletesMontoDispensar);
		
		JLabel lblRetiroParcialMonto = new JLabel("");
		lblRetiroParcialMonto.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiroParcialMonto.setFont(new Font("Tahoma", Font.BOLD, 99));
		lblRetiroParcialMonto.setForeground(new Color(255, 255, 255));
		lblRetiroParcialMonto.setBounds(10, 569, 1900, 241);
		add(lblRetiroParcialMonto);

	}

	public static void dispense() {

		dispenseError = false;
		
		// Iniciamos el dispensado
		JcmGlobalData.jcm1cass1Dispensed = false;
		JcmGlobalData.jcm1cass2Dispensed = false;
		JcmGlobalData.jcm2cass1Dispensed = false;
		JcmGlobalData.jcm2cass2Dispensed = false;

		JcmGlobalData.partialAmountDispensed = 0;

		RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, ""+ ((Flow.jcms[0].billsToDispenseFromCassette1 * Flow.jcms[0].billCounters.Cass1Denom) + (Flow.jcms[0].billsToDispenseFromCassette2 * Flow.jcms[0].billCounters.Cass2Denom) + (Flow.jcms[1].billsToDispenseFromCassette1 * Flow.jcms[1].billCounters.Cass1Denom) + (Flow.jcms[1].billsToDispenseFromCassette2 * Flow.jcms[1].billCounters.Cass2Denom)));
		RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + Flow.jcms[0].billsToDispenseFromCassette1  + "x" +  Flow.jcms[0].billCounters.Cass1Denom + ";" + Flow.jcms[0].billsToDispenseFromCassette2  + "x" +  Flow.jcms[0].billCounters.Cass2Denom
				+ ";" + Flow.jcms[1].billsToDispenseFromCassette1  + "x" +  Flow.jcms[1].billCounters.Cass1Denom + ";" + Flow.jcms[1].billsToDispenseFromCassette2  + "x" +  Flow.jcms[1].billCounters.Cass2Denom);
	
				

		//Checamos para JCM1
		if(Flow.jcms[0].billsToDispenseFromCassette1 > 0 || Flow.jcms[0].billsToDispenseFromCassette2 > 0) {
			System.out.println("Deshabilitamos JCM[0] para dispense");
			Flow.jcms[0].currentOpertion = jcmOperation.Dispense;
			Flow.jcms[0].dispensingFromCassette = 1;
			
			// primero el inhibit
			Flow.jcms[0].jcmMessage[3] = 0x01;
			Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
		}
		else {
			//No hay nada que dispensar del JCM[1];
			JcmGlobalData.jcm1cass1Dispensed = true;
			JcmGlobalData.jcm1cass2Dispensed = true;
			Flow.jcms[0].dispensingFromCassette = -1;
		}		


		screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {			
				if(JcmGlobalData.jcm1cass1Dispensed && JcmGlobalData.jcm1cass2Dispensed) {
					System.out.println("Dispense JCM[1] terminado");
					if(Flow.jcms[1].billsToDispenseFromCassette1 > 0 || Flow.jcms[1].billsToDispenseFromCassette2 > 0) {
							
						Flow.jcms[1].dispensingFromCassette = 1;
						System.out.println("Deshabilitamos JCM2 para dispense");
						Flow.jcms[1].currentOpertion = jcmOperation.Dispense;

						// primero el inhibit
						Flow.jcms[1].jcmMessage[3] = 0x01;
						Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
					}
					else{						
						System.out.println("Nada que dispensar del JCM[2]");
						JcmGlobalData.jcm2cass1Dispensed = true;
						JcmGlobalData.jcm2cass2Dispensed = true;
						Flow.jcms[1].dispensingFromCassette = -1;
					}
					
					
					screenTimerDispense.cancel();
				}            	
			}
		}, 1000,1000);
	}
	
	
	public static void dispenseError() {
		
		screenTimerDispense.cancel();
				
		
		System.out.println("Dispensado parcial [" + JcmGlobalData.partialAmountDispensed + "]");
		
		if(JcmGlobalData.partialAmountDispensed == 0) {
			//DO NOTHING
		}
		else {
			//Si dispenso algo tenemos que sumar lo que no dispenso al reverso para mandarlo.
			System.out.println("Reverso original [" + CurrentUser.WithdrawalChange + "]");
			CurrentUser.WithdrawalChange += (CurrentUser.WithdrawalDispense - JcmGlobalData.partialAmountDispensed); 
			System.out.println("Reverso nuevo  [" + CurrentUser.WithdrawalChange + "]");
		}
		
		JcmGlobalData.jcm1cass1Dispensed = true;
		JcmGlobalData.jcm1cass2Dispensed = true;
		JcmGlobalData.jcm2cass1Dispensed = true;
		JcmGlobalData.jcm2cass2Dispensed = true;
		
	}

	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelDispense]");
		
		

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload [PanelDispense]");
	}
}
