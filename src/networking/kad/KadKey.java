package networking.kad;

import java.io.Serializable;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import networking.config.Config;
import networking.torrent.Digestor;

public class KadKey extends BitSet implements Serializable, Comparable<KadKey> {
	private static final long serialVersionUID = 4365250646141655668L;
	
	public KadKey() {
		super(Config.SizeOfKey);
	}
	
	public KadKey(byte[] data) {
		super(Config.SizeOfKey);
		this.or(BitSet.valueOf(Arrays.copyOf(data, Config.SizeOfKey >> 3)));
	}
	
	/*
	 * Distance function
	 */
	public KadKey metric(KadKey o) {
		KadKey result = (KadKey) this.clone();
		result.xor(o);
		return result;
	}

	// https://stackoverflow.com/questions/27331175/
	@Override
	public int compareTo(KadKey o) {
		
		if (this.equals(o)) 
			return 0;
		
		KadKey xor = (KadKey) this.clone();
		xor.xor(o);
		
		int different = xor.length() - 1;
		
		if( different < 0)
			return 0;

		return o.get(different) ? 1 : -1;

	}
	
	@Override
	public Object clone() {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public static KadKey makeKey(InetAddress address, int port)
	{
		Digestor digestor = new Digestor();
		digestor.get().update(address.getAddress());
		digestor.get().update(ByteBuffer.allocate(4).putInt(port).array());
		return new KadKey(digestor.get().digest().clone());
	}
	
}
