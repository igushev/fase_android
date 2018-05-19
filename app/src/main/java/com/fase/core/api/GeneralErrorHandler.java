package com.fase.core.api;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.akaita.java.rxjava2debug.RxJava2Debug;
import com.fase.R;
import com.fase.base.BaseView;
import com.fase.model.exception.NoNetworkException;

import java.lang.ref.WeakReference;
import java.net.SocketException;

import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;
import retrofit2.HttpException;
import timber.log.Timber;

public class GeneralErrorHandler implements Consumer<Throwable> {

    private final WeakReference<BaseView> mViewReference;
    private final Runnable mWithAction;
    private final Runnable mRetryAction;
    private final Runnable mReloadAction;

    public GeneralErrorHandler(BaseView view) {
        this(view, null, null, null);
    }

    public GeneralErrorHandler(BaseView view, Runnable withAction) {
        this(view, withAction, null, null);
    }

    public GeneralErrorHandler(BaseView view, Runnable withAction, Runnable retryAction, Runnable reloadAction) {
        mViewReference = new WeakReference<>(view);
        mWithAction = withAction;
        mRetryAction = retryAction;
        mReloadAction = reloadAction;
    }

    @Override
    public void accept(@NonNull Throwable throwable) {
        hideProgress();
        if (throwable instanceof NoNetworkException) {
            if (mRetryAction != null) {
                showNoNetworkError(mReloadAction);
            } else {
                showError(R.string.message_no_internet);
            }
        } else if (throwable instanceof SocketException) {
            showError(R.string.network_connection_problem);
            Timber.e(RxJava2Debug.getEnhancedStackTrace(throwable));
        } else if (throwable instanceof HttpException) {
            if (throwable.getMessage().contains("500")) {
                showErrorSkipRetry(mRetryAction);
                return; // ignore withAction call
            } else {
                showError(throwable.getMessage());
                Timber.e(RxJava2Debug.getEnhancedStackTrace(throwable));
            }
        } else if (throwable instanceof CompositeException) {
            CompositeException e = (CompositeException) throwable;
            Timber.e(RxJava2Debug.getEnhancedStackTrace(throwable));
            for (Throwable throwable1 : e.getExceptions()) {
                Timber.e(RxJava2Debug.getEnhancedStackTrace(throwable1).toString());
            }
        } else {
            if (!TextUtils.isEmpty(throwable.getMessage())) {
                showError(throwable.getMessage());
            } else {
                showError(R.string.error_occurred);
            }
            Timber.e(RxJava2Debug.getEnhancedStackTrace(throwable));
        }
        if (mWithAction != null) {
            mWithAction.run();
        }
    }

    private void showError(String message) {
        BaseView view = mViewReference.get();
        if (view != null) {
            view.showError(message);
        }
    }

    private void showError(@StringRes int strResId) {
        BaseView view = mViewReference.get();
        if (view != null) {
            view.showError(strResId);
        }
    }

    private void hideProgress() {
        BaseView view = mViewReference.get();
        if (view != null) {
            view.hideProgress();
        }
    }

    private void showNoNetworkError(Runnable reloadAction) {
        BaseView view = mViewReference.get();
        if (view != null) {
            view.showNoNetworkError(reloadAction);
        }
    }

    private void showErrorSkipRetry(Runnable retryAction) {
        BaseView view = mViewReference.get();
        if (view != null) {
            view.showErrorSkipRetryDialog(retryAction);
        }
    }
}
