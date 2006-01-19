package jmu.net;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import jmu.gui.MainWindow;
import jmu.gui.MooWindow;
import jmu.gui.dialog.ExceptionDialog;

/**
 *
 */
public class Connector extends Thread {

	private final MainWindow main;
	private final ResourceBundle translations;

	private final Vector pending;
	private final Vector threads;

	private boolean run;
	private long lastClean;

	public Connector(final MainWindow win, final ResourceBundle translations) {
		main = win;
		this.translations = translations;

		pending = new Vector();
		threads = new Vector();

		super.start();

		Runtime.getRuntime().addShutdownHook(
			new ConnectionShutdownHook(threads));
	}
	
	public void request(final String host, final String port) {
		synchronized (pending) {
			pending.add(new PendingConnection(host, port));
			pending.notifyAll();
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		run = true;

		lastClean = System.currentTimeMillis();

		PendingConnection pConn = null;

		while (run) {
			synchronized (pending) {
				waitForRequests();

				if (!run) {
					return;
				}

				pConn = (PendingConnection)pending.remove(0);
			}
			
			processRequest(pConn);
		}
	}

	public void halt() {
		run = false;
		interrupt();
		try {
			join();
		} catch (InterruptedException e) {
		}
	}
	
	private void waitForRequests() {
		while (pending.size() == 0) {
			if (!run) {
				return;
			}

			if (System.currentTimeMillis() - lastClean > 300000) {
				cleanDeadThreads();
				lastClean = System.currentTimeMillis();
			}

			try {
				pending.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	private void cleanDeadThreads() {
		for (final Iterator iter = threads.iterator(); iter.hasNext();) {
			final MooWindow conn = (MooWindow)iter.next();

			if (!conn.isAlive()) {
				iter.remove();
			}
		}
	}
	
	private void processRequest(PendingConnection pConn) {
		if (pConn == null) {
			return;
		}
				
		try {
			final MooConnection mooConn =
				new MooConnection(pConn.host, Integer.parseInt(pConn.port));
			final MooWindow mooWin = new MooWindow(mooConn, translations);

			main.getMooTabs().add(pConn.host + ":" + pConn.port, mooWin);

			threads.add(mooWin);

		} catch (IOException ioe) {
			new ExceptionDialog(
				main,
				translations.getString("Exception"),
				ioe).setVisible(
				true);
		} catch (NumberFormatException nfe) {
			new ExceptionDialog(
				main,
				translations.getString("Exception"),
				nfe).setVisible(
				true);
		}

	}

	private class PendingConnection {
		final String host;
		final String port;

		public PendingConnection(final String _host, final String _port) {
			host = _host;
			port = _port;
		}

	}

	private class ConnectionShutdownHook extends Thread {

		private final Vector threads;

		public ConnectionShutdownHook(Vector _threads) {
			threads = _threads;
		}

		public void run() {
			halt();

			for (final Iterator iter = threads.iterator(); iter.hasNext();) {
				final MooWindow conn = (MooWindow)iter.next();
				conn.interrupt();
			}

		}

	}

}
