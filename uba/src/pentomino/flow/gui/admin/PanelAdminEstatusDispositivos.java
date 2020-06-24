package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.ImagePanel;

public class PanelAdminEstatusDispositivos extends ImagePanel{

	private static final long serialVersionUID = 1L;

	public static JButton btnPruebaDeImpresion;
	public static JButton btnAdminMenuReiniciarRaspberry;
	private JButton btnReiniciarDispositivos;
	private JButton btnDetalleError;
	
	public static JLabel lblJcm1Denom1; 
	public static JLabel lblJcm1Denom2;
	public static JLabel lblJcm2Denom1;
	public static JLabel lblJcm2Denom2;
	
	public static JLabel lblJcm1Status = new JLabel();
	public static JLabel lblJcm2Status = new JLabel();
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminEstatusDispositivos(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
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

		btnReiniciarDispositivos = new JButton(new ImageIcon("./images/Btn_AdminReiniciaDisp.png"));
		btnReiniciarDispositivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminResetDispositivos);
			}
		});
		btnReiniciarDispositivos.setOpaque(false);
		btnReiniciarDispositivos.setContentAreaFilled(false);
		btnReiniciarDispositivos.setBorderPainted(false);
		btnReiniciarDispositivos.setBounds(1532, 261, 388, 132);
		add(btnReiniciarDispositivos);

		btnDetalleError = new JButton(new ImageIcon("./images/Btn_AdminDetalleErrores.png"));
		btnDetalleError.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelIdle);	
			}
		});
		btnDetalleError.setBackground(Color.BLUE);
		btnDetalleError.setOpaque(false);
		btnDetalleError.setContentAreaFilled(false);
		btnDetalleError.setBorderPainted(false);
		btnDetalleError.setBounds(1522, 571, 388, 132);
		add(btnDetalleError);

		JButton btnRegresar = new JButton(new ImageIcon("./images/BTN_7p_Admin_Regresar.png"));
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);
			}
		});
		btnRegresar.setOpaque(false);
		btnRegresar.setContentAreaFilled(false);
		btnRegresar.setBorderPainted(false);
		btnRegresar.setBounds(1336, 879, 574, 171);
		add(btnRegresar);

		JLabel lblJcm1 = new JLabel("JCM1");
		lblJcm1.setForeground(Color.WHITE);
		lblJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1.setBounds(373, 334, 494, 140);
		add(lblJcm1);

		JLabel lblJcm2 = new JLabel("JCM2");
		lblJcm2.setForeground(Color.WHITE);
		lblJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2.setBounds(999, 334, 494, 140);
		add(lblJcm2);

		lblJcm1Status = new JLabel("Status OK");
		lblJcm1Status.setForeground(Color.WHITE);
		lblJcm1Status.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Status.setBounds(373, 485, 500, 46);
		add(lblJcm1Status);

		lblJcm2Status = new JLabel("Status OK");
		lblJcm2Status.setForeground(Color.WHITE);
		lblJcm2Status.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Status.setBounds(999, 485, 500, 46);
		add(lblJcm2Status);

		JLabel lblJcm1Texto1 = new JLabel("Denominaciones");
		lblJcm1Texto1.setForeground(Color.WHITE);
		lblJcm1Texto1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Texto1.setBounds(373, 586, 290, 46);
		add(lblJcm1Texto1);

		JLabel lblJcm2Texto1 = new JLabel("Denominaciones");
		lblJcm2Texto1.setForeground(Color.WHITE);
		lblJcm2Texto1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Texto1.setBounds(999, 586, 290, 46);
		add(lblJcm2Texto1);

		lblJcm1Denom1 = new JLabel("N/A");
		lblJcm1Denom1.setForeground(Color.WHITE);
		lblJcm1Denom1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Denom1.setBounds(373, 670, 150, 46);
		add(lblJcm1Denom1);

		lblJcm1Denom2 = new JLabel("N/A");
		lblJcm1Denom2.setForeground(Color.WHITE);
		lblJcm1Denom2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Denom2.setBounds(571, 670, 150, 46);
		add(lblJcm1Denom2);

		lblJcm2Denom1 = new JLabel("N/A");
		lblJcm2Denom1.setForeground(Color.WHITE);
		lblJcm2Denom1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Denom1.setBounds(1000, 670, 150, 46);
		add(lblJcm2Denom1);

		lblJcm2Denom2 = new JLabel("N/A");
		lblJcm2Denom2.setForeground(Color.WHITE);
		lblJcm2Denom2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Denom2.setBounds(1198, 670, 150, 46);
		add(lblJcm2Denom2);

		btnPruebaDeImpresion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			

				CurrentUser.currentOperation = jcmOperation.Dotacion;
				PanelAdminContadoresActuales.GetCurrentCounters();
				Flow.redirect(Flow.panelAdminContadoresActuales,15000,Flow.panelAdminMenu);			
			}
		});

	}


	public JPanel getPanel() {
		return this;
	}

	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelAdminEstatusDispositivos");
		
		Flow.jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[0].jcmMessage);
		Flow.jcms[1].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0, Flow.jcms[1].jcmMessage);
		
		if(Flow.jcms[0].recycleBoxStatus.isEmpty() && Flow.jcms[0].stackerStatus.isEmpty())
			lblJcm1Status.setText("Status OK");
		else
			lblJcm1Status.setText("Status " + Flow.jcms[0].recycleBoxStatus + " " + Flow.jcms[0].stackerStatus);
		
		if(Flow.jcms[1].recycleBoxStatus.isEmpty() && Flow.jcms[1].stackerStatus.isEmpty())
			lblJcm2Status.setText("Status OK");
		else
			lblJcm2Status.setText("Status " + Flow.jcms[1].recycleBoxStatus + " " + Flow.jcms[1].stackerStatus);
		

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelAdminEstatusDispositivos");

	}
}
