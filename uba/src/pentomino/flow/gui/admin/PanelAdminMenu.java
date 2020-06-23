package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import pentomino.common.BusinessEvent;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.core.devices.Ptr;
import pentomino.flow.CurrentUser;
import pentomino.flow.EventListenerClass;
import pentomino.flow.Flow;
import pentomino.flow.MyEvent;
import pentomino.flow.gui.ImagePanel;
import pentomino.jcmagent.BEA;

public class PanelAdminMenu extends ImagePanel {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JButton btnAdminMenuCorte;
	public static JButton btnAdminMenuImpresionContadores;
	public static JButton btnAdminMenuReiniciarRaspberry;
	private JButton btnAdminMenuEstatusDispositivos;
	private JButton btnAdminMenuSalir;

	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminMenu(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	



	@Override
	public void ContentPanel() {


		btnAdminMenuImpresionContadores = new JButton(new ImageIcon("./images/BTN_7p_Admin_imprimir contadores.png"));
		btnAdminMenuImpresionContadores.setOpaque(false);
		btnAdminMenuImpresionContadores.setBounds(31, 680, 574, 171);
		btnAdminMenuImpresionContadores.setOpaque(false);
		btnAdminMenuImpresionContadores.setContentAreaFilled(false);
		btnAdminMenuImpresionContadores.setBorderPainted(false);
		add(btnAdminMenuImpresionContadores);

		btnAdminMenuCorte = new JButton(new ImageIcon("./images/BTN_7p_Admin_corte.png"));		
		btnAdminMenuCorte.setOpaque(false);
		btnAdminMenuCorte.setContentAreaFilled(false);
		btnAdminMenuCorte.setBorderPainted(false);
		btnAdminMenuCorte.setBounds(31, 450, 574, 171);
		add(btnAdminMenuCorte);

		btnAdminMenuEstatusDispositivos = new JButton(new ImageIcon("./images/BTN_7p_Admin_EstatusDispositivos.png"));
		btnAdminMenuEstatusDispositivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminEstatusDispositivos);
			}
		});
		btnAdminMenuEstatusDispositivos.setOpaque(false);
		btnAdminMenuEstatusDispositivos.setContentAreaFilled(false);
		btnAdminMenuEstatusDispositivos.setBorderPainted(false);
		btnAdminMenuEstatusDispositivos.setBounds(1299, 680, 574, 171);
		add(btnAdminMenuEstatusDispositivos);

		btnAdminMenuSalir = new JButton(new ImageIcon("./images/BTN_7p_Admin_Salir.png"));
		btnAdminMenuSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//BEA.BusinessEvent(BusinessEvent.SessionEnd, true, false, "");
				BEA.BusinessEvent(BusinessEvent.AdministrativeOperatonEnded, true, false,"");
				Flow.redirect(Flow.panelIdle);	
			}
		});
		btnAdminMenuSalir.setBackground(Color.BLUE);
		btnAdminMenuSalir.setOpaque(false);
		btnAdminMenuSalir.setContentAreaFilled(false);
		btnAdminMenuSalir.setBorderPainted(false);
		btnAdminMenuSalir.setBounds(1299, 900, 574, 171);
		add(btnAdminMenuSalir);

		JButton btnAdminMenuReiniciar = new JButton(new ImageIcon("./images/BTN_7p_Admin_ReiniciarAtm.png"));
		btnAdminMenuReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Reiniciando CAJERITO BUTTON");
				boolean busy = false;
	            do {
	                try {	                	
						Thread.sleep(2000);
					} catch (InterruptedException ie) {
						// TODO Auto-generated catch block
						ie.printStackTrace();
					}                
	                busy = Config.GetPersistence("BoardStatus", "Busy").equalsIgnoreCase("Busy");
	                System.out.println("BUSY [" + busy +"]");
	            } while (busy);
	            System.out.println("");
	            Flow.redirect(Flow.panelReinicio);
							
				
				EventListenerClass.fireMyEvent(new MyEvent("reboot"));
			}
		});
		btnAdminMenuReiniciar.setOpaque(false);
		btnAdminMenuReiniciar.setContentAreaFilled(false);
		btnAdminMenuReiniciar.setBorderPainted(false);
		btnAdminMenuReiniciar.setBounds(1299, 450, 574, 171);
		add(btnAdminMenuReiniciar);

		btnAdminMenuImpresionContadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Ptr.printContadores();				
			}
		});

		btnAdminMenuCorte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				CurrentUser.currentOperation = jcmOperation.Dotacion;
				PanelAdminContadoresActuales.GetCurrentCounters();
				Flow.redirect(Flow.panelAdminContadoresActuales,15000,Flow.panelAdminMenu);			
			}
		});
	}


	@Override
	public void OnLoad() {
		// TODO Auto-generated method stub
		System.out.println("OnLoad PanelAdminMenu");

	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		System.out.println("OnUnload PanelAdminMenu");
	}












}
