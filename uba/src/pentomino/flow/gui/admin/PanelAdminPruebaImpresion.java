package pentomino.flow.gui.admin;

import javax.swing.JLabel;

import pentomino.core.devices.Ptr;
import pentomino.flow.Flow;
import pentomino.flow.gui.ImagePanel;
import pentomino.flow.gui.helpers.ImageButton;

import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelAdminPruebaImpresion extends ImagePanel {

	public static JLabel lblDetalleError = new JLabel("");

	public static JLabel lblContadores = new JLabel("Imprimiendo tique contadores ...");
	public static JLabel lblRetiro = new JLabel("Imprimiendo tique retiro ...");
	public static JLabel lblDeposito = new JLabel("Imprimiendo tique dep\u00F3sito ...");
	
	public static JLabel lblContadoresResult = new JLabel("");
	public static JLabel lblRetiroResult = new JLabel("");
	public static JLabel lblDepositoResult = new JLabel("");
	
	public PanelAdminPruebaImpresion(String img, String name, long _timeout, ImagePanel _redirect) {
		super(img, name, _timeout, _redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void ContentPanel() {
		
		
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	

		JLabel lblMensaje = new JLabel("Ve tomando los tiques");
		lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMensaje.setForeground(Color.WHITE);
		lblMensaje.setBounds(584, 185, 734, 92);
		add(lblMensaje);

		ImageButton btnRegresar = new ImageButton("./images/BTN_7p_Admin_Regresar.png");
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminEstatusDispositivos);
			}
		});
		btnRegresar.setLocation(1323, 850);
		btnRegresar.setSize(574, 171);
		add(btnRegresar);

		JLabel lblTitulo = new JLabel("Prueba de impresi\u00F3n.");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblTitulo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTitulo.setBounds(1312, 23, 598, 80);
		add(lblTitulo);
		
		
		lblContadores.setHorizontalAlignment(SwingConstants.LEFT);
		lblContadores.setForeground(Color.WHITE);
		lblContadores.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblContadores.setBounds(584, 362, 500, 92);
		//lblContadores.setVisible(false);
		add(lblContadores);
		
		
		lblRetiro.setHorizontalAlignment(SwingConstants.LEFT);
		lblRetiro.setForeground(Color.WHITE);
		lblRetiro.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblRetiro.setBounds(584, 532, 510, 92);
		//lblRetiro.setVisible(false);
		add(lblRetiro);
		
		
		lblDeposito.setHorizontalAlignment(SwingConstants.LEFT);
		lblDeposito.setForeground(Color.WHITE);
		lblDeposito.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblDeposito.setBounds(584, 696, 500, 92);
		//lblDeposito.setVisible(false);
		add(lblDeposito);
		
		
		lblContadoresResult.setHorizontalAlignment(SwingConstants.LEFT);
		lblContadoresResult.setForeground(Color.WHITE);
		lblContadoresResult.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblContadoresResult.setBounds(1119, 362, 653, 92);
		add(lblContadoresResult);
		
		
		lblRetiroResult.setHorizontalAlignment(SwingConstants.LEFT);
		lblRetiroResult.setForeground(Color.WHITE);
		lblRetiroResult.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblRetiroResult.setBounds(1119, 532, 653, 92);
		add(lblRetiroResult);
		
		
		lblDepositoResult.setHorizontalAlignment(SwingConstants.LEFT);
		lblDepositoResult.setForeground(Color.WHITE);
		lblDepositoResult.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblDepositoResult.setBounds(1119, 696, 653, 92);
		add(lblDepositoResult);
		

	}

	public void imprimeTickets() {

		lblContadores.setVisible(true);
		if(Ptr.printContadoresTest()) {
			System.out.println("printContadoresTest OK");
			lblContadoresResult.setForeground(Color.WHITE);
			lblContadoresResult.setText("Impresi�n exitosa");
		}else {
			System.out.println("printContadoresTest ERROR");
			lblContadoresResult.setForeground(Color.RED);
			lblContadoresResult.setText("ERROR");
		}

		
		lblDeposito.setVisible(true);
		if(Ptr.printDepositTest()) {
			lblDepositoResult.setForeground(Color.WHITE);
			lblDepositoResult.setText("Impresi�n exitosa");
		}else {
			lblDepositoResult.setForeground(Color.RED);
			lblDepositoResult.setText("ERROR");
		}

		lblRetiro.setVisible(true);
		if(Ptr.printDispenseTest()) {
			lblRetiroResult.setForeground(Color.WHITE);
			lblRetiroResult.setText("Impresi�n exitosa");
		}else {
			lblRetiroResult.setForeground(Color.RED);
			lblRetiroResult.setText("ERROR");
		}

	}

	
	
	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelAdminPruebaImpresion");		
		imprimeTickets();
	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelAdminPruebaImpresion");
		lblContadores.setVisible(false);
		lblRetiro.setVisible(false);
		lblDeposito.setVisible(false);
		lblContadoresResult.setText("");
		lblDepositoResult.setText("");
		lblRetiroResult.setText("");

	}
}