package com.fase.util;

import android.support.annotation.Nullable;

import timber.log.Timber;

public class CrashReportingTree extends Timber.Tree {

    private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private static final String CRASHLYTICS_KEY_TAG = "tag";
    private static final String CRASHLYTICS_KEY_MESSAGE = "message";

    @Override
    protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {
        // TODO: add crash reporter
//        if (!Fabric.isInitialized()) {
//            return;
//        }
//        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
//            return;
//        }
//        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
//        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
//        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);
//
//        if (t == null) {
//            Crashlytics.logException(new Exception(message));
//        } else {
//            Crashlytics.logException(t);
//        }
    }
}
