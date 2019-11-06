package networking.torrent.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

import networking.pwp.PWPPeer;
import networking.torrent.file.FileState;
import networking.torrent.file.Piece;
import networking.torrent.protocol.query.TNani;
import networking.torrent.protocol.query.TWant;
import networking.torrent.protocol.rspnd.TGive;
import networking.torrent.protocol.rspnd.THave;

public class TClient {

	Socket socket; FileState fstate;

	ObjectInputStream  inp; 
	ObjectOutputStream out;
	
	public TClient(PWPPeer peer, FileState fstate) throws IOException
	{
		this.socket = new Socket(peer.get().getAddress(), peer.get().getPort());
		this.fstate = fstate;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		this.inp = new ObjectInputStream(socket.getInputStream());
		
	}
	
	public Set<Integer> getPieceNos()
	{
		try {
			TNani tnani = new TNani(fstate.getInfoHash());
			out.writeObject(tnani);
			THave thave = (THave) inp.readObject();
			out.flush();
			return thave.pieces;
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Returning null pieces");
			return null;
		}
	}

	public Piece getPiece(int pcno)
	{
		try {
			TWant twant = new TWant(fstate.getInfoHash(), pcno);
			out.writeObject(twant);
			TGive tgive = (TGive) inp.readObject();
			out.flush();
			return tgive.piece;
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Returning null piece");
			return null;
		}
	}
	
	public void close() throws IOException
	{
		this.socket.close();
	}
	
}
