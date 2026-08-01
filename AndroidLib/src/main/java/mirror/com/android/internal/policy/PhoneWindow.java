package mirror.com.android.internal.policy;

import android.os.IInterface;
import android.util.Log;

import mirror.RefClass;
import mirror.RefStaticObject;

public class PhoneWindow {
    private static final String TAG = "PhoneWindow-App";
    public static Class<?> TYPE;
    public static RefStaticObject<IInterface> sWindowManager;

    static {
        Log.d(TAG, "static initializer() called");
        TYPE = RefClass.load(PhoneWindow.class, "com.android.internal.policy.impl.PhoneWindow$WindowManagerHolder");
        if (TYPE == null) {
            TYPE = RefClass.load(PhoneWindow.class, "com.android.internal.policy.PhoneWindow$WindowManagerHolder");
        }
    }
}