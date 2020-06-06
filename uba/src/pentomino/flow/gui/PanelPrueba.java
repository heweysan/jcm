package pentomino.flow.gui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import pentomino.cashmanagement.CmQueue;
import pentomino.cashmanagement.vo.CmMessageRequest;
import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;

public class PanelPrueba extends Screen{
	
	public static JButton btnMenuRetiro;
	public static JButton btnMenuDeposito;

	public PanelPrueba(Image img, String name, int _timeout, String _redirect) {
		super(img, name, _timeout, _redirect);
				
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("OnLoad PanelPrueba [" + name + "]");
				screenTimer = new Timer();
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						System.out.println("Redireccionado [" + name + "] -> [" + panelRedirect + "]");
						screenTimer.cancel();						
						Flow.redirect(panelRedirect);					
					}
				}, screenTimeOut);		
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("OnUnload  PanelPrueba [" + name + "]");
				screenTimer.cancel();
			}
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void content() {
		/*
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		*/
		
		btnMenuDeposito = new JButton(new ImageIcon("./images/BTN7Deposito.png"));
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setBounds(360, 502, 492, 498);
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setContentAreaFilled(false);
		btnMenuDeposito.setBorderPainted(false);
		this.add(btnMenuDeposito);
		
		btnMenuRetiro = new JButton(new ImageIcon("./images/BTN7Retiro.png"));		
		btnMenuRetiro.setOpaque(false);
		btnMenuRetiro.setContentAreaFilled(false);
		btnMenuRetiro.setBorderPainted(false);
		btnMenuRetiro.setBounds(989, 502, 492, 498);
		this.add(btnMenuRetiro);
		
		this.add(new DebugButtons().getPanel());	
		
		btnMenuDeposito.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.panelMenuHolder.screenTimerCancel();				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);
				PanelLogin.lblLoginUser.setLocation(257, 625);	
				PanelLogin.lblLoginUser.setText("");
				PanelLogin.lblLoginPassword.setText("");
				CurrentUser.currentOperation = jcmOperation.Deposit;
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

				Flow.panelLoginHolder.setBackground("./images/Scr7IngresaDatos.png");
				Flow.redirect(Flow.panelLoginHolder,7000,"panelOperacionCancelada");
								
				CmMessageRequest request =  CmQueue.queueList.getFirst();				
				CurrentUser.token = "" + request.token;
				Flow.montoRetiro = request.amount;
				
				PanelToken.lblTokenMontoRetiro.setText("$" + Flow.montoRetiro);
				PanelToken.lblToken.setText(CurrentUser.token);
				CurrentUser.tokenConfirmacion = "";	
				CurrentUser.referencia = request.reference;
				PanelToken.lblTokenConfirmacion.setText(CurrentUser.tokenConfirmacion);				
			}
		});
		
		
	}

	
	
}
