package pentomino.gui;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;

public final class SolicitudRetiro {

	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static JDialog generaSolicitudRetiro() {
		//Redone for larger OK button
		
		JPanel myPanel = new JPanel();
		myPanel.setBackground(Color.GREEN);
		myPanel.setLayout(null);
		 
       JOptionPane theOptionPane = new JOptionPane(myPanel,JOptionPane.INFORMATION_MESSAGE);
       
       
       JLabel lblNewLabel = new JLabel("Tienes una solicitud de retiro");
       lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
       lblNewLabel.setBounds(10, 31, 534, 49);
       myPanel.add(lblNewLabel);
       theOptionPane.setBounds(0, 0, 621, 369);
             theOptionPane.setBackground(Color.CYAN);
       
       
	
       JDialog theDialog = theOptionPane.createDialog(null,"SOLICITUD DE RETIRO");
       JPanel buttonPanel = (JPanel)theOptionPane.getComponent(1);
       // get the handle to the Ok button
       JButton buttonOk = (JButton)buttonPanel.getComponent(0);
       // set the text
       buttonOk.setText("OK");
       buttonOk.setFont(new Font("Arial",Font.BOLD,44));
       buttonOk.setPreferredSize(new Dimension(300,200));  //Set Button size here
       buttonOk.validate();
       
       
       theDialog.setBounds(0, 0, 500, 500);
       
       return theDialog;
	}
}
