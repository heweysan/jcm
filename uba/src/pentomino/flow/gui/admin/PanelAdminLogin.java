package pentomino.flow.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pentomino.cashmanagement.Transactions;
import pentomino.cashmanagement.vo.CMUserVO;
import pentomino.common.AccountType;
import pentomino.common.JcmGlobalData;
import pentomino.common.PinpadMode;
import pentomino.common.TransactionType;
import pentomino.common.jcmOperation;
import pentomino.config.Config;
import pentomino.flow.CurrentUser;
import pentomino.flow.Flow;
import pentomino.flow.gui.DebugButtons;
import pentomino.flow.gui.PanelDeposito;
import pentomino.flow.gui.PanelPinpad;
import pentomino.flow.gui.PinKey;
import pentomino.flow.gui.PinpadEvent;
import pentomino.flow.gui.PinpadListener;
import pentomino.jcmagent.RaspiAgent;


public class PanelAdminLogin extends JPanel implements PinpadListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JPanel contentPanel= new JPanel();
	public JButton btnMenuRetiro;
	public JButton btnMenuDeposito;
	public final static JLabel lblLoginUser = new JLabel("");
	public final static JLabel lblLoginPassword = new JLabel("");
	public final static JLabel lblLoginOpcion = new JLabel(".");
	final JLabel lblLoginRow1 = new JLabel("");
	private Image img;

	public PanelAdminLogin() {

		contentPanel.setBounds(0, 0, 1920, 1080);
		contentPanel.setOpaque(false);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);	
		
		contentPanel.add(new DebugButtons().getPanel());
		
		lblLoginUser.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginUser.setForeground(Color.WHITE);
		lblLoginUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginUser.setBounds(257, 625, 496, 87);
		contentPanel.add(lblLoginUser);
		contentPanel.add(new DebugButtons().getPanel());
		
		
		lblLoginPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginPassword.setForeground(Color.WHITE);
		lblLoginPassword.setFont(new Font("Tahoma", Font.BOLD, 90));
		lblLoginPassword.setBounds(257, 793, 496, 87);
		contentPanel.add(lblLoginPassword);

		contentPanel.add(new DebugButtons().getPanel());
		
		
		lblLoginRow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginRow1.setForeground(Color.WHITE);
		lblLoginRow1.setFont(new Font("Tahoma", Font.BOLD, 60));
		lblLoginRow1.setBounds(89, 70, 837, 70);
		contentPanel.add(lblLoginRow1);
		
		lblLoginOpcion.setFont(new Font("Tahoma", Font.BOLD, 88));
		lblLoginOpcion.setForeground(Color.WHITE);
		lblLoginOpcion.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginOpcion.setBounds(230, 520, 87, 87);   //Este es login sin password
		contentPanel.add(lblLoginOpcion);
		contentPanel.add(new DebugButtons().getPanel());
		
			
		PanelPinpad panelPinpad = new PanelPinpad();
		panelPinpad.addPinKeyListener(this);
		
		contentPanel.add(panelPinpad.getPanel());
	}

	public JPanel getPanel() {
		return contentPanel;
	}
	
	public void pinKeyReceived(PinpadEvent event) {
		
		PinKey digito = event.key();
		
		//Flow.panelLoginHolder.screenTimer.cancel();
		Flow.panelLoginHolder.screenTimerReset(7000,"");

		switch(digito)
        {
        
		case _Cancel:
		
			System.out.println("Es cancel Papawh");        	
			CurrentUser.cleanPinpadData();
			lblLoginUser.setText("");
			lblLoginPassword.setText("");			
			CurrentUser.asteriscos = "";							
			Flow.redirect(Flow.panelOperacionCanceladaHolder,5000, "panelIdle");
		break;
		case _Ok:
			
			switch(CurrentUser.pinpadMode) {
			case loginUser:					
				System.out.println("admin loginUser");
				
				

					
					lblLoginOpcion.setBounds(230, 675, 87, 87);   //Este es password 
					CurrentUser.pinpadMode = PinpadMode.loginPassword;
				
				break;
			case loginPassword:

				System.out.println("admin loginPassword");
				//No ha ingresado su user o pwd
				if(CurrentUser.loginUser.length() <= 0 || CurrentUser.loginPassword.length() <= 0) {						
					CurrentUser.pinpadMode = PinpadMode.loginUser;						
					return;
				}

				System.out.println("Validando usuario....");
				//Validamos el usuario
				
				Flow.redirect(Flow.panelAdminMenuHolder,5000, "panelIdle");

				break;
			
			default:
				break;

			}
		
			break;
		default:
		
		switch(CurrentUser.pinpadMode) {
		case None:
			break;
		case loginUser:
			if (CurrentUser.loginUser.length() > 7)				
				return;
			CurrentUser.loginUser = CurrentUser.loginUser + digito.getDigit();
			lblLoginUser.setText(CurrentUser.loginUser);			
			break;
		case loginPassword:
			if (CurrentUser.loginPassword.length() > 7)
				return;
			CurrentUser.loginPassword += digito.getDigit();
			CurrentUser.asteriscos += "*";
			lblLoginPassword.setText(CurrentUser.asteriscos);
			break;	
		default:
			break;				
		}	
		break;
        }

	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	// Metodo donde le pasaremos la dirección de la imagen a cargar.
	public void setBackground(String imagePath) {

		// Construimos la imagen y se la asignamos al atributo background.
		this.setOpaque(false);
		this.img = new ImageIcon(imagePath).getImage();
		repaint();
	}

}