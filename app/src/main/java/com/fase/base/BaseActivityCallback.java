package com.fase.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.fase.base.navigator.Navigator;

public interface BaseActivityCallback {

    Context getContext();

    void showError(@StringRes int strResId);

    void showError(String message);

    void showProgress();

    void hideProgress();

    void hideKeyboard();

    void updateTitle(int titleResId);

    void updateTitle(String title);

    Toolbar getToolbar();

    void showBackButton();

    void hideBackButton();

    void hideNavigationButton();

    int getToolbarHeight();

    int getStatusBarHeight();

    int getNavigationBarHeight();

    DisplayMetrics getScreenParams();

    Navigator getNavigator();

    void showNoNetworkError(Runnable retryAction);

    void trackScreen(String screenName);

    void trackAction(String category, String action, String label, Integer value);
}
