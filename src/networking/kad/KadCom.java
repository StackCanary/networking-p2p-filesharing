package networking.kad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import networking.config.Config;
import networking.kad.protocol.KadMessage;
import networking.kad.protocol.Pair;

public class KadCom {

	int port;
	DatagramSocket socket = null;
	
	public KadCom(int port) {
		
		this.port = port;
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}
	
	public Pair<KadMessage, DatagramPacket> receive() throws IOException 
	{
		byte[] data = new byte[Config.dgramSize];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
		
		
		ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
		
		KadMessage message = null;
		
		try {
			message = (KadMessage) objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return new Pair<KadMessage, DatagramPacket>(message, packet);
	}

	public synchronized void send(KadMessage message, KadNode kNode) throws IOException 
	{
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(Config.dgramSize);
		new ObjectOutputStream(byteOutputStream).writeObject(message);
		byte[] data = byteOutputStream.toByteArray();
		DatagramPacket packet = new DatagramPacket(data, data.length, kNode.ip, kNode.udpPort);
		socket.send(packet);
	}
	
	
}
