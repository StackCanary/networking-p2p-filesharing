package networking.kad.protocol.respnse;

import networking.kad.KadKey;
import networking.kad.protocol.KadMessage;

/*
 * A response to an announce.
 */
public class StoreResponse extends KadMessage {
	private static final long serialVersionUID = -5897839355471101491L;

	public StoreResponse(KadKey key) {
		super(key);
	}

	@Override
	public MessageType getType() {
		return MessageType.StoreResponse;
	}

}
