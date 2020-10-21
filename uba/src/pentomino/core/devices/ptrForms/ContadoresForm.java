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
import pentomino.flow.CurrentUser;

public class ContadoresForm implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	ImageIcon printImage = new javax.swing.ImageIcon("./a.png");
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		
		if (pageIndex >= 1) 
			return Printable.NO_SUCH_PAGE;
		
		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);
		
		int total = 0;
		int total20 = 0;
		int total50 = 0;
		int total100 = 0;
		int total200 = 0;
		int total500 = 0;
		int total1000 = 0;

		int b20 = Integer.parseInt(Config.GetPersistence("Accepted20", "0"));
		int b50 = Integer.parseInt(Config.GetPersistence("Accepted50", "0"));
		int b100 = Integer.parseInt(Config.GetPersistence("Accepted100", "0"));
		int b200 = Integer.parseInt(Config.GetPersistence("Accepted200", "0"));
		int b500 = Integer.parseInt(Config.GetPersistence("Accepted500", "0"));
		int b1000 = Integer.parseInt(Config.GetPersistence("Accepted1000", "0"));

		total20 = 20 * b20;
		total50 = 50 * b50;
		total100 = 100 * b100;
		total200 = 200 * b200;
		total500 = 500 * b500;
		total1000 = 1000 * b1000;

		total = total20 + total50 + total100 + total200 + total500 + total1000;

		int mIzq = 10;

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		int montoMargin = mIzq + 140;
		int billetesMargin = mIzq + 70;

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("CORTE", 80, 80);
		
		g2d.drawString(Config.GetDirective("ATMMunicipio", "MEXICO") + "   " + Config.GetDirective("ATMEstado", "MEXICO"), mIzq + 5, 100);
		g2d.drawString(Config.GetDirective("ATMPais", "MEXICO"), mIzq + 15, 110);
		
		
		g2d.drawString("FECHA:", mIzq, 130);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 130);

		g2d.drawString("HORA:", mIzq, 140);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 140);

		g2d.drawString("MONTO:", mIzq, 160);
		g2d.drawString(currencyFormat.format(total), mIzq + 60, 160);

		g2d.drawString("PIEZAS", mIzq, 180);
		g2d.drawString("BILLETES", billetesMargin, 180);
		g2d.drawString("MONTO", montoMargin, 180);

		// Piezas
		g2d.drawString(String.format("%1$-5s", b20), mIzq, 190);
		g2d.drawString(String.format("%1$-5s", b50), mIzq, 200);
		g2d.drawString(String.format("%1$-5s", b100), mIzq, 210);
		g2d.drawString(String.format("%1$-5s", b200), mIzq, 220);
		g2d.drawString(String.format("%1$-5s", b500), mIzq, 230);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 190);
		g2d.drawString("$50", billetesMargin + 10, 200);
		g2d.drawString("$100", billetesMargin + 10, 210);
		g2d.drawString("$200", billetesMargin + 10, 220);
		g2d.drawString("$500", billetesMargin + 10, 230);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total20)), montoMargin-10, 190);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total50)), montoMargin-10, 200);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total100)), montoMargin-10, 210);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total200)), montoMargin-10, 220);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total500)), montoMargin-10, 230);

		// Footer
		g2d.drawString("CORTE:", mIzq, 250);
		g2d.drawString("USUARIO:", mIzq, 260);
		g2d.drawString("ATMID:", mIzq, 270);

		g2d.drawString(Config.GetPersistence("CorteCount", "-1"), mIzq + 100, 250);
		g2d.drawString(CurrentUser.loginUser, mIzq + 100, 260);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 270);

		return Printable.PAGE_EXISTS;
	}
}

