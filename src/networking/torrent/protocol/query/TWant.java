package networking.torrent.protocol.query;

import networking.kad.KadKey;
import networking.torrent.protocol.TQuery;

public class TWant extends TQuery {
	private static final long serialVersionUID = -2172272676913608085L;

	public KadKey info; public int pcno;
	
	public TWant(KadKey info, int pcno)
	{
		this.info = info; this.pcno = pcno;
	}
	
	@Override
	public TQueryType getType()
	{
		return TQueryType.Want;
	}

}
