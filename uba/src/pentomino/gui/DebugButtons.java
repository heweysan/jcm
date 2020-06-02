package pentomino.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import pentomino.flow.Flow;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
				Flow.cl.show(Flow.panelContainer,"panelComandos");	
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
		
		JButton btnDepositoIdle = new JButton("IDLE");
		btnDepositoIdle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Flow.cl.show(Flow.panelContainer,"panelIdle");
			}
		});
		btnDepositoIdle.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnDepositoIdle.setBackground(Color.GREEN);
		btnDepositoIdle.setBounds(10, 10, 200, 73);
		panelDebugButtons.add(btnDepositoIdle);
		
		panelDebugButtons.add(btnSalir);
		
		panelDebugButtons.add(btnComandos);		
	}
	
	public JPanel getPanel() {
		return panelDebugButtons;
	}
}
