package jmu.gui;

import java.io.File;
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

	private final File homeDir;
	private final File jmuDir;

	private final ResourceBundle translations;

	private final JMenuBar menuBar;
	private final ConnectMenu connMenu;

	private final JTabbedPane mooTabs;

	public MainWindow(final String title, final ResourceBundle translations) {
		super(title);
		this.translations = translations;

		homeDir = new File(System.getProperty("user.home"));
		File _jmuDir = new File(homeDir, ".jmu");

		if (!_jmuDir.exists() && homeDir.canWrite()) {
			if (!_jmuDir.mkdirs()) {
				jmuDir = null;
			} else {
				jmuDir = _jmuDir;
			}
		} else {
			jmuDir = _jmuDir;
		}

		menuBar = new JMenuBar();
		connMenu = new ConnectMenu(this, translations.getString("ConnectMenu"));
		mooTabs = new JTabbedPane();

		__layoutComponents();
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
					MooWindow mooWin =
						new MooWindow(mooConn, getTranslations());
					getMooTabs().add(host + ":" + port, mooWin);

					new Thread(mooWin).start();
				} catch (IOException ioe) {
					new ExceptionDialog(
						mwin,
						getTranslations().getString("Exception"),
						ioe)
						.setVisible(true);
				} catch (NumberFormatException nfe) {
					new ExceptionDialog(
						mwin,
						getTranslations().getString("Exception"),
						nfe)
						.setVisible(true);
				}
			}
		}).start();
	}

	public File getJmuDir() {
		return jmuDir;
	}

	public void showException(final String title, final Exception ex) {
		ExceptionDialog dialog = new ExceptionDialog(this, title, ex);
		dialog.setVisible(true);
	}

	private void __layoutComponents() {
		setJMenuBar(menuBar);
		menuBar.add(connMenu);
		getContentPane().add(mooTabs);
		setSize(640, 480);
	}

}
