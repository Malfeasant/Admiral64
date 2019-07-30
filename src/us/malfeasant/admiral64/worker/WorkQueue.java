package us.malfeasant.admiral64.worker;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import us.malfeasant.admiral64.timing.RunMode;

public class WorkQueue {
	private final BlockingQueue<RunMode> queue;
	private final WorkSender sender = new WorkSender();
	private final WorkReceiver receiver = new WorkReceiver();
	
	public WorkQueue() {
		queue = new LinkedBlockingQueue<>();
	}
	
	/**
	 *	Intended to be given to the application thread
	 */
	public class WorkSender {
		private WorkSender() {}	// Only WorkQueue should be able to construct
		public void changeMode(RunMode mode) {
			queue.add(mode);	// Throws exception if fails- since queue is unbounded, that shouldn't ever happen.
		}
	}
	/**
	 *	Intended to be given to the worker thread
	 */
	class WorkReceiver {
		private WorkReceiver() {}	// Only WorkQueue should be able to construct
		Optional<RunMode> receive() {
			RunMode mode = queue.poll();
			return Optional.ofNullable(mode);
		}
		RunMode receiveWait() throws InterruptedException {
			return queue.take();
		}
	}
	public WorkSender getSender() { return sender; }
	public WorkReceiver getReceiver() { return receiver; }
}
