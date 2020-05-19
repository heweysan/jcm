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
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public final class LoginForm {

	
	public static JTextField tfUser;
	public static JTextField tfPassword;
	public static JButton btnUser = new JButton("USUARIO");
	public static JButton btnPassword = new JButton("CONTRASE\u00D1A");
	public static JButton btnSubmit = new JButton("INGRESAR");
	public static JDialog loginDialog;
	public static JLabel lblMensaje;
	public static JButton btnCancel = new JButton("CANCELAR");
	private static JPanel panelPinPad;
	private static JButton btn2;
	private static JButton btn3;
	private static JButton btn4;
	private static JButton btn5;
	private static JButton btn6;
	private static JButton btn7;
	private static JButton btn8;
	private static JButton btn9;
	private static JButton btn0;
	private static JButton btnCancel_1;
	private static JButton btnConfirmar;
	private static JButton btn1;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static JDialog loginForm(JFrame mainFrame) {
		//Redone for larger OK button	
		
		
		ImagePanel panelUserLogin = new ImagePanel(new ImageIcon("ScrPlaceholder.png").getImage(),"");
		
		
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
		loginDialog.setBounds(10, 10, 1920, 1079);
		
		loginDialog.getContentPane().setLayout(null);
		loginDialog.getContentPane().add(panelUserLogin);	
		
		btnCancel = new JButton("CANCELAR");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCancel.setBackground(Color.RED);
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnCancel.setBounds(10, 549, 400, 200);
		panelUserLogin.add(btnCancel);
		
		panelPinPad = new JPanel();
		panelPinPad.setLayout(null);
		panelPinPad.setOpaque(false);
		panelPinPad.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelPinPad.setBackground(Color.GRAY);
		panelPinPad.setBounds(958, 0, 946, 1080);
		loginDialog.getContentPane().add(panelPinPad);
		
		btn2 = new JButton("2");
		btn2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn2.setBounds(359, 47, 262, 220);
		panelPinPad.add(btn2);
		
		btn3 = new JButton("3");
		btn3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn3.setBounds(674, 47, 262, 220);
		panelPinPad.add(btn3);
		
		btn4 = new JButton("4");
		btn4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn4.setBounds(50, 302, 259, 220);
		panelPinPad.add(btn4);
		
		btn5 = new JButton("5");
		btn5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn5.setBounds(359, 302, 262, 220);
		panelPinPad.add(btn5);
		
		btn6 = new JButton("6");
		btn6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn6.setBounds(674, 302, 262, 220);
		panelPinPad.add(btn6);
		
		btn7 = new JButton("7");
		btn7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn7.setBounds(50, 557, 259, 220);
		panelPinPad.add(btn7);
		
		btn8 = new JButton("8");
		btn8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn8.setBounds(359, 557, 267, 220);
		panelPinPad.add(btn8);
		
		btn9 = new JButton("9");
		btn9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn9.setBounds(664, 557, 272, 220);
		panelPinPad.add(btn9);
		
		btn0 = new JButton("0");
		btn0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn0.setBounds(359, 812, 267, 220);
		panelPinPad.add(btn0);
		
		btnCancel_1 = new JButton("CANCELAR");
		btnCancel_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnCancel_1.setBackground(Color.RED);
		btnCancel_1.setBounds(50, 812, 259, 220);
		panelPinPad.add(btnCancel_1);
		
		btnConfirmar = new JButton("CONFIRMAR");
		btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnConfirmar.setBackground(Color.GREEN);
		btnConfirmar.setBounds(664, 812, 272, 220);
		panelPinPad.add(btnConfirmar);
		
		btn1 = new JButton("1");
		btn1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn1.setBounds(50, 47, 260, 220);
		panelPinPad.add(btn1);
		
		
		return loginDialog;
	}
	

	
}


