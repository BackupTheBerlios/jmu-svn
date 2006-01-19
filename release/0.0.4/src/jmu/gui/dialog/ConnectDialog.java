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
	
	private final ResourceBundle translations;
	
	private final MainWindow main;
	
	private final JButton connect;
	private final JButton cancel;
	
	private final JLabel hostLabel;
	private final JLabel portLabel;
	
	private final JTextField host;
	private final JTextField port;
	
	public ConnectDialog(final MainWindow win, final ResourceBundle translations) {
		super(win, translations.getString("ConnectDialog"), true);
		main = win;
		
		this.translations = translations;
		
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
	public void actionPerformed(final ActionEvent ae) {
		final Object source = ae.getSource();
		
		if (source == connect) {
			dispose();
			main.connect(host.getText(), port.getText());
		} else {
			dispose();
		}
	}
	
	private void __layoutComponents() {
		final Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		
		final GridBagConstraints c = new GridBagConstraints();
		
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
