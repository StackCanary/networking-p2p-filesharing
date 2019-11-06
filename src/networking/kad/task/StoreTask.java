package networking.kad.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import networking.kad.KadKey;
import networking.kad.KadNode;
import networking.kad.KademliaService;
import networking.kad.protocol.KadMessage;
import networking.kad.protocol.request.Store;
import networking.pwp.PWPPeer;

public class StoreTask extends Task {
	
	PWPPeer peer;
	KadKey infohash;
	Store store;
	
	public StoreTask(KademliaService service, KadKey infohash, PWPPeer peer) {
		super(service);
		this.peer = peer;
		this.infohash = infohash;
		
		// Create a fetch message
		store = new Store(getService().myKey, infohash, peer);
	}

	@Override
	public void execute() {
		FindNodeTask findNodeTask = new FindNodeTask(this.kService, this.infohash);
		findNodeTask.execute();
		ArrayList<KadNode> kClosestNodes = findNodeTask.getResults();
		
		List<Callable<KadMessage>> callables = new ArrayList<Callable<KadMessage>>();
		
		for (int i = 0; i < kClosestNodes.size(); i++) {
			if (kClosestNodes.get(i).equals(this.kService.myKey))
					this.kService.kStorage.put(infohash, peer);
			else 
				callables.add(kService.query(kClosestNodes.get(i), store));
		}
			
		try {
			executor.invokeAll(callables)
			.stream()
			.map(future -> 
			{
				try {
					return future.get();
				} catch (InterruptedException | ExecutionException e) {
					return null;
				}

			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();
		
	}

}
