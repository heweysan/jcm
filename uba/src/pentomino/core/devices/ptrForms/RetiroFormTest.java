package pentomino.core.devices.ptrForms;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;

import pentomino.common.JcmGlobalData;
import pentomino.config.Config;

public class RetiroFormTest implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	ImageIcon printImage = new javax.swing.ImageIcon("./a.png");
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		if (pageIndex >= 1) {
			return Printable.NO_SUCH_PAGE;
		}

		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);
		
		System.out.println("Ptr.RetiroPageTest");

		int mIzq = 10;

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("RETIRO", 80, 80);
		
		g2d.drawString(Config.GetDirective("ATMMunicipio", "MEXICO") + "   " + Config.GetDirective("ATMEstado", "MEXICO"), mIzq + 5, 100);
		g2d.drawString(Config.GetDirective("ATMPais", "MEXICO"), mIzq + 15, 110);

		g2d.drawString("FECHA:", mIzq, 130);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 130);

		g2d.drawString("HORA:", mIzq, 140);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 140);

		g2d.drawString("MONTO:", mIzq, 160);
		g2d.drawString(currencyFormat.format(0), mIzq + 60, 160);


		// Footer
		g2d.drawString("OPERACION:", mIzq, 250);
		g2d.drawString("REFERENCIA:", mIzq, 260);
		g2d.drawString("USUARIO:", mIzq, 270);
		g2d.drawString("ATMID:", mIzq, 280);

		g2d.drawString("-----", mIzq + 100, 250);
		g2d.drawString("-----", mIzq + 100, 260);
		g2d.drawString("-----", mIzq + 100, 270);		
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 280);

		return Printable.PAGE_EXISTS;
	}
}
