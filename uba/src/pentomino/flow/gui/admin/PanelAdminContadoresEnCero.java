package pentomino.flow.gui.admin;


import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.config.Config;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import javax.swing.Icon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelAdminContadoresEnCero {

	public JPanel contentPanel;


	static JLabel lbl20 = new JLabel("0");
	static JLabel lbl50 = new JLabel("0");
	static JLabel lbl100 = new JLabel("0");
	static JLabel lbl200 = new JLabel("0");
	static JLabel lbl500 = new JLabel("0");
	static JLabel lbl1000 = new JLabel("0");
	static JLabel lblTotal = new JLabel("0");

	public PanelAdminContadoresEnCero() {

		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	

		contentPanel.add(new DebugButtons().getPanel());




		JLabel lblNewLabel = new JLabel("$20");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel.setBounds(469, 213, 98, 30);
		contentPanel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("$50");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_1.setBounds(469, 261, 98, 30);
		contentPanel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("$100");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2.setBounds(469, 306, 98, 30);
		contentPanel.add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel("$200");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_1.setBounds(469, 347, 98, 30);
		contentPanel.add(lblNewLabel_2_1);

		JLabel lblNewLabel_2_2 = new JLabel("$500");
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_2.setBounds(469, 388, 98, 30);
		contentPanel.add(lblNewLabel_2_2);

		JLabel lblNewLabel_2_3 = new JLabel("$1000");
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_3.setBounds(469, 429, 98, 30);
		contentPanel.add(lblNewLabel_2_3);


		lbl20.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl20.setBounds(652, 213, 98, 30);
		contentPanel.add(lbl20);

		lbl50.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl50.setBounds(652, 261, 98, 30);
		contentPanel.add(lbl50);


		lbl100.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl100.setBounds(652, 306, 98, 30);
		contentPanel.add(lbl100);


		lbl200.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl200.setBounds(652, 347, 98, 30);
		contentPanel.add(lbl200);


		lbl500.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl500.setBounds(652, 388, 98, 30);
		contentPanel.add(lbl500);


		lbl1000.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl1000.setBounds(652, 429, 98, 30);
		contentPanel.add(lbl1000);

		JLabel lblNewLabel_3 = new JLabel("TOTAL");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_3.setBounds(444, 484, 129, 35);
		contentPanel.add(lblNewLabel_3);


		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTotal.setBounds(652, 484, 129, 35);
		contentPanel.add(lblTotal);

		JButton btnEnviarCeros = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminGuardar.png"));
		btnEnviarCeros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminDotarResultadosHolder);
			}
		});
		btnEnviarCeros.setOpaque(false);
		btnEnviarCeros.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnEnviarCeros.setContentAreaFilled(false);
		btnEnviarCeros.setBorderPainted(false);
		btnEnviarCeros.setBounds(1660, 643, 250, 90);
		contentPanel.add(btnEnviarCeros);

		JButton btnSalir = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminCancelar.png"));
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminDotarCancelarHolder);
			}
		});
		btnSalir.setOpaque(false);
		btnSalir.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnSalir.setContentAreaFilled(false);
		btnSalir.setBorderPainted(false);
		btnSalir.setBounds(1660, 877, 250, 90);
		contentPanel.add(btnSalir);

	}


	public JPanel getPanel() {
		return contentPanel;
	}


}
