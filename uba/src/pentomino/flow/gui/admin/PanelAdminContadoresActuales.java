package pentomino.flow.gui.admin;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.config.Config;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.ImagePanel;

public class PanelAdminContadoresActuales extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public PanelAdminContadoresActuales(String img,String name) {
		super(new ImageIcon(img).getImage(),name);
	}

	public PanelAdminContadoresActuales(Image img,String name, int _timeout, String _redirect) {
		super(img,name,_timeout,_redirect);
	}	

	public PanelAdminContadoresActuales(Image img, String name) {
		super(img,name);
	}




	static JLabel lbl20 = new JLabel("-");
	static JLabel lbl50 = new JLabel("-");
	static JLabel lbl100 = new JLabel("-");
	static JLabel lbl200 = new JLabel("-");
	static JLabel lbl500 = new JLabel("-");
	static JLabel lbl1000 = new JLabel("-");
	static JLabel lblTotal = new JLabel("-");

	@Override
	public void ContentPanel() {


		setBounds(0, 0, 120, 574);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	

		add(new DebugButtons().getPanel());	


		JButton btnImprimirContadores = new JButton(new ImageIcon("./images/Btn_AdminImpContadores.png"));
		btnImprimirContadores.setBounds(41, 939, 250, 90);
		btnImprimirContadores.setContentAreaFilled(false);
		btnImprimirContadores.setBorderPainted(false);
		btnImprimirContadores.setOpaque(false);
		btnImprimirContadores.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnImprimirContadores);

		JLabel lblNewLabel = new JLabel("$20");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel.setBounds(469, 213, 98, 30);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("$50");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_1.setBounds(469, 261, 98, 30);
		add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("$100");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2.setBounds(469, 306, 98, 30);
		add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel("$200");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_1.setBounds(469, 347, 98, 30);
		add(lblNewLabel_2_1);

		JLabel lblNewLabel_2_2 = new JLabel("$500");
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_2.setBounds(469, 388, 98, 30);
		add(lblNewLabel_2_2);

		JLabel lblNewLabel_2_3 = new JLabel("$1000");
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_3.setBounds(469, 429, 98, 30);
		add(lblNewLabel_2_3);


		lbl20.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl20.setBounds(652, 213, 98, 30);
		add(lbl20);

		lbl50.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl50.setBounds(652, 261, 98, 30);
		add(lbl50);


		lbl100.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl100.setBounds(652, 306, 98, 30);
		add(lbl100);


		lbl200.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl200.setBounds(652, 347, 98, 30);
		add(lbl200);


		lbl500.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl500.setBounds(652, 388, 98, 30);
		add(lbl500);


		lbl1000.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl1000.setBounds(652, 429, 98, 30);
		add(lbl1000);

		JLabel lblNewLabel_3 = new JLabel("TOTAL");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_3.setBounds(444, 484, 129, 35);
		add(lblNewLabel_3);


		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTotal.setBounds(652, 484, 129, 35);
		add(lblTotal);

		JButton btnEnviarCeros = new JButton(new ImageIcon("./images/Btn_AdminCeros.png"));
		btnEnviarCeros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				Flow.redirect(Flow.panelAdminContadoresEnCero,30000,"panelAdminMenu");
			}
		});
		btnEnviarCeros.setOpaque(false);
		btnEnviarCeros.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnEnviarCeros.setContentAreaFilled(false);
		btnEnviarCeros.setBorderPainted(false);
		btnEnviarCeros.setBounds(1660, 643, 250, 90);
		add(btnEnviarCeros);

		JButton btnSalir = new JButton(new ImageIcon("./images/Btn_AdminSalir.png"));
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);
			}
		});
		btnSalir.setOpaque(false);
		btnSalir.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnSalir.setContentAreaFilled(false);
		btnSalir.setBorderPainted(false);
		btnSalir.setBounds(1660, 877, 250, 90);
		add(btnSalir);



	}


	public JPanel getPanel() {
		return this;
	}


	public static void GetCurrentCounters() {

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

		lbl20.setText("$" + total20);
		lbl50.setText("$" + total50);
		lbl100.setText("$" + total100);
		lbl200.setText("$" + total200);
		lbl500.setText("$" + total500);
		lbl1000.setText("$" + total1000);

		lblTotal.setText("$" + total);


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
