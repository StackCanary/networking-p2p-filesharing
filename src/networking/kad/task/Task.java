package networking.kad.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import networking.config.Config;
import networking.kad.KadNode;
import networking.kad.KademliaService;

public abstract class Task {
	
	ExecutorService executor = Executors.newFixedThreadPool(Config.alpha);
	KademliaService kService;
	
	public Task(KademliaService service) {
		this.kService = service;
	}
	
	public KademliaService getService() {
		return this.kService;
	}
	
	protected ExecutorService getExecutor() {
		return this.executor;
	}
	
	public abstract void execute();
}
