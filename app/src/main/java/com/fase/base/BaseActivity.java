package com.fase.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.fase.R;
import com.fase.ui.fragment.dialog.AlertDialogFragment;
import com.fase.ui.fragment.dialog.ProgressDialogFragment;

import java.util.ArrayDeque;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends MvpAppCompatActivity implements BaseActivityCallback {

    @Nullable
    @BindView(android.R.id.content)
    View vRootView;
    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar vToolbar;

    protected ActionBarDrawerToggle mDrawerToggle;
    protected Unbinder mUnBinder;
    protected boolean isBackAction = false;

    private ProgressDialogFragment mProgressDialog;
    private Snackbar mSnackBar;
    private Queue<String> mErrorsList = new ArrayDeque<>();

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        initToolbar();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (layoutResID != 0) {
            super.setContentView(layoutResID);
            mUnBinder = ButterKnife.bind(this);
        }
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showError(int strResId) {
        showSnackbar(getString(strResId));
    }

    @Override
    public void showError(String message) {
        showSnackbar(message);
    }

    private void showSnackbar(String message) {
        if (vRootView != null) {
            if (mSnackBar != null && mSnackBar.isShown()) {
                mErrorsList.add(message);
            } else {
                mSnackBar = Snackbar.make(vRootView, message, Snackbar.LENGTH_LONG);
                mSnackBar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (!mErrorsList.isEmpty()) {
                            showSnackbar(mErrorsList.poll());
                        }
                    }
                });
                mSnackBar.show();
            }
        }
    }

    @Override
    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialogFragment.newInstance();
        }
        try {
            mProgressDialog.show(getSupportFragmentManager());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void initToolbar() {
        if (vToolbar != null) {
            setSupportActionBar(vToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
            }
        }
    }

    @Override
    public void updateTitle(int titleResId) {
        updateTitle(getString(titleResId));
    }

    @Override
    public void updateTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public Toolbar getToolbar() {
        return vToolbar;
    }

    protected void hideToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    protected void showToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    @Override
    public void showBackButton() {
        if (getSupportActionBar() != null) {
            if (mDrawerToggle != null) {
                mDrawerToggle.setDrawerIndicatorEnabled(false);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            isBackAction = true;
            holdDrawer(true);
        }
    }

    protected void holdDrawer(boolean hold) {
    }

    @Override
    public void showNavigationBurger() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            if (mDrawerToggle != null) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                mDrawerToggle.syncState();
            }
            isBackAction = false;
            holdDrawer(false);
        }
    }

    @Override
    public void hideNavigationButton() {
        if (isBackAction) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            if (mDrawerToggle != null) {
                mDrawerToggle.setDrawerIndicatorEnabled(false);
            }
        }
        holdDrawer(true);
    }

    @Override
    public int getToolbarHeight() {
        int height = 0;
        if (getToolbar() != null && getToolbar().isShown()) {
            height = getToolbar().getHeight();
        }
        return height;
    }

    @Override
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public DisplayMetrics getScreenParams() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    @Override
    public void showNoNetworkError(Runnable retryAction) {
        AlertDialogFragment.newInstance(getString(R.string.message_no_internet), getString(R.string.retry), (dialog, which) -> retryAction.run())
                .show(getSupportFragmentManager());
    }

    @Override
    public void showErrorSkipRetryDialog(Runnable retryAction) {
        AlertDialogFragment.newInstance(getString(R.string.error_occurred), getString(R.string.restart), getString(R.string.skip),
                (dialog, which) -> retryAction.run(), (dialog, which) -> { /* skip */ })
                .show(getSupportFragmentManager());
    }
}