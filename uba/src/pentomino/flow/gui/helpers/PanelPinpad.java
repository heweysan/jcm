package pentomino.flow.gui.helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import pentomino.flow.gui.PinKey;
import pentomino.flow.gui.PinpadEvent;
import pentomino.flow.gui.PinpadListener;

public class PanelPinpad {
	
	public JPanel contentPanel;
	//https://www.javaworld.com/article/2077333/mr-happy-object-teaches-custom-events.html
	
	private List<PinpadListener> _listeners = new ArrayList<PinpadListener>();
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public PanelPinpad() {
		
		contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setBackground(Color.blue);
		contentPanel.setBounds(0, 0, 642, 94);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);
		
		contentPanel.setOpaque(false);
		contentPanel.setBackground(Color.GRAY);
		contentPanel.setBounds(936, 0, 946, 1080);
		contentPanel.setBorder(null);
		contentPanel.setLayout(null);


		JButton btnPinPad1 = new JButton(new ImageIcon("./images/BTN7_1.png"));
		btnPinPad1.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad1.setBounds(50, 47, 260, 220);
		btnPinPad1.setOpaque(false);
		btnPinPad1.setContentAreaFilled(false);
		btnPinPad1.setBorderPainted(false);
		contentPanel.add(btnPinPad1);

		JButton btnPinPad2 = new JButton(new ImageIcon("./images/BTN7_2.png"));
		btnPinPad2.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad2.setBounds(359, 47, 262, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad2.setContentAreaFilled(false);
		btnPinPad2.setBorderPainted(false);
		contentPanel.add(btnPinPad2);

		JButton btnPinPad3 = new JButton(new ImageIcon("./images/BTN7_3.png"));
		btnPinPad3.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad3.setBounds(684, 47, 262, 220);
		btnPinPad3.setOpaque(false);
		btnPinPad3.setContentAreaFilled(false);
		btnPinPad3.setBorderPainted(false);
		contentPanel.add(btnPinPad3);

		JButton btnPinPad4 = new JButton(new ImageIcon("./images/BTN7_4.png"));
		btnPinPad4.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad4.setBounds(50, 302, 259, 220);
		btnPinPad4.setOpaque(false);
		btnPinPad4.setContentAreaFilled(false);
		btnPinPad4.setBorderPainted(false);
		contentPanel.add(btnPinPad4);

		JButton btnPinPad5 = new JButton(new ImageIcon("./images/BTN7_5.png"));
		btnPinPad5.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad5.setBounds(359, 302, 262, 220);
		btnPinPad5.setOpaque(false);
		btnPinPad5.setContentAreaFilled(false);
		btnPinPad5.setBorderPainted(false);
		contentPanel.add(btnPinPad5);

		JButton btnPinPad6 = new JButton(new ImageIcon("./images/BTN7_6.png"));
		btnPinPad6.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad6.setBounds(674, 302, 262, 220);
		btnPinPad6.setOpaque(false);
		btnPinPad6.setContentAreaFilled(false);
		btnPinPad6.setBorderPainted(false);
		contentPanel.add(btnPinPad6);

		JButton btnPinPad7 = new JButton(new ImageIcon("./images/BTN7_7.png"));
		btnPinPad7.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad7.setBounds(50, 557, 259, 220);
		btnPinPad7.setOpaque(false);
		btnPinPad7.setContentAreaFilled(false);
		btnPinPad7.setBorderPainted(false);
		contentPanel.add(btnPinPad7);

		JButton btnPinPad8 = new JButton(new ImageIcon("./images/BTN7_8.png"));
		btnPinPad8.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad8.setBounds(359, 557, 267, 220);
		btnPinPad2.setOpaque(false);
		btnPinPad8.setContentAreaFilled(false);
		btnPinPad8.setBorderPainted(false);
		contentPanel.add(btnPinPad8);

		JButton btnPinPad9 = new JButton(new ImageIcon("./images/BTN7_9.png"));
		btnPinPad9.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad9.setBounds(664, 557, 272, 220);
		btnPinPad9.setOpaque(false);
		btnPinPad9.setContentAreaFilled(false);
		btnPinPad9.setBorderPainted(false);
		contentPanel.add(btnPinPad9);

		JButton btnPinPad0 = new JButton(new ImageIcon("./images/BTN7_0.png"));
		btnPinPad0.setFont(new Font("Tahoma", Font.BOLD, 44));
		btnPinPad0.setBounds(359, 812, 267, 220);
		btnPinPad0.setOpaque(false);
		btnPinPad0.setContentAreaFilled(false);
		btnPinPad0.setBorderPainted(false);
		contentPanel.add(btnPinPad0);

		JButton btnPinPadCancel = new JButton(new ImageIcon("./images/BTN7_NO.png"));
		btnPinPadCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadCancel.setBackground(Color.RED);
		btnPinPadCancel.setBounds(50, 812, 259, 220);
		btnPinPadCancel.setOpaque(false);
		btnPinPadCancel.setContentAreaFilled(false);
		btnPinPadCancel.setBorderPainted(false);
		contentPanel.add(btnPinPadCancel);

		JButton btnPinPadConfirmar = new JButton(new ImageIcon("./images/BTN7_OK.png"));		
		btnPinPadConfirmar.setFont(new Font("Tahoma", Font.BOLD, 30));
		btnPinPadConfirmar.setBackground(Color.GREEN);
		btnPinPadConfirmar.setBounds(664, 812, 272, 220);
		btnPinPadConfirmar.setOpaque(false);
		btnPinPadConfirmar.setContentAreaFilled(false);
		btnPinPadConfirmar.setBorderPainted(false);
		contentPanel.add(btnPinPadConfirmar);		
		
		
		btnPinPad1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._1);
			}
		});

		btnPinPad2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._2);
			}
		});

		btnPinPad3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._3);
			}
		});

		btnPinPad4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._4);
			}
		});

		btnPinPad5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._5);
			}
		});

		btnPinPad6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._6);
			}
		});

		btnPinPad7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._7);
			}
		});

		btnPinPad8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._8);
			}
		});

		btnPinPad9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._9);
			}
		});

		btnPinPad0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_firePinKeyEvent(PinKey._0);
			}
		});
		
		

		btnPinPadCancel.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {

				_firePinKeyEvent(PinKey._Cancel);				
				
			}
		});
		
		btnPinPadConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				_firePinKeyEvent(PinKey._Ok);
				
				
			}
		});
		
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
		
	public JPanel getPanel() {
		return contentPanel;
	}
	
    public synchronized void addPinKeyListener( PinpadListener l ) {
    	
        _listeners.add( l );
    }
    
    public synchronized void removePinKeyListener( PinpadListener l ) {
    	
        _listeners.remove( l );
    }
     
    private synchronized void _firePinKeyEvent(PinKey key) {
    	
    	PinpadEvent mood = new PinpadEvent( this, key );
        Iterator<PinpadListener> listeners = _listeners.iterator();
        while( listeners.hasNext() ) {
            ( (PinpadListener) listeners.next() ).pinKeyReceived( mood );
        }
    }


}
