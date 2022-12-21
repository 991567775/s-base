package cn.ezeyc.edpbase.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程池
 * @author wz
 */
public class ThreadPool {
    public   static ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.
                    Builder().namingPattern("edp").daemon(true).build());



}
