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

	
	public static JDialog validationDialog;
	public static JTextField textFieldConfirmacion;
	public static JButton btnConfirmacion = new JButton("CONFIRMACI\u00D3N");
	public static JButton btnAceptar = new JButton("ACEPTAR");	
	public static JButton btnCancelar = new JButton("CANCELAR");
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static JDialog validationForm(JFrame mainFrame, String referenciaNumerica, String monto) {
		
		
		ImagePanel panelUserLogin = new ImagePanel(new ImageIcon("ScrPlaceholder.png").getImage(),"panelUserLogin");
		
		JTextField textFieldReferenciaNumerica;
		JLabel lblMonto = new JLabel("$2,500.00");
		
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
		
		textFieldConfirmacion = new JTextField();
		textFieldConfirmacion.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldConfirmacion.setFont(new Font("Tahoma", Font.BOLD, 32));
		textFieldConfirmacion.setColumns(10);
		textFieldConfirmacion.setBounds(433, 203, 400, 91);
		panelUserLogin.add(textFieldConfirmacion);
		btnAceptar.setBackground(Color.GREEN);
		
				
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		btnAceptar.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnAceptar.setBounds(441, 340, 400, 200);
		panelUserLogin.add(btnAceptar);
		
		validationDialog = new JDialog(mainFrame,"VALIDACION RETIRO", true);
		
		validationDialog.setAlwaysOnTop(true);
		validationDialog.setModalityType(ModalityType.MODELESS);
		validationDialog.setBounds(10, 10, 870, 602);
		
		validationDialog.getContentPane().setLayout(null);
		validationDialog.getContentPane().add(panelUserLogin);	
		
		btnCancelar.setBackground(Color.RED);
		btnCancelar.setFont(new Font("Tahoma", Font.BOLD, 22));
		btnCancelar.setBounds(10, 340, 400, 200);
		
		panelUserLogin.add(btnCancelar);
		lblMonto.setForeground(Color.WHITE);
		lblMonto.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblMonto.setBounds(372, 33, 461, 24);
		lblMonto.setText(monto);
		
		panelUserLogin.add(lblMonto);
		
		
		return validationDialog;
	}
	

	
}



	
	

