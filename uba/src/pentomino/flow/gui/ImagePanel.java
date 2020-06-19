package pentomino.flow.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import pentomino.flow.Flow;

public  abstract class ImagePanel extends JPanel {




	private static final long serialVersionUID = 1L;
	private Image img;
	public int screenTimeOut = 15000;
	public ImagePanel panelRedirect = null;
	private String _name;
	Timer screenTimer = new Timer();
	
	/**
	 * @wbp.parser.constructor
	 */
	public ImagePanel(String img,String name, int _timeout, ImagePanel _redirect) {
		
	
		this.screenTimeOut = _timeout;
		this.panelRedirect = _redirect;

		add(new DebugButtons().getPanel());
		
		ContentPanel();
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				
				OnLoad();
				if(screenTimeOut == 0 || panelRedirect == null)
					return;
				
				//System.out.println("componentShown [" + name + "]");
				screenTimer = new Timer();
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						//System.out.println("Redireccionado [" + name + "] -> [" + panelRedirect.getName() + "]");
						screenTimer.cancel();						
						Flow.redirect(panelRedirect);					
					}
				}, screenTimeOut);	
				
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				OnUnload();
				//System.out.println("componentHidden [" + name + "]");
				screenTimer.cancel();
				
			}
		});
		
		
		this.img = new ImageIcon(img).getImage();
		this.setName(name);
		_name = name;
		Dimension size = new Dimension(this.img.getWidth(null), this.img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
		
		
	}

	
	public abstract void ContentPanel();
	
	public abstract void OnLoad();
	
	public abstract void OnUnload();

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	// Metodo donde le pasaremos la dirección de la imagen a cargar.
	public void setBackground(String imagePath) {

		// Construimos la imagen y se la asignamos al atributo background.
		this.setOpaque(false);
		this.img = new ImageIcon(imagePath).getImage();
		repaint();
	}
	
	public void screenTimerReset(int timeOut, ImagePanel redirect) {
		
		if(timeOut == 0 || redirect == null)
			return;
			
			
		panelRedirect = redirect;
		
		screenTimeOut = timeOut;
		
		screenTimer.cancel();
		screenTimer = new Timer();
		screenTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//System.out.println("Redireccionado [" + _name + "] -> [" + panelRedirect.getName() + "]");
				screenTimer.cancel();						
				Flow.redirect(panelRedirect);					
			}
		}, screenTimeOut);	
	}
	
	public void screenTimerCancel() {
		
		screenTimer.cancel();
		
	}
	




}


