package jmu.data;

/**
 * @author dietzla
 */
public class Moo implements Comparable {
	
	private String name;
	private String[] addresses;
	private String[] credentials;
	
	public Moo() {
		addresses = new String[0];
		credentials = new String[0];
	}
	
	public String[] getAddresses() {
		return addresses;
	}

	public String[] getCredentials() {
		return credentials;
	}

	public String getName() {
		return name;
	}

	public void addAddress(final String added) {
		String[] tmp = new String[addresses.length + 1];
		System.arraycopy(addresses, 0, tmp, 0, addresses.length);
		tmp[addresses.length] = added;
		addresses = tmp;
	}

	public void setAddresses(final String[] strings) {
		addresses = strings;
	}

	public void addCredentials(final String added) {
		String[] tmp = new String[credentials.length + 1];
		System.arraycopy(credentials, 0, tmp, 0, credentials.length);
		tmp[credentials.length] = added;
		credentials = tmp;
	}

	public void setCredentials(final String[] strings) {
		credentials = strings;
	}

	public void setName(final String string) {
		name = string;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Object o) {
		Moo other = (Moo)o;
		return name.compareTo(other.name);
	}

}
