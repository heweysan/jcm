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
	static JLabel lbl1000 = new JLabel("0");

	@Override
	public void ContentPanel() {

		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	



		btnImprimirContadores = new JButton(new ImageIcon("./images/BTN_7p_Admin_Imprimir.png"));
		btnImprimirContadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Ptr.printContadores();
			}
		});

		JButton btnSalir = new JButton(new ImageIcon("./images/BTN_7p_Admin_Salir.png"));
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
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel.setBounds(539, 498, 98, 30);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("$50");
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_1.setBounds(539, 578, 98, 30);
		add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("$100");
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2.setBounds(539, 658, 98, 30);
		add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel("$200");
		lblNewLabel_2_1.setForeground(Color.WHITE);
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2_1.setBounds(539, 738, 98, 30);
		add(lblNewLabel_2_1);

		JLabel lblNewLabel_2_2 = new JLabel("$500");
		lblNewLabel_2_2.setForeground(Color.WHITE);
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2_2.setBounds(539, 818, 98, 30);
		add(lblNewLabel_2_2);

		JLabel lblNewLabel_2_3 = new JLabel("$1000");
		lblNewLabel_2_3.setForeground(Color.WHITE);
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNewLabel_2_3.setBounds(539, 898, 98, 30);
		add(lblNewLabel_2_3);
		lbl20.setHorizontalAlignment(SwingConstants.CENTER);
		lbl20.setForeground(Color.WHITE);


		lbl20.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl20.setBounds(1279, 498, 100, 30);
		add(lbl20);
		lbl50.setHorizontalAlignment(SwingConstants.CENTER);
		lbl50.setForeground(Color.WHITE);

		lbl50.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl50.setBounds(1279, 578, 100, 30);
		add(lbl50);
		lbl100.setHorizontalAlignment(SwingConstants.CENTER);
		lbl100.setForeground(Color.WHITE);


		lbl100.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl100.setBounds(1279, 658, 100, 30);
		add(lbl100);
		lbl200.setHorizontalAlignment(SwingConstants.CENTER);
		lbl200.setForeground(Color.WHITE);


		lbl200.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl200.setBounds(1279, 738, 100, 30);
		add(lbl200);
		lbl500.setHorizontalAlignment(SwingConstants.CENTER);
		lbl500.setForeground(Color.WHITE);


		lbl500.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl500.setBounds(1279, 818, 100, 30);
		add(lbl500);
		lbl1000.setHorizontalAlignment(SwingConstants.CENTER);
		lbl1000.setForeground(Color.WHITE);


		lbl1000.setFont(new Font("Tahoma", Font.BOLD, 30));
		lbl1000.setBounds(1279, 898, 100, 30);
		add(lbl1000);



	}



	public static void GetCurrentCounters() {

		/*
		int total20;
		int total50;
		int total100;
		int total200;
		int total500;
		int total1000;

		total20 = 20 * Integer.parseInt(Config.GetPersistence("Accepted20", ""));;
		total50 = 50 * Integer.parseInt(Config.GetPersistence("Accepted50", ""));;
		total100 = 100 * Integer.parseInt(Config.GetPersistence("Accepted100", ""));;
		total200 = 200 * Integer.parseInt(Config.GetPersistence("Accepted200", ""));;
		total500 = 500 * Integer.parseInt(Config.GetPersistence("Accepted500", ""));;
		total1000 = 1000 * Integer.parseInt(Config.GetPersistence("Accepted1000", ""));;

		int total = total20 + total50 + total100 + total200 + total500 + total1000;

		*/
		lbl20.setText(Config.GetPersistence("Accepted20", ""));
		lbl50.setText(Config.GetPersistence("Accepted50", ""));
		lbl100.setText(Config.GetPersistence("Accepted100", ""));
		lbl200.setText(Config.GetPersistence("Accepted200", ""));
		lbl500.setText(Config.GetPersistence("Accepted500", ""));
		lbl1000.setText(Config.GetPersistence("Accepted1000", ""));



	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelAdminContadoresActuales");

	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelAdminContadoresActuales");
	}
}
