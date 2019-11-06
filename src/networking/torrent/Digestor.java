package networking.torrent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digestor {

	public MessageDigest digest = null; 
	
	public Digestor()
	{
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public MessageDigest get()
	{
		return this.digest;
	}
	
}
