package pentomino.flow.gui.admin;



import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pentomino.common.AccountType;
import pentomino.common.BusinessEvent;
import pentomino.common.TransactionType;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.ImagePanel;
import pentomino.jcmagent.BEA;
import pentomino.jcmagent.RaspiAgent;

public class PanelAdminDotarResultados extends ImagePanel{

	

	public static JLabel lblMensaje = new JLabel("");
	
	private static final long serialVersionUID = 1L;


	public PanelAdminDotarResultados(String img,String name) {
		this(new ImageIcon(img).getImage(),name);
	}

	public PanelAdminDotarResultados(Image img,String name, int _timeout, String _redirect) {
		super(img,name,_timeout,_redirect);
	}	

	public PanelAdminDotarResultados(Image img, String name) {
		super(img,name);
	}
	

	@Override
	public void ContentPanel() {
		
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);	

		add(new DebugButtons().getPanel());	


		lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblMensaje.setBounds(10, 213, 1900, 84);
		add(lblMensaje);


		JButton btnImprimirContadores = new JButton(new ImageIcon("./images/Btn_AdminImpContadores.png"));
		btnImprimirContadores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);	
			}
		});
		btnImprimirContadores.setBounds(41, 939, 250, 90);
		btnImprimirContadores.setContentAreaFilled(false);
		btnImprimirContadores.setBorderPainted(false);
		btnImprimirContadores.setOpaque(false);
		btnImprimirContadores.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(btnImprimirContadores);

		JButton btnRegresar = new JButton(new ImageIcon("./images/Btn_AdminRegresar.png"));
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.redirect(Flow.panelAdminMenu);	
			}
		});
		btnRegresar.setOpaque(false);
		btnRegresar.setFont(new Font("Tahoma", Font.BOLD, 40));
		btnRegresar.setContentAreaFilled(false);
		btnRegresar.setBorderPainted(false);
		btnRegresar.setBounds(1660, 877, 250, 90);
		add(btnRegresar);

	}

	public static boolean actualizaContadoresCeros() {

		System.out.println("actualizaContadoresCeros");

		String d20 = Config.GetPersistence("Accepted20", "-1");
		String d50 = Config.GetPersistence("Accepted50", "-1");
		String d100 = Config.GetPersistence("Accepted100", "-1");
		String d200 = Config.GetPersistence("Accepted200", "-1");
		String d500 = Config.GetPersistence("Accepted500", "-1");
		String d1000 = Config.GetPersistence("Accepted1000", "-1");

		String resultOrigJson = "{{'denominacion':'20', 'cantidad':'" + d20 + "'}},{{'denominacion':'50', 'cantidad':'" + d50 + "'}}";
		resultOrigJson += "{{'denominacion':'100', 'cantidad':'" + d100 + "'}},{{'denominacion':'200', 'cantidad':'" + d200 + "'}}";
		resultOrigJson += "{{'denominacion':'500', 'cantidad':'" + d500 + "'}},{{'denominacion':'1000', 'cantidad':'" + d1000 + "'}}";

		int resultOrigTotal = 0;
		resultOrigTotal += (Integer.parseInt(d20) * 20);
		resultOrigTotal += (Integer.parseInt(d50) * 50);
		resultOrigTotal += (Integer.parseInt(d100) * 100);
		resultOrigTotal += (Integer.parseInt(d200) * 200);
		resultOrigTotal += (Integer.parseInt(d500) * 500);
		resultOrigTotal += (Integer.parseInt(d1000) * 1000);

		String result = "20x" + d20 + "|";
		result += "50x" + d50 + "|";
		result += "100x" + d100 + "|";
		result += "200x" + d200 + "|";
		result += "500x" + d500 + "|";
		result += "1000x" + d1000;

		String resultAceptados = result;
		int resultAceptadosTotal = resultOrigTotal;
		String resultAceptadosJson = resultOrigJson;

		/*
		String resultFinalJson = "{{'denominacion':'20', 'cantidad':'0'}},{{'denominacion':'50', 'cantidad':'0'}}";
		resultFinalJson += "{{'denominacion':'100', 'cantidad':'0'}},{{'denominacion':'200', 'cantidad':'0'}}";
		resultFinalJson += "{{'denominacion':'500', 'cantidad':'0'}},{{'denominacion':'1000', 'cantidad':'0'}}";

		int resultFinalTotal = 0;
		 */

		//int total =  resultAceptadosTotal + resultOrigTotal;		

		RaspiAgent.WriteToJournal("ADMIN", resultAceptadosTotal,0, "", CurrentUser.loginUser, "DOTAR CAPTURA CONTADORES - ANTES DE CERO[" + resultAceptados + "][][total=" + resultAceptadosTotal + "]", AccountType.None, TransactionType.Administrative);

		/*
	       string json =  String.Format("{{'type': 'Dotacion'
                   , 'billetesAntes': [{0}]
                   , 'billetesAntesTotal': '{1}'
                   , 'billetesDespues':[{2}]
                   , 'billetesDespuesTotal': '{3}'
                   , 'billetesAceptados':[{4}]
                   , 'billetesAceptadosTotal': '{5}'}}"
                   , Client.Client.Hash.Get("resultOrigJson","")
                   , Client.Client.Hash.Get("resultOrigTotal","")
                   ,resultFinalJson
                   ,resultFinalTotal
                   ,Client.Client.Hash.Get("resultAceptadosJson","")
                   , Client.Client.Hash.Get("resultAceptadosTotal",""));
		 */

		//Actualizamos los datos de BEA
		String json = "{{'type': 'Dotacion' "
				+ ", 'billetesAntes': []"
				+ ", 'billetesAntesTotal': '0'"
				+ ", 'billetesDespues':[]"
				+ ", 'billetesDespuesTotal': '0'"
				+ ", 'billetesAceptados':[" + resultAceptadosJson + "]"
				+ ", 'billetesAceptadosTotal': '" + resultAceptadosTotal + "'}}";



		//Setemos los contadores a 0 de accepted que son los unicos que se llevan
		Config.SetPersistence("Accepted20", "0");
		Config.SetPersistence("Accepted50", "0");
		Config.SetPersistence("Accepted100", "0");
		Config.SetPersistence("Accepted200", "0");
		Config.SetPersistence("Accepted500", "0");
		Config.SetPersistence("Accepted1000", "0");


		//Validamos que sea cierto!!

		d20 = Config.GetPersistence("Accepted20", "-1");
		d50 = Config.GetPersistence("Accepted50", "-1");
		d100 = Config.GetPersistence("Accepted100", "-1");
		d200 = Config.GetPersistence("Accepted200", "-1");
		d500 = Config.GetPersistence("Accepted500", "-1");
		d1000 = Config.GetPersistence("Accepted1000", "-1");

		boolean ok20 = true;
		boolean ok50 = true;
		boolean ok100 = true;
		boolean ok200 = true;
		boolean ok500 = true;
		boolean ok1000 = true;

		if(!d20.equalsIgnoreCase("0"))
			ok20 = false;

		if(!d50.equalsIgnoreCase("0"))
			ok50 = false;

		if(!d100.equalsIgnoreCase("0"))
			ok100 = false;

		if(!d200.equalsIgnoreCase("0"))
			ok200 = false;

		if(!d500.equalsIgnoreCase("0"))
			ok500 = false;

		if(!d1000.equalsIgnoreCase("0"))
			ok1000 = false;

		if(ok20 && ok50 && ok100 && ok200 && ok500 && ok1000) {

			BEA.BusinessEvent(BusinessEvent.CashCollectionEnded, true,false,json);

			String corteCount = Config.GetPersistence("CorteCount","0");
			if (corteCount.isEmpty())
				corteCount = "0";

			Config.SetPersistence("CorteCount",(Integer.parseInt(corteCount)+1) + "");

			return true;
		}



		BEA.BusinessEvent(BusinessEvent.CashCollectionException, true,false,"");
		return false;

	}

	@Override
	public void OnLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnUnload() {
		// TODO Auto-generated method stub
		
	}


}
