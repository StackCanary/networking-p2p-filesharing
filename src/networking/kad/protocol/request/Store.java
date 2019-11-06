package networking.kad.protocol.request;

import networking.kad.KadKey;
import networking.kad.protocol.KadMessage;
import networking.pwp.PWPPeer;

/*
 * Announce our controlling peer is downloading the torrent.
 */
public class Store extends KadMessage {
	private static final long serialVersionUID = -1481009505601517117L;
	
	KadKey infohash;
	PWPPeer peer;

	public Store(KadKey myKey, KadKey infohash, PWPPeer peer) 
	{
		super(myKey);
		this.peer = peer;
		this.infohash = infohash;
	}

	@Override
	public MessageType getType() 
	{
		return MessageType.Store;
	}
	
	public PWPPeer getPeer() 
	{
		return this.peer;
	}

	
	public KadKey getInfoHash()
	{
		return this.infohash;
	}
}
