package pentomino.flow.gui.admin;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import pentomino.flow.Flow;
import pentomino.flow.gui.helpers.ImagePanel;

public class PanelAdminDotarCancelar extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminDotarCancelar(String img,String name, int _timeout, ImagePanel _redirect) {
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


		//TODO: AQUI JButton btnSi = new JButton(new ImageIcon("./images/BTN7_OK.png"));
		JButton btnSi = new JButton(Flow.botonOk);
		btnSi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);	
			}
		});
		btnSi.setBounds(50, 810, 262, 219);
		btnSi.setContentAreaFilled(false);
		btnSi.setBorderPainted(false);
		btnSi.setOpaque(false);
		btnSi.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnSi);

		JButton btnNo = new JButton(Flow.botonNo);
		btnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminContadoresEnCero);	
			}
		});
		btnNo.setOpaque(false);
		btnNo.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnNo.setContentAreaFilled(false);
		btnNo.setBorderPainted(false);
		btnNo.setBounds(1610, 810, 262, 219);
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
