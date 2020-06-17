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
	public String panelRedirect = "";
	private String _name;
	Timer screenTimer = new Timer();
	
	/**
	 * @wbp.parser.constructor
	 */
	public ImagePanel(String img,String name) {
		this(new ImageIcon(img).getImage(),name);
		System.out.println("Constructor 1");
		

	}

	public ImagePanel(Image img,String name, int _timeout, String _redirect) {
		this(new ImageIcon(img).getImage(),name);
		this.screenTimeOut = _timeout;
		this.panelRedirect = _redirect;
		System.out.println("Constructor 2");
	}	
	
	public ImagePanel(Image img, String name) {
		System.out.println("Constructor 3");

		ContentPanel();
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("componentShown ImagePanel del abrstarct [" + name + "]");
				screenTimer = new Timer();
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						System.out.println("Redireccionado [" + name + "] -> [" + panelRedirect + "]");
						screenTimer.cancel();						
						Flow.redirect(panelRedirect);					
					}
				}, screenTimeOut);	
				OnLoad();
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("componentHidden OnUnload del abstract [" + name + "]");
				screenTimer.cancel();
				OnUnload();
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
	
	public abstract void ContentPanel();
	
	public abstract void OnLoad();
	
	public abstract void OnUnload();

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


