package networking.config;

public class Config {

	public static final int SizeOfPce = 1 << 24;
	public static final int SizeOfBlk = 1 << 14;
	
	public static final int SizeOfKey = 8;
	public static final int SizeOfBkt = 6;
	
	public static final int timeout = 5; // TODO change this back to 30
	
	public static final int dgramSize = 2000;
	
	public static final int alpha = 3; // kad concurrency parameter
	
	public static final int defaultNodePort = 14331;
	public static final int defaultPeerPort = 49052;
	
	public static final String torrntdir = "torrents/";
	public static final String downlsdir = "/cs/scratch/saaz/";
	
	
	public static final String bootstrapper = "pc2-061-l.cs.st-andrews.ac.uk";
}
