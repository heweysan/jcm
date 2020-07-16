package pentomino.flow.gui.admin;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import pentomino.common.JcmGlobalData;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImageButton;
import pentomino.flow.gui.helpers.ImagePanel;

import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelAdminDetalleError extends ImagePanel {

	
	
	
	public static JLabel lblDetalleError= new JLabel("");

	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminDetalleError(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	
	}	
	
	
	public PanelAdminDetalleError(ImageIcon bgPlaceHolder, String name, long _timeout, ImagePanel _redirect) {
		super(bgPlaceHolder, name, _timeout, _redirect);
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
		
		
		lblDetalleError.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblDetalleError.setHorizontalAlignment(SwingConstants.CENTER);
		lblDetalleError.setForeground(Color.WHITE);
		lblDetalleError.setBounds(657, 313, 896, 92);
		add(lblDetalleError);
		
		ImageButton btnRegresar = new ImageButton("./images/BTN_7p_Admin_Regresar.png");
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminEstatusDispositivos);
			}
		});
		btnRegresar.setLocation(1305, 880);
		btnRegresar.setSize(574, 171);
		add(btnRegresar);
	}

	@Override
	public void OnLoad() {
		if(JcmGlobalData.printerReady) {
			lblDetalleError.setText("No hay errores que mostrar.");
		}
		else {
			lblDetalleError.setText(JcmGlobalData.printerStatus);
		}
			
	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		
	}
}
