package pentomino.flow.gui.admin;



	import java.awt.Font;

	import javax.swing.ImageIcon;
	import javax.swing.JButton;
	import javax.swing.JLabel;
	import javax.swing.JPanel;

	import pentomino.config.Config;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
	import javax.swing.Icon;
	import java.awt.event.ActionListener;
	import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

	public class PanelAdminDotarResultados {
		
		public JPanel contentPanel;
		
		public PanelAdminDotarResultados() {
			contentPanel = new JPanel();
			contentPanel.setBounds(0, 0, 1920, 1080);
			contentPanel.setOpaque(false);
			contentPanel.setBorder(null);
			contentPanel.setLayout(null);	
			
			contentPanel.add(new DebugButtons().getPanel());	
			
			
			
			
			JLabel lblMensaje = new JLabel("Se present\u00F3 un error al modificar los contadores");
			lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
			lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 40));
			lblMensaje.setBounds(10, 213, 1900, 84);
			contentPanel.add(lblMensaje);
			
			
			JButton btnImprimirContadores = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminImpContadores.png"));
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
			
			JButton btnRegresar = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminRegresar.png"));
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
		
		
		}
