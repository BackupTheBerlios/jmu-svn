package jmu.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

import jmu.gui.MainWindow;

/**
 * @author ritter
 */
public class ExceptionDialog extends JDialog implements ActionListener {

	private ResourceBundle translations;
	
	private MainWindow main;
	
	private JTextArea traceWin;
	
	private JButton ok;
	
	public ExceptionDialog(MainWindow win, String title, Exception ex) {
		super(win, title, true);
		main = win;
		
		translations = ResourceBundle.getBundle("jmu.data.i18n.Translations");
		
		StackTraceElement[] st = ex.getStackTrace();
		StringBuffer trace = new StringBuffer(ex.toString());
		for (int i = 0; i < st.length; i++) {
			trace.append('\t');
			trace.append(st[i]);
			trace.append('\n');
		}
		
		traceWin = new JTextArea(40, 10);
		traceWin.setEditable(false);
		traceWin.setText(trace.toString());
		
		ok = new JButton(translations.getString("OK"));
		ok.addActionListener(this);
		
		getContentPane().add(traceWin);
		getContentPane().add(ok);
		
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}

}
