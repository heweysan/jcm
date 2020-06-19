package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.config.Config;

public class PanelError extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	public static JLabel lblPanelError = new JLabel("PANEL ERROR");
	
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelError(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	


	@Override
	public void ContentPanel() {

		lblPanelError.setHorizontalAlignment(SwingConstants.CENTER);
		lblPanelError.setFont(new Font("Tahoma", Font.PLAIN, 50));
		lblPanelError.setForeground(Color.WHITE);
		lblPanelError.setBounds(10, 500, 1900, 103);
		add(lblPanelError);
	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelError");
		

	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelError");
		Config.SetPersistence("BoardStatus", "Available");
	}

}
