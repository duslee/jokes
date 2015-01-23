package com.common.as.utils;

import java.util.concurrent.Future;

public interface ThreadPoolsInterface {

	public Future<?> submit(Runnable run, boolean isHigh);
}
