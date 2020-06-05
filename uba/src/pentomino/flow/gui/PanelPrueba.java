package pentomino.flow.gui;

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import pentomino.flow.Flow;

public class PanelPrueba extends Screen{

	public PanelPrueba(Image img, String name, int _timeout, String _redirect) {
		super(img, name, _timeout, _redirect);
				
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("OnLoad Screen [" + name + "]");
				screenTimer = new Timer();
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						System.out.println("Redireccionado [" + name + "] -> [" + panelRedirect + "]");
						screenTimer.cancel();						
						Flow.redirect(panelRedirect);					
					}
				}, screenTimeOut);		
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("OnUnload  Screen [" + name + "]");
				screenTimer.cancel();
			}
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void content() {
		this.lblLoginUser.setText("Este es un nuevo elemento papaw");
		
		
	}

	
	
}
