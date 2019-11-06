package networking.kad.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import networking.kad.protocol.request.FindNode;
import networking.kad.protocol.respnse.FindNodeResponse;

public class FindNodeTask extends Task {
	
	KadKey target;
	FindNode findNode;
	
	// Create Priority Queue to store KadNodes
	PriorityQueue<KadNode> queue; 
	PriorityQueue<KadNode> shorl;
	PriorityQueue<KadNode> sortd;
	
	AtomicBoolean finished = new AtomicBoolean(false);
	
	HashSet<KadNode> visited = new HashSet<KadNode>();
	KadNode best;
	
	ArrayList<KadNode> results = new ArrayList<KadNode>();
	
	Comparator<KadNode> comparator; 
	
	public FindNodeTask(KademliaService service, KadKey target) {
		super(service);
		this.target = target;
		this.comparator = new KadNodeComparator(target);
		
		queue = new PriorityQueue<KadNode>(20, comparator);
		shorl = new PriorityQueue<KadNode>(20, comparator);
		
		sortd = new PriorityQueue<KadNode>(comparator);
		
		// Create a find node message
		findNode = new FindNode(getService().myKey, target);
	}
	
	public void updateShorL() 
	{
		for (int i = 0; i < Config.alpha && !queue.isEmpty(); i++) {
			shorl.add(queue.remove());
		}
	}
	
	public ArrayList<KadNode> getResults()
	{
		return this.results;
	}

	@Override
	public void execute() {
		
		System.out.println("Target" + target);
		
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
				callables.add(kService.query(shorl.remove(), findNode));
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
						.filter(Objects::nonNull)
						.collect(Collectors.toList());

				for (KadMessage message : replies) {
					for (KadNode node : ((FindNodeResponse) message).getNodes()) {
						if (!visited.contains(node)) {
							shorl.add(node);
							kService.kBuckets.put(node);
						} else {  
							visited.add(node);
						}
					}
				}

				if (shorl.isEmpty()) {
					if (queue.isEmpty()) {
						finished.set(true);
					} else {
						updateShorL();
					}
				} 
				
				if (comparator.compare(best, shorl.peek()) >= 0)
					finished.set(true);
				else 
					best = shorl.peek();
				
				sortd.addAll(queue);
				sortd.addAll(shorl);
				sortd.addAll(visited);
				
				for (int i = 0; i < Config.SizeOfBkt && !sortd.isEmpty(); i++) {
					results.add(sortd.remove());
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			executor.shutdown();
		}
		
	}
	
}
