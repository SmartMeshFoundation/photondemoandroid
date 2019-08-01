package com.smartmesh.photon.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * thread pool
 * */
public class ThreadPoolUtils {

    private static volatile ThreadPoolUtils S_INST;

    public static ExecutorService mThreadPool;

    public static ThreadPoolUtils getInstance() {
        if (S_INST == null) {
            synchronized (ThreadPoolUtils.class) {
                if (S_INST == null) {
                    S_INST = new ThreadPoolUtils();
                }
            }
        }
        return S_INST;
    }

    /**
     * 创建可缓存线程池
     * Create a cache able thread pool
     * */
    public ExecutorService getCachedThreadPool(){
        if (mThreadPool == null){
            mThreadPool = Executors.newCachedThreadPool();
        }
        return mThreadPool;
    }

}
