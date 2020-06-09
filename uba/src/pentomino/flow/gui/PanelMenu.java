package pentomino.flow.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.vo.CmMessageRequest;
import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;

public class PanelMenu {

	public JPanel contentPanel;
	public static JButton btnMenuRetiro;
	public static JButton btnMenuDeposito;
	
	public PanelMenu() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		
		
		btnMenuDeposito = new JButton(new ImageIcon("./images/BTN7Deposito.png"));
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setBounds(360, 502, 492, 498);
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setContentAreaFilled(false);
		btnMenuDeposito.setBorderPainted(false);
		contentPanel.add(btnMenuDeposito);
		
		btnMenuRetiro = new JButton(new ImageIcon("./images/BTN7Retiro.png"));		
		btnMenuRetiro.setOpaque(false);
		btnMenuRetiro.setContentAreaFilled(false);
		btnMenuRetiro.setBorderPainted(false);
		btnMenuRetiro.setBounds(989, 502, 492, 498);
		contentPanel.add(btnMenuRetiro);
		
		contentPanel.add(new DebugButtons().getPanel());	
		
		btnMenuDeposito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.panelMenuHolder.screenTimerCancel();				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);
				PanelLogin.lblLoginUser.setLocation(257, 625);	
				PanelLogin.lblLoginUser.setText("");
				PanelLogin.lblLoginPassword.setText("");
				CurrentUser.currentOperation = jcmOperation.Deposit;
				PanelLogin.lblLoginOpcion.setBounds(240, 530, 87, 87);   //Este es login sin password
				Flow.panelLoginHolder.setBackground("./images/Scr7IdentificateDeposito.png");
				Flow.redirect(Flow.panelLoginHolder,7000,"panelOperacionCancelada");				
			}
		});
		
		btnMenuRetiro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				Flow.panelMenuHolder.screenTimerCancel();			
				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);			
				CurrentUser.asteriscos = "";
				
				PanelLogin.lblLoginUser.setLocation(257, 525);
				PanelLogin.lblLoginUser.setText("");
				PanelLogin.lblLoginPassword.setText("");
				
				CurrentUser.currentOperation = jcmOperation.Dispense;

				PanelLogin.lblLoginOpcion.setBounds(230, 430, 87, 87);   //Este es login con password
				
				Flow.panelLoginHolder.setBackground("./images/Scr7IngresaDatos.png");
				Flow.redirect(Flow.panelLoginHolder,7000,"panelOperacionCancelada");
								
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
	
	
	public JPanel getPanel() {
		return contentPanel;
	}
	
	
}
