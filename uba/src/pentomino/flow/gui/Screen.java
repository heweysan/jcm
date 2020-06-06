package pentomino.flow.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.flow.Flow;

abstract class Screen  extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	abstract public void content();
	
	private Image img;
	public int screenTimeOut = 15000;
	public String panelRedirect = "";
	private String _name;
	Timer screenTimer = new Timer();
		
	final JLabel lblLoginUser = new JLabel("DEBE SALIR EN TODOS LO LADOS");
	
	/**
	 * @wbp.parser.constructor
	 */
	public Screen(String img,String name) {
		this(new ImageIcon(img).getImage(),name);
	}

	public Screen(Image img,String name, int _timeout, String _redirect) {
		this(new ImageIcon(img).getImage(),name);
		this.screenTimeOut = _timeout;
		this.panelRedirect = _redirect;
	}
	
	
	public Screen(Image img, String name) {
		
		this.add(new DebugButtons().getPanel());
		
		content();
		
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("OnLoad Screen [" + name + "]");
				screenTimer = new Timer();
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						System.out.println("OnLoad Screen Redireccionado [" + name + "] -> [" + panelRedirect + "]");
						screenTimer.cancel();						
						Flow.redirect(panelRedirect);					
					}
				}, screenTimeOut);		
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("OnUnload Screen [" + name + "]");
				screenTimer.cancel();
			}
		});
		
		this.img = img;
		this.setName(name);
		_name = name;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}
	
	

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	// Metodo donde le pasaremos la direcci�n de la imagen a cargar.
	public void setBackground(String imagePath) {

		// Construimos la imagen y se la asignamos al atributo background.
		this.setOpaque(false);
		this.img = new ImageIcon(imagePath).getImage();
		repaint();
	}
	
	public void screenTimerReset(int timeOut, String redirect) {
		
		if(!redirect.isEmpty())
			panelRedirect = redirect;
		
		screenTimeOut = timeOut;
		
		screenTimer.cancel();
		screenTimer = new Timer();
		screenTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Redireccionado [" + _name + "] -> [" + panelRedirect + "]");
				screenTimer.cancel();						
				Flow.redirect(panelRedirect);					
			}
		}, screenTimeOut);	
	}
	
	public void screenTimerCancel() {		
		screenTimer.cancel();		
	}

}
