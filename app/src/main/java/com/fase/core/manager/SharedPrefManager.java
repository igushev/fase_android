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

    private static final String EMAIL = "email";
    private static final String USER_ID = "userId";
    private static final String CURRENT_SCREEN_ID = "currentScreenId";
    private static final String CURRENT_SESSION_ID = "currentSessionId";

    private static final String DEVICE_TOKEN = "deviceToken";

    private CachedValueFactory mCachedValueFactory;
    private SecurePreferences mSecurePreferences;
    private CachedValue<Long> mUserId;
    private CachedValue<String> mDeviceToken;
    private CachedValue<String> mCurrentScreenId;
    private CachedValue<String> mCurrentSessionId;

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
        Context context = FaseApp.getAppInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences(BuildConfig.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        mCachedValueFactory = new CachedValueFactory(sharedPreferences);
        mSecurePreferences = new SecurePreferences(sharedPreferences, BuildConfig.SECURE_SHARED_PREFS_KEY, true);
        initCachedValues();
    }

    private void initCachedValues() {
        mUserId = mCachedValueFactory.getValue(USER_ID, -1L, Long.class);
        mDeviceToken = mCachedValueFactory.getValue(DEVICE_TOKEN, String.class);
        mCurrentScreenId = mCachedValueFactory.getValue(CURRENT_SCREEN_ID, String.class);
        mCurrentSessionId = mCachedValueFactory.getValue(CURRENT_SESSION_ID, String.class);
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

    void setEmail(String email) {
        mSecurePreferences.put(EMAIL, email);
    }

    String getEmail() {
        return mSecurePreferences.getString(EMAIL);
    }

    void setUserId(Long userId) {
        mUserId.setValue(userId);
    }

    Long getUserId() {
        return mUserId.getValue();
    }
}
