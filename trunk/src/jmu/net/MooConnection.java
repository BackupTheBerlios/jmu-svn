package jmu.net;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author dietzla
 */
public class MooConnection {

	private Socket conn;
	private BufferedReader in;
	private BufferedWriter out;

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
		InetAddress hosts[] = InetAddress.getAllByName(hostname);
		
		for (int i = 0; i < hosts.length && conn == null; i++) {
			try {
				conn = new Socket(hosts[i], port);
			} catch (IOException e) {
				// ignore, connection refused no doubt
			}
		}

		if (conn == null) {
			throw new IOException("No connection attempt succeeded.");
		} else {
			in =
				new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			out =
				new BufferedWriter(
					new OutputStreamWriter(conn.getOutputStream()));
		}
	}

	/**
	 * Checks to see if a line is able to be read. 
	 * @see java.io.BufferedReader#ready()
	 * @return true is data is ready for reading
	 * @throws IOException if an I/O error occurs.
	 */
	public boolean ready() throws IOException {
		return in.ready();
	}

	/**
	 * Sends a line to the MOO. A newline is added if neccessary.
	 * @param msg the line to send
	 * @throws IOException if an I/O error occurs
	 */
	public void send(String msg) throws IOException {
		out.write(msg);
		if (!msg.endsWith("\n")) {
			out.write('\n');
		}
		out.flush();
	}

	/**
	 * Reads a line of data from the MOO.
	 * @return the line read
	 * @throws IOException if an I/O error occurs
	 */
	public String read() throws IOException {
		return in.readLine();
	}

	/**
	 * Closes the connection to the MOO.
	 * @throws IOException if an I/O error occurs
	 */
	public void close() throws IOException {
		in.close();
		out.close();
		conn.close();
	}

}
