package com.utils.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolFactory {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    public ThreadPoolFactory() {
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix){
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon){
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadPool(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE_SIZE,
                    KEEP_ALIVE_TIME,TimeUnit.SECONDS,queue,threadFactory);

    }

    public static ThreadFactory createThreadPool(String threadNamePrefix, Boolean daemon){
        if(threadNamePrefix != null){
            if(daemon != null){
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            }else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%D")
                        .build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
