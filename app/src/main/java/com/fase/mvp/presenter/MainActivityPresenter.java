package com.fase.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.fase.base.BasePresenter;
import com.fase.mvp.view.MainActivityView;

@InjectViewState
public class MainActivityPresenter extends BasePresenter<MainActivityView> {

    public void test() {
        mDataManager.getService().compose(getBasicNetworkFlowSingleTransformer(true))
                .subscribe((response, throwable) -> {
                    getViewState().hideProgress();
                    if (throwable != null) {
                        // TODO: render view state
                    }
                });
    }
}
