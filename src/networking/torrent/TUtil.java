package networking.torrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import networking.config.Config;
import networking.torrent.client.TorrentService;
import networking.torrent.file.FileState;

public class TUtil {

	/*
	 * https://stackoverflow.com/questions/10242380
	 */
	public static Collection<Integer> sequence(int start, int last)
	{
		return IntStream.rangeClosed(start, last).boxed().collect(Collectors.toList());
	}
	
	public static void write(Serializable object, File file) throws IOException
	{
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
		stream.writeObject(object);
		stream.close();
	}
	
	public static Object read(File file) throws IOException
	{
	     ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
	     
	     try {
			return stream.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		} finally {
			stream.close();
		}
	}
	
	
	public static List<MyTorrent> loadAll()
	{
		List<MyTorrent> result = new ArrayList<MyTorrent>();
		
		File tdir = new File(Config.torrntdir);
		
		for (File file : tdir.listFiles()) {
			try {
				result.add((MyTorrent) TUtil.read(file));
			} catch (IOException e) {
				System.out.println("Non-mytorrent file found, ignoring " + file);
			}
		}
		
		
		return result;
	}
	
	/* 
	 * This routine loads/creates torrent files
	 */
	public static void ldStates(TorrentService tService)
	{
		MakeTorrent mkTorrent = new MakeTorrent();
		
		Set<String> files = new HashSet<String>();
		
		for (MyTorrent torrent : TUtil.loadAll()) {
				try {
					FileState fstate = new FileState(torrent);
					tService.fStatez.put(fstate.getInfoHash(), fstate);
					files.add(torrent.filename);
				} catch (IOException e) {
					System.out.println("Cannot open file " + torrent);
				}
		}
		
		for (File file : new File(Config.downlsdir).listFiles()) {
			if (!files.contains(file.getName()) && !file.getName().equals("00README.txt")) {
				try {
					MyTorrent torrent = mkTorrent.mkTorrent(file);
					torrent.save();
					FileState fstate = new FileState(torrent);
					tService.fStatez.put(fstate.getInfoHash(), fstate);
				} catch (IOException e) {
					System.out.println("Could not create torrent for file " + file);
				}
			}
		}
	
	}
	
}
