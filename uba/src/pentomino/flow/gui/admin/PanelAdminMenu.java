package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import pentomino.common.BusinessEvent;
import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.PanelLogin;
import pentomino.jcmagent.BEA;

public class PanelAdminMenu {
	
	public JPanel contentPanel;
	public static JButton btnAdminMenuCorte;
	public static JButton btnAdminMenuImpresionContadores;
	public static JButton btnAdminMenuReiniciarRaspberry;
	private JButton btnAdminMenuEstatusDispositivos;
	private JButton btnAdminMenuSalir;
	
public PanelAdminMenu() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		
		
		btnAdminMenuImpresionContadores = new JButton(new ImageIcon("./images/Btn_AdminImpContadores.png"));
		btnAdminMenuImpresionContadores.setOpaque(false);
		btnAdminMenuImpresionContadores.setBounds(31, 449, 361, 140);
		btnAdminMenuImpresionContadores.setOpaque(false);
		btnAdminMenuImpresionContadores.setContentAreaFilled(false);
		btnAdminMenuImpresionContadores.setBorderPainted(false);
		contentPanel.add(btnAdminMenuImpresionContadores);
		
		btnAdminMenuCorte = new JButton(new ImageIcon("./images/Btn_AdminCorteDotacion.png"));		
		btnAdminMenuCorte.setOpaque(false);
		btnAdminMenuCorte.setContentAreaFilled(false);
		btnAdminMenuCorte.setBorderPainted(false);
		btnAdminMenuCorte.setBounds(31, 811, 388, 132);
		contentPanel.add(btnAdminMenuCorte);
		
		contentPanel.add(new DebugButtons().getPanel());	
		
		btnAdminMenuEstatusDispositivos = new JButton(new ImageIcon("./images/Btn_AdminEstDisp.png"));
		btnAdminMenuEstatusDispositivos.setOpaque(false);
		btnAdminMenuEstatusDispositivos.setContentAreaFilled(false);
		btnAdminMenuEstatusDispositivos.setBorderPainted(false);
		btnAdminMenuEstatusDispositivos.setBounds(1522, 596, 388, 132);
		contentPanel.add(btnAdminMenuEstatusDispositivos);
		
		btnAdminMenuSalir = new JButton(new ImageIcon("./images/Btn_AdminSalir.png"));
		btnAdminMenuSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				BEA.BusinessEvent(BusinessEvent.AdministrativeOperatonEnded, true, false,"");
				
				Flow.redirect(Flow.panelIdleHolder);	
			}
		});
		btnAdminMenuSalir.setBackground(Color.BLUE);
		btnAdminMenuSalir.setOpaque(false);
		btnAdminMenuSalir.setContentAreaFilled(false);
		btnAdminMenuSalir.setBorderPainted(false);
		btnAdminMenuSalir.setBounds(1508, 901, 388, 132);
		contentPanel.add(btnAdminMenuSalir);
		
		btnAdminMenuImpresionContadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.panelMenuHolder.screenTimerCancel();				
				CurrentUser.cleanPinpadData(PinpadMode.loginUser);
				PanelLogin.lblLoginUser.setLocation(257, 625);	
				PanelLogin.lblLoginUser.setText("");
				PanelLogin.lblLoginPassword.setText("");
				CurrentUser.currentOperation = jcmOperation.Deposit;
				PanelLogin.lblLoginOpcion.setBounds(240, 530, 87, 87);   //Este es login sin password
				Flow.panelLoginHolder.setBackground("./images/Scr7IdentificateDeposito.png");
				Flow.redirect(Flow.panelAdminContadoresActualesHolder,7000,"panelOperacionCancelada");				
			}
		});
		
		btnAdminMenuCorte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				CurrentUser.currentOperation = jcmOperation.Dotacion;
				PanelAdminContadoresActuales.GetCurrentCounters();
				Flow.redirect(Flow.panelAdminContadoresActualesHolder,15000,"panelAdminMenu");			
			}
		});
		
	}
	
	
	public JPanel getPanel() {
		return contentPanel;
	}

}
