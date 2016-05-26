package com.fun.threadsafe;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

import com.fun.PerfTestBase;

public class FreeLockTest extends PerfTestBase {
	private AtomicLong ITERATIONS = null;
	
	long initIterations = 100 * 1000 * 1000L;
	int concurrentCount = 5;
	
	public FreeLockTest(int concurrentCount, long initIterations){
		this.initIterations = initIterations;
		this.concurrentCount = concurrentCount;
	}
	
	private long saleTicket() {
		
		for(;;){
			long tmp = ITERATIONS.get();
			if (tmp > 0) {
				doSomething();
				if(ITERATIONS.compareAndSet(tmp, tmp-1))
					return tmp;
			}else{
				return -1;
			}
		}
		
	}

	@Override
	protected long run() {

		ITERATIONS = new AtomicLong(initIterations);
		long opsSenconds = 0;
		final CyclicBarrier cyclicBarrier = new CyclicBarrier(concurrentCount + 1);
		//n sale ticket threads
		for (int i = 0; i < concurrentCount; i++) {
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
		long initIterations = 100 * 1000 * 1000L;
		int concurrentCount = 5;
		if(args.length>0){
			concurrentCount = Integer.parseInt(args[0]);
			if(args.length>1){
				initIterations = Long.parseLong(args[1]);
			}
		}
		
		new FreeLockTest(concurrentCount, initIterations).runTests();
	}
}
