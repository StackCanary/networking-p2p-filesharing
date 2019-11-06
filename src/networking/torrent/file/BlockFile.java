package networking.torrent.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import networking.config.Config;
import networking.torrent.MyTorrent;

public class BlockFile {
	
	RandomAccessFile device;
	MyTorrent mytorrent;
	
	
	public BlockFile(MyTorrent mytorrent) throws IOException
	{
		this.mytorrent = mytorrent;
		device = new RandomAccessFile(new File(Config.downlsdir + mytorrent.filename), "rw");
	}
	
	public synchronized void put(byte[] block, int i) throws IOException
	{
		int bytesToWrite = mytorrent.getPSize();
		
		if (i == mytorrent.noOfPieces() - 1)
			bytesToWrite = mytorrent.filesize % mytorrent.getPSize();
		
		System.out.println(bytesToWrite);
		System.out.println("Size of " + block.length);
		
		device.seek(i * mytorrent.getPSize());
		device.write(block, 0, bytesToWrite);
		
	}
	
	public synchronized void get(byte[] block, int i) throws IOException
	{
		device.seek(i * mytorrent.getPSize());
		device.read(block, 0, mytorrent.getPSize());
	}
	

}
