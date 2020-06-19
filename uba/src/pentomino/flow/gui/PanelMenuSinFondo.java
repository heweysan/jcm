package pentomino.flow.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import pentomino.common.jcmOperation;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;


public class PanelMenuSinFondo extends ImagePanel {

	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelMenuSinFondo(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void ContentPanel() {
		JButton btnAceptar = new JButton(new ImageIcon("./images/BTN7Aceptar.png"));
		btnAceptar.setBounds(547, 757, 782, 159);
		btnAceptar.setContentAreaFilled(false);
		btnAceptar.setBorderPainted(false);
		btnAceptar.setOpaque(false);
		btnAceptar.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnAceptar);

		add(new DebugButtons().getPanel());	

		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CurrentUser.currentOperation = jcmOperation.Deposit;
				Flow.redirect(Flow.panelLogin);
			}
		});		

	}

	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelDepositoSinFondo]");
		

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload [PanelDepositoSinFondo]");
	}

}
