package com.fase.base;

import com.arellomobile.mvp.MvpPresenter;
import com.fase.core.manager.DataManager;
import com.fase.util.RxUtil;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<VIEW extends BaseView> extends MvpPresenter<VIEW> {

    protected final DataManager mDataManager;
    private CompositeDisposable mCompositeDisposable;

    public BasePresenter() {
        mDataManager = DataManager.getInstance();
    }

    public final CompositeDisposable getCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        return mCompositeDisposable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.safeUnSubscribe(mCompositeDisposable);
        mCompositeDisposable = null;
    }

    protected <T> ObservableTransformer<T, T> getBasicNetworkObservableTransformer(boolean showProgress) {
        return RxUtil.basicNetworkFlowObservableTransformer(this, getViewState(), showProgress);
    }

    protected CompletableTransformer getBasicNetworkFlowCompletableTransformer(boolean showProgress) {
        return RxUtil.basicNetworkFlowCompletableTransformer(this, getViewState(), showProgress);

    }

    protected <T> SingleTransformer<T, T> getBasicNetworkFlowSingleTransformer(boolean showProgress) {
        return RxUtil.basicNetworkFlowSingleTransformer(this, getViewState(), showProgress);
    }
}
