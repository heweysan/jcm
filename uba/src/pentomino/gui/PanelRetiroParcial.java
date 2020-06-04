package pentomino.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelRetiroParcial {
	public JPanel contentPanel;

	public static JLabel lblRetiraBilletesMontoDispensarParcial = new JLabel("New label");
	
public PanelRetiroParcial() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
				
		contentPanel.add(new DebugButtons().getPanel());		
		
		
		lblRetiraBilletesMontoDispensarParcial.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetiraBilletesMontoDispensarParcial.setForeground(Color.WHITE);
		lblRetiraBilletesMontoDispensarParcial.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblRetiraBilletesMontoDispensarParcial.setBounds(501, 677, 622, 153);
		contentPanel.add(lblRetiraBilletesMontoDispensarParcial);
}

public JPanel getPanel() {
	return contentPanel;
}
	
}
