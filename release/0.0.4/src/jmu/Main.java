package jmu;

import java.util.ResourceBundle;

import javax.swing.JFrame;

import jmu.gui.MainWindow;

/**
 * @author ritter
 */
public class Main {

	public static void main(final String[] args) {
		MainWindow main =
			new MainWindow(
				"jMu",
				ResourceBundle.getBundle("jmu.data.i18n.Translations"));
		main.setVisible(true);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
