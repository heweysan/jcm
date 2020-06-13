package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelError {

	public JPanel contentPanel;

	public static JLabel lblPanelError = new JLabel("PANEL ERROR");

	public PanelError() {

		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);

		lblPanelError.setHorizontalAlignment(SwingConstants.CENTER);
		lblPanelError.setFont(new Font("Tahoma", Font.PLAIN, 50));
		lblPanelError.setForeground(Color.WHITE);
		lblPanelError.setBounds(10, 500, 1900, 103);
		contentPanel.add(lblPanelError);
	}

	public JPanel getPanel() {
		return contentPanel;
	}

}
