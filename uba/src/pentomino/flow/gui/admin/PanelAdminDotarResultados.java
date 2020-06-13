package pentomino.flow.gui.admin;



import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.config.Config;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;

public class PanelAdminDotarResultados {

	public JPanel contentPanel;
	
	public static JLabel lblMensaje = new JLabel("");

	public PanelAdminDotarResultados() {
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	

		contentPanel.add(new DebugButtons().getPanel());	

		
		lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblMensaje.setBounds(10, 213, 1900, 84);
		contentPanel.add(lblMensaje);


		JButton btnImprimirContadores = new JButton(new ImageIcon("./images/Btn_AdminImpContadores.png"));
		btnImprimirContadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenuHolder);	
			}
		});
		btnImprimirContadores.setBounds(41, 939, 250, 90);
		btnImprimirContadores.setContentAreaFilled(false);
		btnImprimirContadores.setBorderPainted(false);
		btnImprimirContadores.setOpaque(false);
		btnImprimirContadores.setFont(new Font("Tahoma", Font.BOLD, 40));
		contentPanel.add(btnImprimirContadores);

		JButton btnRegresar = new JButton(new ImageIcon("./images/Btn_AdminRegresar.png"));
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenuHolder);	
			}
		});
		btnRegresar.setOpaque(false);
		btnRegresar.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnRegresar.setContentAreaFilled(false);
		btnRegresar.setBorderPainted(false);
		btnRegresar.setBounds(1660, 877, 250, 90);
		contentPanel.add(btnRegresar);

	}


	public JPanel getPanel() {
		return contentPanel;
	}
	
	public static boolean actualizaContadoresCeros() {
		
		//Setemos los contadores a 0 de accepted que son los unicos que se llevan
		Config.SetPersistence("Accepted20", "0");
		Config.SetPersistence("Accepted50", "0");
		Config.SetPersistence("Accepted100", "0");
		Config.SetPersistence("Accepted200", "0");
		Config.SetPersistence("Accepted500", "0");
		Config.SetPersistence("Accepted1000", "0");
		
		
		//Validamos que sea cierto!!
		
		String d20 = Config.GetPersistence("Accepted20", "-1");
		String d50 = Config.GetPersistence("Accepted50", "-1");
		String d100 = Config.GetPersistence("Accepted100", "-1");
		String d200 = Config.GetPersistence("Accepted200", "-1");
		String d500 = Config.GetPersistence("Accepted500", "-1");
		String d1000 = Config.GetPersistence("Accepted1000", "-1");
		
		boolean ok20 = true;
		boolean ok50 = true;
		boolean ok100 = true;
		boolean ok200 = true;
		boolean ok500 = true;
		boolean ok1000 = true;
		
		if(!d20.equalsIgnoreCase("0"))
			ok20 = false;
		
		if(!d50.equalsIgnoreCase("0"))
			ok50 = false;
		
		if(!d100.equalsIgnoreCase("0"))
			ok100 = false;
		
		if(!d200.equalsIgnoreCase("0"))
			ok200 = false;
		
		if(!d500.equalsIgnoreCase("0"))
			ok500 = false;
		
		if(!d1000.equalsIgnoreCase("0"))
			ok1000 = false;
		
		if(ok20 && ok50 && ok100 && ok200 && ok500 && ok1000)
			return true;
		
		return false;
		
	}


}
