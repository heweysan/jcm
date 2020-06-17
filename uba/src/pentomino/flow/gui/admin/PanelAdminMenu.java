package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import pentomino.common.BusinessEvent;
import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.EventListenerClass;
import pentomino.flow.Flow;
import pentomino.flow.MyEvent;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.ImagePanel;
import pentomino.flow.gui.PanelLogin;
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
	public PanelAdminMenu(String img,String name) {
		super(new ImageIcon(img).getImage(),name);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}

	public PanelAdminMenu(Image img,String name, int _timeout, String _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	

	public PanelAdminMenu(Image img, String name) {
		super(img,name);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}


	@Override
	public void ContentPanel() {


		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	


		btnAdminMenuImpresionContadores = new JButton(new ImageIcon("./images/Btn_AdminImpContadores.png"));
		btnAdminMenuImpresionContadores.setOpaque(false);
		btnAdminMenuImpresionContadores.setBounds(31, 450, 361, 140);
		btnAdminMenuImpresionContadores.setOpaque(false);
		btnAdminMenuImpresionContadores.setContentAreaFilled(false);
		btnAdminMenuImpresionContadores.setBorderPainted(false);
		add(btnAdminMenuImpresionContadores);

		btnAdminMenuCorte = new JButton(new ImageIcon("./images/Btn_AdminCorteDotacion.png"));		
		btnAdminMenuCorte.setOpaque(false);
		btnAdminMenuCorte.setContentAreaFilled(false);
		btnAdminMenuCorte.setBorderPainted(false);
		btnAdminMenuCorte.setBounds(31, 810, 388, 132);
		add(btnAdminMenuCorte);

		add(new DebugButtons().getPanel());	

		btnAdminMenuEstatusDispositivos = new JButton(new ImageIcon("./images/Btn_AdminEstDisp.png"));
		btnAdminMenuEstatusDispositivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminEstatusDispositivos);
			}
		});
		btnAdminMenuEstatusDispositivos.setOpaque(false);
		btnAdminMenuEstatusDispositivos.setContentAreaFilled(false);
		btnAdminMenuEstatusDispositivos.setBorderPainted(false);
		btnAdminMenuEstatusDispositivos.setBounds(1522, 450, 388, 132);
		add(btnAdminMenuEstatusDispositivos);

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
		btnAdminMenuSalir.setBounds(1522, 918, 388, 132);
		add(btnAdminMenuSalir);

		JButton btnAdminMenuReiniciar = new JButton(new ImageIcon("./images/Btn_AdminReiniciarATM.png"));
		btnAdminMenuReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Reiniciando CAJERITO BUTTON");
				EventListenerClass.fireMyEvent(new MyEvent("reboot"));
			}
		});
		btnAdminMenuReiniciar.setOpaque(false);
		btnAdminMenuReiniciar.setContentAreaFilled(false);
		btnAdminMenuReiniciar.setBorderPainted(false);
		btnAdminMenuReiniciar.setBounds(1522, 810, 388, 132);
		add(btnAdminMenuReiniciar);

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
				Flow.redirect(Flow.panelAdminContadoresActuales,7000,"panelOperacionCancelada");				
			}
		});

		btnAdminMenuCorte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				CurrentUser.currentOperation = jcmOperation.Dotacion;
				PanelAdminContadoresActuales.GetCurrentCounters();
				Flow.redirect(Flow.panelAdminContadoresActuales,15000,"panelAdminMenu");			
			}
		});
	}



	public JPanel getPanel() {
		return this;
		//return contentPanel;
	}

	@Override
	public void OnLoad() {
		// TODO Auto-generated method stub
		System.out.println("Aqui carga este jalisco");

	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		System.out.println("Aqui se sale este jalisco");
	}












}
