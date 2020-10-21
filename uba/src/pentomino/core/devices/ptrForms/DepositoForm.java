package pentomino.core.devices.ptrForms;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;

import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.JcmGlobalData;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;

public class DepositoForm implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	ImageIcon printImage = new javax.swing.ImageIcon("./a.png");
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    DepositOpVO depositOpVO;	
	
	public DepositoForm(DepositOpVO depositOpVO) {
		this.depositOpVO = depositOpVO;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		
		System.out.println("Ptr.DepositoForm");
		
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

		g2d.drawString("DEPOSITO", 80, 80);

		
		g2d.drawString(Config.GetDirective("ATMMunicipio", "MEXICO") + "   " + Config.GetDirective("ATMEstado", "MEXICO"), mIzq + 5 , 100);
		g2d.drawString(Config.GetDirective("ATMPais", "MEXICO"), mIzq + 15, 110);
		
		
		g2d.drawString("FECHA:", mIzq, 130);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 130);

		g2d.drawString("HORA:", mIzq, 140);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 140);

		g2d.drawString("MONTO:", mIzq, 160);
		g2d.drawString(currencyFormat.format(depositOpVO.amount), mIzq + 60, 160);

		g2d.drawString("PIEZAS", mIzq, 180);
		g2d.drawString("BILLETES", billetesMargin, 180);
		g2d.drawString("MONTO", montoMargin, 180);

		// Piezas
		g2d.drawString(String.format("%1$-5s", depositOpVO.b20), mIzq, 190);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b50), mIzq, 200);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b100), mIzq, 210);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b200), mIzq, 220);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b500), mIzq, 230);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 190);
		g2d.drawString("$50", billetesMargin + 10, 200);
		g2d.drawString("$100", billetesMargin + 10, 210);
		g2d.drawString("$200", billetesMargin + 10, 220);
		g2d.drawString("$500", billetesMargin + 10, 230);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b20 * 20)), montoMargin-10, 190);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b50 * 50)), montoMargin-10, 200);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b100 * 100)), montoMargin-10, 210);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b200 * 200)), montoMargin-10, 220);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b500 * 500)), montoMargin-10, 230);

		// Footer
		g2d.drawString("OPERACION:", mIzq, 250);
		g2d.drawString("REFERENCIA:", mIzq, 260);
		g2d.drawString("USUARIO:", mIzq, 270);
		g2d.drawString("ATMID:", mIzq, 280);

		g2d.drawString(Config.GetPersistence("TxCASHMANAGEMENTCounter", "0"), mIzq + 100, 250);
		g2d.drawString(CurrentUser.movementId, mIzq + 100, 260);
		g2d.drawString(depositOpVO.userName, mIzq + 100, 270);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 280);
		

		return Printable.PAGE_EXISTS;
	}
}