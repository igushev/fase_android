package com.fase.util;

import android.content.SharedPreferences;

public class CachedValueFactory {

    private SharedPreferences mSharedPreferences;

    public CachedValueFactory(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public <T> CachedValue<T> getValue(String name, Class type) {
        return new CachedValue<>(mSharedPreferences, name, type);
    }

    public <T> CachedValue<T> getValue(String name, T defValue, Class type) {
        return new CachedValue<>(mSharedPreferences, name, defValue, type);
    }

    public <T> CachedValue<T> getValue(String name, T value, T defValue, Class type) {
        return new CachedValue<>(mSharedPreferences, name, value, defValue, type);
    }
}
