package networking.kad;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import networking.pwp.PWPPeer;

public class KadStorage {
	
	ConcurrentHashMap<KadKey, ArrayList<PWPPeer>> storage = new ConcurrentHashMap<KadKey, ArrayList<PWPPeer>>(); 
	
	public synchronized void put(KadKey key, PWPPeer peer) 
	{
		if (key == null || peer == null)
			return;
		
		if (!storage.containsKey(key))
			storage.put(key, new ArrayList<PWPPeer>());
		
		
		ArrayList<PWPPeer> peers = storage.get(key);
		
		if (!peers.contains(peer))
			peers.add(peer);
	}
	
	public synchronized ArrayList<PWPPeer> get(KadKey key)
	{
		return storage.get(key);
	}
	
	public synchronized void rem(KadKey key) 
	{
		storage.remove(key);
	}
	
	
}
