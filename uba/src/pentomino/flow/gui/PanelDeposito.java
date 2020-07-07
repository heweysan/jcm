package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.AccountType;
import pentomino.common.BusinessEvent;
import pentomino.common.DeviceEvent;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.core.devices.Ptr;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.DebugButtons;
import pentomino.flow.gui.helpers.ImagePanel;
import pentomino.jcmagent.BEA;
import pentomino.jcmagent.RaspiAgent;

public class PanelDeposito extends ImagePanel{

	private static final long serialVersionUID = 1L;


	public final static JLabel lblMontoDepositado = new JLabel("$0");
	
	public static JLabel lblJCMIzq = new JLabel();
	public static JLabel lblJCMDer = new JLabel();
	
	/**
	 * Se habilita cuando esta aceptando un billete, para que no se pueda oprimmir aceptar en el proceso de validacion
	 */
	public static boolean bussy1 = false;
	
	/**
	 * Se habilita cuando esta aceptadno un billete, para que no se pueda oprimmir aceptar en el proceso de validacion
	 */
	public static boolean bussy2 = false;
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelDeposito(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	



	@Override
	public void ContentPanel() {


		JButton btnAceptar = new JButton(new ImageIcon("./images/BTN7Aceptar.png"));
		btnAceptar.setBounds(560, 891, 782, 159);
		btnAceptar.setContentAreaFilled(false);
		btnAceptar.setBorderPainted(false);
		btnAceptar.setOpaque(false);
		btnAceptar.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnAceptar);

		lblMontoDepositado.setHorizontalAlignment(SwingConstants.CENTER);
		lblMontoDepositado.setBounds(10, 721, 1900, 130);
		lblMontoDepositado.setForeground(Color.WHITE);
		lblMontoDepositado.setFont(new Font("Tahoma", Font.BOLD, 50));

		add(lblMontoDepositado);

		add(new DebugButtons().getPanel());	
		
		lblJCMIzq = new JLabel("$0 / $0");
		lblJCMIzq.setHorizontalAlignment(SwingConstants.CENTER);
		lblJCMIzq.setForeground(Color.WHITE);
		lblJCMIzq.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJCMIzq.setBounds(369, 396, 360, 66);
		add(lblJCMIzq);
		
		lblJCMDer = new JLabel("$0 / $0");
		lblJCMDer.setHorizontalAlignment(SwingConstants.CENTER);
		lblJCMDer.setForeground(Color.WHITE);
		lblJCMDer.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJCMDer.setBounds(837, 396, 360, 66);
		add(lblJCMDer);

		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(bussy1 || bussy2)
					return;

				String atmId = Config.GetDirective("AtmId", "");

				switch(CurrentUser.currentOperation) {
				case Deposit:

					System.out.println("JCM1 INHIBIT DESHABILITAMOS ACEPTADOR");
					Flow.jcms[0].jcmMessage[3] = 0x01;
					Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);

					System.out.println("JCM2 INHIBIT DESHABILITAMOS ACEPTADOR");
					Flow.jcms[1].jcmMessage[3] = 0x01;
					Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
					
					System.out.println("totalAmountInserted [" + CurrentUser.totalAmountInserted + ")");
					if(CurrentUser.totalAmountInserted == 0) {
						Flow.redirect(Flow.panelOperacionCancelada,TimeUnit.SECONDS.toMillis(3),Flow.panelIdle);
						return;
					}
					
					


					//Actualizamos contadores de deposito
					int depositCounter = Integer.parseInt(Config.GetPersistence("TxCASHMANAGEMENTCounter","0"));
					depositCounter++;
					Config.SetPersistence("TxCASHMANAGEMENTCounter","" + depositCounter);
					
					//Terminamos el deposito
					DepositOpVO depositOpVO = new DepositOpVO();

					depositOpVO.atmId = atmId; 
					depositOpVO.amount = (long) CurrentUser.totalAmountInserted;
					depositOpVO.b20 = Flow.depositBillsCounter.x20;
					depositOpVO.b50 = Flow.depositBillsCounter.x50;
					depositOpVO.b100 = Flow.depositBillsCounter.x100;
					depositOpVO.b200 = Flow.depositBillsCounter.x200;
					depositOpVO.b500 = Flow.depositBillsCounter.x500;
					depositOpVO.b1000 = 0;
					depositOpVO.operatorId = Integer.parseInt(CurrentUser.loginUser);
					depositOpVO.operationDateTimeMilliseconds = java.lang.System.currentTimeMillis();
					depositOpVO.userName = CurrentUser.loginUser;										

					String billetes = "[" + depositOpVO.b20 + "x20|" + depositOpVO.b50 + "x50|" + depositOpVO.b100 + "x100|" + depositOpVO.b200 + "x200|" + depositOpVO.b500 + "x500|" + depositOpVO.b1000 + "x1000]";
					String billetesNotesValidated = "" + depositOpVO.b20 + "x20;" + depositOpVO.b50 + "x50;" + depositOpVO.b100 + "x100;" + depositOpVO.b200 + "x200;" + depositOpVO.b500 + "x500;" + depositOpVO.b1000 + "x1000";

					System.out.println("billetes " + billetes);
					System.out.println("billetesNotesValidated " + billetesNotesValidated);
					
					Transactions.ConfirmaDeposito(depositOpVO);

					RaspiAgent.Broadcast(DeviceEvent.DEP_TotalAmountInserted,"" + CurrentUser.totalAmountInserted);
					RaspiAgent.Broadcast(DeviceEvent.DEP_NotesValidated, billetesNotesValidated);
					RaspiAgent.Broadcast(DeviceEvent.DEP_CashInEndOk, "" + CurrentUser.totalAmountInserted);
					RaspiAgent.WriteToJournal("CASH MANAGEMENT", CurrentUser.totalAmountInserted,0, CurrentUser.movementId, CurrentUser.loginUser, "PROCESADEPOSITO ConfirmaDeposito " + billetes, AccountType.Administrative, TransactionType.CashManagement);
					BEA.BusinessEvent(BusinessEvent.DepositEnd, true, false,"");

					
					
					
					if(!Ptr.printDeposit(depositOpVO)){
						//Si no pudo imprimir lo mandamos a la pantalla de no impresion.
						Flow.redirect(Flow.panelNoTicket,5000,Flow.panelTerminamos);						
					}
					else {
						Flow.redirect(Flow.panelTerminamos);
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


	@Override
	public void OnLoad() {		
		System.out.println("OnLoad [PanelDeposito]");
		CurrentUser.totalAmountInserted = 0;
		CurrentUser.currentOperation = jcmOperation.Deposit;
		
		bussy1 = false;
		bussy2 = false;
		
		lblMontoDepositado.setText("");
		
		System.out.println("RE INHIBIT JCM1");
		Flow.jcms[0].jcmMessage[3] = 0x00;
		Flow.jcms[0].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[0].jcmMessage, false);
		
		System.out.println("RE INHIBIT JCM2");
		Flow.jcms[1].jcmMessage[3] = 0x00;
		Flow.jcms[1].id003_format((byte) 0x6, (byte) 0xC3, Flow.jcms[1].jcmMessage, false);
		

	}

	@Override
	public void OnUnload() {		
		//System.out.println("OnUnload [PanelDeposito]");
	}
}
