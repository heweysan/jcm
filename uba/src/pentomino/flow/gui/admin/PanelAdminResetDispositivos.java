package pentomino.flow.gui.admin;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import pentomino.common.jcmOperation;
import pentomino.flow.Flow;
import pentomino.flow.protocol;
import pentomino.flow.gui.ImagePanel;

public class PanelAdminResetDispositivos  extends ImagePanel {

	private static final long serialVersionUID = 1L;

	private JButton btnRegresar;


	/**
	 * @wbp.parser.constructor
	 */
	public PanelAdminResetDispositivos(String img,String name, int _timeout, ImagePanel _redirect) {
		super(img,name,_timeout,_redirect);
		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);

	}	

	@Override
	public void ContentPanel() {


		setBounds(0, 0, 1920, 1080);
		setOpaque(false);
		setBorder(null);
		setLayout(null);


		btnRegresar = new JButton(new ImageIcon("./images/BTN_7p_Admin_Regresar.png"));
		btnRegresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Flow.redirect(Flow.panelAdminEstatusDispositivos);	
			}
		});
		btnRegresar.setBackground(Color.BLUE);
		btnRegresar.setOpaque(false);
		btnRegresar.setContentAreaFilled(false);
		btnRegresar.setBorderPainted(false);
		btnRegresar.setBounds(1336, 863, 574, 171);
		add(btnRegresar);

		JButton btnResetIzq = new JButton("Reciclador Izq");
		btnResetIzq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screenTimerCancel();
				Flow.jcms[1].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...
				Flow.jcms[1].id003_format((byte)5, protocol.SSR_VERSION, Flow.jcms[1].jcmMessage,true); //SSR_VERSION 0x88
			}
		});
		btnResetIzq.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnResetIzq.setBounds(10, 350, 565, 255);
		add(btnResetIzq);

		JButton btnResetDer = new JButton("Reciclador Der");
		btnResetDer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screenTimerCancel();
				Flow.jcms[0].currentOpertion = jcmOperation.Reset;
				//Primero hacemos los get versions...				
				Flow.jcms[0].id003_format((byte)5, protocol.SSR_VERSION, Flow.jcms[0].jcmMessage,true); //SSR_VERSION 0x88
			}
		});
		btnResetDer.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnResetDer.setBounds(1345, 350, 565, 255);
		add(btnResetDer);

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