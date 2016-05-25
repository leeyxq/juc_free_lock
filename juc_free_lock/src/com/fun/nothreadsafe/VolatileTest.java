package com.fun.nothreadsafe;

import java.util.concurrent.CyclicBarrier;

import com.fun.PerfTestBase;

/**
 * 无锁、Volatile、锁性能比较
 * 
 * @author lixiangqian
 *
 */
public class VolatileTest extends PerfTestBase {
	private volatile long ITERATIONS = 0L;

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
						if (ITERATIONS-- == 0)
							break;
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
		new VolatileTest().runTests();
	}
}
