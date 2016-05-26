package com.fun.threadsafe;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

import com.fun.PerfTestBase;

/**
 * 无锁、Volatile、锁性能比较
 * 
 * @author lixiangqian
 *
 */
public class LockTest extends PerfTestBase {
	private Long ITERATIONS = 0L;

	private ReentrantLock lock = new ReentrantLock();
	private Object lock2 = new Object();

	long initIterations = 100 * 1000 * 1000L;
	int concurrentCount = 5;

	public LockTest(int concurrentCount, long initIterations) {
		this.initIterations = initIterations;
		this.concurrentCount = concurrentCount;
	}

	synchronized long saleTicket() {
		if (ITERATIONS > 0) {
			doSomething();
			return ITERATIONS--;
		} else {
			return -1;
		}
	}

	long saleTicket2() {
		lock.lock();
		try {
			if (ITERATIONS > 0) {
				return ITERATIONS--;
			} else {
				return -1;
			}
		} finally {
			lock.unlock();
		}
	}

	long saleTicket3() {
		synchronized (lock2) {
			if (ITERATIONS > 0) {
				return ITERATIONS--;
			} else {
				return -1;
			}
		}
	}

	@Override
	protected long run() {
		ITERATIONS = initIterations;
		long opsSenconds = 0;
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(concurrentCount + 1);
		// n sale ticket threads
		for (int i = 0; i < concurrentCount; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cyclicBarrier.await();
						long ticket = -1;
						while ((ticket = saleTicket()) > 0) {
							// System.out.printf("%s---sale ticket:%,d\n", Thread.currentThread().getName(), ticket);
						}
						System.out.printf("%s--sale ticket is done:%,d\n", Thread.currentThread().getName(), ticket);
						cyclicBarrier.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, "thread" + i).start();
		}

		try {
			cyclicBarrier.await();
			cyclicBarrier.reset();
			long startTime = System.currentTimeMillis();
			cyclicBarrier.await();
			opsSenconds = (System.currentTimeMillis() - startTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return opsSenconds;
	}

	@Override
	protected long getWaitTime() {
		return 0;
	}

	public static void main(String[] args) {
		long initIterations = 100 * 1000 * 1000L;
		int concurrentCount = 5;
		if (args.length > 0) {
			concurrentCount = Integer.parseInt(args[0]);
			if (args.length > 1) {
				initIterations = Long.parseLong(args[1]);
			}
		}
		new LockTest(concurrentCount, initIterations).runTests();
	}
}
