package pentomino.flow.gui.admin;


import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.common.PinpadMode;
import pentomino.flow.CurrentUser;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.ImagePanel;

public class PanelAdminError  extends ImagePanel {


	public static JLabel lblSubMensaje = new JLabel("");

	private static final long serialVersionUID = 1L;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminError(String img,String name) {
		super(new ImageIcon(img).getImage(),name);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
		
	}

	public PanelAdminError(Image img,String name, int _timeout, String _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
		
	}	

	public PanelAdminError(Image img, String name) {
		super(img,name);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}
	

	@Override
	public void ContentPanel() {

		
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	

		add(new DebugButtons().getPanel());


		JLabel lblMensaje = new JLabel("Error al autenticar al usuario, vuelve a intentarlo.");
		lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblMensaje.setBounds(10, 213, 1900, 76);
		add(lblMensaje);


		lblSubMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubMensaje.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblSubMensaje.setBounds(10, 375, 1900, 76);
		add(lblSubMensaje);


		CurrentUser.loginUser = "";
		CurrentUser.loginPassword = "";
		CurrentUser.pinpadMode = PinpadMode.loginUser;
		PanelAdminLogin.lblLoginUser.setText("");
		PanelAdminLogin.lblLoginPassword.setText("");

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
