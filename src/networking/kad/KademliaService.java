package networking.kad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import networking.config.Config;
import networking.kad.protocol.KadMessage;
import networking.kad.protocol.Pair;
import networking.kad.protocol.request.Fetch;
import networking.kad.protocol.request.FindNode;
import networking.kad.protocol.request.Store;
import networking.kad.protocol.respnse.FetchResponse;
import networking.kad.protocol.respnse.FindNodeResponse;
import networking.kad.protocol.respnse.Pong;
import networking.kad.protocol.respnse.StoreResponse;
import networking.kad.task.FetchTask;
import networking.kad.task.FindNodeTask;
import networking.kad.task.StoreTask;
import networking.kad.task.Task;
import networking.pwp.PWPPeer;

public class KademliaService {

	// Routing Table TODO must generate unique KadKey
	public KadKBuckets kBuckets;
	public KadStorage  kStorage = new KadStorage();
	public KadCom kCom  = null;
	public KadKey myKey = null;
	
	AwaitingResponse awaiting = new AwaitingResponse();
	
	public KademliaService(int port) 
	{
		kCom = new KadCom(port);
		
		InetAddress addr;
		try {
			this.myKey = KadKey.makeKey(java.net.InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		 
		System.out.println("My KadKey " + this.myKey);
		
		this.kBuckets = new KadKBuckets(this.myKey); 
	}
	
	public KadNode getKadNode() 
	{
		return new KadNode(myKey, kCom.port, kCom.socket.getLocalAddress());
	}

	public void run() 
	{
		while(true) {
			try {

				KadMessage reply = null;
				
				Pair<KadMessage, DatagramPacket> pair = kCom.receive();
				
				KadMessage received = pair.a;
				DatagramPacket packet = pair.b;
				
				if (received.isQuery()) {
					//Received Query to which we respond to immediately
					ArrayList<KadNode> nodes = null;
					ArrayList<PWPPeer> peers = null;
					
					KadNode node = new KadNode(received.getMyKey(), packet.getPort(), packet.getAddress());
					
					kBuckets.put(node);
					
					switch(received.getType()) 
					{
					case Fetch:
						Fetch ft = (Fetch) received;
						peers = kStorage.get(ft.getInfoHash());
						if (peers == null)
							nodes = kBuckets.get(ft.getInfoHash(), Config.SizeOfBkt);
						
						reply = new FetchResponse(myKey, nodes, peers);
						break;
					case FindNode:
						FindNode fn = (FindNode) received;
						nodes = kBuckets.get(fn.getTarget(), Config.SizeOfBkt);
						reply = new FindNodeResponse(myKey, nodes);
						break;
					case Ping: 
						reply = new Pong(myKey);
						break;
					case Store:
						Store st = (Store) received;
						kStorage.put(st.getInfoHash(), st.getPeer());
						reply = new StoreResponse(myKey);
						
						break;
					default:
						break;
					}
					
					reply.setId(received.getId());
					kCom.send(reply, node);
					
				} else {
					// Otherwise we are dealing with a response
					try {
						awaiting.get(received.getId()).put(received);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public synchronized void announce(KadKey infohash, PWPPeer peer)
	{
		System.out.println("Annnouncing infohash: " + infohash + " peer " + peer );
		StoreTask store = new StoreTask(this, infohash, peer);
		store.execute();
	}
	
	public synchronized void bootstrap(KadNode node) 
	{
		// perform lookup for our own id
		kBuckets.put(node);
		Task task = new FindNodeTask(this, myKey);
		task.execute();
	}
	
	public Callable<KadMessage> query(KadNode node, KadMessage message) {
		message.prepare();
		BlockingQueue<KadMessage> queue = new LinkedBlockingQueue<KadMessage>(); 
		awaiting.put(message.getId(), queue);
		
		try {
			kCom.send(message, node);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Callable<KadMessage> task = () -> {	
			KadMessage reply = queue.poll(Config.timeout, TimeUnit.SECONDS);
			
			if (reply == null)
				awaiting.remove(message.getId());
			
			return reply;
		};

		return task;
	}
	
}

