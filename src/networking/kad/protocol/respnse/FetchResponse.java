package networking.kad.protocol.respnse;

import java.util.ArrayList;

import networking.kad.KadKey;
import networking.kad.KadNode;
import networking.kad.protocol.KadMessage;
import networking.pwp.PWPPeer;

/*
 * Returns either list of nodes closest or actual value
 */
public class FetchResponse extends KadMessage {
	private static final long serialVersionUID = 9197896530157514714L;
	
	ArrayList<KadNode> nodes = new ArrayList<KadNode>();
	ArrayList<PWPPeer> peers = new ArrayList<PWPPeer>();

	public FetchResponse(KadKey key, ArrayList<KadNode> nodes, ArrayList<PWPPeer> peers) {
		super(key);
		this.nodes = nodes;
		this.peers = peers;
	}

	@Override
	public MessageType getType() {
		return MessageType.FetchResponse;
	}
	
	public ArrayList<KadNode> getNodes() 
	{
		return this.nodes;
	}

	public ArrayList<PWPPeer> getPeers() 
	{
		return this.peers;
	}

}
