package jmu.gui.dialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jmu.gui.MainWindow;

/**
 * @author ritter
 */
public class ExceptionDialog extends JDialog implements ActionListener {

	private final ResourceBundle translations;
	
	private final MainWindow main;
	
	private final JTextArea traceWin;
	
	private final JButton ok;
	
	public ExceptionDialog(MainWindow win, String title, Exception ex) {
		super(win, title, true);
		main = win;
		
		translations = win.getTranslations();
		
		final StackTraceElement[] st = ex.getStackTrace();
		final StringBuffer trace = new StringBuffer(ex.toString());
		for (int i = 0; i < st.length; i++) {
			trace.append('\t');
			trace.append(st[i]);
			trace.append('\n');
		}
		
		traceWin = new JTextArea();
		traceWin.setEditable(false);
		traceWin.setText(trace.toString());
		
		ok = new JButton(translations.getString("OK"));
		ok.addActionListener(this);
		
		__layoutComponents();
		
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		dispose();
	}

	private void __layoutComponents() {
		final Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		
		final GridBagConstraints c = new GridBagConstraints();
		
		c.gridy = 0; c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		pane.add(new JScrollPane(traceWin), c);
		
		c.gridy = 1; c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		pane.add(ok, c);
		
		pack();
	}
}
