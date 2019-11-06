package networking.kad.protocol.respnse;

import java.util.ArrayList;

import networking.kad.KadKey;
import networking.kad.KadNode;
import networking.kad.protocol.KadMessage;

/*
 * Returns either the exact information of the node being searched or the closest nodes.
 */
public class FindNodeResponse extends KadMessage {
	private static final long serialVersionUID = 3252427040974908563L;
	
	ArrayList<KadNode> nodes = new ArrayList<KadNode>();

	public FindNodeResponse(KadKey key, ArrayList<KadNode> nodes)
	{
		super(key);
		this.nodes = nodes;
	}

	@Override
	public MessageType getType() 
	{
		return MessageType.FindNodeResponse;
	}
	
	public ArrayList<KadNode> getNodes() 
	{
		return this.nodes;
	}
	
}
