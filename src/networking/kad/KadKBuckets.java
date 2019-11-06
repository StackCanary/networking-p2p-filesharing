package networking.kad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import networking.config.Config;

public class KadKBuckets {

	ArrayList<KadBucket> buckets = new ArrayList<KadBucket>(Config.SizeOfKey);
	
	KadKey myKey;
	
	public KadKBuckets(KadKey myKey) 
	{
		this.myKey = myKey;
		
		while ((1 << Config.SizeOfKey) >= buckets.size())
			buckets.add(new KadBucket());
	}
	
	/*
	 * Inserts nodes into bucket.
	 * Closer nodes are placed into lower numbered buckets
	 */
	public synchronized void put(KadNode node) 
	{	
		KadKey xor = myKey.metric(node.key);
		int bucket = xor.length() - 1;
		
		if (bucket < 0)
			return;
		
		buckets.get(bucket).put(node);
	}
	
	/*
	 * Finds k KadNodes close to the given KadKey
	 */
	public synchronized ArrayList<KadNode> get(KadKey key, int k) 
	{
		HashSet<Integer> visited = new HashSet<Integer>();
		
		PriorityQueue<KadNode> queue = new PriorityQueue<KadNode>(new KadNodeComparator(key));
		
		KadKey xor = myKey.metric(key);
		
		while( !xor.isEmpty() && k > 0) {
			int next = xor.length() - 1;
			visited.add(next);
			
			for (int i = 0; i < buckets.get(next).nodes().size() && k > 0; i++) {
				queue.add(buckets.get(next).nodes().get(i));
				k--;
			}
			
			xor.clear(next);
		};
		
		for (int next = 0; next < Config.SizeOfKey && k > 0; next++) {
			if (!visited.contains(next)) {
				for (int i = 0; i < buckets.get(next).nodes().size() && k > 0; i++) {
					queue.add(buckets.get(next).nodes().get(i));
					k--;
				}
			}
			
		}
		
		return new ArrayList<KadNode>(queue);
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Buckets for node: " + myKey + "\n");
		
		for (KadBucket bucket : buckets) {
			
			if (bucket.bucket.size() > 0)
				sb.append(bucket.bucket + "\n");
			
		}
		
		sb.append("Done\n");
		
		return sb.toString();
		
	}
	
}
