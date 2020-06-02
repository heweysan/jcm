package pentomino.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import pentomino.flow.Flow;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

public class ImagePanel extends JPanel{


	private static final long serialVersionUID = 1L;
	private Image img;
	public int screenTimeOut = 15000;
	public String panelRedirect = "";
	Timer screenTimer = new Timer();
	
	/**
	 * @wbp.parser.constructor
	 */
	public ImagePanel(String img,String name) {
		this(new ImageIcon(img).getImage(),name);
	}

	public ImagePanel(Image img, String name) {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				System.out.println("componentShown ImagePanel [" + name + "]");
				System.out.println("Entre a [" + name + "]");
				screenTimer = new Timer();
				screenTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						System.out.println("AQUI  [" + name + "] me voy a [" + panelRedirect + "]");
						screenTimer.cancel();						
						Flow.redirect(panelRedirect);						
					}
				}, screenTimeOut);		
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("componentHidden ImagePanel  [" + name + "]");
				screenTimer.cancel();
			}
		});
		
		this.img = img;
		this.setName(name);
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

	// Metodo donde le pasaremos la dirección de la imagen a cargar.
	public void setBackground(String imagePath) {

		// Construimos la imagen y se la asignamos al atributo background.
		this.setOpaque(false);
		this.img = new ImageIcon(imagePath).getImage();
		repaint();
	}

}


