package networking.kad.protocol.respnse;

import networking.kad.KadKey;
import networking.kad.protocol.KadMessage;

/*
 * A reply to a ping.
 */
public class Pong extends KadMessage {
	private static final long serialVersionUID = 3116321775128168627L;

	public Pong(KadKey key) {
		super(key);
	}

	@Override
	public MessageType getType() {
		return MessageType.Pong;
	}

}
