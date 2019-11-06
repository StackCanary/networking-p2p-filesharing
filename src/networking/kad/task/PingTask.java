package networking.kad.task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import networking.kad.KadNode;
import networking.kad.KademliaService;
import networking.kad.protocol.KadMessage;
import networking.kad.protocol.request.Ping;

public class PingTask extends Task {
	
	KadNode target;
	
	
	public PingTask(KademliaService service, KadNode target) {
		super(service);
		this.target = target;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		Future<KadMessage> future = getExecutor().submit(kService.query(target, new Ping(getService().myKey)));
		
		try {
			KadMessage reply = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		executor.shutdownNow();
	}

}
