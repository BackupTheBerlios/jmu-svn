package jmu.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import jmu.net.MooConnection;

/**
 * @author ritter
 * 
 * Provides an input field and output window, for a user's interaction with a 
 * MOO. This class needs a Thread in order to read input from the connection.
 */
public class MooWindow extends JPanel implements Runnable, ActionListener {

	public static final int SCROLLBACK_HACK_FACTOR = 10;

	private final ResourceBundle translations;

	private final MooConnection conn;

	private final JTextPane text;
	private final JScrollPane textScroller;
	private final JScrollBar vertScrollBar;

	private final StyledDocument doc;
	private final AnsiStyle style;

	private final JComboBox input;
	private final JButton send;

	private boolean running = false;

	private Exception ex;

	/**
	 * Creates a new MooWindow, that read reads and writes from the specified 
	 * connection.
	 * 
	 * @param conn The connection to use when communicating with the MOO.
	 */
	public MooWindow(final MooConnection conn, final ResourceBundle translations) {
		this.translations = translations;

		this.conn = conn;

		text = new JTextPane();
		text.setEditable(false);
		text.setBackground(AnsiStyle.COLOURS[0][0]);
		text.setForeground(AnsiStyle.COLOURS[0][7]);

		textScroller = new JScrollPane(text);
		vertScrollBar = textScroller.getVerticalScrollBar();

		doc = text.getStyledDocument();
		style = new AnsiStyle();

		text.setPreferredSize(new Dimension(640, 480));

		input = new JComboBox();
		input.setEditable(true);
		input.addActionListener(this);

		send = new JButton(translations.getString("Send"));
		send.addActionListener(this);

		__layoutComponents();
		__initTextStyles();
	}

	/**
	 * Signals the window to stop reading data from the connection.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Closes the connection to the MOO. The user should probably use the 
	 * MOO provided quit command.
	 * @throws IOException if an I/O exception is encountered while closing the
	 * 		connection
	 */
	public void close() throws IOException {
		conn.close();
	}

	/**
	 * Sends the text in the input window to the MOO. Also adds it to the 
	 * command history.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent ae) {
		if (ae.getActionCommand().equals("comboBoxEdited")) {
			final ComboBoxEditor inputEditor = input.getEditor();
			final String text = inputEditor.getItem().toString();

			input.addItem(text);
			input.getEditor().selectAll();
			try {
				conn.send(text);
			} catch (IOException ioe) {
				running = false;
				ex = ioe;
			}
		}
	}

	/**
	 * Processes the data coming in from the MOO server.
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (running) {
			return;
		} else {
			running = true;
		}

		boolean scrollDown;
		int linePos;
		Matcher matcher;

		while (running) {
			try {
				while (!conn.ready()) {
					if (!running) {
						return;
					}
					Thread.yield();
				}

				scrollDown = false;

				int sbAnchorPos =
					vertScrollBar.getMaximum() - textScroller.getHeight();
				sbAnchorPos = (sbAnchorPos < 0) ? 0 : sbAnchorPos;

				if (Math.abs(sbAnchorPos - vertScrollBar.getValue())
					< SCROLLBACK_HACK_FACTOR) {
					scrollDown = true;
				}

				String line = conn.read();
				if (!line.endsWith("\n")) {
					line += "\n";
				}

				matcher = style.getMatcher(line);

				linePos = 0;

				while (linePos < line.length()) {
					if (matcher.find()) {
						addString(line.substring(linePos, matcher.start()));
						style.setColour(matcher.group());
						text.setCharacterAttributes(
							text.getStyle(style.toString()),
							true);
						linePos = matcher.end();
					} else {
						addString(line.substring(linePos));
						linePos = line.length();
					}
				}

				if (scrollDown) {
					vertScrollBar.setValue(vertScrollBar.getMaximum());
				}

			} catch (IOException ioe) {
				ex = ioe;
				running = false;
			} catch (BadLocationException ble) {
				ex = ble;
				running = false;
			}
		}
	}

	/**
	 * Adds the text to the end of the output window.
	 * @param string the String to add
	 * @throws BadLocationException should never happen
	 */
	private void addString(final String string) throws BadLocationException {
		doc.insertString(
			doc.getLength(),
			string,
			text.getStyle(style.toString()));
	}

	/**
	 * Does the hard work of laying out the window components.
	 */
	private void __layoutComponents() {
		setLayout(new GridBagLayout());

		final GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 100;
		c.weighty = 100;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		add(textScroller, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(input, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		add(send, c);
	}

	/**
	 * Initialises the text styles that ANSI is capable of producing.
	 */
	private void __initTextStyles() {
		// default

		Style def = text.addStyle("def", null);
		StyleConstants.setAlignment(def, StyleConstants.ALIGN_LEFT);
		StyleConstants.setBackground(def, AnsiStyle.COLOURS[0][0]);
		StyleConstants.setForeground(def, AnsiStyle.COLOURS[0][7]);
		StyleConstants.setFontFamily(def, "monospaced");
		StyleConstants.setFontSize(def, 12);

		AnsiStyle ansi = new AnsiStyle();

		for (int st = 0; st < AnsiStyle.STYLES.length; st++) {
			ansi.setStyle(st);
			for (int fg = 0; fg < AnsiStyle.COLOUR_NAMES.length; fg++) {
				ansi.setForeground(fg);
				for (int bg = 0; bg < AnsiStyle.COLOUR_NAMES.length; bg++) {
					ansi.setBackground(bg);

					Style style = text.addStyle(ansi.toString(), def);

					StyleConstants.setBackground(
						style,
						AnsiStyle.COLOURS[0][bg]);

					StyleConstants.setForeground(
						style,
						AnsiStyle.COLOURS[st][fg]);
				}
			}
		}

	}

}
