package pentomino.gui;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public final class LoginForm {

	
	public static JTextField tfUser;
	public static JTextField tfPassword;
	public static JButton btnUser = new JButton("USUARIO");
	public static JButton btnPassword = new JButton("CONTRASE\u00D1A");
	public static JButton btnSubmit = new JButton("INGRESAR");
	public static JDialog loginDialog;
	public static JLabel lblMensaje;
	public static JButton btnCancel = new JButton("");;
	
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
		
		
		
		lblMensaje = new JLabel("Ingresa tu n\u00FAmero de usuario y contrase\u00F1a");
		lblMensaje.setForeground(Color.WHITE);
		lblMensaje.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblMensaje.setBounds(26, 11, 711, 66);
		panelUserLogin.add(lblMensaje);		
		
		btnUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnUser.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnUser.setBounds(10, 88, 400, 200);
		panelUserLogin.add(btnUser);
		
		tfUser = new JTextField();
		tfUser.setHorizontalAlignment(SwingConstants.CENTER);
		tfUser.setFont(new Font("Tahoma", Font.BOLD, 32));
		tfUser.setBounds(433, 88, 400, 200);
		panelUserLogin.add(tfUser);
		tfUser.setColumns(10);
		
		
		
		btnPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
			}
		});
		btnPassword.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnPassword.setBounds(10, 322, 400, 200);
		panelUserLogin.add(btnPassword);
		
		tfPassword = new JTextField();
		tfPassword.setHorizontalAlignment(SwingConstants.CENTER);
		tfPassword.setFont(new Font("Tahoma", Font.BOLD, 32));
		tfPassword.setColumns(10);
		tfPassword.setBounds(433, 319, 400, 200);
		panelUserLogin.add(tfPassword);
		btnSubmit.setBackground(Color.GREEN);
		
				
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		btnSubmit.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnSubmit.setBounds(433, 549, 400, 200);
		panelUserLogin.add(btnSubmit);
		
		loginDialog = new JDialog(mainFrame,"LOGIN", true);
		
		loginDialog.setAlwaysOnTop(true);
		loginDialog.setModalityType(ModalityType.MODELESS);
		loginDialog.setBounds(10, 10, 870, 804);
		
		loginDialog.getContentPane().setLayout(null);
		loginDialog.getContentPane().add(panelUserLogin);	
		
		btnCancel = new JButton("CANCELAR");
		btnCancel.setBackground(Color.RED);
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnCancel.setBounds(10, 549, 400, 200);
		panelUserLogin.add(btnCancel);
		
		
		return loginDialog;
	}
	

	
}


