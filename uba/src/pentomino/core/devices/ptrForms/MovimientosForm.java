package pentomino.core.devices.ptrForms;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import pentomino.cashmanagement.DepositoDelDia;
import pentomino.common.JcmGlobalData;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;

public class MovimientosForm implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	List<DepositoDelDia >datosL;
	
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

		g2d.drawString("CASH MANAGEMENT", 80, 80);
		g2d.drawString("DEPÓSITOS DEL DÍA", 70, 90);
		
				
		g2d.drawString(Config.GetDirective("ATMMunicipio", "MEXICO") + "   " + Config.GetDirective("ATMEstado", "MEXICO"), mIzq + 5, 110);
		g2d.drawString(Config.GetDirective("ATMPais", "MEXICO"), mIzq + 15, 120);
		
		
		g2d.drawString("ATMID:", mIzq, 140);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 140);
		
		g2d.drawString("FECHA:", mIzq, 150);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 150);

		g2d.drawString("HORA:", mIzq, 160);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 160);

		g2d.drawString("USUARIO:", mIzq, 180);
		g2d.drawString(CurrentUser.loginUser, mIzq + 100, 180);
		
		g2d.drawString("RESUME DEL DÍA::", mIzq, 200);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 120, 200);
		
		
		g2d.drawString("CANTIDAD", billetesMargin, 220);
		g2d.drawString("MONTO", montoMargin, 220);
		
		
		g2d.drawString("TOTAL:", mIzq, 230);						
		g2d.drawString(currencyFormat.format(total), billetesMargin, 230);
		g2d.drawString(currencyFormat.format(total), montoMargin, 230);

		
		
		g2d.drawString("*Es posible que no se vean reflejados los depósitos offline", mIzq, 250);
		
		
		g2d.drawString("Hora", mIzq, 270);
		g2d.drawString("Usuario", billetesMargin, 270);
		g2d.drawString("Monto", montoMargin, 270);

		int datosY = 280;
		
		for (DepositoDelDia entry : datosL) {
			g2d.drawString(entry.date,mIzq,datosY);
			g2d.drawString(entry.user,billetesMargin,datosY);
			g2d.drawString(currencyFormat.format(entry.monto),montoMargin,datosY);
			datosY = datosY + 10;
		}

		

		return Printable.PAGE_EXISTS;
	}
}


