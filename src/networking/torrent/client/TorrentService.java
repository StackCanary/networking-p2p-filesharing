package networking.torrent.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import networking.config.Config;
import networking.kad.KadKey;
import networking.kad.KadNode;
import networking.kad.KademliaService;
import networking.torrent.TUtil;
import networking.torrent.file.FileState;

/*
 * This class schedules the connections that need to be made by me
 */
public class TorrentService {
	
	TCom tCom;
	KademliaService kService = new KademliaService(Config.defaultNodePort);
	
	public Map<KadKey, FileState> fStatez = new ConcurrentHashMap<KadKey, FileState>();
	
	public TorrentService()
	{
		TUtil.ldStates(this);
		
		new Thread(
				new Runnable() {

					@Override
					public void run() {
						kService.run();
					}

				}).start();;

		try {
			int port = Config.defaultNodePort; 
			InetAddress address = InetAddress.getByName(Config.bootstrapper);
			KadNode other = new KadNode(KadKey.makeKey(address, port), port, address);
			
			if (!address.isAnyLocalAddress())
				kService.bootstrap(other);
			
		} catch (UnknownHostException e) {
			System.out.println("Could not bootstrap");
			e.printStackTrace();
		}
		
		new Thread(tCom = new TCom(this)).start();
		
		run();
	}
	
	public void run()
	{
		for (Entry<KadKey, FileState> o : fStatez.entrySet())
			new Thread(new TDownload(o.getValue(), this)).start();;
		
		// Send store message
		while(true) {
			for (Entry<KadKey, FileState> o : fStatez.entrySet()) {
				kService.announce(o.getKey(), tCom.getMyPeer());
			}
			
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String ...strings)
	{
		TorrentService tService = new TorrentService();
	}
}
