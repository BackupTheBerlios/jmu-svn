package jmu.gui.dialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jmu.data.Moo;
import jmu.gui.MainWindow;
import jmu.xml.moo.MooListHandler;

import org.xml.sax.SAXException;

/**
 * @author dietzla
 */
public class MooListDialog extends JDialog implements ActionListener {

	private final ResourceBundle translations;

	private final MainWindow main;

	private final Moo[] moos;
	
	private final JList mooList;
	private final JButton connect;
	private final JButton cancel;

	public MooListDialog(
		final MainWindow win,
		final ResourceBundle translations)
		throws IOException, ParserConfigurationException, SAXException {
		super(win, translations.getString("MooListDialogTitle"));

		main = win;
		this.translations = translations;

		Vector list = loadMooList();
		if (list != null) {
			moos = new Moo[list.size()];
			final String[] mooNames = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				Moo moo = (Moo)list.elementAt(i);
				moos[i] = moo;
				mooNames[i] = moo.getName();
			}

			mooList = new JList(mooNames);

			connect = new JButton(translations.getString("Connect"));
			connect.addActionListener(this);
		} else {
			moos = new Moo[0];
			mooList = new JList();
			connect = null;
		}
		
		cancel = new JButton(translations.getString("Cancel"));
		cancel.addActionListener(this);
		
		__layoutComponents();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent ae) {
		final Object source = ae.getSource();
		
		if (source == connect) {
			final Moo selected = moos[mooList.getSelectedIndex()];
		
			final String address = selected.getAddresses()[0];
			final int portSep = address.lastIndexOf(':');
			final String host = address.substring(0, portSep);
			final String port = address.substring(portSep + 1);
		
			dispose();
			main.connect(host, port);
		} else {
			dispose();
		}
	}
	
	private Vector loadMooList()
		throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		File list = new File(main.getJmuDir(), "moo-list.xml");

		if (list.canRead()) {
			SAXParser parser = parserFactory.newSAXParser();
			MooListHandler handler = new MooListHandler();

			parser.parse(list, handler);

			return handler.getMooList();

		} else {
			return null;
		}

	}
	
	private void __layoutComponents() {
		final Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		
		final GridBagConstraints c = new GridBagConstraints();
		
		c.gridy = 0; c.gridx = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		pane.add(new JScrollPane(mooList), c);
		
		c.gridy = 1; c.gridx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		
		if (connect != null) {
			pane.add(connect, c);

			c.gridy = 1;
			c.gridx = 1;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.NONE;
		}

		pane.add(cancel, c);
		
		pack();
	}

}
