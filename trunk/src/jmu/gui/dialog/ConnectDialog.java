package jmu.gui.dialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jmu.gui.MainWindow;

/**
 * @author ritter
 */
public class ConnectDialog extends JDialog implements ActionListener {
	
	private ResourceBundle translations;
	
	private MainWindow main;
	
	private JButton connect;
	private JButton cancel;
	
	private JLabel hostLabel;
	private JLabel portLabel;
	
	private JTextField host;
	private JTextField port;
	
	public ConnectDialog(MainWindow win, String title) {
		super(win, title, true);
		main = win;
		
		translations = win.getTranslations();
		
		connect = new JButton(translations.getString("Connect"));
		connect.addActionListener(this);
		
		cancel = new JButton(translations.getString("Cancel"));
		cancel.addActionListener(this);
		
		hostLabel = new JLabel(translations.getString("Host"));
		portLabel = new JLabel(translations.getString("Port"));
		
		host = new JTextField(16);
		port = new JTextField(5);
		
		__layoutComponents();
		
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		
		if (source == connect) {
			dispose();
			main.connect(host.getText(), port.getText());
		} else {
			dispose();
		}
	}
	
	private void __layoutComponents() {
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridy = 0; c.gridx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		pane.add(hostLabel, c);
		
		c.gridy = 0; c.gridx = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;
		pane.add(host, c);
		
		c.gridy = 1; c.gridx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		pane.add(portLabel, c);
		
		c.gridy = 1; c.gridx = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.WEST;
		pane.add(port, c);
		
		c.gridy = 2; c.gridx = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		pane.add(connect, c);
		
		c.gridy = 2; c.gridx = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		pane.add(cancel, c);
		
		pack();
	}

}
