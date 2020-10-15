package pentomino.flow.gui;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingConstants;

import pentomino.flow.EventListenerClass;
import pentomino.flow.MyEvent;
import pentomino.flow.gui.helpers.ImagePanel;

public class PanelReinicio extends ImagePanel {

	public PanelReinicio(ImageIcon bgPlaceHolder, String name, int _timeout, ImagePanel _redirect) {
		super(bgPlaceHolder, name, _timeout, _redirect);
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
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(10, 499, 1900, 146);
		add(lblNewLabel);
		
	}

	@Override
	public void OnLoad() {
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(15));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EventListenerClass.fireMyEvent(new MyEvent("reboot"));
	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		
	}

}
