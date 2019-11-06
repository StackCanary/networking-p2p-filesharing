package networking.kad;

import java.util.Comparator;

public class KadNodeComparator implements Comparator<KadNode>{

	KadKey target;
	
	public KadNodeComparator(KadKey target) {
		this.target = target;
	}
	
	@Override
	public int compare(KadNode o1, KadNode o2) {
		return -(o1.getKey().metric(target).compareTo(o1.getKey().metric(target)));
	}

}
