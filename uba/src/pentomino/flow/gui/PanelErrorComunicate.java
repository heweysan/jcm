package pentomino.flow.gui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import pentomino.flow.Flow;

public class PanelErrorComunicate  extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**
	 * @wbp.parser.constructor
	 */
	public PanelErrorComunicate(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);
	}	


	@Override
	public void ContentPanel() {
	 
	}


	@Override
	public void OnLoad() {
		System.out.println("OnLoad PanelErrorComunicate");
		Timer screenTimerNetwork = new Timer();
		screenTimerNetwork.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {				
				if(netIsAvailable()) {
					Flow.redirect(Flow.panelIdle);
					screenTimerNetwork.cancel();
				}
			}
		}, 1000,60000);
		
	}

private static boolean netIsAvailable() {    
		
	System.out.println("netIsAvailable");
		try {			
			Socket socket2 = new Socket();
			socket2.connect(new InetSocketAddress("11.50.0.7", 5672), 5000);		
			socket2.close();
			System.out.println("netIsAvailable true");
			return true;
		} catch (UnknownHostException e) {		
		} catch (IOException e) {		
		}
		System.out.println("netIsAvailable false");
		return false;
	}

	@Override
	public void OnUnload() {
		System.out.println("OnUnload PanelErrorComunicate");
		
	}
}
