package pentomino.core.devices.ptrForms;

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

	public MovimientosDelDiaForm(MovimientosDelDiaVO movimientosDelDiaVO) {
		mddVO = movimientosDelDiaVO;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		
		System.out.println("pageIndex [" + pageIndex + "]");
		
		if (pageIndex >= 1) 
			return Printable.NO_SUCH_PAGE;
		
		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);

		int mIzq = 10;
		

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		int montoMargin = mIzq + 140;
		int billetesMargin = mIzq + 70;

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("CASH MANAGEMENT", 80, 80);
		g2d.drawString("MOVIMIETNTOS DEL DÍA", 70, 90);
		
				
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
		g2d.drawString(String.valueOf(mddVO.totalDeposits), billetesMargin, 230);
		g2d.drawString(currencyFormat.format(mddVO.depositsAmount), montoMargin, 230);

		
		g2d.drawString("*Es posible que no se vean reflejados los depósitos offline", mIzq, 250);
		
		
		g2d.drawString("Hora", mIzq, 270);
		g2d.drawString("Usuario", billetesMargin, 270);
		g2d.drawString("Monto", montoMargin, 270);

		int datosY = 280;
		
		for (DepositoDelDia entry : mddVO.depositsDetail) {
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


