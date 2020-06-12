package pentomino.flow.gui.admin;


import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.common.PinpadMode;
import pentomino.flow.CurrentUser;
import pentomino.flow.gui.DebugButtons;

public class PanelAdminError {

	public JPanel contentPanel;


	public static JLabel lblSubMensaje = new JLabel("");

	public PanelAdminError() {

		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	

		contentPanel.add(new DebugButtons().getPanel());


		JLabel lblMensaje = new JLabel("Error al autenticar al usuario, vuelve a intentarlo.");
		lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblMensaje.setBounds(10, 213, 1900, 76);
		contentPanel.add(lblMensaje);


		lblSubMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblSubMensaje.setBounds(10, 375, 1900, 76);
		contentPanel.add(lblSubMensaje);


		CurrentUser.loginUser = "";
		CurrentUser.loginPassword = "";
		CurrentUser.pinpadMode = PinpadMode.loginUser;
		PanelAdminLogin.lblLoginUser.setText("");
		PanelAdminLogin.lblLoginPassword.setText("");

	}


	public JPanel getPanel() {
		return contentPanel;
	}
}
