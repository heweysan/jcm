package pentomino.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import pentomino.flow.Flow;

public class DebugButtons {


	public JPanel panelDebugButtons;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public DebugButtons() {
		panelDebugButtons = new JPanel();
		panelDebugButtons.setOpaque(false);
		panelDebugButtons.setBackground(Color.blue);
		panelDebugButtons.setBounds(0, 0, 642, 94);
		panelDebugButtons.setBorder(null);
		panelDebugButtons.setLayout(null);
		
		
		JButton btnComandos = new JButton("COMANDOS");
		btnComandos.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnComandos.setBackground(Color.ORANGE);
		btnComandos.setBounds(220, 10, 200, 73);
		btnComandos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelComandosHolder);	
			}
		});
		
		JButton btnSalir = new JButton("SALIR");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnSalir.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnSalir.setBackground(Color.RED);
		btnSalir.setBounds(430, 10, 200, 73);
		
		JButton btnIdle = new JButton("IDLE");
		btnIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Flow.redirect(Flow.panelIdleHolder);
			}
		});
		btnIdle.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnIdle.setBackground(Color.GREEN);
		btnIdle.setBounds(10, 10, 200, 73);
		panelDebugButtons.add(btnIdle);
		
		panelDebugButtons.add(btnSalir);
		
		panelDebugButtons.add(btnComandos);		
	}
	
	public JPanel getPanel() {
		return panelDebugButtons;
	}
}
