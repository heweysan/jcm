package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import pentomino.config.Config;
import pentomino.core.devices.Ptr;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;
import javax.swing.SwingConstants;

public class PanelAdminContadoresActuales extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btnImprimirContadores; 


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminContadoresActuales(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}	


	static JLabel lbl20 = new JLabel("0");
	static JLabel lbl50 = new JLabel("0");
	static JLabel lbl100 = new JLabel("0");
	static JLabel lbl200 = new JLabel("0");
	static JLabel lbl500 = new JLabel("0");

	@Override
	public void ContentPanel() {

		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	



		btnImprimirContadores = new JButton(Flow.botonAdminImprimir);
		btnImprimirContadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Ptr.printContadores();
				Ptr.ptrContadores();
			}
		});

		JButton btnSalir = new JButton(Flow.botonAdminSalir);
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);
			}
		});
		btnSalir.setOpaque(false);
		btnSalir.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnSalir.setContentAreaFilled(false);
		btnSalir.setBorderPainted(false);
		btnSalir.setBounds(1300, 880, 583, 161);
		add(btnSalir);

		JButton btnEnviarCeros = new JButton(new ImageIcon("./images/BTN_7p_Admin_EnviarACeros.png"));
		btnEnviarCeros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				Flow.redirect(Flow.panelAdminContadoresEnCero,30000,Flow.panelAdminMenu);
			}
		});
		btnEnviarCeros.setOpaque(false);
		btnEnviarCeros.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnEnviarCeros.setContentAreaFilled(false);
		btnEnviarCeros.setBorderPainted(false);
		btnEnviarCeros.setBounds(670, 880, 601, 155);
		add(btnEnviarCeros);
		btnImprimirContadores.setBounds(50, 880, 592, 149);
		btnImprimirContadores.setContentAreaFilled(false);
		btnImprimirContadores.setBorderPainted(false);
		btnImprimirContadores.setOpaque(false);
		btnImprimirContadores.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnImprimirContadores);

		JLabel lblNewLabel = new JLabel("$20");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel.setBounds(459, 511, 335, 30);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("$50");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_1.setBounds(459, 581, 335, 30);
		add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("$100");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2.setBounds(459, 659, 335, 30);
		add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel("$200");
		lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_1.setForeground(Color.WHITE);
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2_1.setBounds(459, 729, 335, 30);
		add(lblNewLabel_2_1);

		JLabel lblNewLabel_2_2 = new JLabel("$500");
		lblNewLabel_2_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_2.setForeground(Color.WHITE);
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2_2.setBounds(459, 799, 335, 30);
		add(lblNewLabel_2_2);
		lbl20.setHorizontalAlignment(SwingConstants.CENTER);
		lbl20.setForeground(Color.WHITE);


		lbl20.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl20.setBounds(1279, 511, 160, 30);
		add(lbl20);
		lbl50.setHorizontalAlignment(SwingConstants.CENTER);
		lbl50.setForeground(Color.WHITE);

		lbl50.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl50.setBounds(1279, 581, 160, 30);
		add(lbl50);
		lbl100.setHorizontalAlignment(SwingConstants.CENTER);
		lbl100.setForeground(Color.WHITE);


		lbl100.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl100.setBounds(1279, 659, 160, 30);
		add(lbl100);
		lbl200.setHorizontalAlignment(SwingConstants.CENTER);
		lbl200.setForeground(Color.WHITE);


		lbl200.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl200.setBounds(1279, 729, 160, 30);
		add(lbl200);
		lbl500.setHorizontalAlignment(SwingConstants.CENTER);
		lbl500.setForeground(Color.WHITE);


		lbl500.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl500.setBounds(1279, 799, 160, 30);
		add(lbl500);



	}



	public static void GetCurrentCounters() {

		lbl20.setText(Config.GetPersistence("Accepted20", ""));
		lbl50.setText(Config.GetPersistence("Accepted50", ""));
		lbl100.setText(Config.GetPersistence("Accepted100", ""));
		lbl200.setText(Config.GetPersistence("Accepted200", ""));
		lbl500.setText(Config.GetPersistence("Accepted500", ""));
	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelAdminContadoresActuales]");

	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelAdminContadoresActuales");
	}
}
