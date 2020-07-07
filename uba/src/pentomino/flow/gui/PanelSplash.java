package pentomino.flow.gui;

import pentomino.flow.gui.helpers.ImagePanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;

public class PanelSplash  extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelSplash(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	


	@Override
	public void ContentPanel() {
		JLabel lblInicializando = new JLabel("Inicializando....");
		lblInicializando.setHorizontalAlignment(SwingConstants.CENTER);
		lblInicializando.setForeground(Color.WHITE);
		lblInicializando.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblInicializando.setBounds(0, 391, 1920, 175);
		add(lblInicializando);
		
	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad [PanelSplash]");
		
	}

	@Override
	public void OnUnload() {
		//System.out.println("OnUnload PanelSplash");
		
	}
}