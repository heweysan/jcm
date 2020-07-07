package pentomino.flow.gui.admin;

import javax.swing.JLabel;

import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImageButton;
import pentomino.flow.gui.helpers.ImagePanel;

import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelAdminDetalleError extends ImagePanel {

	
	public static JLabel lblDetalleError = new JLabel("");
	
	public PanelAdminDetalleError(String img, String name, long _timeout, ImagePanel _redirect) {
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
		
		JLabel lblNewLabel = new JLabel("No hay errores que mostrar");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(657, 313, 734, 92);
		add(lblNewLabel);
		
		ImageButton btnRegresar = new ImageButton("./images/BTN_7p_Admin_Regresar.png");
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminEstatusDispositivos);
			}
		});
		btnRegresar.setLocation(1323, 850);
		btnRegresar.setSize(574, 171);
		add(btnRegresar);
	}

	@Override
	public void OnLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		
	}
}
