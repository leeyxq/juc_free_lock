package com.fun.nothreadsafe;

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
	private long ITERATIONS = 0L;
	private ReentrantLock lock = new ReentrantLock();

	@Override
	protected long run() {
		ITERATIONS = 1000 * 1000 * 1000L;
		long opsSenconds = 0;
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					cyclicBarrier.await();
					while (ITERATIONS > 0) {
						lock.lock();
						try {
							if (ITERATIONS-- == 0)
								break;
						} finally {
							lock.unlock();
						}
					}
					cyclicBarrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {
			cyclicBarrier.await();
			long startTime = System.currentTimeMillis();
			cyclicBarrier.reset();
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
		new LockTest().runTests();
	}
}
