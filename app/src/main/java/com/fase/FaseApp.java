package com.fase;

import android.app.Application;
import android.content.res.Resources;

import com.akaita.java.rxjava2debug.RxJava2Debug;
import com.crashlytics.android.Crashlytics;
import com.fase.util.CrashReportingTree;
import com.fase.util.GoogleApiHelper;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import io.reactivex.plugins.RxJavaPlugins;
import io.realm.Realm;
import timber.log.Timber;

public class FaseApp extends Application {

    public static FaseApp sInstance;
    private GoogleApiHelper mGoogleApiHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        mGoogleApiHelper = new GoogleApiHelper(this);

        JodaTimeAndroid.init(this);
        Realm.init(this);
        RxJava2Debug.enableRxJava2AssemblyTracking();
        RxJavaPlugins.setErrorHandler(throwable -> Timber.e(RxJava2Debug.getEnhancedStackTrace(throwable)));
    }

    public static Resources getRes() {
        return sInstance.getResources();
    }

    public static synchronized FaseApp getApplication() {
        return sInstance;
    }

    private GoogleApiHelper getGoogleApiHelperInstance() {
        return this.mGoogleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getApplication().getGoogleApiHelperInstance();
    }
}
