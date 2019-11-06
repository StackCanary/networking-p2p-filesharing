package networking.torrent.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import networking.kad.task.FetchTask;
import networking.pwp.PWPPeer;
import networking.torrent.file.FileState;
import networking.torrent.file.Piece;

public class TDownload implements Runnable {

	FileState fstate;
	TorrentService tsrvce; 
	ExecutorService pool = Executors.newFixedThreadPool(1);
	
	Future<ArrayList<PWPPeer>> future = null;
	
	Random random = new Random(System.nanoTime());
	
	TDownload(FileState fstate, TorrentService tsrvce)
	{
		this.fstate = fstate;
		this.tsrvce = tsrvce;
	}
	
	Callable<ArrayList<PWPPeer>> getPeers =
			() -> {	
				FetchTask task = new FetchTask(tsrvce.kService, fstate.getInfoHash());
				task.execute();

//				ArrayList<PWPPeer> result = new ArrayList<PWPPeer>();
//				
//				result.add(new PWPPeer(new InetSocketAddress(InetAddress.getByName("pc2-020-l.cs.st-andrews.ac.uk"), Config.defaultPeerPort)));
//				result.add(new PWPPeer(new InetSocketAddress(InetAddress.getByName("pc2-022-l.cs.st-andrews.ac.uk"), Config.defaultPeerPort)));
//				result.add(new PWPPeer(new InetSocketAddress(InetAddress.getByName("pc2-026-l.cs.st-andrews.ac.uk"), Config.defaultPeerPort)));
//				
//				return result;results
				
				System.out.println("Found Peers " + task.getResults());
				
				return task.getResults();
			};
			
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Map<Integer, HashSet<PWPPeer>> map = new HashMap<Integer, HashSet<PWPPeer>>();
		
		long start = System.currentTimeMillis();
		
		// Try obtaining peers using dht
		while(!fstate.complete())
		{
			update();
			
			map.clear();
			
			
			
			
			for (Iterator<PWPPeer> it = fstate.getPeers().iterator(); it.hasNext(); ) {
				
				PWPPeer peer = it.next();
				
				try {
					
					TClient client = new TClient(peer, fstate);
					
					for (int i : client.getPieceNos())
					{
						if (map.get(i) == null)
							map.put(i, new HashSet<PWPPeer>());
						
						map.get(i).add(peer);
					}
					
					client.close();
					
				} catch (IOException e) {
					
					it.remove();
				}
			}
			
			if (fstate.getPeers().isEmpty())
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			
			// Intersect keys can retrieve and the ones we need
			Set<Integer> potential = new HashSet<Integer>(map.keySet()); potential.retainAll(fstate.getNeed());

			
			
			for (int pcno :  byRandom(potential)) {
				for (PWPPeer peer : byRandomPeer(map, pcno)) {
					
					TClient client;
					try {
						client = new TClient(peer, fstate);
						Piece piece = client.getPiece(pcno);
						client.close();
						
						System.out.println("Downloaded " + pcno);
						
						if (piece != null)
							fstate.add(piece.getData(), pcno);
						else
							continue;
						
						break;
					} catch (IOException e) {
						continue;
					}
					
				}
				
				break;
			}
			
		}

		long finish = System.currentTimeMillis();
		
		long tTaken = finish - start;
		
		System.out.println("Completed " + fstate.getMyTorrent().filename);
		
		
		System.out.println("Start :" + start);
		System.out.println("End   :" + finish);
		System.out.println("Total :" + tTaken);
			
	}
	
	
	// From https://stackoverflow.com/questions/30853117
	public List<Integer> byLowestAvailability(Map<Integer, HashSet<PWPPeer>> map, Set<Integer> potential)
	{
		
		List<Integer> sorted = map.entrySet().stream()
		        .sorted(Comparator.comparingInt(e->e.getValue().size()))
		        .map(Map.Entry::getKey)
		        .collect(Collectors.toList());
		
		for (Iterator<Integer> it = sorted.iterator(); it.hasNext(); ) 
		{
			
			Integer next = it.next();
			
			if (!potential.contains(next))
				it.remove();
			
		}
		
		return sorted;
	}
	
	public List<PWPPeer> byRandomPeer(Map<Integer, HashSet<PWPPeer>> map, int pcno)
	{
		List<PWPPeer> peers = new ArrayList<PWPPeer>(map.get(pcno));
		Collections.shuffle(peers);
		return peers;
	}
	
	public List<Integer> byRandom(Set<Integer> potential)
	{
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(potential);
		Collections.shuffle(list, random);
		return list;
	}
	
	public void update()
	{
		if (future == null)
			future = pool.submit(getPeers);
		
		
		if (fstate.getPeers().size() <= 3)
			if (future.isDone()) {
				try {
					ArrayList<PWPPeer> peers = future.get();

					if (peers != null)
						fstate.getPeers().addAll(peers);

					future = pool.submit(getPeers);

				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			} 
	}

}
