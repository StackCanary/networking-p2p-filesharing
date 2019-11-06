package networking.torrent.protocol.query;

import networking.kad.KadKey;
import networking.torrent.protocol.TQuery;

public class TNani extends TQuery {
	private static final long serialVersionUID = 2818611783252761716L;

	public KadKey info;
	
	public TNani(KadKey infohash)
	{
		this.info = infohash;
	}
	
	@Override
	public TQueryType getType()
	{
		return TQueryType.Nani;
	}
	
}
