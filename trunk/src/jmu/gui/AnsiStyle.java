package jmu.gui;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides a class for easily storing the attributes of an ANSI escape
 * @author ritter
 */
class AnsiStyle {

	/**
	 * The style in use. 0 is normal, 1 is bold.
	 */
	private int style = 0;

	/**
	 * The ANSI foreground colour, 0 - 7
	 */
	private int fg = 7;

	/**
	 * The ANSI background colour, 0 - 7
	 */
	private int bg = 0;

	private static final String COLOUR_SEQUENCE_REGEX =
		"\\\u001b\\[(\\d?\\d;)*?\\d?\\dm";
	private static final Pattern COLOUR_SEQUENCE_PATTERN =
		Pattern.compile(COLOUR_SEQUENCE_REGEX);

	private static final String COLOUR_PARAMETER_REGEX = "\\d?\\d";
	private static final Pattern COLOUR_PARAMETER_PATTERN =
		Pattern.compile(COLOUR_PARAMETER_REGEX);

	public static final String[] STYLES = { "normal", "bright" };

	public static final String[] COLOUR_NAMES =
		{
			"black",
			"red",
			"green",
			"yellow",
			"blue",
			"magenta",
			"cyan",
			"white" };

	public static final Color[][] COLOURS =
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
	 * @param MooWindow
	 */
	public AnsiStyle() {
	}
	
	public int getStyle() {
		return style;
	}
	
	public void setStyle(int style) {
		if (style == 0 || style == 1) {
			this.style = style;
		}
	}
	
	public int getForeground() {
		return fg;
	}
	
	public void setForeground(int fg) {
		if (fg >= 0 || fg <= 7) {
			this.fg = fg;
		}
	}
	
	public int getBackground() {
		return bg;
	}

	public void setBackground(int bg) {
		if (bg >= 0 || bg <= 7) {
			this.bg = bg;
		}
	}
	
	/**
	 * Returns a regex matcher that can be used to find ANSI colour sequences.
	 * @param data the text to search
	 * @return a suitable matcher instance
	 */
	public Matcher getMatcher(String data) {
		return COLOUR_SEQUENCE_PATTERN.matcher(data);
	}
	
	/**
	 * Change the colour to what the ANSI escape specifies.
	 * @param ansi the ANSI escape sequence to parse
	 */
	public void setColour(String ansi) {
		Matcher matcher = COLOUR_PARAMETER_PATTERN.matcher(ansi);

		int parm;

		while (matcher.find()) {
			parm = Integer.parseInt(matcher.group(), 16);

			if (parm == 0) {
				style = 0;
				bg = 0;
				fg = 7;
			} else if (parm == 1) {
				style = 1;
			} else {
				int ground = (parm & 0x70) >> 4;
				int colour = parm & 0x0F;

				if (ground == 3 && colour < 8) {
					fg = colour;
				} else if (ground == 4 && colour < 8) {
					bg = colour;
				}
			}
		}

	}

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