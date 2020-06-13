package pentomino.flow.gui.admin;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;

public class PanelAdminDotarCancelar {

	public JPanel contentPanel;

	public PanelAdminDotarCancelar() {

		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	

		contentPanel.add(new DebugButtons().getPanel());	




		JLabel lblNewLabel = new JLabel("\u00BFEsta seguro que desea cancelar los cambios?");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblNewLabel.setBounds(10, 213, 1900, 84);
		contentPanel.add(lblNewLabel);


		JButton btnSi = new JButton(new ImageIcon("./images/Btn_AdminSi.png"));
		btnSi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenuHolder);	
			}
		});
		btnSi.setBounds(41, 939, 250, 90);
		btnSi.setContentAreaFilled(false);
		btnSi.setBorderPainted(false);
		btnSi.setOpaque(false);
		btnSi.setFont(new Font("Tahoma", Font.BOLD, 40));
		contentPanel.add(btnSi);

		JButton btnNo = new JButton(new ImageIcon("./images/Btn_AdminNo.png"));
		btnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminContadoresEnCeroHolder);	
			}
		});
		btnNo.setOpaque(false);
		btnNo.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnNo.setContentAreaFilled(false);
		btnNo.setBorderPainted(false);
		btnNo.setBounds(1660, 877, 250, 90);
		contentPanel.add(btnNo);

	}


	public JPanel getPanel() {
		return contentPanel;
	}


}
