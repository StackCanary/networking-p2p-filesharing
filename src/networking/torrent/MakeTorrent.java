package networking.torrent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;

import networking.config.Config;

public class MakeTorrent {
	
	private Digestor digest = new Digestor();

	public MakeTorrent()
	{
		
	}
	
	public MyTorrent mkTorrent(File file) throws IOException
	{
		RandomAccessFile finStream = new RandomAccessFile(file, "r");
		
		MyTorrent result = new MyTorrent(file);
		
		for (int i = 0; i < Math.ceil(( (double) file.length() / Config.SizeOfPce)); i++) {
			byte[] block = new byte[Config.SizeOfPce];
			finStream.seek(i * Config.SizeOfPce);
			finStream.read(block, 0, Config.SizeOfPce);
			digest.get().reset();
			digest.get().update(block);
			byte[] data = digest.get().digest().clone();
			System.out.println(new String(Base64.getEncoder().encode(data)));
			result.insert(data);
		}
		
		finStream.close();
		
		return result;
	}	
	
	
}
