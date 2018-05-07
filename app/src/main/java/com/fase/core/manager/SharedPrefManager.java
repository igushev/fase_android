package com.fase.core.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.fase.BuildConfig;
import com.fase.FaseApp;
import com.fase.core.SecurePreferences;
import com.fase.util.CachedValue;
import com.fase.util.CachedValueFactory;

public class SharedPrefManager {

    private static volatile SharedPrefManager mSingleton;

    private static final String CURRENT_SCREEN_ID = "currentScreenId";
    private static final String CURRENT_SESSION_ID = "currentSessionId";
    private static final String CURRENT_VERSION = "currentVersion";

    private static final String DEVICE_TOKEN = "deviceToken";

    private CachedValueFactory mCachedValueFactory;
    private SecurePreferences mSecurePreferences;
    private CachedValue<String> mDeviceToken;
    private CachedValue<String> mCurrentScreenId;
    private CachedValue<String> mCurrentSessionId;
    private CachedValue<String> mCurrentVersionInfo;

    static synchronized SharedPrefManager getInstance() {
        if (mSingleton == null) {
            synchronized (SharedPrefManager.class) {
                if (mSingleton == null) {
                    mSingleton = new SharedPrefManager();
                }
            }
        }
        return mSingleton;
    }

    private SharedPrefManager() {
        SharedPreferences sharedPreferences = FaseApp.getApplication().getSharedPreferences(BuildConfig.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        mCachedValueFactory = new CachedValueFactory(sharedPreferences);
        mSecurePreferences = new SecurePreferences(sharedPreferences, BuildConfig.SECURE_SHARED_PREFS_KEY, true);
        initCachedValues();
    }

    private void initCachedValues() {
        mDeviceToken = mCachedValueFactory.getValue(DEVICE_TOKEN, String.class);
        mCurrentScreenId = mCachedValueFactory.getValue(CURRENT_SCREEN_ID, String.class);
        mCurrentSessionId = mCachedValueFactory.getValue(CURRENT_SESSION_ID, String.class);
        mCurrentVersionInfo = mCachedValueFactory.getValue(CURRENT_VERSION, String.class);
    }

    /**
     * Methods
     */
    void setDeviceToken(String deviceId) {
        mDeviceToken.setValue(deviceId);
    }

    String getDeviceToken() {
        return mDeviceToken.getValue();
    }

    void setCurrentScreenId(String screenId) {
        mCurrentScreenId.setValue(screenId);
    }

    String getCurrentScreenId() {
        return mCurrentScreenId.getValue();
    }

    void setCurrentSessionId(String sessionId) {
        mCurrentSessionId.setValue(sessionId);
    }

    String getCurrentSessionId() {
        return mCurrentSessionId.getValue();
    }

    void setVersion(String version) {
        mCurrentVersionInfo.setValue(version);
    }

    String getVersion() {
        return mCurrentVersionInfo.getValue();
    }
}
