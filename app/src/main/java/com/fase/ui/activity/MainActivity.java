package com.fase.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.fase.R;
import com.fase.model.enums.Screens;
import com.fase.mvp.presenter.MainActivityPresenter;
import com.fase.mvp.view.MainActivityView;
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;

public class MainActivity extends CommonActivity implements MainActivityView {

    @InjectPresenter
    MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showScreen(Screens.NONE); // TODO: change to main screen


        mPresenter.test();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbarClickListener() {
        if (vToolbar != null) {
            mPresenter.getCompositeDisposable()
                    .add(RxToolbar.navigationClicks(vToolbar)
                            .subscribe(event -> {
                                if (isBackAction) {
                                    onBackPressed();
                                } else {
                                    openNavigationDrawer();
                                }
                            }));
        }
    }

    @Override
    protected void processScreen(Screens screen) {
        switch (screen) {
//            case MAIN:
//                mNavigator.replaceFragment(MainFragment.newInstance());
//                break;
        }
    }

    @Override
    protected Screens getMainScreen() {
        return Screens.NONE; // TODO: change to main screen
    }

    @Override
    protected void holdDrawer(boolean hold) {
        if (vDrawerLayout != null) {
            vDrawerLayout.setDrawerLockMode(hold ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
