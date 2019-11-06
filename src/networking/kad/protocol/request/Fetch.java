package networking.kad.protocol.request;

import networking.kad.KadKey;
import networking.kad.protocol.KadMessage;

/*
 * A request to find peers currently downloading file.
 */
public class Fetch extends KadMessage {
	private static final long serialVersionUID = -4132590306457447953L;

	KadKey infohash;
	
	public Fetch(KadKey myKey, KadKey infohash) {
		super(myKey);
		this.infohash = infohash;
	}

	@Override
	public MessageType getType() {
		return MessageType.Fetch;
	}

	public KadKey getInfoHash() {
		return infohash;
	}
}
