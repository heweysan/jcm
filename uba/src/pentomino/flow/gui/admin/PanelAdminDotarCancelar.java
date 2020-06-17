package pentomino.flow.gui.admin;


import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.ImagePanel;

public class PanelAdminDotarCancelar extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminDotarCancelar(String img,String name) {
		super(new ImageIcon(img).getImage(),name);
	
	}


	public PanelAdminDotarCancelar(Image img,String name, int _timeout, String _redirect) {
		super(img,name,_timeout,_redirect);	
	}	



	public PanelAdminDotarCancelar(Image img, String name) {
		super(img,name);		
	}
	

	@Override
	public void ContentPanel() {


		add(new DebugButtons().getPanel());	

		JLabel lblNewLabel = new JLabel("\u00BFEsta seguro que desea cancelar los cambios?");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblNewLabel.setBounds(10, 213, 1900, 84);
		add(lblNewLabel);


		JButton btnSi = new JButton(new ImageIcon("./images/Btn_AdminSi.png"));
		btnSi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);	
			}
		});
		btnSi.setBounds(41, 939, 250, 90);
		btnSi.setContentAreaFilled(false);
		btnSi.setBorderPainted(false);
		btnSi.setOpaque(false);
		btnSi.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnSi);

		JButton btnNo = new JButton(new ImageIcon("./images/Btn_AdminNo.png"));
		btnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminContadoresEnCero);	
			}
		});
		btnNo.setOpaque(false);
		btnNo.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnNo.setContentAreaFilled(false);
		btnNo.setBorderPainted(false);
		btnNo.setBounds(1660, 877, 250, 90);
		add(btnNo);

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
