package com.fase.base.navigator;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.fase.base.BaseFragment;
import com.fase.model.enums.FragmentTransition;

public interface Navigator {

    String EXTRA_ARG = "args";

    void finishActivity();

    void finishActivity(boolean affinity);

    void startActivity(@NonNull String action);

    void startActivity(@NonNull Class<? extends Activity> activityClass);

    void startActivity(@NonNull Class<? extends Activity> activityClass, boolean clearTop);

    void startActivity(@NonNull Class<? extends Activity> activityClass, Bundle args);

    void startActivity(@NonNull Class<? extends Activity> activityClass, Bundle args, boolean clearTop);

    void startActivityForResult(@NonNull Class<? extends Activity> activityClass, int requestCode);

    void startActivityForResult(@NonNull Class<? extends Activity> activityClass, Bundle args, int requestCode);

    void replaceFragment(@NonNull BaseFragment fragment);

    void replaceFragment(@NonNull BaseFragment fragment, FragmentTransition transition);

    void replaceFragment(@IdRes int containerId, @NonNull BaseFragment fragment);

    void replaceFragment(@IdRes int containerId, @NonNull BaseFragment fragment, FragmentTransition transition);

    void replaceFragment(@NonNull BaseFragment fragment, boolean addToBackStack);

    void replaceFragment(@NonNull BaseFragment fragment, boolean addToBackStack, FragmentTransition transition);

    void replaceFragment(@IdRes int containerId, @NonNull BaseFragment fragment, boolean addToBackStack);

    void replaceFragment(@IdRes int containerId, @NonNull BaseFragment fragment, boolean addToBackStack, FragmentTransition transition);

    void clearBackStack(boolean exceptParent);

    void closeFragment();
}
