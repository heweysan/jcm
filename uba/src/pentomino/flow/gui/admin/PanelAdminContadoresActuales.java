package pentomino.flow.gui.admin;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.config.Config;
import pentomino.flow.gui.DebugButtons;

public class PanelAdminContadoresActuales {
	
	public JPanel contentPanel;

	
	static JLabel lbl20 = new JLabel("-");
	static JLabel lbl50 = new JLabel("-");
	static JLabel lbl100 = new JLabel("-");
	static JLabel lbl200 = new JLabel("-");
	static JLabel lbl500 = new JLabel("-");
	static JLabel lbl1000 = new JLabel("-");
	static JLabel lblTotal = new JLabel("-");
	
	public PanelAdminContadoresActuales() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		
		
		contentPanel.add(new DebugButtons().getPanel());	
		
		JLabel lblNewLabel = new JLabel("$20");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel.setBounds(106, 165, 98, 30);
		contentPanel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("$50");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_1.setBounds(106, 213, 84, 30);
		contentPanel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("$100");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2.setBounds(106, 258, 84, 30);
		contentPanel.add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("$200");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_1.setBounds(106, 299, 98, 30);
		contentPanel.add(lblNewLabel_2_1);
		
		JLabel lblNewLabel_2_2 = new JLabel("$500");
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_2.setBounds(106, 340, 84, 30);
		contentPanel.add(lblNewLabel_2_2);
		
		JLabel lblNewLabel_2_3 = new JLabel("$1000");
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_3.setBounds(106, 381, 98, 30);
		contentPanel.add(lblNewLabel_2_3);
		
		
		lbl20.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl20.setBounds(289, 165, 98, 30);
		contentPanel.add(lbl20);
		
		lbl50.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl50.setBounds(289, 213, 84, 30);
		contentPanel.add(lbl50);
		
		
		lbl100.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl100.setBounds(289, 258, 84, 30);
		contentPanel.add(lbl100);
		
		
		lbl200.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl200.setBounds(289, 299, 98, 30);
		contentPanel.add(lbl200);
		
		
		lbl500.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl500.setBounds(289, 340, 84, 30);
		contentPanel.add(lbl500);
		
		
		lbl1000.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl1000.setBounds(289, 381, 98, 30);
		contentPanel.add(lbl1000);
		
		JLabel lblNewLabel_3 = new JLabel("TOTAL");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_3.setBounds(81, 436, 129, 35);
		contentPanel.add(lblNewLabel_3);
		
		
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTotal.setBounds(289, 436, 129, 35);
		contentPanel.add(lblTotal);
		
		
		
	}
	
	
	public JPanel getPanel() {
		return contentPanel;
	}
	
	
	public static void GetCurrentCounters() {
	
		int total = 0;
		
		int value;
		int count;
		
		value = Integer.parseInt(Config.GetPersistence("Cassette1Value", ""));
		count = Integer.parseInt(Config.GetPersistence("Cassette1Total", ""));			
		printValue(value,count);
		total = total + (value * count);
		
		value = Integer.parseInt(Config.GetPersistence("Cassette2Value", ""));
		count = Integer.parseInt(Config.GetPersistence("Cassette2Total", ""));			
		printValue(value,count);
		total = total + (value * count);
		
		value = Integer.parseInt(Config.GetPersistence("Cassette3Value", ""));
		count = Integer.parseInt(Config.GetPersistence("Cassette3Total", ""));			
		printValue(value,count);
		total = total + (value * count);
		
		value = Integer.parseInt(Config.GetPersistence("Cassette4Value", ""));
		count = Integer.parseInt(Config.GetPersistence("Cassette4Total", ""));			
		printValue(value,count);
		total = total + (value * count);
		
		lblTotal.setText("$" + total);
		
	}
	
	private static void printValue(int value, int count) {
		switch(value) {
		case 20:
			lbl20.setText("" + count);
			break;
		case 50:
			lbl50.setText("" + count);
			break;
		case 100:
			lbl100.setText("" + count);
			break;
		case 200:
			lbl200.setText("" + count);
			break;
		case 500:
			lbl500.setText("" + count);
			break;
		case 1000:
			lbl1000.setText("" + count);
			break;
		}
	}
}
