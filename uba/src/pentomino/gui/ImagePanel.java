package pentomino.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	
	private static final long serialVersionUID = 1L;
	private Image img;

	  public ImagePanel(String img,String name) {
	    this(new ImageIcon(img).getImage(),name);
	  }

	  public ImagePanel(Image img, String name) {
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

}


