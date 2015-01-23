package com.common.as.network.httpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.common.as.utils.ThreadPoolsInterface;

public class ExecuterServerManager implements ThreadPoolsInterface{
	private static final String TAG = "ExecuterServerManager";	
	
	private static final int CORE_LOW_POOL_SIZE = 4;
	private static final int CORE_HIGH_POOL_SIZE = 2;
	private static ExecuterServerManager instance = null;
	private ExecutorService mExecutorLowService;
	private ExecutorService mExecutorHighService;
	private List<Future<?>> resultLowList = new ArrayList<Future<?>>(CORE_LOW_POOL_SIZE); 
	private List<Future<?>> resultHighList = new ArrayList<Future<?>>(CORE_HIGH_POOL_SIZE); 

	public static synchronized ExecuterServerManager getInstance() {
		if (instance == null) {
			instance = new ExecuterServerManager();
		}
		return instance;
	}
	
	private ExecuterServerManager() {
		ThreadFactory sThreadFactory0 = new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);

	        public Thread newThread(Runnable r) {
	            return new Thread(r, "low thread #" + mCount.getAndIncrement());
	        }
	    };
		ThreadFactory sThreadFactory1 = new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);

	        public Thread newThread(Runnable r) {
	            return new Thread(r, "high thread #" + mCount.getAndIncrement());
	        }
	    };
	    mExecutorLowService = Executors.newFixedThreadPool(CORE_LOW_POOL_SIZE, sThreadFactory0);
	    mExecutorHighService = Executors.newFixedThreadPool(CORE_HIGH_POOL_SIZE, sThreadFactory1);	    
	}
	
	public synchronized Future<?> submit(Runnable run, boolean isHigh){
		Future<?> f = null;
		for (int i = resultLowList.size()-1; i >= 0; i--) {
			f = resultLowList.get(i);
			if (f.isDone()) {
				resultLowList.remove(f);
			}
			
		}
		
		
		for (int i = resultHighList.size()-1; i >= 0; i--) {
			f = resultHighList.get(i);
			if (f.isDone()) {
				resultHighList.remove(f);
			}			
		}
		
		if (isHigh) {
			if (resultHighList.size() >= CORE_HIGH_POOL_SIZE 
					&& resultLowList.size() < CORE_LOW_POOL_SIZE) {
				f = mExecutorLowService.submit(run);
				resultLowList.add(f);
			} else {
				f = mExecutorHighService.submit(run);
				resultHighList.add(f);
			}
		} else {
			f = mExecutorLowService.submit(run);
			resultLowList.add(f);
		}
		
		
	//	MyLog.d(TAG, "high size=" +resultHighList.size()+ ";low size="+resultLowList.size());
//		if (DEBUG) {
//			MyLog.d(TAG, "submit after");
//		}
		return f;
	}
	
}
