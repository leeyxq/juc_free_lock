package com.fun;

/**
 * 性能测试基类
 * 
 * @author lixiangqian
 *
 */
public abstract class PerfTestBase {
	public static int NUM = 7;

	/**
	 * 运行测试方法入口
	 */
	public void runTests() {
		try {
			Thread.sleep(getWaitTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < NUM; i++) {
			System.gc();
			System.out.format("第%d测试：%,d ms%n", i, Long.valueOf(run()));
		}
	}

	protected abstract long run();

	/**
	 * 运行测试前，等待时长，用来jvm连接监控
	 * 
	 * @return
	 */
	protected abstract long getWaitTime();
	
	protected void doSomething(){
	    long i = 100L*1000L;
		while(i-->0);
	}
}