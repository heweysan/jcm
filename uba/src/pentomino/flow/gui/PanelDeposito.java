package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.AccountType;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.core.devices.Ptr;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.jcmagent.RaspiAgent;

public class PanelDeposito {
	
	public JPanel contentPanel = new JPanel();
	
	public final static JLabel lblMontoDepositado = new JLabel(".");
	
	
	public PanelDeposito() {
				
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);

		JButton btnAceptar = new JButton(new ImageIcon("./images/BTN7Aceptar.png"));
		btnAceptar.setBounds(547, 757, 782, 159);
		btnAceptar.setContentAreaFilled(false);
		btnAceptar.setBorderPainted(false);
		btnAceptar.setOpaque(false);
		btnAceptar.setFont(new Font("Tahoma", Font.BOLD, 40));
		contentPanel.add(btnAceptar);
		
		lblMontoDepositado.setHorizontalAlignment(SwingConstants.CENTER);
		lblMontoDepositado.setBounds(10, 484, 1900, 130);
		lblMontoDepositado.setForeground(Color.WHITE);
		lblMontoDepositado.setFont(new Font("Tahoma", Font.BOLD, 50));
		
		contentPanel.add(lblMontoDepositado);
		
		contentPanel.add(new DebugButtons().getPanel());	
		
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String atmId = Config.GetDirective("AtmId", "");
				
				switch(CurrentUser.currentOperation) {
				case Deposit:

					//Terminamos el deposito
					DepositOpVO depositOpVO = new DepositOpVO();

					if(JcmGlobalData.isDebug) {						
						Flow.montoDepositado = 3720;	
						depositOpVO.atmId = atmId;  
						depositOpVO.amount = (long) Flow.montoDepositado;
						depositOpVO.b20 = 1;
						depositOpVO.b50 = 2;
						depositOpVO.b100 = 3;
						depositOpVO.b200 = 4;
						depositOpVO.b500 = 5;
						depositOpVO.b1000 = 6;
						depositOpVO.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
						depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
						depositOpVO.userName = CurrentUser.getLoginUser();							
					}
					else {
						depositOpVO.atmId = atmId; 
						depositOpVO.amount = (long) Flow.montoDepositado;
						depositOpVO.b20 = Flow.contadoresDeposito.x20;
						depositOpVO.b50 = Flow.contadoresDeposito.x50;
						depositOpVO.b100 = Flow.contadoresDeposito.x100;
						depositOpVO.b200 = Flow.contadoresDeposito.x200;
						depositOpVO.b500 = Flow.contadoresDeposito.x500;
						depositOpVO.b1000 = 0;
						depositOpVO.operatorId = Integer.parseInt(CurrentUser.getLoginUser());
						depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
						depositOpVO.userName = CurrentUser.getLoginUser();										
					}

					String billetes = "[" + depositOpVO.b20 + "x20|" + depositOpVO.b50 + "x50|" + depositOpVO.b100 + "x100|" + depositOpVO.b200 + "x200|" + depositOpVO.b500 + "x500|" + depositOpVO.b1000 + "x1000]";
					String billetesNotesValidated = "" + depositOpVO.b20 + "x20;" + depositOpVO.b50 + "x50;" + depositOpVO.b100 + "x100;" + depositOpVO.b200 + "x200;" + depositOpVO.b500 + "x500;" + depositOpVO.b1000 + "x1000";

					Transactions.ConfirmaDeposito(depositOpVO);

					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted,"" + Flow.montoDepositado);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, billetesNotesValidated);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInEndOk, "" + Flow.montoDepositado);
					RaspiAgent.WriteToJournal("CASH MANAGEMENT", Flow.montoDepositado,0, "","", "PROCESADEPOSITO ConfirmaDeposito " + billetes, AccountType.Administrative, TransactionType.CashManagement);
					
					
					if(!Ptr.printDeposit(depositOpVO)){
						//Si no pudo imprimir lo mandamos a la pantalla de no impresion.
						Flow.redirect(Flow.panelNoTicketHolder,7000,"panelTerminamos");
						Flow.panelTerminamosHolder.screenTimeOut = 7000;
						Flow.panelTerminamosHolder.panelRedirect = "panelIdle";
					}
					else {
						Flow.redirect(Flow.panelTerminamosHolder,7000,"panelIdle");
					}		
					
					break;
				case Dispense:
					break;
				default:
					break;
				}
			}
		});		
	
		
	}
	
	
	public JPanel getPanel() {
		return contentPanel;
	}

}