package com.fase;

import android.app.Application;
import android.content.res.Resources;

import com.fase.util.CrashReportingTree;

import net.danlew.android.joda.JodaTimeAndroid;

import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

public class FaseApp extends Application {

    public static FaseApp sInstance;

//    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

//        Fabric.with(this, new Crashlytics.Builder()
//                .core(new CrashlyticsCore.Builder()
//                        .disabled(BuildConfig.DEBUG)
//                        .build())
//                .build());

        JodaTimeAndroid.init(this);
        RxJavaPlugins.setErrorHandler(Timber::e);
    }

//    public static void logUserInCrashlytics(String email, String fullName) {
//        if (BuildConfig.DEBUG) {
//            return;
//        }
//
//        Crashlytics.setUserEmail(email);
//        Crashlytics.setUserName(fullName);
//    }

    public static Resources getRes() {
        return sInstance.getResources();
    }

    public static FaseApp getAppInstance() {
        return sInstance;
    }

//    private synchronized Tracker getDefaultTracker() {
//        if (mTracker == null) {
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            mTracker = analytics.newTracker(BuildConfig.GOOGLE_ANALYTICS_TRACK_ID);
//        }
//        return mTracker;
//    }

    public void trackScreen(String screenName) {
//        if (!TextUtils.isEmpty(screenName)) {
//            Tracker tracker = getDefaultTracker();
//            tracker.setScreenName(screenName);
//            getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
//        }
    }

    public void trackAction(String category, String action, String label, Integer value) {
//        if (TextUtils.isEmpty(category) && TextUtils.isEmpty(action) && TextUtils.isEmpty(label) && value == null) {
//            return;
//        }
//        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
//        if (!TextUtils.isEmpty(category)) {
//            eventBuilder.setCategory(category);
//        }
//        if (!TextUtils.isEmpty(action)) {
//            eventBuilder.setAction(action);
//        }
//        if (!TextUtils.isEmpty(label)) {
//            eventBuilder.setLabel(label);
//        }
//        if (value != null) {
//            eventBuilder.setValue(value.longValue());
//        }
//        getDefaultTracker().send(eventBuilder.build());
    }
}
