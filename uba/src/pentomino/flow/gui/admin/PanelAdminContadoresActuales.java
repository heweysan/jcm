package pentomino.flow.gui.admin;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pentomino.common.AccountType;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.jcmagent.RaspiAgent;

import javax.swing.Icon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelAdminContadoresActuales {
	
	public JPanel contentPanel;

	
	static JLabel lbl20 = new JLabel("-");
	static JLabel lbl50 = new JLabel("-");
	static JLabel lbl100 = new JLabel("-");
	static JLabel lbl200 = new JLabel("-");
	static JLabel lbl500 = new JLabel("-");
	static JLabel lbl1000 = new JLabel("-");
	static JLabel lblTotal = new JLabel("-");
	
	public PanelAdminContadoresActuales() {
		
		contentPanel = new JPanel();
		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		
		contentPanel.add(new DebugButtons().getPanel());	
		
		
		JButton btnImprimirContadores = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminImpContadores.png"));
		btnImprimirContadores.setBounds(41, 939, 250, 90);
		btnImprimirContadores.setContentAreaFilled(false);
		btnImprimirContadores.setBorderPainted(false);
		btnImprimirContadores.setOpaque(false);
		btnImprimirContadores.setFont(new Font("Tahoma", Font.BOLD, 40));
		contentPanel.add(btnImprimirContadores);
		
		
		
		
		JLabel lblNewLabel = new JLabel("$20");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel.setBounds(469, 213, 98, 30);
		contentPanel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("$50");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_1.setBounds(469, 261, 98, 30);
		contentPanel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("$100");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2.setBounds(469, 306, 98, 30);
		contentPanel.add(lblNewLabel_2);
		
		JLabel lblNewLabel_2_1 = new JLabel("$200");
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_1.setBounds(469, 347, 98, 30);
		contentPanel.add(lblNewLabel_2_1);
		
		JLabel lblNewLabel_2_2 = new JLabel("$500");
		lblNewLabel_2_2.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_2.setBounds(469, 388, 98, 30);
		contentPanel.add(lblNewLabel_2_2);
		
		JLabel lblNewLabel_2_3 = new JLabel("$1000");
		lblNewLabel_2_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_2_3.setBounds(469, 429, 98, 30);
		contentPanel.add(lblNewLabel_2_3);
		
		
		lbl20.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl20.setBounds(652, 213, 98, 30);
		contentPanel.add(lbl20);
		
		lbl50.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl50.setBounds(652, 261, 98, 30);
		contentPanel.add(lbl50);
		
		
		lbl100.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl100.setBounds(652, 306, 98, 30);
		contentPanel.add(lbl100);
		
		
		lbl200.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl200.setBounds(652, 347, 98, 30);
		contentPanel.add(lbl200);
		
		
		lbl500.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl500.setBounds(652, 388, 98, 30);
		contentPanel.add(lbl500);
		
		
		lbl1000.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lbl1000.setBounds(652, 429, 98, 30);
		contentPanel.add(lbl1000);
		
		JLabel lblNewLabel_3 = new JLabel("TOTAL");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_3.setBounds(444, 484, 129, 35);
		contentPanel.add(lblNewLabel_3);
		
		
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTotal.setBounds(652, 484, 129, 35);
		contentPanel.add(lblTotal);
		
		JButton btnEnviarCeros = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminCeros.png"));
		btnEnviarCeros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				/*TODO: HEWEY AQUI  LO DE BEA */
				doBeaStuff();
				
				Flow.redirect(Flow.panelAdminContadoresEnCeroHolder,30000,"panelAdminMenu");
			}
		});
		btnEnviarCeros.setOpaque(false);
		btnEnviarCeros.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnEnviarCeros.setContentAreaFilled(false);
		btnEnviarCeros.setBorderPainted(false);
		btnEnviarCeros.setBounds(1660, 643, 250, 90);
		contentPanel.add(btnEnviarCeros);
		
		JButton btnSalir = new JButton(new ImageIcon("D:\\Repos\\HeweySan\\jcm\\uba\\images\\Btn_AdminSalir.png"));
		btnSalir.setOpaque(false);
		btnSalir.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnSalir.setContentAreaFilled(false);
		btnSalir.setBorderPainted(false);
		btnSalir.setBounds(1660, 877, 250, 90);
		contentPanel.add(btnSalir);
		
		
		
	}
	
	
	public JPanel getPanel() {
		return contentPanel;
	}
	
	
	public static void GetCurrentCounters() {
	
		int total20;
		int total50;
		int total100;
		int total200;
		int total500;
		int total1000;
		
		total20 = 20 * Integer.parseInt(Config.GetPersistence("Accepted20", ""));;
		total50 = 50 * Integer.parseInt(Config.GetPersistence("Accepted50", ""));;
		total100 = 100 * Integer.parseInt(Config.GetPersistence("Accepted100", ""));;
		total200 = 200 * Integer.parseInt(Config.GetPersistence("Accepted200", ""));;
		total500 = 500 * Integer.parseInt(Config.GetPersistence("Accepted500", ""));;
		total1000 = 1000 * Integer.parseInt(Config.GetPersistence("Accepted1000", ""));;
				
		int total = total20 + total50 + total100 + total200 + total500 + total1000;
		
		lbl20.setText("$" + total20);
		lbl50.setText("$" + total50);
		lbl100.setText("$" + total100);
		lbl200.setText("$" + total200);
		lbl500.setText("$" + total500);
		lbl1000.setText("$" + total1000);
		
		lblTotal.setText("$" + total);
		
		
	}
	
	private static void printValue(int value, int count) {
		switch(value) {
		case 20:
			lbl20.setText("" + count);
			break;
		case 50:
			lbl50.setText("" + count);
			break;
		case 100:
			lbl100.setText("" + count);
			break;
		case 200:
			lbl200.setText("" + count);
			break;
		case 500:
			lbl500.setText("" + count);
			break;
		case 1000:
			lbl1000.setText("" + count);
			break;
		}
	}
	
	
	private static void doBeaStuff() {
		
		int total = 0;
		int resultAceptadosTotal = 0;
		String result = "";
		int resultOrigTotal = 0;
		String resultOrigJson = "";
		String resultAceptadosJson = "";
		try {
			int i = 1;

			while (!Config.GetPersistence(String.format("Cassette%dValue", i),"").isEmpty()) {
				if (i != 1) {
					resultOrigJson += ",";
				}
				int origAux = Integer.parseInt(Config.GetPersistence("Cassette" + i + "Original", "0"));
				int valueAux = Integer.parseInt(Config.GetPersistence("Cassette" + i + "Value", "0"));
				int dispenseAux = Integer.parseInt(Config.GetPersistence("Cassette" + i + "Dispensed", "0"));
				int cantidad = origAux - dispenseAux;
				resultOrigJson += String.format("{{'denominacion':'%d', 'cantidad':'{1}'}}", valueAux, cantidad);
				resultOrigTotal += cantidad * valueAux;
				result += valueAux + "x" + cantidad + "|";
				i++;
			}
		} catch (Exception e) {

		}
		String resultAceptados = "";
		try {
			int denoms[] = new int[] {
				20,
				50,
				100,
				200,
				500,
				1000
			};
			for (int j = 0; j < 6; j++) {
				if (j != 0) {
					resultAceptadosJson += ",";
				}
				int acc = Integer.parseInt(Config.GetPersistence(String.format("Accepted%d", denoms[j]), "0"));
				resultAceptados = resultAceptados + denoms[j] + "x" + acc + "|";
				resultAceptadosJson += String.format("{{'denominacion': '%d', 'cantidad':'{1}'}}", denoms[j], acc);
				resultAceptadosTotal += (int)(denoms[j] * acc);
			}

			total = resultAceptadosTotal + resultOrigTotal;

		} catch (Exception e) {

		}

		/*
		WriteToJournal("ADMIN", total, 0, "", "", Client.Client.Hash["adminUser"].ToString(), "", "", "", "",
		GetAtmId("Financial", GetConfigurationDirective("FullAtmId")), "DOTAR CAPTURA CONTADORES - ANTES DE CERO[" + result + "][" + resultAceptados + "][total=" + total + "]", AccountType.None, Pentomino.Common.TransactionType.Administrative, "", "", 0);
		*/
		
		RaspiAgent.WriteToJournal("ADMIN", total,0, "", CurrentUser.loginUser, "DOTAR CAPTURA CONTADORES - ANTES DE CERO[" + result + "][" + resultAceptados + "][total=" + total + "]", AccountType.None, TransactionType.Administrative);
		
		
		/*
		Client.Client.Hash["resultAceptadosJson"] = resultAceptadosJson;
		Client.Client.Hash["resultAceptadosTotal"] = resultAceptadosTotal.ToString();
		Client.Client.Hash["resultOrigJson"] = resultOrigJson;
		Client.Client.Hash["resultOrigTotal"] = resultOrigTotal.ToString();
		Client.Client.Hash["scU"] = "0";
		
		for (int i = 1; i < 5; i++) {
			Client.Client.Hash["dot" + i] = "0";
		}
		
		*/
		//PrintCounters();

		//return FlowTransitions.ActivityAdminDotar.contadoresDotacion;
		
	}
}
