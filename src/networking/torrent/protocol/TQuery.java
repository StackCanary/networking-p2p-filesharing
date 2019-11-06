package networking.torrent.protocol;

import java.io.Serializable;

public abstract class TQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public enum TQueryType 
	{
		Nani,
		Want,
	};

	public TQuery() 
	{
		
	}
	
	public abstract TQueryType getType();

}
