package networking.kad;

import java.io.Serializable;
import java.net.InetAddress;

public class KadNode implements Comparable<KadNode>, Serializable {
	private static final long serialVersionUID = 3450581888145140045L;
	
	KadKey key;
	Integer udpPort;
	InetAddress ip;
	
	public KadNode(KadKey key, Integer port, InetAddress ip) {
		this.key = key;
		this.udpPort = port;
		this.ip = ip;
	}
	
	public KadKey getKey() {
		return this.key;
	}
	
	public InetAddress getIP() {
		return this.ip;
	}
	
	public Integer getPort() {
		return this.udpPort;
	}

	@Override
	public int compareTo(KadNode o) {
		return this.getKey().compareTo(o.getKey());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof KadNode))
			return false;
		
		KadNode node = (KadNode) obj;
		
		return node.getKey().equals(this.getKey());
		
	}

	@Override
	public String toString() {
		return "Node: " + this.getKey().toString();
	}
}
