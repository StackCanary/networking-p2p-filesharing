package networking.kad;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import networking.kad.protocol.KadMessage;

public class AwaitingResponse extends ConcurrentHashMap<Long, BlockingQueue<KadMessage>>
{
	private static final long serialVersionUID = 1L;

}
