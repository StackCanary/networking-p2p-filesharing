package networking.torrent;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import networking.config.Config;
import networking.kad.KadNode;

/*
 * Self note, perhaps use merkle tree
 */
public class MyTorrent implements Serializable {
	private static final long serialVersionUID = -1609391218139114008L;

	public ArrayList<KadNode> nodes;
	
	public int pSize = Config.SizeOfPce;
	
	public String filename; public int filesize;
	
	
	public ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	
	public MyTorrent(File file) 
	{
		this.filename = file.getName();
		this.filesize = (int) file.length();
	}
	
	public int getPSize()
	{
		return pSize;
	}
	
	public int noOfPieces()
	{
		return (int) Math.ceil(((double) filesize) / getPSize());
	}
	
	public void addNode(KadNode node) 
	{
		this.nodes.add(node);
	}
	
	public void insert(byte[] hash)
	{
		pieces.add(hash);
	}
	
	public void save() throws IOException
	{
		TUtil.write(this, new File(Config.torrntdir + filename + ".torrent"));
	}
	
	public byte[] getInfoHash()
	{
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			
			digest.update(filename.getBytes());
			digest.update(ByteBuffer.allocate(8).putLong(filesize).array());
			
			for (byte[] key: pieces)
				digest.update(key);
			
			digest.update(ByteBuffer.allocate(4).putInt(pSize).array());
			
			return digest.digest();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("File: " + filename + "\n");
		sb.append("Size: " + filesize + "\n");
		sb.append("Pces: " + pieces   + "\n"); 
		
		for (byte[] pce : pieces)
			sb.append(new String(Base64.getEncoder().encode(pce)));
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return filename.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MyTorrent)
			return ((MyTorrent) obj).filename.equals(this.filename);
		
		return false;
	}
	
	
}
