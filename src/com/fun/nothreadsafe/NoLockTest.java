package com.fun.nothreadsafe;

import java.util.concurrent.CyclicBarrier;

import com.fun.PerfTestBase;

public class NoLockTest extends PerfTestBase {

	private long ITERATIONS = 0L;

	@Override
	protected long run() {
		ITERATIONS = 1000 * 1000 * 1000L;
		long opsSenconds = 0;
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 1、计数线程和计时主线程同时开启
					cyclicBarrier.await();
					while (ITERATIONS > 0) {
						if (ITERATIONS-- == 0)
							break;
					}
					// 2、计数结束和计时主线程统计耗时
					cyclicBarrier.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {
			// 1、计数线程和计时主线程同时开启
			cyclicBarrier.await();
			long startTime = System.currentTimeMillis();
			cyclicBarrier.reset();
			// 2、计数结束和计时主线程统计耗时
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
		new NoLockTest().runTests();
	}

}
