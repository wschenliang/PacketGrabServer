package com.jiangnan.utils;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class AutoRemove<T> implements AutoCloseable {

    private ThreadLocal<T> tThreadLocal;

    public AutoRemove(ThreadLocal<T> tThreadLocal) {
        this.tThreadLocal = tThreadLocal;
    }

    @Override
    public void close() throws Exception {
        if (tThreadLocal != null){
            tThreadLocal.remove();
        }
    }

    public T closeAndGet(){
        if (tThreadLocal == null){
            return null;
        }
        T t = tThreadLocal.get();
        tThreadLocal.remove();
        return t;
    }
}
