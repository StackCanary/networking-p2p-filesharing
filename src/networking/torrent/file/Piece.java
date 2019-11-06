package networking.torrent.file;

import java.io.Serializable;

public class Piece implements Serializable {
	private static final long serialVersionUID = 2915226348822263626L;
	
	byte[] data;
	
	public Piece(byte[] data)
	{
		this.data = data;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	
}
