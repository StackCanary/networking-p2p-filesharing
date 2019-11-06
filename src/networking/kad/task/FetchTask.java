package networking.kad.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import networking.config.Config;
import networking.kad.KadKey;
import networking.kad.KadNode;
import networking.kad.KadNodeComparator;
import networking.kad.KademliaService;
import networking.kad.protocol.KadMessage;
import networking.kad.protocol.KadMessage.MessageType;
import networking.kad.protocol.request.Fetch;
import networking.kad.protocol.respnse.FetchResponse;
import networking.pwp.PWPPeer;

public class FetchTask extends Task {

	KadKey target;
	Fetch fetch;
	
	// Create Priority Queue to store KadNodes
	PriorityQueue<KadNode> queue; 
	PriorityQueue<KadNode> shorl;
	
	AtomicBoolean finished = new AtomicBoolean(false);
	
	HashSet<KadNode> visited = new HashSet<KadNode>();
	
	int probed = 0;
	
	KadNode best;
	
	ArrayList<PWPPeer> results = null;
	
	Comparator<KadNode> comparator; 

	public FetchTask(KademliaService service, KadKey target) {
		super(service);
		this.target = target;
		this.comparator = new KadNodeComparator(target);
		
		queue = new PriorityQueue<KadNode>(20, comparator);
		shorl = new PriorityQueue<KadNode>(20, comparator);
		
		// Create a find node message
		fetch = new Fetch(getService().myKey, target);
	}
	
	public ArrayList<PWPPeer> getResults()
	{
		return results;
	}
	
	public void updateShorL() 
	{
		for (int i = 0; i < Config.alpha && !queue.isEmpty(); i++) {
			shorl.add(queue.remove());
		}
	}

	@Override
	public void execute() {
		
		if (kService.kStorage.get(target) != null)
			this.results = kService.kStorage.get(target);
		
		Comparator<KadNode> comparator = new KadNodeComparator(target);
		
		// Add all node in buckets to queue
		queue.addAll(getService().kBuckets.get(target, Config.SizeOfBkt));
		
		if (queue.size() == 0)
			return;
		
		updateShorL();
		
		best = shorl.peek();
		
		while(!finished.get()) {
			
			List<Callable<KadMessage>> callables = new ArrayList<Callable<KadMessage>>();

			for (int i = 0; i < Config.alpha && !shorl.isEmpty(); i++) {
				callables.add(kService.query(shorl.remove(), fetch));
			}
			
			try {
				
				List<KadMessage> replies = executor.invokeAll(callables)
						.stream()
						.map(future -> 
						{
							try {
								return future.get();
							} catch (InterruptedException | ExecutionException e) {
								return null;
							}

						})
						.filter(x -> x != null && x.getType() == MessageType.FetchResponse)
						.collect(Collectors.toList());

				for (KadMessage message : replies) {
					
					FetchResponse reply = (FetchResponse) message;
					
					if (reply.getNodes() != null) {
						for (KadNode node : reply.getNodes()) {
							if (!visited.contains(node)) {
								shorl.add(node);
								kService.kBuckets.put(node);
							} else {  
								visited.add(node);
							}
						}
					}
					
					if (reply.getPeers() != null) {
						results = reply.getPeers();
						finished.set(true);
						executor.shutdown();
						return;
					}
					
				}

				if (shorl.isEmpty()) {
					if (queue.isEmpty()) {
						finished.set(true);
						executor.shutdown();
						return;
					} else {
						updateShorL();
					}
				} 
				
				if (comparator.compare(best, shorl.peek()) >= 0)
					finished.set(true);
				else 
					best = shorl.peek();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
	}

}
