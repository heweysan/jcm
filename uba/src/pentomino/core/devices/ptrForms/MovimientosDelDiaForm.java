package pentomino.core.devices.ptrForms;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;

import pentomino.cashmanagement.vo.DepositoDelDia;
import pentomino.cashmanagement.vo.MovimientosDelDiaVO;
import pentomino.common.JcmGlobalData;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;

public class MovimientosDelDiaForm implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	MovimientosDelDiaVO mddVO;
	
	ImageIcon printImage = new javax.swing.ImageIcon("./a.png");
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

	static int mIzq = 10;
	static int montoMargin = mIzq + 140;
	static int billetesMargin = mIzq + 70;
	static Font formFont  = new Font("Arial", Font.PLAIN, 14);
	static int renglon;
	static int renglon2;
	
	public MovimientosDelDiaForm(MovimientosDelDiaVO movimientosDelDiaVO) {
		renglon = 10;
		renglon2 = renglon * 2;
		mddVO = movimientosDelDiaVO;		
	}

	private static void drawCenteredText(String text, int posY,Graphics2D g2d,PageFormat pageFormat) {
		 
		// Get the FontMetrics
	    FontMetrics metrics = g2d.getFontMetrics(formFont);
		// Determine the X coordinate for the text
	    int x = (int) ((pageFormat.getImageableWidth() * 72 - metrics.stringWidth(text)) / 2);
		g2d.drawString(text, x, posY);
	}
	
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		
		System.out.println("pageIndex [" + pageIndex + "]");
		
		if (pageIndex >= 1) 
			return Printable.NO_SUCH_PAGE;
		
		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);

				
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
		g2d.setFont(formFont);
		
		int datosY = 80;
		
		g2d.drawImage(printImage.getImage(), 80, 0, null);

		drawCenteredText("CASH MANAGEMENT", datosY, g2d,pageFormat);
		drawCenteredText("MOVIMIETNTOS DEL DÍA", datosY+=renglon, g2d,pageFormat);
		
		
		g2d.drawString(Config.GetDirective("ATMMunicipio", "MEXICO") + "   " + Config.GetDirective("ATMEstado", "MEXICO"), mIzq + 20, datosY+=renglon2);
		drawCenteredText(Config.GetDirective("ATMPais", "MEXICO"), datosY+=renglon, g2d,pageFormat);
						
		g2d.drawString("ATMID:", mIzq, datosY+=renglon2);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, datosY);
		
		g2d.drawString("FECHA:", mIzq, datosY+=renglon);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, datosY);

		g2d.drawString("HORA:", mIzq, datosY+=renglon);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, datosY);

		g2d.drawString("USUARIO:", mIzq, datosY+=renglon2);
		g2d.drawString(CurrentUser.loginUser, mIzq + 100, datosY);
		
		g2d.drawString("RESUME DEL DÍA:", mIzq, datosY+=renglon2);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 120, datosY);
		
		drawCenteredText("DEPÓSITOS", datosY+=renglon2, g2d,pageFormat);
		
		g2d.drawString("CANTIDAD", billetesMargin, datosY+=renglon2);
		g2d.drawString("MONTO", montoMargin, datosY);
				
		g2d.drawString("TOTAL:", mIzq, datosY+=renglon);						
		g2d.drawString(String.valueOf(mddVO.totalDeposits), billetesMargin, datosY);
		g2d.drawString(currencyFormat.format(mddVO.depositsAmount), montoMargin, datosY);

		g2d.setFont(new Font("Arial", Font.PLAIN, 8));
		g2d.drawString("*Es posible que no se vean reflejados los depósitos offline", mIzq, datosY+=renglon2);
		g2d.setFont(new Font("Arial", Font.PLAIN, 14));
		
		g2d.drawString("Hora", mIzq, datosY+=renglon2);
		g2d.drawString("Usuario", billetesMargin, datosY);
		g2d.drawString("Monto", montoMargin, datosY);

		datosY+=10;
		
		for (DepositoDelDia entry : mddVO.depositsDetail) {
			Date operationDate = new Date(entry.datetime);

			g2d.drawString(timeFormat.format(operationDate),mIzq,datosY);
			g2d.drawString(entry.cashier,billetesMargin,datosY);
			g2d.drawString(currencyFormat.format(entry.amount),montoMargin,datosY);
			datosY = datosY + 10;
			
			System.out.println("hora: " + timeFormat.format(operationDate) + " operador: " + entry.cashier + " monto: " + currencyFormat.format(entry.amount));
		}

		drawCenteredText("RETIROS", datosY+=renglon , g2d,pageFormat);
		
		datosY+=20;
		
		g2d.drawString("Hora", mIzq, datosY);
		g2d.drawString("Usuario", billetesMargin, datosY);
		g2d.drawString("Monto", montoMargin, datosY);

		datosY+=10;
		for (DepositoDelDia entry : mddVO.withdrawalsDetail) {
			Date operationDate = new Date(entry.datetime);

			g2d.drawString(timeFormat.format(operationDate),mIzq,datosY);
			g2d.drawString(entry.cashier,billetesMargin,datosY);
			g2d.drawString(currencyFormat.format(entry.amount),montoMargin,datosY);
			datosY = datosY + 10;
			
			System.out.println("hora: " + timeFormat.format(operationDate) + " operador: " + entry.cashier + " monto: " + currencyFormat.format(entry.amount));
		}

		return Printable.PAGE_EXISTS;
	}
}


