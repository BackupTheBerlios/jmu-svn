package jmu.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private ResourceBundle translations;

	private MooConnection conn;

	private JTextPane text;
	private JScrollPane textScroller;
	private JScrollBar vertScrollBar;

	private StyledDocument doc;
	private AnsiStyle style;

	private JTextField input;
	private JButton send;

	private Vector commands;

	private boolean running = false;

	private Exception ex;

	private static final String ANSI_COLOUR_SEQUENCE_REGEX =
		"\\\u001b\\[(\\d?\\d;)*?\\d?\\dm";
	private static final Pattern ANSI_COLOUR_SEQUENCE_PATTERN =
		Pattern.compile(ANSI_COLOUR_SEQUENCE_REGEX);

	private static final String ANSI_COLOUR_PARAMETER_REGEX = "\\d?\\d";
	private static final Pattern ANSI_COLOUR_PARAMETER_PATTERN =
		Pattern.compile(ANSI_COLOUR_PARAMETER_REGEX);

	static final String[] STYLES = { "normal", "bright" };

	static final String[] COLOUR_NAMES =
		{
			"black",
			"red",
			"green",
			"yellow",
			"blue",
			"magenta",
			"cyan",
			"white" };

	static final Color[][] COLOURS =
		{
			{
				new Color(0, 0, 0),
				new Color(192, 0, 0),
				new Color(0, 192, 0),
				new Color(192, 192, 0),
				new Color(0, 0, 192),
				new Color(192, 0, 192),
				new Color(0, 192, 192),
				new Color(192, 192, 192)},
			{
			new Color(128, 128, 128),
				new Color(255, 0, 0),
				new Color(0, 255, 0),
				new Color(255, 255, 0),
				new Color(0, 0, 255),
				new Color(255, 0, 255),
				new Color(0, 255, 255),
				new Color(255, 255, 255)
			}
	};

	/**
	 * Creates a new MooWindow, that read reads and writes from the specified 
	 * connection.
	 * 
	 * @param conn The connection to use when communicating with the MOO.
	 */
	public MooWindow(MooConnection conn) {
		translations = ResourceBundle.getBundle("jmu.data.i18n.Translations");
		
		this.conn = conn;

		text = new JTextPane();
		text.setEditable(false);
		text.setBackground(COLOURS[0][0]);
		text.setForeground(COLOURS[0][7]);

		textScroller = new JScrollPane(text);
		vertScrollBar = textScroller.getVerticalScrollBar();

		doc = text.getStyledDocument();
		style = new AnsiStyle();

		text.setPreferredSize(new Dimension(640, 480));

		input = new JTextField(80);
		input.addActionListener(this);

		send = new JButton(translations.getString("Send"));
		send.addActionListener(this);

		commands = new Vector();

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
	public void actionPerformed(ActionEvent ae) {
		commands.add(input.getText());
		input.selectAll();
		try {
			conn.send(input.getText());
		} catch (IOException ioe) {
			running = false;
			ex = ioe;
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
				
				style.style = 0;

				scrollDown = false;

				int sbAnchorPos =
					vertScrollBar.getMaximum() - textScroller.getHeight();
				sbAnchorPos = (sbAnchorPos < 0) ? 0 : sbAnchorPos;

				if (Math.abs(sbAnchorPos - vertScrollBar.getValue()) < 10) {
					scrollDown = true;
				}

				String line = conn.read();
				if (!line.endsWith("\n")) {
					line += "\n";
				}

				matcher = ANSI_COLOUR_SEQUENCE_PATTERN.matcher(line);

				linePos = 0;

				while (linePos < line.length()) {
					if (matcher.find()) {
						addString(line.substring(linePos, matcher.start()));
						parseColour(matcher.group());
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
	private void addString(String string) throws BadLocationException {
		doc.insertString(
			doc.getLength(),
			string,
			text.getStyle(style.toString()));
	}

	/**
	 * Processes the given ANSI escapes, and changes the style appropriately.
	 * @param ansi the ANSI escape sequence to parse
	 */
	private void parseColour(String ansi) {
		Matcher matcher = ANSI_COLOUR_PARAMETER_PATTERN.matcher(ansi);

		int parm;

		while (matcher.find()) {
			parm = Integer.parseInt(matcher.group(), 16);

			if (parm == 0) {
				style.style = 1;
				style.bg = 0;
				style.fg = 7;
			} else if (parm == 1) {
				style.style = 1;
			} else {
				int ground = (parm & 0x70) >> 4;
				int colour = parm & 0x0F;

				if (ground == 3 && colour < 8) {
					style.fg = colour;
				} else if (ground == 4 && colour < 8) {
					style.bg = colour;
				}
			}
		}

	}

	/**
	 * Does the hard work of laying out the window components.
	 */
	private void __layoutComponents() {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

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
		StyleConstants.setBackground(def, COLOURS[0][0]);
		StyleConstants.setForeground(def, COLOURS[0][7]);
		StyleConstants.setFontFamily(def, "monospaced");
		StyleConstants.setFontSize(def, 12);

		AnsiStyle ansi = new AnsiStyle();

		for (ansi.style = 0; ansi.style < STYLES.length; ansi.style++) {
			for (ansi.fg = 0; ansi.fg < COLOUR_NAMES.length; ansi.fg++) {
				for (ansi.bg = 0; ansi.bg < COLOUR_NAMES.length; ansi.bg++) {
					Style style = text.addStyle(ansi.toString(), def);
					StyleConstants.setBackground(style, COLOURS[0][ansi.bg]);
					StyleConstants.setForeground(
						style,
						COLOURS[ansi.style][ansi.fg]);
				}
			}
		}

	}

	/**
	 * Provides a class for easily storing the attributes of an ANSI escape
	 * @author ritter
	 */
	private class AnsiStyle {
		
		/**
		 * The style in use. 0 is normal, 1 is bold.
		 */
		int style = 0;
		
		/**
		 * The ANSI foreground colour, 0 - 7
		 */
		int fg = 7;

		/**
		 * The ANSI background colour, 0 - 7
		 */
		int bg = 0;

		/**
		 * Provides a string we can use to recall styles.
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return STYLES[style]
				+ " "
				+ COLOUR_NAMES[fg]
				+ " "
				+ COLOUR_NAMES[bg];
		}
	}

}
