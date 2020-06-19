package pentomino.flow.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.vo.CmMessageRequest;
import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;

public class PanelMenu extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static JButton btnMenuRetiro;
	public static JButton btnMenuDeposito;
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelMenu(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	


	@Override
	public void ContentPanel() {
		

		
		btnMenuDeposito = new JButton(new ImageIcon("./images/BTN7Deposito.png"));
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setBounds(360, 502, 492, 498);
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setContentAreaFilled(false);
		btnMenuDeposito.setBorderPainted(false);
		add(btnMenuDeposito);
		
		btnMenuRetiro = new JButton(new ImageIcon("./images/BTN7Retiro.png"));		
		btnMenuRetiro.setOpaque(false);
		btnMenuRetiro.setContentAreaFilled(false);
		btnMenuRetiro.setBorderPainted(false);
		btnMenuRetiro.setBounds(989, 502, 492, 498);
		add(btnMenuRetiro);
			
		btnMenuDeposito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.panelMenu.screenTimerCancel();				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);
				PanelLogin.lblLoginUser.setLocation(257, 625);					
				CurrentUser.currentOperation = jcmOperation.Deposit;
				PanelLogin.lblLoginOpcion.setBounds(240, 530, 87, 87);   //Este es login sin password
				Flow.panelLogin.setBackground("./images/Scr7IdentificateDeposito.png");
				Flow.redirect(Flow.panelLogin,7000,Flow.panelOperacionCancelada);				
			}
		});
		
		btnMenuRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				Flow.panelMenu.screenTimerCancel();			
				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);			
				CurrentUser.asteriscos = "";				
				PanelLogin.lblLoginUser.setLocation(257, 525);				
				CurrentUser.currentOperation = jcmOperation.Dispense;
				PanelLogin.lblLoginOpcion.setBounds(230, 430, 87, 87);   //Este es login con password
				
				Flow.panelLogin.setBackground("./images/Scr7IngresaDatos.png");
				Flow.redirect(Flow.panelLogin,7000,Flow.panelOperacionCancelada);
								
				CmMessageRequest request =  CmQueue.queueList.getFirst();				
				CurrentUser.token = "" + request.token;
				CurrentUser.WithdrawalRequested = request.amount;
				
				PanelToken.lblTokenMontoRetiro.setText("$" + CurrentUser.WithdrawalRequested);
				PanelToken.lblToken.setText(CurrentUser.token);
				CurrentUser.tokenConfirmacion = "";	
				CurrentUser.reference = request.reference;
				PanelToken.lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);				
			}
		});
		
	}
	
	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelMennu");

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelMenu");
	}
	
	
}
