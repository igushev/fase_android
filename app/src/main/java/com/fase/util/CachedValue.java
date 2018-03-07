package com.fase.util;

import android.content.SharedPreferences;

public class CachedValue<T> {

    private static final Object mLock = new Object();

    private T mValue;
    private T mDefaultValue;
    private Class mType;
    private String mName;
    private boolean loaded = false;
    private SharedPreferences mSharedPreferences;

    public CachedValue(SharedPreferences preferences, String name, Class type) {
        this(preferences, name, null, null, type);
    }

    public CachedValue(SharedPreferences preferences, String name, T defValue, Class type) {
        this(preferences, name, null, defValue, type);
    }

    public CachedValue(SharedPreferences preferences, String name, T value, T defValue, Class type) {
        this.mName = name;
        this.mType = type;
        this.loaded = mValue != null;
        this.mValue = value;
        this.mDefaultValue = defValue;
        this.mSharedPreferences = preferences;
    }

    public void setValue(T value) {
        synchronized (mLock) {
            loaded = true;
            write(this.mValue = value);
        }
    }

    public T getValue() {
        synchronized (mLock) {
            if (!loaded) {
                this.mValue = load();
                loaded = true;
            }
            return this.mValue;
        }
    }

    public String getName() {
        return mName;
    }

    private void write(T value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (value instanceof String) {
            editor.putString(mName, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(mName, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(mName, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(mName, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(mName, (Boolean) value);
        }

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    private T load() {
        if (mType == String.class) {
            return (T) mSharedPreferences.getString(mName, (String) mDefaultValue);
        } else if (mType == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(mName, (Integer) mDefaultValue));
        } else if (mType == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(mName, (Float) mDefaultValue));
        } else if (mType == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(mName, (Long) mDefaultValue));
        } else if (mType == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(mName, (Boolean) mDefaultValue));
        }
        return null;
    }

    public void delete() {
        synchronized (mLock) {
            mSharedPreferences.edit().remove(mName).apply();
            clear();
        }
    }

    public void clear() {
        synchronized (mLock) {
            loaded = false;
            this.mValue = null;
        }
    }
}
