package jmu.gui;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

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

	private Vector moos;

	public MainWindow(String title) {
		super(title);
		
		translations = ResourceBundle.getBundle("jmu.data.i18n.Translations");
		moos = new Vector();

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		connMenu = new ConnectMenu(this, translations.getString("ConnectMenu"));
		menuBar.add(connMenu);
		
		mooTabs = new JTabbedPane();
		getContentPane().add(mooTabs);

		setSize(640, 480);
	}

	/**
	 * Connects to a MOO, and adds a tab to the window. If the connection fails 
	 * a dialog is popped up with a stack trace.
	 * 
	 * @param host the host to connect to.
	 * @param port the port to connect on, as a decimal string
	 */
	public void connect(String host, String port) {
		try {
			System.err.println("Attempting connection");
			MooConnection mooConn =
				new MooConnection(host, Integer.parseInt(port));
			System.err.println("Connection made");
			MooWindow mooWin = new MooWindow(mooConn);
			moos.add(mooWin);
			mooTabs.add(host + ":" + port, mooWin);
			
			new Thread(mooWin).start();
		} catch (IOException ioe) {
			new ExceptionDialog(this, translations.getString("Exception"), ioe);
		} catch (NumberFormatException nfe) {
			new ExceptionDialog(this, translations.getString("Exception"), nfe);
		}
	}
	
}
