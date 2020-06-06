package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.jcmOperation;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;

public class PanelDispense {
	public JPanel contentPanel;

	public static JLabel lblRetiraBilletesMontoDispensar = new JLabel(".");
	
public PanelDispense() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
				
		contentPanel.add(new DebugButtons().getPanel());		
		
		lblRetiraBilletesMontoDispensar.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiraBilletesMontoDispensar.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblRetiraBilletesMontoDispensar.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensar.setBounds(10, 509, 1900, 153);
		contentPanel.add(lblRetiraBilletesMontoDispensar);
		
}

public static void dispense() {
	System.out.println("Retirar Flow.jcms[0].jcmCass1 [" + Flow.jcms[0].jcmCass1 + "] Flow.jcms[0].jcmCass2 [" + Flow.jcms[0].jcmCass2 + "] Flow.jcms[1].jcmCass1 [" + Flow.jcms[1].jcmCass1 + "] Flow.jcms[1].jcmCass2 [" + Flow.jcms[1].jcmCass2 + "]" );

	// Iniciamos el dispensado
	Flow.jcm1cass1Dispensed = false;
	Flow.jcm1cass2Dispensed = false;
	Flow.jcm2cass1Dispensed = false;
	Flow.jcm2cass2Dispensed = false;

	
	RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, ""+ ((Flow.jcms[0].jcmCass1 * Flow.jcms[0].contadores.Cass1Denom) + (Flow.jcms[0].jcmCass2 * Flow.jcms[0].contadores.Cass2Denom) + (Flow.jcms[1].jcmCass1 * Flow.jcms[1].contadores.Cass1Denom) + (Flow.jcms[1].jcmCass2 * Flow.jcms[1].contadores.Cass2Denom)));
	RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + Flow.jcms[0].jcmCass1  + "x" +  Flow.jcms[0].contadores.Cass1Denom + ";" + Flow.jcms[0].jcmCass2  + "x" +  Flow.jcms[0].contadores.Cass2Denom
			+ ";" + Flow.jcms[1].jcmCass1  + "x" +  Flow.jcms[1].contadores.Cass1Denom + ";" + Flow.jcms[1].jcmCass2  + "x" +  Flow.jcms[1].contadores.Cass2Denom);

	if(JcmGlobalData.isDebug)
		return;
	
	
	//Checamos para JCM1
	if(Flow.jcms[0].jcmCass1 > 0 || Flow.jcms[0].jcmCass2 > 0) {				
		//El primer denominate se genera aqui:
		//RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (Flow.jcms[0].jcmCass1 * Flow.jcms[0].contadores.Cass1Denom));
		//RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + Flow.jcms[0].jcmCass1  + "x" +  Flow.jcms[0].contadores.Cass1Denom);
		System.out.println("Deshabilitamos JCM[1] para dispense");
		Flow.jcms[0].currentOpertion = jcmOperation.Dispense;

		// primero el inhibit
		Flow.jcms[0].jcmMessage[3] = 0x01;
		Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
	}
	else {
		//No hay nada que dispensar del JCM[1];
		Flow.jcm1cass1Dispensed = true;
		Flow.jcm1cass2Dispensed = true;
	}


	Timer screenTimerDispense = new Timer();

	screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
		@Override
		public void run() {			
			if(Flow.jcm1cass1Dispensed && Flow.jcm1cass2Dispensed) {
				System.out.println("Dispense JCM[1] terminado");
				if(Flow.jcms[1].jcmCass1 > 0 || Flow.jcms[1].jcmCass2 > 0) {

					//El primer denominate se genera aqui:
					//RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateOk, "" + (Flow.jcms[1].jcmCass1 * Flow.jcms[1].contadores.Cass1Denom));
					//RaspiAgent.Broadcast(DeviceEvent.AFD_DenominateInfo, "" + Flow.jcms[1].jcmCass1  + "x" +  Flow.jcms[1].contadores.Cass1Denom);

					System.out.println("Deshabilitamos JCM2 para dispense");
					Flow.jcms[1].currentOpertion = jcmOperation.Dispense;

					// primero el inhibit
					Flow.jcms[1].jcmMessage[3] = 0x01;
					Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
				}
				else{						
					System.out.println("Nada que dispensar del JCM[2]");
					Flow.jcm2cass1Dispensed = true;
					Flow.jcm2cass2Dispensed = true;
				}
				screenTimerDispense.cancel();
			}            	
		}
	}, 1000,1000);
}


public JPanel getPanel() {
	return contentPanel;
}
	
}
