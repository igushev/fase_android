package com.fase.base;

import android.support.annotation.StringRes;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface BaseView extends MvpView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showError(@StringRes int strResId);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showError(String message);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showProgress();

    void hideProgress();

    void hideKeyboard();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showNoNetworkError(Runnable retryAction);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showErrorSkipRetryDialog(Runnable retryAction);
}
