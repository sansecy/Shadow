package com.bytedance.boost_multidex;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public abstract class OptimizeIntentService {

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        
        public void handleMessage(Message msg) {
            onHandleIntent();
        }
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public OptimizeIntentService(String name) {
        mName = name;
    }

    public void onCreate() {
        // TODO: It would be nice to have an option to hold a partial wakelock
        // during processing, and to have a static startService(Context, Intent)
        // method that would launch the service & hand off a wakelock.
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    
    public void onStart() {
        Message msg = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(msg);
    }

    public void onDestroy() {
        mServiceLooper.quit();
    }

//    @WorkerThread
    protected abstract void onHandleIntent();
}
