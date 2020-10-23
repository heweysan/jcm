package pentomino.core.devices;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;

import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.cashmanagement.vo.MovimientosDelDiaVO;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.core.devices.ptrForms.ContadoresForm;
import pentomino.core.devices.ptrForms.ContadoresFormTest;
import pentomino.core.devices.ptrForms.DepositoForm;
import pentomino.core.devices.ptrForms.DepositoFormTest;
import pentomino.core.devices.ptrForms.MovimientosDelDiaForm;
import pentomino.core.devices.ptrForms.RetiroForm;
import pentomino.core.devices.ptrForms.RetiroFormTest;
import pentomino.jcmagent.RaspiAgent;

public class Ptr{

	private static boolean printing = false;

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());

	private static final String FIFO = "jcmprinter";

	private static CupsClient cupsClient;
	private static int spoolCount = 0;

	/**
	 * Esta variable indica si el satatus de usb de la impreso marco si movio el
	 * motor de impresion. Si no lo movio por el momento asumimos que no pudo
	 * imprimir nada y que se quedo en spool en CUPS. Si CUPS marco error pues este
	 * ya vale gorro. Pero si CUPS dijo que si peude que se quedara en spool y no
	 * imprimiera realmente.
	 * 
	 * Asumimos de incio que no pudo imprimir. Solo si se movio el motor es TRUE
	 */
	private static boolean usbPrintingStatus = false;

	private static int usbTestingCounter = 0;

	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


	

	public static void main(String[] args) {

	}

	public static boolean ptrContadores() {
		
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}
		
		
		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width " + paper.getWidth());
		System.out.println("paper height " + paper.getHeight());

		double width = 4d * 72d;
		double height = 4d * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));

		pf.setPaper(paper);

		pjob.setPrintable(new ContadoresForm(), pf);
		
		PrintingStatus();
		
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}

		return true;
	}
	
	public static boolean ptrDeposito(DepositOpVO depositOpVO) {
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}
		
		
		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width " + paper.getWidth());
		System.out.println("paper height " + paper.getHeight());

		double width = 4d * 72d;
		double height = 4d * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));

		pf.setPaper(paper);

		pjob.setPrintable(new DepositoForm(depositOpVO), pf);

		PrintingStatus();
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}
		return true;
	}
	
	public static boolean ptrRetiro(double montoRetiro, String currentUser) {
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}

		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width " + paper.getWidth());
		System.out.println("paper height " + paper.getHeight());

		double width = 4d * 72d;
		double height = 4d * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));

		pf.setPaper(paper);

		pjob.setPrintable(new RetiroForm(montoRetiro, currentUser), pf);
		
		PrintingStatus();
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}

		return true;
	}
		
	public static boolean ptrContadoresTest() {
		
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}
		
		
		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width " + paper.getWidth());
		System.out.println("paper height " + paper.getHeight());

		double width = 4d * 72d;
		double height = 4d * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));

		pf.setPaper(paper);

		pjob.setPrintable(new ContadoresFormTest(), pf);
		
		PrintingStatus();
		
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}

		return true;
	}
	
	public static boolean ptrDepositoTest() {
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}
		
		
		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width " + paper.getWidth());
		System.out.println("paper height " + paper.getHeight());

		double width = 4d * 72d;
		double height = 4d * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));

		pf.setPaper(paper);

		pjob.setPrintable(new DepositoFormTest(), pf);

		PrintingStatus();
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}
		return true;
	}
	
	public static boolean ptrRetiroTest() {
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}

		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width " + paper.getWidth());
		System.out.println("paper height " + paper.getHeight());

		double width = 4d * 72d;
		double height = 4d * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));

		pf.setPaper(paper);

		pjob.setPrintable(new RetiroFormTest(), pf);
		
		PrintingStatus();
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}

		return true;
	}
			
	public static boolean ptrMovimientosDelDia(MovimientosDelDiaVO movimientosDelDiaVO) {
		
		
		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}
		
		//Calsulamos el tamaño que necesitamos del papel a partir de los depositos
		int extra =  (int) Math.ceil(movimientosDelDiaVO.totalDeposits / 5.0);
		
		extra = extra + (int) Math.ceil(movimientosDelDiaVO.totalWithdrawals / 5.0);
		
		PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
		attr.add(new MediaPrintableArea(0, 0, 4, 4 + extra, MediaPrintableArea.INCH));

		// The area of the printable area
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat pf = pjob.defaultPage();
		Paper paper = pf.getPaper();
		System.out.println("paper width [" + paper.getWidth() + "] height [" + paper.getHeight() + "]");

		

		double width = 4d * 72d;
		double height = (4d + extra) * 72d;
		double margin = 1d * 72d;

		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - (margin * 2), height - (margin * 2));
		
		pf.setPaper(paper);

		pjob.setPrintable(new MovimientosDelDiaForm(movimientosDelDiaVO), pf);

		PrintingStatus();
		
		try {
			pjob.print(attr);			
			
			// Ahora revisamos si la impresora mando el evento de que si pudo imprimir (por
			// el movimiento del motoro de impresion)
			while (printing) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (usbPrintingStatus) {
				System.out.println("USB print Status OK");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, "");
			} else {
				System.out.println("USB print Status FAIL");				
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				return false;
			}
			
			
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
			return false;
		}
		return true;
	}
	
	
	public static void initializeCupsClient() {

		if (JcmGlobalData.isDebug)
			return;

		try {
			System.out.println("Inicializando CUPS client");
			cupsClient = new CupsClient("127.0.0.1", 631);
			URL printerURL = new URL("http://127.0.0.1:631/printers/CUSTOM_SPA_MODUS3");
			cupsClient.getPrinter(printerURL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("[PTR] Inicializando CUPS client EXCEPTION");
			e.printStackTrace();
		}
		System.out.println("[PTR] Inicializando CUPS client END");
	}

	public static void BroadcastFullStatus() {
		
		
		if (JcmGlobalData.isDebug) {
			System.out.println("[PTR] BroadcastFullStatus DEBUG");
			return;
		}

		// Si esta imprimiendo no hacemos el check de status
		if (printing) {
			System.out.println("[PTR] BroadcastFullStatus skip printing.");
			return;
		}

		
		System.out.println("[PTR] BroadcastFullStatus");
		
		boolean existe = false;
		String line;
		
		
		try {

			Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c", "ps -a | grep ReadSingle" });
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
				if (line.contains("ReadSingle")) {
					existe = true;
					break;
				}

			}
		} catch (Exception err) {
			System.out.println(err);
		}

		if (!existe) {
			String command = "./ReadSingleStatus";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(command);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		

		
		
		Timer screenTimerPrinterStatus = new Timer();

		JcmGlobalData.printerStatus = "OK";
		JcmGlobalData.printerReady = true;

		screenTimerPrinterStatus.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					
					BufferedReader in = new BufferedReader(new FileReader(FIFO));
					
					while (in.ready()) {
						
						String ptrStatus = in.readLine();
						System.out.println("ptrStatus [" + ptrStatus + "]");
						logger.info("ptrStatus [" + ptrStatus + "]");

						String[] arrOfStr = ptrStatus.split("\\|"); // Se usa \\ ya que | es un metacaracter en regex
																	// por loq ue hay que escaparlo
						if (arrOfStr[0].toString().equalsIgnoreCase("ERROR")
								|| arrOfStr[0].toString().equalsIgnoreCase("FATAL")) {
							RaspiAgent.Broadcast(DeviceEvent.PTR_Status, "ERROR");
							RaspiAgent.Broadcast(DeviceEvent.PTR_DetailStatus, arrOfStr[1].toString());
							JcmGlobalData.printerStatus = arrOfStr[1].toString();
							JcmGlobalData.printerReady = false;
						} else {
							RaspiAgent.Broadcast(DeviceEvent.PTR_Status, "OK");

							if (arrOfStr[0].toString().equalsIgnoreCase("PAPER")) {

								if (arrOfStr[1].toString().equalsIgnoreCase("No paper")) {
									System.out.println("NO PAPER");
									JcmGlobalData.printerReady = false;
									JcmGlobalData.printerStatus = " SIN PAPEL";
									RaspiAgent.Broadcast(DeviceEvent.PTR_PaperThreshold, "PaperOut");
									RaspiAgent.Broadcast(DeviceEvent.PTR_FullStatus, "EMPTY");
								}
								if (arrOfStr[1].toString().equalsIgnoreCase("Near paper end")) {
									System.out.println("BAJO NIVEL DE PAPEL");
									JcmGlobalData.printerStatus += " BAJO NIVEL DE PAPEL";
									RaspiAgent.Broadcast(DeviceEvent.PTR_PaperThreshold, "PaperLow");
									RaspiAgent.Broadcast(DeviceEvent.PTR_FullStatus, "LOW");									
								}
							}
							else
								RaspiAgent.Broadcast(DeviceEvent.PTR_FullStatus, "OK");
							
							if (arrOfStr[0].toString().equalsIgnoreCase("OFFLINE")) {
								System.out.println("OFFLINE");
								JcmGlobalData.printerStatus += " FUERA DE LINEA";
								JcmGlobalData.printerReady = false;
							}
							if (arrOfStr[0].toString().equalsIgnoreCase("STATUS")) {
								RaspiAgent.Broadcast(DeviceEvent.PTR_DetailStatus, arrOfStr[1]);
							}
						}
					}

					in.close();
					screenTimerPrinterStatus.cancel();
					
				} catch (IOException ex) {
					logger.error(ex);
					System.err.println("IO Exception at buffered read!!");
					System.out.println("IO EXCEPTION " + ex.getMessage());
				}
			}
		}, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));

	}

	/**
	 * Este metodo revisa el estatus de la impresora mientras se esta imprimiendo.
	 * La idea es que tome todos los mensajes hasta que llegue el de motor. El de
	 * motor ya que es el unico estatus que tengo ahorita para ver que si esta
	 * imprimiendo algo.
	 */
	public static void PrintingStatus() {

		System.out.println("PTR PrintingStatus");
		printing = true;
		usbPrintingStatus = false;
		usbTestingCounter = 0;

		boolean existe = false;
		String line;
		try {

			Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c", "ps -a | grep ReadPrinting" });
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {

				if (line.contains("ReadPrinting")) {
					existe = true;
					break;
				}
			}
		} catch (Exception err) {
			System.out.println(err);
		}

		if (!existe) {
			String command = "./ReadPrintingStatus";
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(command);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		spoolCount = 0;

		Timer screenTimerDispense = new Timer();

		screenTimerDispense.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					if (++usbTestingCounter >= 10) {
						printing = false;
						screenTimerDispense.cancel();
					}

					BufferedReader in = new BufferedReader(new FileReader(FIFO));

					while (in.ready()) {

						String ptrStatus = in.readLine();
						System.out.println(ptrStatus);
						String[] arrOfStr = ptrStatus.split("\\|"); // Se usa \\ ya que | es un metacaracter en regex
																	// por loq ue hay que escaparlo
						if (arrOfStr[0].equalsIgnoreCase("ERROR") || arrOfStr[0].equalsIgnoreCase("FATAL")) {
							System.out.println("PrintingStatus ERROR");
							RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, arrOfStr[1].toString());
							printing = false;
							screenTimerDispense.cancel();
							break;
						} else {
							if (arrOfStr[0].equalsIgnoreCase("STATUS")) {
								// Lo asumo como que esta bien ya que esta moviendo el motor, ergo hay papel y
								// todo el show
								if (arrOfStr[1].equalsIgnoreCase("Drag paper motor on")) {
									System.out.println("PrintingStatus OK");
									usbPrintingStatus = true;
									printing = false;
									screenTimerDispense.cancel();
									break;
								}
								if (arrOfStr[1].equalsIgnoreCase("spooling")) {
									System.out.println("spooling");
									if (++spoolCount >= 4) {
										usbPrintingStatus = false;
										printing = false;
										screenTimerDispense.cancel();
										break;
									}
								}
							}
						}

					}
					in.close();
				} catch (IOException ex) {
					System.err.println("IO Exception at buffered read!!");
				}
			}
		}, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1));

	}

}













