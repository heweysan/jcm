package pentomino.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelRetiraBilletes {
	public JPanel contentPanel;

	public static JLabel lblRetiraBilletesMontoDispensar = new JLabel(".");
	
public PanelRetiraBilletes() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
				
		contentPanel.add(new DebugButtons().getPanel());		
		
		lblRetiraBilletesMontoDispensar.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiraBilletesMontoDispensar.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblRetiraBilletesMontoDispensar.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensar.setBounds(408, 579, 622, 153);
		contentPanel.add(lblRetiraBilletesMontoDispensar);
}

public JPanel getPanel() {
	return contentPanel;
}
	
}