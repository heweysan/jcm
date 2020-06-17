package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.common.BusinessEvent;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.ImagePanel;
import pentomino.jcmagent.BEA;

public class PanelAdminEstatusDispositivos extends ImagePanel{

	private static final long serialVersionUID = 1L;
	
	public static JButton btnPruebaDeImpresion;
	public static JButton btnAdminMenuReiniciarRaspberry;
	private JButton btnReiniciarDispositivos;
	private JButton btnDetalleError;
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminEstatusDispositivos(String img,String name) {
		super(new ImageIcon(img).getImage(),name);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}

	public PanelAdminEstatusDispositivos(Image img,String name, int _timeout, String _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	

	public PanelAdminEstatusDispositivos(Image img, String name) {
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

		btnPruebaDeImpresion = new JButton(new ImageIcon("./images/Btn_AdminPruebaImpresion.png"));		
		btnPruebaDeImpresion.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnPruebaDeImpresion.setOpaque(false);
		btnPruebaDeImpresion.setContentAreaFilled(false);
		btnPruebaDeImpresion.setBorderPainted(false);
		btnPruebaDeImpresion.setBounds(1522, 23, 388, 132);
		add(btnPruebaDeImpresion);

		add(new DebugButtons().getPanel());	

		btnReiniciarDispositivos = new JButton(new ImageIcon("./images/Btn_AdminReiniciaDisp.png"));
		btnReiniciarDispositivos.setOpaque(false);
		btnReiniciarDispositivos.setContentAreaFilled(false);
		btnReiniciarDispositivos.setBorderPainted(false);
		btnReiniciarDispositivos.setBounds(1532, 261, 388, 132);
		add(btnReiniciarDispositivos);

		btnDetalleError = new JButton(new ImageIcon("./images/Btn_AdminDetalleErrores.png"));
		btnDetalleError.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				BEA.BusinessEvent(BusinessEvent.AdministrativeOperatonEnded, true, false,"");

				Flow.redirect(Flow.panelIdleHolder);	
			}
		});
		btnDetalleError.setBackground(Color.BLUE);
		btnDetalleError.setOpaque(false);
		btnDetalleError.setContentAreaFilled(false);
		btnDetalleError.setBorderPainted(false);
		btnDetalleError.setBounds(1522, 571, 388, 132);
		add(btnDetalleError);

		JButton btnRegresar = new JButton(new ImageIcon("./images/Btn_AdminRegresar.png"));
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);
			}
		});
		btnRegresar.setOpaque(false);
		btnRegresar.setContentAreaFilled(false);
		btnRegresar.setBorderPainted(false);
		btnRegresar.setBounds(1522, 904, 388, 132);
		add(btnRegresar);
		
		JLabel lblJcm1 = new JLabel("JCM1");
		lblJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1.setBounds(61, 143, 494, 140);
		add(lblJcm1);
		
		JLabel lblJcm2 = new JLabel("JCM2");
		lblJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2.setBounds(614, 143, 494, 140);
		add(lblJcm2);
		
		JLabel lblJcm1Status = new JLabel("Status OK");
		lblJcm1Status.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Status.setBounds(61, 294, 290, 46);
		add(lblJcm1Status);
		
		JLabel lblJcm2Status = new JLabel("Status OK");
		lblJcm2Status.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Status.setBounds(596, 294, 290, 46);
		add(lblJcm2Status);
		
		JLabel lblJcm1Texto1 = new JLabel("Denominaciones");
		lblJcm1Texto1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Texto1.setBounds(61, 395, 290, 46);
		add(lblJcm1Texto1);
		
		JLabel lblJcm2Texto1 = new JLabel("Denominaciones");
		lblJcm2Texto1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Texto1.setBounds(596, 395, 290, 46);
		add(lblJcm2Texto1);
		
		JLabel lblJcm1Denom1 = new JLabel("$20");
		lblJcm1Denom1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Denom1.setBounds(61, 479, 150, 46);
		add(lblJcm1Denom1);
		
		JLabel lblJcm1Denom2 = new JLabel("$50");
		lblJcm1Denom2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Denom2.setBounds(259, 479, 150, 46);
		add(lblJcm1Denom2);
		
		JLabel lblJcm2Denom1 = new JLabel("$100");
		lblJcm2Denom1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Denom1.setBounds(596, 479, 150, 46);
		add(lblJcm2Denom1);
		
		JLabel lblJcm2Denom2 = new JLabel("$200");
		lblJcm2Denom2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Denom2.setBounds(794, 479, 150, 46);
		add(lblJcm2Denom2);

		btnPruebaDeImpresion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				CurrentUser.currentOperation = jcmOperation.Dotacion;
				PanelAdminContadoresActuales.GetCurrentCounters();
				Flow.redirect(Flow.panelAdminContadoresActuales,15000,"panelAdminMenu");			
			}
		});

	}


	public JPanel getPanel() {
		return this;
	}

	@Override
	public void OnLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		
	}
}
