package jmu.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @author dietzla
 */
public class MooConnection {
	private static final int BUFFER_SIZE = 8192;

	private final SocketChannel conn;

	private final CharsetDecoder decoder;
	private final CharsetEncoder encoder;

	// all buffers should be write-ready, ie you need to flip for reading, and 
	// compact when you're done.
	private final ByteBuffer srBuf;
	private final ByteBuffer swBuf;

	private final CharBuffer rBuf;
	private final CharBuffer wBuf;

	/**
	 * Creates a new MOO Connection, connected the the specified host and port.
	 * All known addresses are tried, until one sucessfully connects, so if a 
	 * host has any published IPv6 addresses, they will be tried as well.
	 * 
	 * @param hostname the name of the host to connect to
	 * @param port the port to connect on
	 * @throws IOException if there was an error was encountered while 
	 * 		connecting, or opening the I/O streams
	 */
	public MooConnection(String hostname, int port) throws IOException {
		InetAddress[] addresses = InetAddress.getAllByName(hostname);

		InetSocketAddress sAddr;
		SocketChannel _conn = null;

		for (int i = 0; i < addresses.length && _conn == null; i++) {
			sAddr = new InetSocketAddress(addresses[i], port);

			try {
				_conn = SocketChannel.open();
				_conn.configureBlocking(true);

				_conn.connect(sAddr);

				assert _conn
					.isConnected() : "connect() returned, not connected";
			} catch (IOException ioe) {
				_conn.close();
				_conn = null;
			}
		}

		if (_conn == null) {
			throw new ConnectException("Unable to connect");
		} else {
			conn = _conn;
		}

		Charset charset = Charset.forName("ISO-8859-1");
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();

		srBuf = ByteBuffer.allocate(BUFFER_SIZE);
		swBuf = ByteBuffer.allocate(BUFFER_SIZE);

		rBuf = CharBuffer.allocate(BUFFER_SIZE);
		wBuf = CharBuffer.allocate(BUFFER_SIZE);
	}

	/**
	 * Checks to see if a line is able to be read. 
	 * @see java.io.BufferedReader#ready()
	 * @return true is data is ready for reading
	 * @throws IOException if an I/O error occurs.
	 */
	public boolean ready() throws IOException {
		synchronized (rBuf) {
			boolean ret = false;

			rBuf.flip();
			ret = rBuf.toString().indexOf('\n') != -1;
			rBuf.compact();

			return ret;
		}
	}

	/**
	 * Sends a line to the MOO. A newline is added if neccessary.
	 * @param msg the line to send
	 * @throws IOException if an I/O error occurs
	 */
	public void send(final String msg) throws IOException {
		synchronized (wBuf) {
			wBuf.put(msg);
			if (!msg.endsWith("\n")) {
				wBuf.put('\n');
			}

			wBuf.flip();
			encoder.encode(wBuf, swBuf, false);
			wBuf.compact();

			swBuf.flip();
			conn.write(swBuf);
			swBuf.compact();
		}
	}

	/**
	 * Reads a line of data from the MOO.
	 * @return the line read
	 * @throws IOException if an I/O error occurs
	 */
	public String read() throws IOException {
		StringBuffer line = new StringBuffer();
		
		char curr = '\u0000';
		int readyChars = 0;
		
		while (curr != '\n') {
			synchronized (rBuf) {
				fillReadBuffers();
				rBuf.flip();
				readyChars = rBuf.remaining();
			}
			
			while (readyChars > 0 && curr != '\n') {
				synchronized (rBuf) {
					curr = rBuf.get();
					readyChars = rBuf.remaining();
				}
				
				line.append(curr);
			}
			
			rBuf.compact();
		}
		
		return line.toString();
	}

	/**
	 * Closes the connection to the MOO.
	 * @throws IOException if an I/O error occurs
	 */
	public void close() throws IOException {
		conn.close();
	}
	
	private final void fillReadBuffers() throws IOException {
		synchronized (rBuf) {
			if (srBuf.position() == 0 && rBuf.position() == 0) {
				conn.configureBlocking(true);
			} else {
				conn.configureBlocking(false);
			}

			int read = conn.read(srBuf);

			if (read == -1 && rBuf.position() == 0) {
				throw new EOFException();
			} else if (read == 0 && srBuf.position() == 0) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException ie) {
					InterruptedIOException iioe = new InterruptedIOException();
					iioe.initCause(ie);
				}
			} else {
				srBuf.flip();
				decoder.decode(srBuf, rBuf, false);
				srBuf.compact();
			}
		}
	}
	
}
