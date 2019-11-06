package networking.torrent.protocol.rspnd;

import networking.torrent.file.Piece;
import networking.torrent.protocol.TRspnd;

public class TGive extends TRspnd {
	private static final long serialVersionUID = -2088427710508733562L;
	
	public Piece piece;
	
	public TGive(Piece piece)
	{
		this.piece = piece;
	}
	
	@Override
	public TRspndType getType()
	{
		return TRspndType.Give;
	}

}
