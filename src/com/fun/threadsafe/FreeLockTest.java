package com.fun.threadsafe;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import com.fun.PerfTestBase;

public class FreeLockTest extends PerfTestBase {
	private AtomicLong ITERATIONS = null;

	private long saleTicket() {
		long tmp = ITERATIONS.getAndDecrement();
		if (tmp > 0) {
			return tmp;
		}
		return -1;
	}

	@Override
	protected long run() {

		ITERATIONS = new AtomicLong(100 * 1000 * 1000L);
		// ITERATIONS = new AtomicLong(10L);
		long opsSenconds = 0;
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
		// two sale ticket threads
		for (int i = 0; i < 4; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cyclicBarrier.await();
						long ticket = -1;
						while ((ticket = saleTicket()) > 0) {
							// System.out.printf("sale ticket:%,d\n",ticket);
						}
						System.out.printf("%s--sale ticket is done:%,d\n", Thread.currentThread().getName(), ticket);
						cyclicBarrier.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}).start();
		}

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
		new FreeLockTest().runTests();
	}
}
