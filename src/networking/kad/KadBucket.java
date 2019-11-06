package networking.kad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import networking.config.Config;

public class KadBucket {
	
	LinkedList<KadNode> bucket = new LinkedList<KadNode>();

	public void Bucket() {
		
	}
	
	public synchronized void put(KadNode knode) {
		
		/*
		 * If already in list, move to front
		 */
		int index = bucket.indexOf(knode);
		
		if (index > 0) {
			Collections.swap(bucket, index, bucket.size() - 1);
			return;
		}
		
		if (bucket.size() == Config.SizeOfBkt && bucket.size() > 0) { 
			bucket.removeFirst();
		}
		
		bucket.push(knode);
	}

	public synchronized List<KadNode> nodes() {
		return bucket;
	}
	
}
