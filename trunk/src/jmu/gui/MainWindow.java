package jmu.gui;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;

import jmu.gui.dialog.ExceptionDialog;
import jmu.gui.menu.ConnectMenu;
import jmu.net.MooConnection;

/**
 * @author ritter
 */
public class MainWindow extends JFrame {

	private ResourceBundle translations;

	private JMenuBar menuBar;
	private ConnectMenu connMenu;

	private JTabbedPane mooTabs;

	public MainWindow(String title) {
		super(title);

		translations = ResourceBundle.getBundle("jmu.data.i18n.Translations");

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		connMenu = new ConnectMenu(this, translations.getString("ConnectMenu"));
		menuBar.add(connMenu);

		mooTabs = new JTabbedPane();
		getContentPane().add(mooTabs);

		setSize(640, 480);
	}
	
	public JTabbedPane getMooTabs() {
		return mooTabs;
	}
	
	public ResourceBundle getTranslations() {
		return translations;
	}

	/**
	 * Connects to a MOO, and adds a tab to the window. If the connection fails 
	 * a dialog is popped up with a stack trace.
	 * 
	 * @param host the host to connect to.
	 * @param port the port to connect on, as a decimal string
	 */
	public void connect(final String host, final String port) {
		final MainWindow mwin = this;
		new Thread(new Runnable() {
			public void run() {
				try {
					System.err.println("Attempting connection");
					MooConnection mooConn =
						new MooConnection(host, Integer.parseInt(port));
					System.err.println("Connection made");
					MooWindow mooWin = new MooWindow(mooConn, getTranslations());
					getMooTabs().add(host + ":" + port, mooWin);

					new Thread(mooWin).start();
				} catch (IOException ioe) {
					new ExceptionDialog(
						mwin,
						getTranslations().getString("Exception"),
						ioe).show();
				} catch (NumberFormatException nfe) {
					new ExceptionDialog(
						mwin,
						getTranslations().getString("Exception"),
						nfe).show();
				}
			}
		}).start();
	}
	
	public void showException(String title, Exception ex) {
		ExceptionDialog dialog = new ExceptionDialog(this, title, ex);
		dialog.show();
	}

}
