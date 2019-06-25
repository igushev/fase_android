package com.fase.base;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

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

    void showErrorSkipRetryDialog(Runnable retryAction);
}
