package networking.torrent.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import networking.config.Config;
import networking.pwp.PWPPeer;

public class TCom implements Runnable {
	
	ServerSocket socket; 
	TorrentService tSrvc;
	
	PWPPeer peer;
	
	public TCom(TorrentService tSrvc)
	{
		this.tSrvc = tSrvc;
		
		try {
			this.peer = new PWPPeer(new InetSocketAddress(java.net.InetAddress.getLocalHost(), Config.defaultPeerPort));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(Config.defaultPeerPort);
		} catch (IOException e) {
			System.out.println("Could not open server socket");
			e.printStackTrace();
		}
		
		while(true)
		{
			try {
				new Thread(new TListener(socket.accept(), tSrvc)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public PWPPeer getMyPeer()
	{
		return peer;
	}

}
