package jmu;

import javax.swing.JFrame;

import jmu.gui.MainWindow;

/**
 * @author ritter
 */
public class Main {

	public static void main(String[] args) {
		MainWindow main = new MainWindow("jMu");
		main.setVisible(true);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
