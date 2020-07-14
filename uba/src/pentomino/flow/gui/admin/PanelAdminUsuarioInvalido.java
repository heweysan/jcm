

package pentomino.flow.gui.admin;


import pentomino.common.PinpadMode;
import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class PanelAdminUsuarioInvalido  extends ImagePanel {


	private static final long serialVersionUID = 1L;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminUsuarioInvalido(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
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
		
		JButton btnReintentar = new JButton(new ImageIcon("./images/BTN_7p_Admin_Intentar de Nuevo.png"));
		btnReintentar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(CurrentUser.currentOperation == jcmOperation.StoreLogin)
					Flow.redirect(Flow.panelAdminLoginTienda);
				else
					Flow.redirect(Flow.panelAdminLogin);
			}
		});
		btnReintentar.setOpaque(false);
		btnReintentar.setContentAreaFilled(false);
		btnReintentar.setBorderPainted(false);
		btnReintentar.setBounds(570, 860, 779, 151);
		add(btnReintentar);



		CurrentUser.loginUser = "";
		CurrentUser.loginPassword = "";
		CurrentUser.pinpadMode = PinpadMode.loginUser;
		PanelAdminLogin.lblAdminLoginUser.setText("");
		PanelAdminLogin.lblAdminLoginPassword.setText("");

	}

	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelAdminUsuarioInvalido]");

	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelAdminUsuarioInvalido");

	}
}
