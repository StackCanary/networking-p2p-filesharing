package networking.kad.protocol.request;

import networking.kad.KadKey;
import networking.kad.protocol.KadMessage;

/*
 * A request to see if a node is online.
 */
public class Ping extends KadMessage {
	private static final long serialVersionUID = -8085897869182455489L;

	public Ping(KadKey myKey) {
		super(myKey);
	}

	@Override
	public MessageType getType() {
		return MessageType.Ping;
	}
	
	

}
