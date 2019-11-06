package networking.kad.protocol.request;

import networking.kad.KadKey;
import networking.kad.protocol.KadMessage;

/*
 * A request to find a specific node.  
 */
public class FindNode extends KadMessage {
	private static final long serialVersionUID = 4733853606780547437L;

	KadKey target;
	
	public FindNode(KadKey myKey, KadKey target) {
		super(myKey);
		this.target = target;
	}

	@Override
	public MessageType getType() {
		return MessageType.FindNode;
	}
	
	public KadKey getTarget() {
		return target;
	}
	

}
