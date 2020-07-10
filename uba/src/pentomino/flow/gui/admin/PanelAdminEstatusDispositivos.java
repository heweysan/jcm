package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;
import javax.swing.SwingConstants;

public class PanelAdminEstatusDispositivos extends ImagePanel{

	private static final long serialVersionUID = 1L;

	public static JButton btnPruebaDeImpresion;
	public static JButton btnAdminMenuReiniciarRaspberry;
	private JButton btnReiniciarDispositivos;
	private JButton btnDetalleError;
	
	public static JLabel lblJcm1Denom; 
	public static JLabel lblJcm2Denom;
	
	
	private static JLabel lblJcm1;
	public static JLabel lblJcm1Status;
	
	private static JLabel lblJcm2;
	public static JLabel lblJcm2Status;
	public static JLabel lblImpresoraStatus;
	
	
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

		btnPruebaDeImpresion = new JButton(new ImageIcon("./images/BTN_7p_Admin_PruebaImpresion.png"));		
		btnPruebaDeImpresion.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnPruebaDeImpresion.setOpaque(false);
		btnPruebaDeImpresion.setContentAreaFilled(false);
		btnPruebaDeImpresion.setBorderPainted(false);
		btnPruebaDeImpresion.setBounds(52, 880, 575, 171);
		add(btnPruebaDeImpresion);

		btnReiniciarDispositivos = new JButton(new ImageIcon("./images/BTN_7p_Admin_Reiniciar.png"));
		btnReiniciarDispositivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminResetDispositivos);
			}
		});
		btnReiniciarDispositivos.setOpaque(false);
		btnReiniciarDispositivos.setContentAreaFilled(false);
		btnReiniciarDispositivos.setBorderPainted(false);
		btnReiniciarDispositivos.setBounds(1321, 206, 575, 151);
		add(btnReiniciarDispositivos);

		btnDetalleError = new JButton(new ImageIcon("./images/BTN_7p_Admin_DetalleErrores.png"));
		btnDetalleError.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Flow.redirect(Flow.panelAdminDetalleError,10000,Flow.panelAdminEstatusDispositivos);	
			}
		});
		btnDetalleError.setBackground(Color.BLUE);
		btnDetalleError.setOpaque(false);
		btnDetalleError.setContentAreaFilled(false);
		btnDetalleError.setBorderPainted(false);
		btnDetalleError.setBounds(680, 880, 575, 171);
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
		btnRegresar.setBounds(1305, 880, 574, 171);
		add(btnRegresar);

		lblJcm1 = new JLabel("JCM1 Status");
		lblJcm1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblJcm1.setForeground(Color.WHITE);
		lblJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1.setBounds(465, 445, 470, 46);
		add(lblJcm1);

		lblJcm2 = new JLabel("JCM2 Status");
		lblJcm2.setHorizontalAlignment(SwingConstants.TRAILING);
		lblJcm2.setForeground(Color.WHITE);
		lblJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2.setBounds(465, 610, 470, 46);
		add(lblJcm2);

		JLabel lblJcm1Texto1 = new JLabel("JCM1\r\n Denom");
		lblJcm1Texto1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblJcm1Texto1.setForeground(Color.WHITE);
		lblJcm1Texto1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Texto1.setBounds(465, 530, 470, 46);
		add(lblJcm1Texto1);

		JLabel lblJcm2Texto1 = new JLabel("JCM2 Denom");
		lblJcm2Texto1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblJcm2Texto1.setForeground(Color.WHITE);
		lblJcm2Texto1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Texto1.setBounds(470, 695, 470, 46);
		add(lblJcm2Texto1);

		lblJcm1Denom = new JLabel("N/A, N/A");
		lblJcm1Denom.setForeground(Color.WHITE);
		lblJcm1Denom.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Denom.setBounds(1203, 530, 250, 46);
		add(lblJcm1Denom);

		lblJcm2Denom = new JLabel("N/A, N/A");
		lblJcm2Denom.setForeground(Color.WHITE);
		lblJcm2Denom.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Denom.setBounds(1203, 695, 250, 46);
		add(lblJcm2Denom);
		
		JLabel lblImpresora = new JLabel("IMPRESORA");
		lblImpresora.setForeground(Color.WHITE);
		lblImpresora.setHorizontalAlignment(SwingConstants.TRAILING);
		lblImpresora.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblImpresora.setBounds(465, 780, 470, 46);
		add(lblImpresora);
		
		lblImpresoraStatus = new JLabel("Paper Out");
		lblImpresoraStatus.setHorizontalAlignment(SwingConstants.LEFT);
		lblImpresoraStatus.setForeground(Color.WHITE);
		lblImpresoraStatus.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblImpresoraStatus.setBounds(1203, 780, 470, 46);
		add(lblImpresoraStatus);
		
		
		lblJcm1Status = new JLabel("OK");
		lblJcm1Status.setHorizontalAlignment(SwingConstants.LEFT);
		lblJcm1Status.setForeground(Color.WHITE);
		lblJcm1Status.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm1Status.setBounds(1203, 445, 470, 46);
		add(lblJcm1Status);
		
		lblJcm2Status = new JLabel("OK MAGUEY");
		lblJcm2Status.setHorizontalAlignment(SwingConstants.LEFT);
		lblJcm2Status.setForeground(Color.WHITE);
		lblJcm2Status.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblJcm2Status.setBounds(1203, 610, 490, 46);
		add(lblJcm2Status);
		
		btnPruebaDeImpresion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				
				Flow.redirect(Flow.panelAdminPruebaImpresion);			
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
			lblJcm1Status.setText("OK");
		else
			lblJcm1Status.setText(Flow.jcms[0].recycleBoxStatus + " " + Flow.jcms[0].stackerStatus);
		
		if(Flow.jcms[1].recycleBoxStatus.isEmpty() && Flow.jcms[1].stackerStatus.isEmpty())
			lblJcm2Status.setText("OK");
		else
			lblJcm2Status.setText(Flow.jcms[1].recycleBoxStatus + " " + Flow.jcms[1].stackerStatus);
		
		
		lblJcm1Denom.setText("$" + Flow.jcms[0].recyclerDenom1 + ", " + "$" + Flow.jcms[0].recyclerDenom2);
				
		lblJcm2Denom.setText("$" + Flow.jcms[1].recyclerDenom1 + ", " + "$" + Flow.jcms[1].recyclerDenom2);
		
		

	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelAdminEstatusDispositivos");

	}
}
