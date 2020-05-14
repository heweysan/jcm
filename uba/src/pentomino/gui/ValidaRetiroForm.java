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

public class ValidaRetiroForm {

	public static JTextField textFieldReferenciaNumerica;
	public static JTextField textFieldDepositoPassword;
	public static JButton btnConfirmacion = new JButton("CONFIRMACI\u00D3N");
	public static JButton btnRetiroSubmit = new JButton("ACEPTAR");
	public static JDialog loginDialog;
	private static final JButton btnRetiroCancelar = new JButton("CANCELAR");
	private static final JLabel lblMonto = new JLabel("$2,500.00");
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static JDialog validationForm(JFrame mainFrame, String referenciaNumerica, String monto) {
		
		
		ImagePanel panelUserLogin = new ImagePanel(new ImageIcon("ScrPlaceholder.png").getImage());
		
		
		//JPanel panelUserLogin = new JPanel();
		panelUserLogin.setBackground(Color.LIGHT_GRAY);
		panelUserLogin.setBounds(0, 0, 854, 576);
		panelUserLogin.setLayout(null);
		
		
		
		JLabel lblTexto = new JLabel("Validaci\u00F3n de retiro por:");
		lblTexto.setForeground(Color.WHITE);
		lblTexto.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblTexto.setBounds(10, 11, 344, 66);
		panelUserLogin.add(lblTexto);
		
		textFieldReferenciaNumerica = new JTextField();
		textFieldReferenciaNumerica.setEditable(false);
		textFieldReferenciaNumerica.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldReferenciaNumerica.setText(referenciaNumerica);
		textFieldReferenciaNumerica.setFont(new Font("Tahoma", Font.BOLD, 32));
		textFieldReferenciaNumerica.setBounds(433, 95, 400, 85);
		panelUserLogin.add(textFieldReferenciaNumerica);
		textFieldReferenciaNumerica.setColumns(10);
		
		
		
		btnConfirmacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
			}
		});
		btnConfirmacion.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnConfirmacion.setBounds(10, 95, 400, 200);
		panelUserLogin.add(btnConfirmacion);
		
		textFieldDepositoPassword = new JTextField();
		textFieldDepositoPassword.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDepositoPassword.setFont(new Font("Tahoma", Font.BOLD, 32));
		textFieldDepositoPassword.setColumns(10);
		textFieldDepositoPassword.setBounds(433, 203, 400, 91);
		panelUserLogin.add(textFieldDepositoPassword);
		btnRetiroSubmit.setBackground(Color.GREEN);
		
				
		btnRetiroSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		btnRetiroSubmit.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnRetiroSubmit.setBounds(441, 340, 400, 200);
		panelUserLogin.add(btnRetiroSubmit);
		
		loginDialog = new JDialog(mainFrame,"VALIDACION RETIRO", true);
		
		loginDialog.setAlwaysOnTop(true);
		loginDialog.setModalityType(ModalityType.MODELESS);
		loginDialog.setBounds(10, 10, 870, 602);
		
		loginDialog.getContentPane().setLayout(null);
		loginDialog.getContentPane().add(panelUserLogin);	
		btnRetiroCancelar.setBackground(Color.RED);
		btnRetiroCancelar.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnRetiroCancelar.setBounds(10, 340, 400, 200);
		
		panelUserLogin.add(btnRetiroCancelar);
		lblMonto.setForeground(Color.WHITE);
		lblMonto.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblMonto.setBounds(372, 33, 461, 24);
		lblMonto.setText(monto);
		
		panelUserLogin.add(lblMonto);
		
		
		return loginDialog;
	}
	

	
}



	
	

