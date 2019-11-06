package networking.pwp;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class PWPPeer implements Serializable {
	private static final long serialVersionUID = -8178766439362077479L;
	
	InetSocketAddress address;
	
	public PWPPeer(InetSocketAddress address)
	{
		this.address = address;
	}
	
	public InetSocketAddress get()
	{
		return address;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PWPPeer)
			return this.address.equals(((PWPPeer) obj).address);
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return address.hashCode();
	}
}
