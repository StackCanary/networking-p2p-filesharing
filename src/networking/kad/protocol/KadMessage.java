package networking.kad.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import networking.kad.KadKey;
import networking.kad.KadNode;

public abstract class KadMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	public enum MessageType {
		Ping,
		Store,
		Fetch,
		FindNode,
		Pong,
		StoreResponse,
		FetchResponse,
		FindNodeResponse,
	};
	
	KadKey myKey;
	long datetime;

	public KadMessage(KadKey key) {
		this.myKey = key;
	}
	
	public void prepare() {
		this.datetime = System.currentTimeMillis();
	}
	
	public void setId(long datetime) {
		this.datetime = datetime;
	}
	
	public long getId() {
		return this.datetime;
	}
	
	public abstract MessageType getType();
	
	public KadKey getMyKey() {
		return myKey;
	}
	
	public Boolean isQuery() {
		return (getType() == MessageType.Ping) 
				|| (getType() == MessageType.Store) 
				|| (getType() == MessageType.Fetch) 
				|| (getType() == MessageType.FindNode);
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
	}

	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof KadMessage))
				return false;
		
		KadMessage kmsg = (KadMessage) obj;
		
		return this.myKey.equals(kmsg.getMyKey()) && this.getId() == kmsg.getId();
		
	}
}
