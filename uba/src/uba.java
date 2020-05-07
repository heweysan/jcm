import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeoutException;
import gnu.io.*;

import javax.comm.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.lang.System;
import java.text.DecimalFormat;

import com.google.gson.Gson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

import pentomino.common.*;
import pentomino.jcmagent.RaspiAgent;
import pentomino.jcmagent.ServerEntry;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.Icon;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;



public class uba {

	private static Gson gson = new Gson();

	private static final Logger logger = LogManager.getLogger(uba.class);

	private static String montoDispensar = "0";
	private static String autorizacionDispensar = "";
	private static String asteriscos = "";

	private static jcmOperation currentOperation = jcmOperation.None;

	static long maxDispenseAmmount = 5000;

	private JFrame frame;

	private static boolean retiroMonto = false;
	private static boolean retiroAutorizacion = false;

	private static int iMonotoDispensar = 0;
	private static boolean validAuthorization = false;
	
	uart[] jcms = new uart[2];
	int contador = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					uba window = new uba();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public uba() throws IOException, TimeoutException {

		//https://logging.apache.org/log4j/2.x/manual/layouts.html
		
		logger.debug("this is an DEBUG message");
		logger.info("this is an INFO message");
		logger.warn("this is an WARN message");
		logger.error("this is an ERROR message");
		logger.fatal("this is an FATAL message");

		// Thread t1 = new Thread(new JcmMonitor ());
		// t1.start();

		JcmMonitor t2 = new JcmMonitor();

		t2.start();

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setTitle("RedBlu JCM Driver (ID003)");
		frame.setBounds(100, 100, 1920, 1100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);

		final uart srlprt1 = new uart();
		final uart srlprt2 = new uart(); // HEWEY SAN AQUI: Pruebas recepcion
		//final uart2 srlprt2 = new uart2(); // HEWEY SAN AQUI: Pruebas recepcion

		String[] BaudArray = { "9600", "19200", "38400" };

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1904, 1061);
		frame.getContentPane().add(tabbedPane);

		JPanel panelPrincipal = new JPanel();
		tabbedPane.addTab("Principal", (Icon) null, panelPrincipal, null);
		tabbedPane.setEnabledAt(0, true);
		panelPrincipal.setLayout(null);

		JPanel panel_estatus = new JPanel();
		panel_estatus.setBounds(10, 11, 850, 538);
		panelPrincipal.add(panel_estatus);
		panel_estatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_estatus.setLayout(null);

		JPanel panelJCM2 = new JPanel();
		panelJCM2.setBounds(440, 11, 400, 473);
		panel_estatus.add(panelJCM2);
		panelJCM2.setLayout(null);
		panelJCM2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JLabel lblRecycler2 = new JLabel("JCM2 RECYCLE BILLS");
		lblRecycler2.setBounds(10, 288, 380, 53);
		panelJCM2.add(lblRecycler2);
		lblRecycler2.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador2 = new JLabel("Reciclador 2");
		lblTitleReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTitleReciclador2.setBounds(10, 11, 380, 28);
		panelJCM2.add(lblTitleReciclador2);

		JLabel lblBilleteIngresado2 = new JLabel("$0");
		lblBilleteIngresado2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblBilleteIngresado2.setBounds(164, 416, 226, 46);
		panelJCM2.add(lblBilleteIngresado2);

		JLabel lblTxtBill2 = new JLabel("Billete:");
		lblTxtBill2.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblTxtBill2.setBounds(10, 416, 144, 46);
		panelJCM2.add(lblTxtBill2);

		JLabel lblContadores2 = new JLabel("100x20 100x50 x100x100 100x200 100x500");
		lblContadores2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblContadores2.setBounds(10, 352, 380, 53);
		panelJCM2.add(lblContadores2);
		
		JButton btnReiniciarJcm2 = new JButton("REINICIAR");
		btnReiniciarJcm2.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnReiniciarJcm2.setBounds(181, 11, 209, 59);
		panelJCM2.add(btnReiniciarJcm2);
		
		JPanel panel_firmware2 = new JPanel();
		panel_firmware2.setLayout(null);
		panel_firmware2.setBorder(new TitledBorder(null, "Firmware:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_firmware2.setBounds(10, 93, 380, 59);
		panelJCM2.add(panel_firmware2);
		
		JLabel label_1 = new JLabel(".");
		label_1.setBounds(10, 18, 329, 30);
		panel_firmware2.add(label_1);
		
		JPanel panelRecycler2 = new JPanel();
		panelRecycler2.setLayout(null);
		panelRecycler2.setBorder(new TitledBorder(
										new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(128, 128, 128)), "Recycler:",
										TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelRecycler2.setBounds(10, 163, 380, 59);
		panelJCM2.add(panelRecycler2);
		
		JLabel lblRecyclerVersion_1 = new JLabel(".");
		lblRecyclerVersion_1.setBounds(10, 19, 225, 29);
		panelRecycler2.add(lblRecyclerVersion_1);

		JPanel panelJCM1 = new JPanel();
		panelJCM1.setBounds(10, 11, 400, 473);
		panel_estatus.add(panelJCM1);
		panelJCM1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelJCM1.setLayout(null);

		JLabel lblRecycler1 = new JLabel("JCM1 RECYCLE BILLS");
		lblRecycler1.setBounds(10, 288, 380, 53);
		panelJCM1.add(lblRecycler1);
		lblRecycler1.setFont(new Font("Tahoma", Font.BOLD, 26));

		JLabel lblTitleReciclador1 = new JLabel("Reciclador 1");
		lblTitleReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTitleReciclador1.setBounds(10, 11, 380, 28);
		panelJCM1.add(lblTitleReciclador1);

		JLabel lblTxtBill1 = new JLabel("Billete:");
		lblTxtBill1.setBounds(10, 416, 144, 46);
		panelJCM1.add(lblTxtBill1);
		lblTxtBill1.setFont(new Font("Tahoma", Font.BOLD, 30));

		final JLabel lblBilleteIngresado1 = new JLabel("$0");
		lblBilleteIngresado1.setBounds(164, 416, 226, 46);
		panelJCM1.add(lblBilleteIngresado1);
		lblBilleteIngresado1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JLabel lblContadores1 = new JLabel("100x20 100x50 x100x100 100x200 100x500");
		lblContadores1.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblContadores1.setBounds(10, 352, 380, 53);
		panelJCM1.add(lblContadores1);
		
		
		
		
		
		JPanel panel_firmware1 = new JPanel();
		panel_firmware1.setBounds(10, 95, 380, 59);
		panelJCM1.add(panel_firmware1);
		panel_firmware1
		.setBorder(new TitledBorder(null, "Firmware:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_firmware1.setLayout(null);
		
				final JLabel label = new JLabel(".");
				label.setBounds(10, 18, 329, 30);
				panel_firmware1.add(label);
				
						JPanel panelRecycler1 = new JPanel();
						panelRecycler1.setBounds(10, 165, 380, 59);
						panelJCM1.add(panelRecycler1);
						panelRecycler1.setLayout(null);
						panelRecycler1.setBorder(new TitledBorder(
								new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(128, 128, 128)), "Recycler:",
								TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
						
								final JLabel lblRecyclerVersion = new JLabel(".");
								lblRecyclerVersion.setBounds(10, 19, 225, 29);
								panelRecycler1.add(lblRecyclerVersion);
								
										JButton btnReiniciarJcm1 = new JButton("REINICIAR");
										btnReiniciarJcm1.setBounds(181, 11, 209, 59);
										panelJCM1.add(btnReiniciarJcm1);
										btnReiniciarJcm1.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												currentOperation = jcmOperation.Reset;
												protocol.currentOpertion = jcmOperation.Reset;
												jcms[0].id003_format((byte) 5, (byte) 0x40, protocol.jcmMessage, true);
											}
										});
										btnReiniciarJcm1.setFont(new Font("Tahoma", Font.BOLD, 30));

		JPanel panelRetiro = new JPanel();
		panelRetiro.setBounds(10, 563, 850, 244);
		panelPrincipal.add(panelRetiro);
		panelRetiro.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelRetiro.setLayout(null);

		JButton btnMonto = new JButton("Monto");
		btnMonto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retiroMonto = true;
				retiroAutorizacion = false;

			}
		});
		btnMonto.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnMonto.setBounds(10, 11, 275, 100);
		panelRetiro.add(btnMonto);

		final JLabel lblMonto = new JLabel("$000,000");
		lblMonto.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblMonto.setBounds(336, 14, 287, 95);
		panelRetiro.add(lblMonto);

		JButton btnAutorizacion = new JButton("Autorizaci\u00F3n");
		btnAutorizacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retiroMonto = false;
				retiroAutorizacion = true;
			}
		});
		btnAutorizacion.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnAutorizacion.setBounds(10, 125, 275, 100);
		panelRetiro.add(btnAutorizacion);

		final JLabel lblAutorizacion = new JLabel(".\r\n");
		lblAutorizacion.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblAutorizacion.setBounds(336, 128, 49, 95);
		panelRetiro.add(lblAutorizacion);

		JButton btnDispensar = new JButton("Retirar");
		btnDispensar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Primero deshabilitamos el que acepte billetes

				if (((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount
						|| Long.parseLong(montoDispensar) == 0)) {
					JOptionPane.showMessageDialog(frame, "Monto inválido, favor de verificar.", "No se puede dispensar",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (autorizacionDispensar.length() == 0) {
					JOptionPane.showMessageDialog(frame, "Autorizacón inválida, favor de verificar.",
							"No se puede dispensar", JOptionPane.ERROR_MESSAGE);
					return;
				}

				System.out.println("Retirar");

				retiroMonto = false;
				retiroAutorizacion = false;

				iMonotoDispensar = 0;
				validAuthorization = false;

				// Iniciamos el dispensado

				protocol.currentOpertion = jcmOperation.Dispense;

				// primero el inhibit
				protocol.jcmMessage[3] = 0x01;
				jcms[0].id003_format((byte) 0x6, (byte) 0xC3, protocol.jcmMessage, false);

			}
		});
		btnDispensar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnDispensar.setBounds(602, 123, 238, 100);
		panelRetiro.add(btnDispensar);

		JPanel panelPinPad = new JPanel();
		panelPinPad.setBackground(Color.GRAY);
		panelPinPad.setBounds(870, 11, 1019, 923);
		panelPrincipal.add(panelPinPad);
		panelPinPad.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelPinPad.setLayout(null);

		JButton btn2 = new JButton("2");
		btn2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn2.setBounds(240, 11, 200, 200);
		panelPinPad.add(btn2);

		JButton btn3 = new JButton("3");
		btn3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn3.setBounds(471, 11, 200, 200);
		panelPinPad.add(btn3);

		JButton btn4 = new JButton("4");
		btn4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn4.setBounds(10, 242, 200, 200);
		panelPinPad.add(btn4);

		JButton btn5 = new JButton("5");
		btn5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn5.setBounds(240, 242, 200, 200);
		panelPinPad.add(btn5);

		JButton btn6 = new JButton("6");
		btn6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn6.setBounds(471, 242, 200, 200);
		panelPinPad.add(btn6);

		JButton btn7 = new JButton("7");
		btn7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn7.setBounds(10, 473, 200, 200);
		panelPinPad.add(btn7);

		JButton btn8 = new JButton("8");
		btn8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn8.setBounds(240, 473, 200, 200);
		panelPinPad.add(btn8);

		JButton btn9 = new JButton("9");
		btn9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn9.setBounds(471, 473, 200, 200);
		panelPinPad.add(btn9);

		JButton btn0 = new JButton("0");
		btn0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn0.setBounds(240, 706, 200, 200);
		panelPinPad.add(btn0);

		JButton btnNullIzq = new JButton("");
		btnNullIzq.setEnabled(false);
		btnNullIzq.setBounds(10, 706, 200, 200);
		panelPinPad.add(btnNullIzq);

		JButton btnNullDer = new JButton("");
		btnNullDer.setEnabled(false);
		btnNullDer.setBounds(471, 706, 200, 200);
		panelPinPad.add(btnNullDer);

		JButton btnCancel = new JButton("CANCELAR");
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnCancel.setBackground(Color.RED);
		btnCancel.setBounds(709, 11, 300, 200);
		panelPinPad.add(btnCancel);

		JButton btnBorrar = new JButton("BORRAR");
		btnBorrar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnBorrar.setBackground(Color.ORANGE);
		btnBorrar.setBounds(709, 242, 300, 200);
		panelPinPad.add(btnBorrar);

		JButton btnConfirmar = new JButton("CONFIRMAR");
		btnConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnConfirmar.setBackground(Color.GREEN);
		btnConfirmar.setBounds(709, 473, 300, 200);
		panelPinPad.add(btnConfirmar);

		JButton btnConfirmar_1 = new JButton("");
		btnConfirmar_1.setEnabled(false);
		btnConfirmar_1.setBackground(UIManager.getColor("Button.background"));
		btnConfirmar_1.setBounds(709, 706, 300, 200);
		panelPinPad.add(btnConfirmar_1);

		JButton btn1 = new JButton("1");
		btn1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btn1.setBounds(10, 11, 200, 200);
		panelPinPad.add(btn1);

		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("1", lblMonto, lblAutorizacion);
			}
		});

		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("2", lblMonto, lblAutorizacion);
			}
		});

		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("3", lblMonto, lblAutorizacion);
			}
		});

		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("4", lblMonto, lblAutorizacion);
			}
		});

		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("5", lblMonto, lblAutorizacion);
			}
		});

		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("6", lblMonto, lblAutorizacion);
			}
		});

		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("7", lblMonto, lblAutorizacion);
			}
		});

		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("8", lblMonto, lblAutorizacion);
			}
		});

		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("9", lblMonto, lblAutorizacion);
			}
		});

		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textoMontoValidacion("0", lblMonto, lblAutorizacion);
			}
		});
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (retiroMonto) {

					if (montoDispensar.length() == 0 || Long.parseLong(montoDispensar) == 0)
						return;

					montoDispensar = montoDispensar.substring(0, montoDispensar.length() - 1);
					lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
				}

				if (retiroAutorizacion) {

					if (autorizacionDispensar.length() == 0)
						return;

					autorizacionDispensar = autorizacionDispensar.substring(0, autorizacionDispensar.length() - 1);
					lblAutorizacion.setText(autorizacionDispensar);

				}

			}
		});

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (retiroMonto) {

					montoDispensar = "0";
					lblMonto.setText("");
				}

				if (retiroAutorizacion) {
					lblAutorizacion.setText("");
				}

			}
		});

		JPanel panelComandos = new JPanel();
		tabbedPane.addTab("Comandos\r\n", (Icon) null, panelComandos, null);
		panelComandos.setLayout(null);

		JPanel panel_comandos = new JPanel();
		panel_comandos.setBounds(10, 113, 1646, 706);
		panelComandos.add(panel_comandos);
		panel_comandos.setLayout(null);

		JButton btnStatusReq = new JButton("Stat Req (11h)");
		btnStatusReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusReq.setBackground(Color.ORANGE);
		btnStatusReq.setBounds(10, 11, 157, 50);
		btnStatusReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x11, protocol.jcmMessage, true);
			}
		});

		panel_comandos.add(btnStatusReq);

		JButton btnReset = new JButton("Reset (40h)");
		btnReset.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReset.setBackground(Color.GREEN);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentOperation = jcmOperation.Reset;
				jcms[0].id003_format((byte) 5, (byte) 0x40, protocol.jcmMessage, true);
			}
		});
		btnReset.setBounds(282, 11, 144, 50);
		panel_comandos.add(btnReset);

		JButton btnAck = new JButton("Ack (50h)");
		btnAck.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnAck.setBackground(Color.ORANGE);
		btnAck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x50, protocol.jcmMessage, true);
			}
		});
		btnAck.setLocation(177, 11);
		btnAck.setSize(95, 50);
		panel_comandos.add(btnAck);

		JButton btnStack1 = new JButton("Stack-1 (41h)");
		btnStack1.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack1.setBackground(Color.ORANGE);
		btnStack1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x41, protocol.jcmMessage, true);
			}
		});
		btnStack1.setBounds(436, 11, 117, 50);
		panel_comandos.add(btnStack1);

		JButton btnStack2 = new JButton("Stack-2 (42h)");
		btnStack2.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack2.setBackground(Color.ORANGE);
		btnStack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x42, protocol.jcmMessage, true);
			}
		});
		btnStack2.setBounds(565, 11, 122, 50);
		panel_comandos.add(btnStack2);

		JButton btnReturn = new JButton("Return (43h)");
		btnReturn.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReturn.setBackground(Color.ORANGE);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x43, protocol.jcmMessage, true);
			}
		});
		btnReturn.setBounds(699, 11, 116, 50);
		panel_comandos.add(btnReturn);

		JButton btnHold = new JButton("Hold (44h)");
		btnHold.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnHold.setBackground(Color.ORANGE);
		btnHold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x44, protocol.jcmMessage, true);
			}
		});
		btnHold.setBounds(827, 11, 108, 50);
		panel_comandos.add(btnHold);

		JButton btnWait = new JButton("Wait (45h)");
		btnWait.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnWait.setBackground(Color.ORANGE);
		btnWait.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x45, protocol.jcmMessage, true);
			}
		});
		btnWait.setBounds(947, 11, 117, 50);
		panel_comandos.add(btnWait);

		JLabel lblNewLabel_2 = new JLabel("Setting Commands +Data");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(10, 69, 167, 50);
		panel_comandos.add(lblNewLabel_2);

		JButton btnEnableDisDenom = new JButton("En/Des Denom (C0h)"); // +DATA
		btnEnableDisDenom.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnableDisDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0xC0, protocol.jcmMessage, true);
			}
		});
		btnEnableDisDenom.setBounds(732, 119, 167, 50);
		panel_comandos.add(btnEnableDisDenom);

		JButton btnSecurotyDenom = new JButton("Security Denom (C1h)");
		btnSecurotyDenom.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSecurotyDenom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSecurotyDenom.setBounds(911, 119, 171, 50);
		panel_comandos.add(btnSecurotyDenom);

		JButton btnCommunicationMode = new JButton("Communication Mode (C2h)");
		btnCommunicationMode.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCommunicationMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCommunicationMode.setBounds(207, 119, 205, 50);
		panel_comandos.add(btnCommunicationMode);

		JButton btnInhibit = new JButton("Inhibit (C3h)");
		btnInhibit.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnInhibit.setBackground(Color.ORANGE);
		btnInhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("INHIBIT");
				protocol.jcmMessage[3] = 0x01;
				jcms[0].id003_format((byte) 0x6, (byte) 0xC3, protocol.jcmMessage, false);
			}
		});
		btnInhibit.setBounds(422, 119, 146, 50);
		panel_comandos.add(btnInhibit);

		JButton btnDirection = new JButton("Direction (C4h)");
		btnDirection.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDirection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDirection.setBounds(1092, 119, 125, 50);
		panel_comandos.add(btnDirection);

		JButton btnOptionalFunc = new JButton("Optional Func (C5h)");
		btnOptionalFunc.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOptionalFunc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnOptionalFunc.setBounds(10, 119, 187, 50);
		panel_comandos.add(btnOptionalFunc);

		JLabel lblNewLabel_2_1 = new JLabel("Setting Status Request");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1.setBounds(10, 182, 167, 50);
		panel_comandos.add(lblNewLabel_2_1);

		JButton btnOptionalFuncReq = new JButton("Optional Func (85h)");
		btnOptionalFuncReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOptionalFuncReq.setBackground(Color.ORANGE);
		btnOptionalFuncReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x85, protocol.jcmMessage, true);
			}
		});
		btnOptionalFuncReq.setBounds(945, 223, 179, 50);
		panel_comandos.add(btnOptionalFuncReq);

		JButton btnEnableDisDenomReq = new JButton("En/Des Denom (80h)");
		btnEnableDisDenomReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnableDisDenomReq.setBackground(Color.ORANGE);
		btnEnableDisDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x80, protocol.jcmMessage, true);
			}
		});
		btnEnableDisDenomReq.setBounds(10, 223, 179, 50);
		panel_comandos.add(btnEnableDisDenomReq);

		JButton btnInhibitReq = new JButton("Inhibit (83h)");
		btnInhibitReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnInhibitReq.setBackground(Color.ORANGE);
		btnInhibitReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(" CONSULTA DE INHIBIT");
				jcms[0].id003_format((byte) 5, (byte) 0x83, protocol.jcmMessage, true);
			}
		});
		btnInhibitReq.setBounds(601, 223, 157, 50);
		panel_comandos.add(btnInhibitReq);

		JButton btnDirectionReq = new JButton("Direction (84h)");
		btnDirectionReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnDirectionReq.setBackground(Color.ORANGE);
		btnDirectionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x84, protocol.jcmMessage, true);
			}
		});
		btnDirectionReq.setBounds(768, 225, 167, 50);
		panel_comandos.add(btnDirectionReq);

		JButton btnSecurotyDenomReq = new JButton("Security Denom (81h)");
		btnSecurotyDenomReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnSecurotyDenomReq.setBackground(Color.ORANGE);
		btnSecurotyDenomReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x81, protocol.jcmMessage, true);
			}
		});
		btnSecurotyDenomReq.setBounds(199, 223, 179, 50);
		panel_comandos.add(btnSecurotyDenomReq);

		JButton btnCommunicationModeReq = new JButton("Communication Mode (82h)");
		btnCommunicationModeReq.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCommunicationModeReq.setBackground(Color.ORANGE);
		btnCommunicationModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x82, protocol.jcmMessage, true);
			}
		});
		btnCommunicationModeReq.setBounds(386, 223, 205, 50);
		panel_comandos.add(btnCommunicationModeReq);

		JButton btnVersionRequest = new JButton("Version Request (88h)");
		btnVersionRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnVersionRequest.setBackground(Color.ORANGE);
		btnVersionRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x88, protocol.jcmMessage, true);
			}
		});
		btnVersionRequest.setBounds(1134, 223, 179, 50);
		panel_comandos.add(btnVersionRequest);

		JButton btnBootVersionrequest = new JButton("Boot Version Request (89h)");
		btnBootVersionrequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnBootVersionrequest.setBackground(Color.ORANGE);
		btnBootVersionrequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 5, (byte) 0x89, protocol.jcmMessage, true);
			}
		});
		btnBootVersionrequest.setBounds(10, 285, 205, 50);
		panel_comandos.add(btnBootVersionrequest);

		JLabel lblNewLabel_2_1_1 = new JLabel("RECYCLER / EXTENSION (F0h + )");
		lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_1.setBounds(10, 348, 218, 50);
		panel_comandos.add(lblNewLabel_2_1_1);

		JButton btnStatusRequestExt = new JButton("Stat Req Ext (+1Ah)");
		btnStatusRequestExt.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStatusRequestExt.setBackground(Color.ORANGE);
		btnStatusRequestExt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x7, (byte) 0xf0, (byte) 0x20, (byte) 0x1a, (byte) 0x1, (byte) 0x2,
						protocol.jcmMessage);
			}
		});
		btnStatusRequestExt.setBounds(528, 457, 163, 50);
		panel_comandos.add(btnStatusRequestExt);

		JButton btnStack3 = new JButton("Stack-3 (49h)");
		btnStack3.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStack3.setBackground(Color.ORANGE);
		btnStack3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 0x5, (byte) 0x49, protocol.jcmMessage, false);
			}
		});
		btnStack3.setBounds(701, 457, 157, 50);
		panel_comandos.add(btnStack3);

		JButton btnPayOut = new JButton("Pay Out (+4Ah)");
		btnPayOut.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPayOut.setBackground(Color.ORANGE);
		btnPayOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4a, (byte) 0x1, (byte) 0x1,
						protocol.jcmMessage);
			}
		});
		btnPayOut.setBounds(873, 457, 161, 50);
		panel_comandos.add(btnPayOut);

		JButton btnCollect = new JButton("Collect (+4Bh+Data)");
		btnCollect.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCollect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCollect.setBounds(10, 399, 179, 50);
		panel_comandos.add(btnCollect);

		JButton btnClear = new JButton("Clear (+4Ch)");
		btnClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnClear.setBackground(Color.ORANGE);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4C, (byte) 0x1, (byte) 0x2,
						protocol.jcmMessage);
			}
		});
		btnClear.setBounds(195, 399, 144, 50);
		panel_comandos.add(btnClear);

		JButton btnEmergencyStop = new JButton("Emergency Stop (+4Dh)");
		btnEmergencyStop.setFont(new Font("Dialog", Font.BOLD, 12));
		btnEmergencyStop.setBackground(Color.ORANGE);
		btnEmergencyStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x9, (byte) 0xf0, (byte) 0x20, (byte) 0x4D, (byte) 0x1, (byte) 0x2,
						protocol.jcmMessage);
			}
		});
		btnEmergencyStop.setBounds(351, 399, 187, 50);
		panel_comandos.add(btnEmergencyStop);

		JButton btnUnitInformationRequest = new JButton("Unit Information Req (92h)");
		btnUnitInformationRequest.setFont(new Font("Dialog", Font.BOLD, 12));
		btnUnitInformationRequest.setBackground(Color.ORANGE);
		btnUnitInformationRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 0x5, (byte) 0x92, protocol.jcmMessage, false);
			}
		});
		btnUnitInformationRequest.setBounds(313, 459, 205, 50);
		panel_comandos.add(btnUnitInformationRequest);

		JButton btnRecycleRefillModeSetting = new JButton("Recycle Refill Mode Setting (D4h+Data)");
		btnRecycleRefillModeSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleRefillModeSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeSetting.setBounds(1314, 399, 289, 50);
		panel_comandos.add(btnRecycleRefillModeSetting);

		JButton btnRecycleKeySetting = new JButton("Recycle Key Setting (+D1h+Data)");
		btnRecycleKeySetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleKeySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleKeySetting.setBounds(814, 399, 244, 50);
		panel_comandos.add(btnRecycleKeySetting);

		JButton btnRecycleCountSetting = new JButton("Recycle Count Setting (+D2h+Data)");
		btnRecycleCountSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCountSetting.setBackground(Color.LIGHT_GRAY);
		btnRecycleCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleCountSetting.setBounds(1068, 399, 236, 50);
		panel_comandos.add(btnRecycleCountSetting);

		JButton btnRecycleCurrencySetting = new JButton("Recycle Currency Setting (+D0h+Data)");
		btnRecycleCurrencySetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCurrencySetting.setBackground(Color.ORANGE);
		btnRecycleCurrencySetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				protocol.jcmMessage[7] = 0x01;
				protocol.jcmMessage[8] = 0x20; // 0x02:20 0x04:50 0x08:100 0x10:200 0x20:500;
				protocol.jcmMessage[9] = 0x00;
				protocol.jcmMessage[10] = 0x02;
				jcms[0].id003_format_ext((byte) 0x0D, (byte) 0xf0, (byte) 0x20, (byte) 0xD0, (byte) 0x20, (byte) 0x0,
						protocol.jcmMessage);
			}
		});
		btnRecycleCurrencySetting.setBounds(548, 399, 256, 50);
		panel_comandos.add(btnRecycleCurrencySetting);

		JButton btnCurrentCountSetting = new JButton("Current Count\u00A0Setting (E2h+Data)");
		btnCurrentCountSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCurrentCountSetting.setBounds(10, 458, 289, 50);
		panel_comandos.add(btnCurrentCountSetting);

		JButton btnRecycleCurrencyReqSetting = new JButton("Recycle Currency Req (+90h)");
		btnRecycleCurrencyReqSetting.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCurrencyReqSetting.setBackground(Color.ORANGE);
		btnRecycleCurrencyReqSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x90, (byte) 0x40, (byte) 0x0,
						protocol.jcmMessage);
			}
		});
		btnRecycleCurrencyReqSetting.setBounds(689, 569, 205, 50);
		panel_comandos.add(btnRecycleCurrencyReqSetting);

		JButton btnRecycleSoftwareVersionReq = new JButton("Recycle Software Version Req (+93h)");
		btnRecycleSoftwareVersionReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleSoftwareVersionReq.setBackground(Color.ORANGE);
		btnRecycleSoftwareVersionReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0x93, (byte) 0x00, (byte) 0x0,
						protocol.jcmMessage);
			}
		});
		btnRecycleSoftwareVersionReq.setBounds(1342, 569, 256, 50);
		panel_comandos.add(btnRecycleSoftwareVersionReq);

		JLabel lblNewLabel_2_1_2 = new JLabel("Setting Status Request (F0h + )");
		lblNewLabel_2_1_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_2.setBounds(17, 518, 205, 50);
		panel_comandos.add(lblNewLabel_2_1_2);

		JButton btnRecycleKeySettingReq = new JButton("Recycle Key Setting Req (+91h)");
		btnRecycleKeySettingReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleKeySettingReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleKeySettingReq.setBounds(904, 569, 224, 50);
		panel_comandos.add(btnRecycleKeySettingReq);

		JButton btnRecycleCountReq = new JButton("Recycle Count Req (+92h)");
		btnRecycleCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleCountReq.setBackground(Color.ORANGE);
		btnRecycleCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format((byte) 0x5, (byte) 0x92, protocol.jcmMessage, false);
			}
		});
		btnRecycleCountReq.setBounds(1140, 569, 187, 50);
		panel_comandos.add(btnRecycleCountReq);

		JButton btnRecycleRefillModeReq = new JButton("Recycle Refill Mode Req (+94h)");
		btnRecycleRefillModeReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnRecycleRefillModeReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRecycleRefillModeReq.setBounds(10, 569, 218, 50);
		panel_comandos.add(btnRecycleRefillModeReq);

		JButton btnTotalCountReq = new JButton("Total Count Req (+A0h)");
		btnTotalCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountReq.setBackground(Color.ORANGE);
		btnTotalCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA0, (byte) 0x00, (byte) 0x0,
						protocol.jcmMessage);
			}
		});
		btnTotalCountReq.setBounds(235, 569, 224, 50);
		panel_comandos.add(btnTotalCountReq);

		JButton btnTotalCountClear = new JButton("Total Count Clear (+A1h)");
		btnTotalCountClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnTotalCountClear.setBackground(Color.ORANGE);
		btnTotalCountClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA1, (byte) 0x00, (byte) 0x0,
						protocol.jcmMessage);
			}
		});
		btnTotalCountClear.setBounds(469, 569, 210, 50);
		panel_comandos.add(btnTotalCountClear);

		JButton btnCurrentCountReq = new JButton("Current Count Req (+A2h)");
		btnCurrentCountReq.setFont(new Font("Dialog", Font.BOLD, 12));
		btnCurrentCountReq.setBackground(Color.ORANGE);
		btnCurrentCountReq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jcms[0].id003_format_ext((byte) 0x07, (byte) 0xf0, (byte) 0x20, (byte) 0xA2, (byte) 0x00, (byte) 0x0,
						protocol.jcmMessage);
			}
		});
		btnCurrentCountReq.setBounds(10, 630, 236, 50);
		panel_comandos.add(btnCurrentCountReq);

		JButton btnReinhibitch = new JButton("REInhibit (C3h)");
		btnReinhibitch.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnReinhibitch.setBackground(Color.ORANGE);
		btnReinhibitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("RE INHIBIT");
				protocol.jcmMessage[3] = 0x00;
				jcms[0].id003_format((byte) 0x6, (byte) 0xC3, protocol.jcmMessage, false);
			}
		});
		btnReinhibitch.setBounds(578, 119, 144, 50);
		panel_comandos.add(btnReinhibitch);

		JButton btnCurrencyAssingRequest = new JButton("Currency Assing Req (8Ah)");
		btnCurrencyAssingRequest.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnCurrencyAssingRequest.setBounds(1326, 223, 224, 50);
		panel_comandos.add(btnCurrencyAssingRequest);
		btnCurrencyAssingRequest.setBackground(Color.ORANGE);
		
		JCheckBox chckbxReciclador1 = new JCheckBox("Reciclador 1");
		chckbxReciclador1.setSelected(true);
		
		chckbxReciclador1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador1.setBounds(18, 24, 148, 51);
		panelComandos.add(chckbxReciclador1);
		
		JCheckBox chckbxReciclador2 = new JCheckBox("Reciclador 2");		
		chckbxReciclador2.setSelected(true);
		chckbxReciclador2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		chckbxReciclador2.setBounds(194, 24, 148, 51);
		panelComandos.add(chckbxReciclador2);
		btnCurrencyAssingRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(chckbxReciclador1.isSelected())
					jcms[0].id003_format((byte) 5, (byte) 0x8A, protocol.jcmMessage, true);
				if(chckbxReciclador2.isSelected())
					jcms[1].id003_format((byte) 5, (byte) 0x8A, protocol.jcmMessage, true);
			}
		});

		/* ------ BORRAR ------------ */
		
		MyClass c = new MyClass();
		c.addMyEventListener(new MyEventListener() {
			public void myEventOccurred(MyEvent evt) {
				System.out.println("myEventOccurred");
				if (evt.getSource() == "version") {
					label.setText(new String(uart.version));
				} else if (evt.getSource() == "bill") {
					lblBilleteIngresado1.setText(String.format("%d", uart.bill));
				} else if (evt.getSource() == "clearbill") {
					lblBilleteIngresado1.setText("");
				} else if (evt.getSource() == "recyclerVersion") {
					lblRecyclerVersion.setText(new String(uart.recyclerVersion));
				} else if (evt.getSource() == "recyclerBillsA") {
					lblRecycler1.setText(uart.recyclerOneA + " " + uart.recyclerOneB);
				}

			}
		});
		// ![2]

		
		
		
		//Identificamos los puertos disponibles
		uart.portList = CommPortIdentifier.getPortIdentifiers();
		contador = 0;
		
		while (uart.portList.hasMoreElements()) {
									
			CommPortIdentifier commPort = (CommPortIdentifier) uart.portList.nextElement();
					
			
        	//Checamos que sea un com{x} port
			if (commPort.getName().toUpperCase().contains("COM")) {
				
				jcms[contador] = new uart();
				jcms[contador].portId = commPort;
				jcms[contador].baud = 9600;
				jcms[contador].openPort(commPort.getName().toString());				
				contador++;
			}
			
			
			/*
			uart.portId = (CommPortIdentifier) uart.portList.nextElement();
						
			//if (uart.portId.getName().equals("COM5")) 
        	//Checamos que sea un com{x} port
			if (uart.portId.getName().toUpperCase().contains("COM")) {
				cbPuerto.addItem(uart.portId.getName().toString());
				
				//Abrimos la conexion
				uart.baud = 9600;
				srlprt1.openPort(uart.portId.getName().toString());
			}
			*/			
		}
	}

	public static String baitsToString(String texto, byte[] baits) {
		String result = texto;
		for (byte theByte : baits) {
			result += " [" + Integer.toHexString(theByte) + "] ";
		}
		return result;
	}

	static public String customFormat(String pattern, double value) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter.format(value);
	}

	private void textoMontoValidacion(String digito, JLabel lblMonto, JLabel lblAutorizacion) {
		if (retiroMonto) {
			if (((montoDispensar.length() > 6) || Long.parseLong(montoDispensar) > maxDispenseAmmount))
				return;

			montoDispensar += digito;
			lblMonto.setText(customFormat("$###,###.###", Long.parseLong(montoDispensar)));
			return;
		}

		if (retiroAutorizacion) {
			if (asteriscos.length() > 5)
				return;

			asteriscos += "*";
			lblAutorizacion.setText(asteriscos);
		}
	}
}
