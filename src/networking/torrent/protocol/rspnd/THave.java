package networking.torrent.protocol.rspnd;

import java.util.Set;

import networking.torrent.protocol.TRspnd;

public class THave extends TRspnd {
	private static final long serialVersionUID = 439037163462644716L;

	public Set<Integer> pieces;
	
	public THave(Set<Integer> pieces)
	{
		this.pieces = pieces;
	}
	
	@Override
	public TRspndType getType()
	{
		return TRspndType.Have;
	}

}
