package pentomino.flow.gui;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;

public class PanelReinicio extends ImagePanel {

	public PanelReinicio(String img, String name, int _timeout, ImagePanel _redirect) {
		super(img, name, _timeout, _redirect);
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
		JLabel lblNewLabel = new JLabel("REINICIANDO ATM...");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(10, 499, 1900, 146);
		add(lblNewLabel);
		
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
