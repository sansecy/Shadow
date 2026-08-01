package com.lody.virtual.client.hook.proxies.am;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lody.virtual.client.interfaces.IInjector;
import com.sansecy.androidlib.VirtualCore;

import mirror.android.app.ActivityThread;

/**
     * @author Lody
     * @see Handler.Callback
     */
    public class HCallbackStub implements Handler.Callback, IInjector {

        private static final String TAG = "HCallbackStub-App";
        private static final HCallbackStub sCallback = new HCallbackStub();

        private boolean mCalling = false;


        private Handler.Callback otherCallback;

        private HCallbackStub() {
        }

        public static HCallbackStub getDefault() {
            return sCallback;
        }

        private static Handler getH() {
            return ActivityThread.mH.get(VirtualCore.mainThread());
        }

        private static Handler.Callback getHCallback() {
            try {
                Handler handler = getH();
                return mirror.android.os.Handler.mCallback.get(handler);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public boolean handleMessage(Message msg) {
            long l = System.currentTimeMillis();
            Handler target = msg.getTarget();
            target.handleMessage(msg);
            Log.d(TAG, "handleMessage() called with: cost = [" + (System.currentTimeMillis() - l) + "]ms");
            return true;
        }


        @Override
        public void inject() throws Throwable {
            otherCallback = getHCallback();
            mirror.android.os.Handler.mCallback.set(getH(), this);
        }

        @Override
        public boolean isEnvBad() {
            Handler.Callback callback = getHCallback();
            boolean envBad = callback != this;
            if (callback != null && envBad) {

            }
            return envBad;
        }

    }
