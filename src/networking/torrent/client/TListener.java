package networking.torrent.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import networking.config.Config;
import networking.torrent.file.FileState;
import networking.torrent.file.Piece;
import networking.torrent.protocol.TQuery;
import networking.torrent.protocol.query.TNani;
import networking.torrent.protocol.query.TWant;
import networking.torrent.protocol.rspnd.TGive;
import networking.torrent.protocol.rspnd.THave;

public class TListener implements Runnable {

	Socket socket;
	TorrentService tSrvce;

	public TListener(Socket socket, TorrentService tSrvce)
	{
		this.tSrvce = tSrvce;
		this.socket = socket;
	}

	/*
	 * Reply to queries
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() 
	{
		byte[] block = new byte[Config.SizeOfPce];
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			ObjectInputStream  inp = new ObjectInputStream(socket.getInputStream());

			while(true) {
				TQuery query = (TQuery) inp.readObject();

				switch(query.getType()) 
				{
				case Nani:
					TNani tnani = (TNani) query;
					out.writeObject(new THave(tSrvce.fStatez.get(tnani.info).getHave()));
					return;
				case Want:
					TWant twant = (TWant) query;
					FileState fstate = tSrvce.fStatez.get(twant.info);
				//	byte[] block = new byte[fstate.getMyTorrent().getPSize()];
					fstate.get(block, twant.pcno);
					out.writeObject(new TGive(new Piece(block)));
					return;
				default:
					break;
				}
			}

			// if receive request for what chunks we have, then send that
			// if receive request for chunk then send chunk

		} catch (IOException e) {
		} catch(ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in TListener");
		} 
	}

}
