package jmu.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import jmu.gui.MainWindow;
import jmu.gui.MooWindow;
import jmu.gui.dialog.ConnectDialog;
import jmu.gui.dialog.MooListDialog;

/**
 * @author ritter
 */
public class ConnectMenu extends JMenu implements ActionListener {

	private final ResourceBundle translations;

	private final MainWindow main;

	private final JMenuItem connect;
	private final JMenuItem disconnect;
	private final JMenuItem exit;
	private final JMenuItem mooList;

	public ConnectMenu(MainWindow win, String title) {
		super(title);
		translations = win.getTranslations();
		main = win;

		connect = newItem("ConnectDialog");
		disconnect = newItem("Disconnect");
		exit = newItem("Exit");
		mooList = newItem("MooListDialog");

		add(connect);
		add(mooList);
		addSeparator();

		add(disconnect);
		addSeparator();

		add(exit);
	}

	public JMenuItem newItem(final String translation) {
		final JMenuItem ret =
			new JMenuItem(translations.getString(translation));
		ret.addActionListener(this);
		return ret;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		final Object source = ae.getSource();

		if (source == connect) {
			new ConnectDialog(main, translations).setVisible(true);
		} else if (source == mooList) {
			try {
				new MooListDialog(main, translations).setVisible(true);
			} catch (IOException e) {
				main.showException(e.getLocalizedMessage(), e);
			} catch (ParserConfigurationException e) {
				main.showException(e.getLocalizedMessage(), e);
			} catch (SAXException e) {
				main.showException(e.getLocalizedMessage(), e);
			}
		} else if (source == disconnect) {
			final JTabbedPane mooTabs = main.getMooTabs();
			final MooWindow closee = (MooWindow)mooTabs.getSelectedComponent();
			if (closee != null) {
				try {
					closee.close();
					mooTabs.remove(closee);
				} catch (IOException e) {
					main.showException("Unable to close", e);
				}
			}
		} else if (source == exit) {
			System.exit(0);
		}

	}

}
