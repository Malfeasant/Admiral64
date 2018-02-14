package us.malfeasant.admiral64.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkQueue {
	private final BlockingQueue<Request> queue;
	
	WorkQueue() {
		queue = new LinkedBlockingQueue<>();
	}
}
