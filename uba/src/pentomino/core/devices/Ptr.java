package pentomino.core.devices;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.JobStateEnum;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import pentomino.cashmanagement.vo.DepositOpVO;
import pentomino.common.DeviceEvent;
import pentomino.common.JcmGlobalData;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.jcmagent.RaspiAgent;

public class Ptr {

	private static boolean printing = false;

	private static final Logger logger = LogManager.getLogger(Ptr.class.getName());

	private static final String FIFO = "jcmprinter";

	private static CupsClient cupsClient;
	private static CupsPrinter cupsPrinter;

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

		pjob.setPrintable(new ContadoresPage(), pf);
		try {
			pjob.print(attr);
		} catch (PrinterException e) {
			System.out.println("PTR EXCEPTION [" + e.getMessage() + "]");
		}

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

		pjob.setPrintable(new ContadoresPage(), pf);
		
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

		pjob.setPrintable(new DepositoPage(depositOpVO), pf);

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

		pjob.setPrintable(new RetiroPage(montoRetiro, currentUser), pf);
		
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

		pjob.setPrintable(new ContadoresPageTest(), pf);
		
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

		pjob.setPrintable(new DepositoPageTest(), pf);

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

		pjob.setPrintable(new RetiroPageTest(), pf);
		
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
			cupsPrinter = cupsClient.getPrinter(printerURL);

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
								}
								if (arrOfStr[1].toString().equalsIgnoreCase("Near paper end")) {
									System.out.println("BAJO NIVEL DE PAPEL");
									JcmGlobalData.printerStatus += " BAJO NIVEL DE PAPEL";
									RaspiAgent.Broadcast(DeviceEvent.PTR_PaperThreshold, "PaperLow");
								}
							}
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

	public static boolean printDeposit(DepositOpVO depositOpVO) {

		System.out.println("Ptr.printDeposit");

		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);

		Map<String, String> printMap = new HashMap<String, String>();
		printMap.put("<fecha>", String.format("%1$-15s", dateFormat.format(date)));
		printMap.put("<hora>", String.format("%1$-15s", timeFormat.format(date)));
		printMap.put("<monto>", currencyFormat.format(depositOpVO.amount));
		printMap.put("<b020>", String.format("%1$-5s", depositOpVO.b20));
		printMap.put("<b050>", String.format("%1$-5s", depositOpVO.b50));
		printMap.put("<b100>", String.format("%1$-5s", depositOpVO.b100));
		printMap.put("<b200>", String.format("%1$-5s", depositOpVO.b200));
		printMap.put("<b500>", String.format("%1$-5s", depositOpVO.b500));
		printMap.put("<monto20>", String.format("%1$9s", currencyFormat.format(depositOpVO.b20 * 20)));
		printMap.put("<monto50>", String.format("%1$9s", currencyFormat.format(depositOpVO.b50 * 50)));
		printMap.put("<monto100>", String.format("%1$9s", currencyFormat.format(depositOpVO.b100 * 100)));
		printMap.put("<monto200>", String.format("%1$9s", currencyFormat.format(depositOpVO.b200 * 200)));
		printMap.put("<monto500>", String.format("%1$9s", currencyFormat.format(depositOpVO.b500 * 500)));
		printMap.put("<referencia>", CurrentUser.movementId);
		printMap.put("<operacion>", Config.GetPersistence("TxCASHMANAGEMENTCounter", "0"));
		printMap.put("<usuario>", depositOpVO.userName);

		return print("deposito", printMap);

	}

	public static boolean printContadores() {

		System.out.println("Ptr.printContadores");

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

		Map<String, String> printMap = new HashMap<String, String>();
		printMap.put("<fecha>", String.format("%1$-15s", dateFormat.format(date)));
		printMap.put("<hora>", String.format("%1$-15s", timeFormat.format(date)));
		printMap.put("<monto>", currencyFormat.format(total));
		printMap.put("<b020>", String.format("%1$-5s", b20));
		printMap.put("<b050>", String.format("%1$-5s", b50));
		printMap.put("<b100>", String.format("%1$-5s", b100));
		printMap.put("<b200>", String.format("%1$-5s", b200));
		printMap.put("<b500>", String.format("%1$-5s", b500));
		printMap.put("<b1000>", String.format("%1$-5s", b1000));
		printMap.put("<monto20>", String.format("%1$9s", currencyFormat.format(total20)));
		printMap.put("<monto50>", String.format("%1$9s", currencyFormat.format(total50)));
		printMap.put("<monto100>", String.format("%1$9s", currencyFormat.format(total100)));
		printMap.put("<monto200>", String.format("%1$9s", currencyFormat.format(total200)));
		printMap.put("<monto500>", String.format("%1$9s", currencyFormat.format(total500)));
		printMap.put("<monto1000>", String.format("%1$9s", currencyFormat.format(total1000)));
		printMap.put("<usuario>", CurrentUser.loginUser);
		printMap.put("<corte>", Config.GetPersistence("CorteCount", "-1"));

		return print("contadores", printMap);

	}

	public static boolean printDispense(double montoRetiro, String currentUser) {

		System.out.println("Prt.printDispense");

		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);

		Map<String, String> printMap = new HashMap<String, String>();
		printMap.put("<fecha>", String.format("%1$-15s", dateFormat.format(date)));
		printMap.put("<hora>", String.format("%1$-15s", timeFormat.format(date)));
		printMap.put("<monto>", currencyFormat.format(montoRetiro));
		printMap.put("<referencia>", CurrentUser.movementId);
		printMap.put("<operacion>", Config.GetPersistence("TxRETIROCASHMANAGEMENTCounter", "0"));
		printMap.put("<usuario>", currentUser);

		return print("retiro", printMap);

	}

	public static boolean printContadoresTest() {

		System.out.println("Ptr.printContadoresTest");
		return print("contadores", new HashMap<String, String>());
	}

	public static boolean printDepositTest() {

		System.out.println("Ptr.printDepositTest");
		return print("deposito", new HashMap<String, String>());
	}

	public static boolean printDispenseTest() {

		System.out.println("Prt.printDispenseTest");
		return print("retiro", new HashMap<String, String>());
	}

	/**
	 * Este metodo prepara el archivo con la forma que se va a imprimir.
	 * 
	 * @return si pudo hacer e archivo con la forma de impresion regresa el
	 *         InputSTream , de lo contrario NULL
	 */
	private static InputStream prepareForm(String form, Map<String, String> formData) {
		BufferedReader reader;
		FileWriter fw = null;
		try {
			fw = new FileWriter("./Form/" + form + "out.txt");
		} catch (IOException e1) {
			System.out.println("Ptr.print IOException");
			e1.printStackTrace();
			logger.error(e1);
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			return null;
		}

		try {
			reader = new BufferedReader(new FileReader("./Form/" + form + ".txt"));
			String line = reader.readLine();
			while (line != null) {
				for (Entry<String, String> entry : formData.entrySet()) {
					line = line.replace(entry.getKey(), entry.getValue());
				}

				fw.write(line + System.getProperty("line.separator"));

				line = reader.readLine();

			}
			reader.close();
			fw.close();
		} catch (FileNotFoundException fe) {
			System.out.println("No se encontro el archivo [./Form/" + form + ".txt]");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "FORM NOT FOUND");
			logger.error(fe);
			return null;

		} catch (IOException e) {
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "EXCEPTION ");
			e.printStackTrace();
			logger.error(e);
			return null;
		}

		// Input the file
		InputStream textStream = null;
		try {
			textStream = new FileInputStream("./Form/" + form + "out.txt");
		} catch (FileNotFoundException ffne) {

			System.out.println(ffne.getMessage());
		}
		if (textStream == null) {
			return null;
		}

		return textStream;
	}

	public static boolean print(String form, Map<String, String> formData) {

		if (!JcmGlobalData.printerReady) {
			System.out.println("La impresora no esta bien, ni intentamos imprimir.");
			return false;
		}

		InputStream textStream = prepareForm(form, formData);

		if (textStream == null)
			return false;

		// CUPS Printing
		try {

			if (cupsClient == null)
				initializeCupsClient();

			PrintJob printJob = new PrintJob.Builder(textStream).build();

			PrintingStatus();

			cupsPrinter.setName(form);
			PrintRequestResult printRequestResult = cupsPrinter.print(printJob);

			int jobId = printRequestResult.getJobId();
			System.out.println("Printer jobId [" + jobId + "]");

			JobStateEnum jobStatus = cupsPrinter.getJobStatus(jobId);

			switch (jobStatus) {
			case ABORTED:
				System.out.println("jobStatus ABORTED");
				break;
			case CANCELED:
				System.out.println("jobStatus CANCELED");
				break;
			case COMPLETED:
				System.out.println("jobStatus COMPLETED");
				break;
			case PENDING:
				System.out.println("jobStatus PENDING");
				break;
			case PENDING_HELD:
				System.out.println("jobStatus PENDING_HELD");
				break;
			case PROCESSING:
				System.out.println("jobStatus PROCESSING");
				break;
			case PROCESSING_STOPPED:
				System.out.println("jobStatus PROCESSING_STOPPED");
				break;
			default:
				break;

			}

			// Para CUPS si pudo imprimir, aunque sea en spooling (COmo que no hay papel por
			// ejemplo)
			if (printRequestResult.isSuccessfulResult()) {
				System.out.println("Cups print OK");

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
					RaspiAgent.Broadcast(DeviceEvent.PTR_PrintOk, printRequestResult.getResultDescription());
				} else {
					System.out.println("USB print Status FAIL");
					System.out.println("Cancelling job [" + jobId + "] [" + cupsClient.cancelJob(jobId) + "]");
					RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "Could not print");
				}
			} else {
				System.out.println("Cups print FAILED");
				RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, printRequestResult.getResultDescription());
				return false;
			}

		} catch (Exception ignored) {
			System.out.println("EXCEPTION print EXCEPTION");
			RaspiAgent.Broadcast(DeviceEvent.PTR_PrintFailed, "");
			printing = false;
			if (ignored.getMessage() != null)
				System.out.println(ignored.getMessage());
			else
				ignored.printStackTrace();

			return false;

		}

		return true;
	}

}

class ContadoresPage implements Printable {

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

		g2d.drawString("FECHA:", mIzq, 100);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 100);

		g2d.drawString("HORA:", mIzq, 110);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 110);

		g2d.drawString("MONTO:", mIzq, 120);
		g2d.drawString(currencyFormat.format(total), mIzq + 60, 120);

		g2d.drawString("PIEZAS", mIzq, 140);
		g2d.drawString("BILLETES", billetesMargin, 140);
		g2d.drawString("MONTO", montoMargin, 140);

		// Piezas
		g2d.drawString(String.format("%1$-5s", b20), mIzq, 150);
		g2d.drawString(String.format("%1$-5s", b50), mIzq, 160);
		g2d.drawString(String.format("%1$-5s", b100), mIzq, 170);
		g2d.drawString(String.format("%1$-5s", b200), mIzq, 180);
		g2d.drawString(String.format("%1$-5s", b500), mIzq, 190);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 150);
		g2d.drawString("$50", billetesMargin + 10, 160);
		g2d.drawString("$100", billetesMargin + 10, 170);
		g2d.drawString("$200", billetesMargin + 10, 180);
		g2d.drawString("$500", billetesMargin + 10, 190);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total20)), montoMargin-10, 150);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total50)), montoMargin-10, 160);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total100)), montoMargin-10, 170);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total200)), montoMargin-10, 180);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total500)), montoMargin-10, 190);

		// Footer
		g2d.drawString("CORTE:", mIzq, 210);
		g2d.drawString("USUARIO:", mIzq, 220);
		g2d.drawString("ATMID:", mIzq, 230);

		g2d.drawString(Config.GetPersistence("CorteCount", "-1"), mIzq + 100, 210);
		g2d.drawString(CurrentUser.loginUser, mIzq + 100, 220);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 230);

		return Printable.PAGE_EXISTS;
	}
}

class DepositoPage implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	ImageIcon printImage = new javax.swing.ImageIcon("./a.png");
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    DepositOpVO depositOpVO;	
	
	public DepositoPage(DepositOpVO depositOpVO) {
		this.depositOpVO = depositOpVO;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		if (pageIndex >= 1) {
			return Printable.NO_SUCH_PAGE;
		}

		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);
		
		System.out.println("Ptr.DepositoPage");

		currencyFormat.setMaximumFractionDigits(0);

		Map<String, String> printMap = new HashMap<String, String>();
		printMap.put("<fecha>", String.format("%1$-15s", dateFormat.format(date)));
		printMap.put("<hora>", String.format("%1$-15s", timeFormat.format(date)));
		printMap.put("<monto>", currencyFormat.format(depositOpVO.amount));
		printMap.put("<b020>", String.format("%1$-5s", depositOpVO.b20));
		printMap.put("<b050>", String.format("%1$-5s", depositOpVO.b50));
		printMap.put("<b100>", String.format("%1$-5s", depositOpVO.b100));
		printMap.put("<b200>", String.format("%1$-5s", depositOpVO.b200));
		printMap.put("<b500>", String.format("%1$-5s", depositOpVO.b500));
		printMap.put("<monto20>", String.format("%1$9s", currencyFormat.format(depositOpVO.b20 * 20)));
		printMap.put("<monto50>", String.format("%1$9s", currencyFormat.format(depositOpVO.b50 * 50)));
		printMap.put("<monto100>", String.format("%1$9s", currencyFormat.format(depositOpVO.b100 * 100)));
		printMap.put("<monto200>", String.format("%1$9s", currencyFormat.format(depositOpVO.b200 * 200)));
		printMap.put("<monto500>", String.format("%1$9s", currencyFormat.format(depositOpVO.b500 * 500)));
		printMap.put("<referencia>", CurrentUser.movementId);
		printMap.put("<operacion>", Config.GetPersistence("TxCASHMANAGEMENTCounter", "0"));
		printMap.put("<usuario>", depositOpVO.userName);
		

		int mIzq = 10;

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		int montoMargin = mIzq + 140;
		int billetesMargin = mIzq + 70;

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("DEPOSITO", 80, 80);

		g2d.drawString("FECHA:", mIzq, 100);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 100);

		g2d.drawString("HORA:", mIzq, 110);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 110);

		g2d.drawString("MONTO:", mIzq, 120);
		g2d.drawString(currencyFormat.format(depositOpVO.amount), mIzq + 60, 120);

		g2d.drawString("PIEZAS", mIzq, 140);
		g2d.drawString("BILLETES", billetesMargin, 140);
		g2d.drawString("MONTO", montoMargin, 140);

		// Piezas
		g2d.drawString(String.format("%1$-5s", depositOpVO.b20), mIzq, 150);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b50), mIzq, 160);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b100), mIzq, 170);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b200), mIzq, 180);
		g2d.drawString(String.format("%1$-5s", depositOpVO.b500), mIzq, 190);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 150);
		g2d.drawString("$50", billetesMargin + 10, 160);
		g2d.drawString("$100", billetesMargin + 10, 170);
		g2d.drawString("$200", billetesMargin + 10, 180);
		g2d.drawString("$500", billetesMargin + 10, 190);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b20 * 20)), montoMargin - 10, 150);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b50 * 50)), montoMargin - 10, 160);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b100 * 100)), montoMargin - 10, 170);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b200 * 200)), montoMargin - 10, 180);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(depositOpVO.b500 * 500)), montoMargin - 10, 190);

		// Footer
		g2d.drawString("OPERACION:", mIzq, 210);
		g2d.drawString("REFERENCIA:", mIzq, 220);
		g2d.drawString("USUARIO:", mIzq, 230);
		g2d.drawString("ATMID:", mIzq, 240);

		g2d.drawString(Config.GetPersistence("TxCASHMANAGEMENTCounter", "0"), mIzq + 100, 210);
		g2d.drawString(CurrentUser.movementId, mIzq + 100, 220);
		g2d.drawString(depositOpVO.userName, mIzq + 100, 230);		
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 240);

		return Printable.PAGE_EXISTS;
	}
}


class RetiroPage implements Printable {

	// http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Printanimageout.htm

	ImageIcon printImage = new javax.swing.ImageIcon("./a.png");
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	double montoRetiro;
	String currentUser;	
	
	public RetiroPage(double montoRetiro, String currentUser) {
		this.montoRetiro = montoRetiro;
		this.currentUser = currentUser;
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		if (pageIndex >= 1) {
			return Printable.NO_SUCH_PAGE;
		}

		Date date = new Date();

		currencyFormat.setMaximumFractionDigits(0);
		
		System.out.println("Ptr.RetiroPage");

		int mIzq = 10;

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("RETIRO", 80, 80);

		g2d.drawString("FECHA:", mIzq, 100);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 100);

		g2d.drawString("HORA:", mIzq, 110);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 110);

		g2d.drawString("MONTO:", mIzq, 120);
		g2d.drawString(currencyFormat.format(montoRetiro), mIzq + 60, 120);


		// Footer
		g2d.drawString("OPERACION:", mIzq, 210);
		g2d.drawString("REFERENCIA:", mIzq, 220);
		g2d.drawString("USUARIO:", mIzq, 230);
		g2d.drawString("ATMID:", mIzq, 240);

		g2d.drawString(Config.GetPersistence("TxCASHMANAGEMENTCounter", "0"), mIzq + 100, 210);
		g2d.drawString(CurrentUser.movementId, mIzq + 100, 220);
		g2d.drawString(currentUser, mIzq + 100, 230);		
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 240);

		return Printable.PAGE_EXISTS;
	}
}



class ContadoresPageTest implements Printable {

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

		int total = 0;
		int total20 = 0;
		int total50 = 0;
		int total100 = 0;
		int total200 = 0;
		int total500 = 0;

		int b20 = 0;
		int b50 = 0;
		int b100 = 0;
		int b200 = 0;
		int b500 = 0;
	

		total20 = 20 * b20;
		total50 = 50 * b50;
		total100 = 100 * b100;
		total200 = 200 * b200;
		total500 = 500 * b500;

		total = 0;

		int mIzq = 10;

		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());

		int montoMargin = mIzq + 140;
		int billetesMargin = mIzq + 70;

		g2d.drawImage(printImage.getImage(), 80, 0, null);

		g2d.drawString("CORTE", 80, 80);

		g2d.drawString("FECHA:", mIzq, 100);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 100);

		g2d.drawString("HORA:", mIzq, 110);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 110);

		g2d.drawString("MONTO:", mIzq, 120);
		g2d.drawString(currencyFormat.format(total), mIzq + 60, 120);

		g2d.drawString("PIEZAS", mIzq, 140);
		g2d.drawString("BILLETES", billetesMargin, 140);
		g2d.drawString("MONTO", montoMargin, 140);

		// Piezas
		g2d.drawString(String.format("%1$-5s", b20), mIzq, 150);
		g2d.drawString(String.format("%1$-5s", b50), mIzq, 160);
		g2d.drawString(String.format("%1$-5s", b100), mIzq, 170);
		g2d.drawString(String.format("%1$-5s", b200), mIzq, 180);
		g2d.drawString(String.format("%1$-5s", b500), mIzq, 190);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 150);
		g2d.drawString("$50", billetesMargin + 10, 160);
		g2d.drawString("$100", billetesMargin + 10, 170);
		g2d.drawString("$200", billetesMargin + 10, 180);
		g2d.drawString("$500", billetesMargin + 10, 190);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total20)), montoMargin-10, 150);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total50)), montoMargin-10, 160);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total100)), montoMargin-10, 170);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total200)), montoMargin-10, 180);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(total500)), montoMargin-10, 190);

		// Footer
		g2d.drawString("CORTE:", mIzq, 210);
		g2d.drawString("USUARIO:", mIzq, 220);
		g2d.drawString("ATMID:", mIzq, 230);

		g2d.drawString("-----", mIzq + 100, 210);
		g2d.drawString("-----", mIzq + 100, 220);
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 230);

		return Printable.PAGE_EXISTS;
	}
}

class DepositoPageTest implements Printable {

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

		g2d.drawString("FECHA:", mIzq, 100);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 100);

		g2d.drawString("HORA:", mIzq, 110);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 110);

		g2d.drawString("MONTO:", mIzq, 120);
		g2d.drawString(currencyFormat.format(0), mIzq + 60, 120);

		g2d.drawString("PIEZAS", mIzq, 140);
		g2d.drawString("BILLETES", billetesMargin, 140);
		g2d.drawString("MONTO", montoMargin, 140);

		// Piezas
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 150);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 160);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 170);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 180);
		g2d.drawString(String.format("%1$-5s", 0), mIzq, 190);

		// Billetes
		g2d.drawString("$20", billetesMargin + 10, 150);
		g2d.drawString("$50", billetesMargin + 10, 160);
		g2d.drawString("$100", billetesMargin + 10, 170);
		g2d.drawString("$200", billetesMargin + 10, 180);
		g2d.drawString("$500", billetesMargin + 10, 190);

		// Monto
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin - 10, 150);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin - 10, 160);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin - 10, 170);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin - 10, 180);
		g2d.drawString(String.format("%1$9s", currencyFormat.format(0)), montoMargin - 10, 190);

		// Footer
		g2d.drawString("OPERACION:", mIzq, 210);
		g2d.drawString("REFERENCIA:", mIzq, 220);
		g2d.drawString("USUARIO:", mIzq, 230);
		g2d.drawString("ATMID:", mIzq, 240);

		g2d.drawString("-----", mIzq + 100, 210);
		g2d.drawString("-----", mIzq + 100, 220);
		g2d.drawString("-----", mIzq + 100, 230);		
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 240);

		return Printable.PAGE_EXISTS;
	}
}

class RetiroPageTest implements Printable {

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

		g2d.drawString("FECHA:", mIzq, 100);
		g2d.drawString(String.format("%1$-15s", dateFormat.format(date)), mIzq + 60, 100);

		g2d.drawString("HORA:", mIzq, 110);
		g2d.drawString(String.format("%1$-15s", timeFormat.format(date)), mIzq + 60, 110);

		g2d.drawString("MONTO:", mIzq, 120);
		g2d.drawString(currencyFormat.format(0), mIzq + 60, 120);


		// Footer
		g2d.drawString("OPERACION:", mIzq, 210);
		g2d.drawString("REFERENCIA:", mIzq, 220);
		g2d.drawString("USUARIO:", mIzq, 230);
		g2d.drawString("ATMID:", mIzq, 240);

		g2d.drawString("-----", mIzq + 100, 210);
		g2d.drawString("-----", mIzq + 100, 220);
		g2d.drawString("-----", mIzq + 100, 230);		
		g2d.drawString(JcmGlobalData.atmId, mIzq + 100, 240);

		return Printable.PAGE_EXISTS;
	}
}