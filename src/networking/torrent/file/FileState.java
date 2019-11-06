package networking.torrent.file;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import networking.kad.KadKey;
import networking.pwp.PWPPeer;
import networking.torrent.Digestor;
import networking.torrent.MyTorrent;

/*
 * Represents a physical file either partially or fully downloaded
 */
public class FileState {

	Set<Integer> have = new TreeSet<Integer>();
	Set<Integer> need = new TreeSet<Integer>();
	
	BlockFile blockfile;
	MyTorrent mytorrent;
	
	Digestor digestor = new Digestor();
	
	Set<PWPPeer> peers = Collections.newSetFromMap(new ConcurrentHashMap<PWPPeer, Boolean>());
	
	byte[] infohash;
 	
	public FileState(MyTorrent mytorrent) throws IOException
	{
		this.mytorrent = mytorrent;
		blockfile = new BlockFile(mytorrent);
		
		for (int i = 0; i < mytorrent.noOfPieces(); i++) {
			if (chk(i))
				have.add(i);
			else 
				need.add(i);
		}
		
		infohash = this.mytorrent.getInfoHash();
	}
	
	public MyTorrent getMyTorrent()
	{
		return this.mytorrent;
	}
	
	/*
	 * Get swarm of peers participating in the file sharing for this file
	 */
	public Set<PWPPeer> getPeers()
	{
		return peers;
	}
	
	/*
	 * Write piece to file
	 */
	public synchronized void add(byte[] block, int i) throws IOException
	{
		blockfile.put(block, i);
		update(i);
	}
	
	/*
	 * Notify that we have obtained piece
	 */
	private synchronized void update(int i)
	{
		need.remove(i); have.add(i);
	}
	
	/*
	 * Read piece
	 */
	public synchronized void get(byte[] block, int i) throws IOException
	{
		blockfile.get(block, i);
	}
	
	/*
	 * Check integrity of piece no.
	 */
	public boolean chk(int i)
	{
		try {
			byte[] block = new byte[mytorrent.getPSize()];
			digestor.get().reset();
			get(block, i);
			digestor.get().update(block);
			return Arrays.equals(digestor.get().digest().clone(), mytorrent.pieces.get(i));
		} catch (IOException e) {
			return false;
		}
	}

	public synchronized Set<Integer> getNeed()
	{
		return need;
	}
	
	public synchronized Set<Integer> getHave()
	{
		return have;
	}
	
	public synchronized boolean complete()
	{
		return need.isEmpty();
	}
	
	public KadKey getInfoHash()
	{
		return new KadKey(this.infohash);
	}
	
}
