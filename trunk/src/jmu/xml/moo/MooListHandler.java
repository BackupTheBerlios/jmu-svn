package jmu.xml.moo;

import java.util.Vector;

import jmu.data.Moo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author dietzla
 */
public class MooListHandler extends DefaultHandler {

	private Moo moo;

	private final Vector moos;

	public MooListHandler() {
		moos = new Vector();
	}

	public Vector getMooList() {
		return moos;
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		final String uri,
		final String localName,
		final String qName,
		final Attributes attributes)
		throws SAXException {
		if (qName.equals("moo")) {
			moo = new Moo();

			// fill in attributes
			for (int i = 0; i < attributes.getLength(); i++) {
				if (attributes.getQName(i).equals("name")) {
					moo.setName(attributes.getValue(i));
				}
			}
		} else if (qName.equals("address")) {
			String host = null;
			String port = null;

			for (int i = 0; i < attributes.getLength(); i++) {
				if (attributes.getQName(i).equals("host")) {
					host = attributes.getValue(i);
				} else if (attributes.getQName(i).equals("port")) {
					port = attributes.getValue(i);
				}
			}

			if (host != null && port != null) {
				moo.addAddress(host + ":" + port);
			}
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(
		final String uri,
		final String localName,
		final String qName)
		throws SAXException {
		if (qName.equals("moo")) {
			moos.add(moo);
		}
	}

}
