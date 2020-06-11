package pentomino.flow.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.cashmanagement.CmQueue;
import pentomino.flow.Flow;

public class PanelIdle {
	public JPanel contentPanel = new JPanel();

	public static JLabel lblPanelError = new JLabel("");

	public PanelIdle() {

		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);

		contentPanel.add(new DebugButtons().getPanel());

		JButton btnIdle = new JButton("");
		btnIdle.setBounds(0, 0, 1920, 1080);		
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnIdle.setOpaque(false);
		btnIdle.setContentAreaFilled(false);
		btnIdle.setBorderPainted(false);		
		contentPanel.add(btnIdle);



		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(CmQueue.queueList.isEmpty()) {
					Flow.panelMenuHolder.setBackground("./images/Scr7SinRetiroAutorizado.png");
					PanelMenu.btnMenuRetiro.setEnabled(false);
				}else {
					Flow.panelMenuHolder.setBackground("./images/Scr7RetiroAutorizado.png");				
					PanelMenu.btnMenuRetiro.setEnabled(true);	
				}
				
				Flow.redirect(Flow.panelMenuHolder,5000,"panelIdle");
			}
		});
		
		


	}

	public JPanel getPanel() {
		return contentPanel;
	}

}
