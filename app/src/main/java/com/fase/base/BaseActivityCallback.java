package com.fase.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

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

    void showNavigationBurger();

    void hideNavigationButton();

    int getToolbarHeight();

    int getStatusBarHeight();

    int getNavigationBarHeight();

    DisplayMetrics getScreenParams();

    void showNoNetworkError(Runnable retryAction);
}
