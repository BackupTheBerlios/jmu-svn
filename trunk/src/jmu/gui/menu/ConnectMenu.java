package jmu.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import jmu.gui.MainWindow;
import jmu.gui.MooWindow;
import jmu.gui.dialog.ConnectDialog;

/**
 * @author ritter
 */
public class ConnectMenu extends JMenu implements ActionListener {

	private ResourceBundle translations;

	private MainWindow main;

	private JMenuItem connect;
	private JMenuItem disconnect;
	private JMenuItem exit;

	public ConnectMenu(MainWindow win, String title) {
		super(title);
		translations = win.getTranslations();
		main = win;

		connect = new JMenuItem(translations.getString("ConnectDialog"));
		connect.addActionListener(this);
		add(connect);

		disconnect = new JMenuItem(translations.getString("Disconnect"));
		disconnect.addActionListener(this);
		add(disconnect);

		exit = new JMenuItem(translations.getString("Exit"));
		exit.addActionListener(this);
		add(exit);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();

		if (source == connect) {
			ConnectDialog dialog =
				new ConnectDialog(
					main,
					translations.getString("ConnectDialog"));
			dialog.show();
		} else if (source == disconnect) {
			JTabbedPane mooTabs = main.getMooTabs();
			MooWindow closee = (MooWindow)mooTabs.getSelectedComponent();
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
