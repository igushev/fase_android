package com.fase.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.fase.base.navigator.Navigator;

import butterknife.ButterKnife;

public abstract class BaseFragment extends MvpAppCompatFragment {

    protected static final String KEY_SAVED_STATE = "SAVED_STATE";

    private BaseActivityCallback mBaseActivityCallback;

    protected abstract int getLayoutResourceId();

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attach(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            attach(activity);
        }
    }

    private void attach(Context context) {
        try {
            mBaseActivityCallback = (BaseActivityCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() + " must implement" + BaseActivityCallback.class.getSimpleName());
        }
    }

    @Override
    public Context getContext() {
        return mBaseActivityCallback.getContext();
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(getLayoutResourceId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBaseActivityCallback = null;
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
    }

    protected void updateTitle(String title) {
        mBaseActivityCallback.updateTitle(title);
    }

    protected void updateTitle(int titleResId) {
        mBaseActivityCallback.updateTitle(titleResId);
    }

    public void showProgress() {
        mBaseActivityCallback.showProgress();
    }

    public void hideProgress() {
        mBaseActivityCallback.hideProgress();
    }

    public void showError(String message) {
        mBaseActivityCallback.showError(message);
    }

    public void showError(@StringRes int strResId) {
        mBaseActivityCallback.showError(strResId);
    }

    public void hideKeyboard() {
        mBaseActivityCallback.hideKeyboard();
    }

    public void showBackButton() {
        mBaseActivityCallback.showBackButton();
    }

    public void hideBackButton() {
        mBaseActivityCallback.hideBackButton();
    }

    public void hideNavigationButton() {
        mBaseActivityCallback.hideNavigationButton();
    }

    public int getStatusBarHeight() {
        return mBaseActivityCallback.getStatusBarHeight();
    }

    public int getNavigationBarHeight() {
        return mBaseActivityCallback.getNavigationBarHeight();
    }

    protected int getToolbarHeight() {
        return mBaseActivityCallback.getToolbarHeight();
    }

    protected DisplayMetrics getScreenParams() {
        return mBaseActivityCallback.getScreenParams();
    }

    public Toolbar getToolbar() {
        return mBaseActivityCallback.getToolbar();
    }

    public void showNoNetworkError(Runnable retryAction) {
        mBaseActivityCallback.showNoNetworkError(retryAction);
    }

    public Navigator getNavigator() {
        return mBaseActivityCallback.getNavigator();
    }

    public void trackScreen(String screenName) {
        mBaseActivityCallback.trackScreen(screenName);
    }

    public void trackAction(String category, String action, String label, Integer value) {
        mBaseActivityCallback.trackAction(category, action, label, value);
    }
}
