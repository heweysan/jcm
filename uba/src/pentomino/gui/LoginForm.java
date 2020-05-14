package pentomino.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pentomino.common.PinpadMode;

public final class LoginForm {

	
	public static JTextField textFieldDepositoUser;
	public static JTextField textFieldDepositoPassword;
	public static JButton btnUser = new JButton("USUARIO");
	public static JButton btnPassword = new JButton("CONTRASE\u00D1A");
	public static JButton btnLoginSubmit = new JButton("INGRESAR");
	public static JDialog loginDialog;
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static JDialog loginForm(JFrame mainFrame) {
		//Redone for larger OK button	
		
		
		ImagePanel panelUserLogin = new ImagePanel(new ImageIcon("ScrPlaceholder.png").getImage());
		
		
		//JPanel panelUserLogin = new JPanel();
		panelUserLogin.setBackground(Color.LIGHT_GRAY);
		panelUserLogin.setBounds(0, 0, 870, 1000);
		panelUserLogin.setLayout(null);
		
		
		
		JLabel lblNewLabel = new JLabel("Ingresa tu n\u00FAmero de usuario y contrase\u00F1a");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblNewLabel.setBounds(27, 27, 711, 66);
		panelUserLogin.add(lblNewLabel);		
		
		btnUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnUser.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnUser.setBounds(10, 116, 400, 200);
		panelUserLogin.add(btnUser);
		
		textFieldDepositoUser = new JTextField();
		textFieldDepositoUser.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDepositoUser.setText("007007");
		textFieldDepositoUser.setFont(new Font("Tahoma", Font.BOLD, 32));
		textFieldDepositoUser.setBounds(433, 115, 400, 200);
		panelUserLogin.add(textFieldDepositoUser);
		textFieldDepositoUser.setColumns(10);
		
		
		
		btnPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
			}
		});
		btnPassword.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnPassword.setBounds(10, 344, 400, 200);
		panelUserLogin.add(btnPassword);
		
		textFieldDepositoPassword = new JTextField();
		textFieldDepositoPassword.setText("007007");
		textFieldDepositoPassword.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDepositoPassword.setFont(new Font("Tahoma", Font.BOLD, 32));
		textFieldDepositoPassword.setColumns(10);
		textFieldDepositoPassword.setBounds(433, 343, 400, 200);
		panelUserLogin.add(textFieldDepositoPassword);
		
				
		btnLoginSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		btnLoginSubmit.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnLoginSubmit.setBounds(441, 589, 400, 200);
		panelUserLogin.add(btnLoginSubmit);
		
		loginDialog = new JDialog(mainFrame,"LOGIN", true);
		
		loginDialog.setAlwaysOnTop(true);
		loginDialog.setModalityType(ModalityType.MODELESS);
		loginDialog.setBounds(10, 10, 870, 1000);
		
		loginDialog.getContentPane().setLayout(null);
		loginDialog.getContentPane().add(panelUserLogin);	
		
		
		return loginDialog;
	}
	

	
}


