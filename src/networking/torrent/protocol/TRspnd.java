package networking.torrent.protocol;

import java.io.Serializable;

public abstract class TRspnd implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum TRspndType 
	{
		Have,
		Give,
	};

	public TRspnd() 
	{

	}

	public abstract TRspndType getType();
}
