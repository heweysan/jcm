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

public class DepositoFormTest implements Printable {

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
		
		System.out.println("Ptr.DepositoPageTest");

		currencyFormat.setMaximumFractionDigits(0);



		int mIzq = 10;

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		int montoMargin = mIzq + 140;
		int billetesMargin = mIzq + 70;

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("DEPOSITO", 80, 80);

		
		g2d.drawString(Config.GetDirective("ATMMunicipio", "MEXICO") + "   " + Config.GetDirective("ATMEstado", "MEXICO"), mIzq + 5, 100);
		g2d.drawString(Config.GetDirective("ATMPais", "MEXICO"), mIzq + 15, 110);
		
		
		g2d.drawString("FECHA:", mIzq, 130);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 130);

		g2d.drawString("HORA:", mIzq, 140);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 140);

		g2d.drawString("MONTO:", mIzq, 160);
		g2d.drawString(currencyFormat.format(0), mIzq + 60, 160);

		g2d.drawString("PIEZAS", mIzq, 180);
		g2d.drawString("BILLETES", billetesMargin, 180);
		g2d.drawString("MONTO", montoMargin, 180);

		// Piezas
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 190);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 200);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 210);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 220);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 230);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 190);
		g2d.drawString("$50", billetesMargin + 10, 200);
		g2d.drawString("$100", billetesMargin + 10, 210);
		g2d.drawString("$200", billetesMargin + 10, 220);
		g2d.drawString("$500", billetesMargin + 10, 230);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin-10, 190);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin-10, 200);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin-10, 210);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin-10, 220);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin-10, 230);

		// Footer
		g2d.drawString("OPERACION:", mIzq, 250);
		g2d.drawString("REFERENCIA:", mIzq, 250);
		g2d.drawString("USUARIO:", mIzq, 270);
		g2d.drawString("ATMID:", mIzq, 280);

		g2d.drawString("-----", mIzq + 100, 250);
		g2d.drawString("-----", mIzq + 100, 260);
		g2d.drawString("-----", mIzq + 100, 270);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 280);
			

		return Printable.PAGE_EXISTS;
	}
}