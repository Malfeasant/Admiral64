package us.malfeasant.admiral64.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import us.malfeasant.admiral64.worker.Request.Type;

public class WorkQueue {
	private final Runnable ack;
	private final BlockingQueue<Request> queue;
	private final WorkSender sender = new WorkSender();
	private final WorkReceiver receiver = new WorkReceiver();
	
	public WorkQueue(Runnable ack) {
		this.ack = ack;
		queue = new LinkedBlockingQueue<>();
	}
	
	/**
	 *	Intended to be given to the application thread
	 */
	public class WorkSender {
		private WorkSender() {}	// Only WorkQueue should be able to construct
		public void requestTick() {
			queue.add(Request.TICK);	// throws exception if full- since it's a linked queue, that shouldn't happen.
		}
		public void requestCycles(int cycles) {
			// TODO: measure performance, may make sense to maintain a cache of common counts
			queue.add(new Request(Type.CYCLES, cycles));
		}
	}
	/**
	 *	Intended to be given to the worker thread
	 */
	public class WorkReceiver {
		private WorkReceiver() {}	// Only WorkQueue should be able to construct
		public Request receive() throws InterruptedException {
			return queue.take();
		}
		/**
		 *	This isn't really necessary for the work itself, but so fast mode doesn't stack up work exponentially
		 */
		public void ack() {
			Platform.runLater(() -> ack.run());
		}
	}
	public WorkSender getSender() { return sender; }
	public WorkReceiver getReceiver() { return receiver; }
}
