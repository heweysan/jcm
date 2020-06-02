package pentomino.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.protocol;

public class PanelMenu {

	public JPanel contentPanel;
	public JButton btnMenuRetiro;
	public JButton btnMenuDeposito;
	
	public PanelMenu() {
		
		contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setBackground(Color.blue);
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
		
		btnMenuDeposito = new JButton(new ImageIcon("./images/BTN7Deposito.png"));
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setBounds(360, 502, 492, 498);
		btnMenuDeposito.setOpaque(false);
		btnMenuDeposito.setContentAreaFilled(false);
		btnMenuDeposito.setBorderPainted(false);
		contentPanel.add(btnMenuDeposito);
		
		btnMenuRetiro = new JButton(new ImageIcon("./images/BTN7Retiro.png"));		
		btnMenuRetiro.setOpaque(false);
		btnMenuRetiro.setContentAreaFilled(false);
		btnMenuRetiro.setBorderPainted(false);
		btnMenuRetiro.setBounds(989, 502, 492, 498);
		contentPanel.add(btnMenuRetiro);
		
		contentPanel.add(new DebugButtons().getPanel());	
		
	}
	
	
	public JPanel getPanel() {
		return contentPanel;
	}
	
	
}
