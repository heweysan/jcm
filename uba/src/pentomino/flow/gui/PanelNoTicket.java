package pentomino.flow.gui;

import javax.swing.JPanel;

public class PanelNoTicket {
	
	public JPanel contentPanel;

	

	public PanelNoTicket() {

		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);

	
	}

	public JPanel getPanel() {
		return contentPanel;
	}

}
