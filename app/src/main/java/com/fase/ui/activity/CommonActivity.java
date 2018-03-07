package com.fase.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.fase.R;
import com.fase.base.BaseActivity;
import com.fase.base.HasBackAction;
import com.fase.model.enums.Screens;

import butterknife.BindView;

public abstract class CommonActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    public static final String EXTRA_SCREEN = "SCREEN";

    @BindView(R.id.drawerLayout)
    DrawerLayout vDrawerLayout;
    @BindView(R.id.navigationView)
    NavigationView vNavigationView;

    private Screens mPage = Screens.NONE;
    protected Screens mDefaultPage = Screens.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDefaultPage = getMainScreen();
        initNavigationDrawer();
        initToolbarClickListener();
        updateTitle("");
    }

    private void initNavigationDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, vDrawerLayout, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }
        };
        vDrawerLayout.addDrawerListener(this);
    }

    protected abstract void initToolbarClickListener();

    protected abstract void processScreen(Screens screen);

    protected abstract Screens getMainScreen();

    protected void openNavigationDrawer() {
        vDrawerLayout.openDrawer(vNavigationView);
    }

    @Override
    protected void holdDrawer(boolean hold) {
        vDrawerLayout.setDrawerLockMode(hold ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        mDrawerToggle.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        mDrawerToggle.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        mDrawerToggle.onDrawerStateChanged(newState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBackAction) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
            }
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void showScreen(Screens screens) {
        if (screens == mPage && screens.fragmentClass != null) {
            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (screens.fragmentClass.isInstance(mFragment)) {
                return;
            }
        }
        this.mPage = screens;
        processScreen(mPage);
    }

    private void closeDrawer() {
        if (vDrawerLayout.isDrawerOpen(vNavigationView)) {
            vDrawerLayout.closeDrawers();
        }
    }

    // TODO: implement menu listeners
//    @OnClick({R.id.mainScreenItem, R.id.newOrderItem, R.id.previousOrdersItem, R.id.contactUsItem, R.id.termsAndConditionsItem})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.mainScreenItem:
//                mNavigator.startActivity(MainActivity.class, true);
//                mNavigator.finishActivity();
//                break;
//            case R.id.newOrderItem:
//                trackAction(TrackerResource.UI_ACTION_CATEGORY, TrackerResource.BUTTON_PRESS_ACTION, TrackerResource.NEW_ORDER_LABEL, null);
//                startMainFlow(Screens.SELECT_SOURCE);
//                break;
//            case R.id.previousOrdersItem:
//                trackAction(TrackerResource.UI_ACTION_CATEGORY, TrackerResource.BUTTON_PRESS_ACTION, TrackerResource.MY_ORDERS_LABEL, null);
//                startMainFlow(Screens.PREVIOUS_ORDER);
//                break;
//            case R.id.contactUsItem:
//                startMainFlow(Screens.CONTACT_US);
//                break;
//            case R.id.termsAndConditionsItem:
//                startMainFlow(Screens.TERMS_AND_CONDITIONS);
//                break;
//        }
//        vDrawerLayout.postDelayed(this::closeDrawer, 300);
//    }

//    private void startMainFlow(Screens screen) {
//        Bundle bundle = new Bundle();
//        if (screen != null) {
//            bundle.putSerializable(EXTRA_SCREEN, screen);
//        }
//        mNavigator.startActivity(MainFlowActivity.class, ActivityNavigator.putTransitionsToBundle(bundle, ActivityTransition.SLIDE_LEFT, ActivityTransition.SLIDE_RIGHT));
//    }

    @Override
    public void onBackPressed() {
        if (vDrawerLayout.isDrawerOpen(vNavigationView)) {
            vDrawerLayout.closeDrawers();
        } else if (isFragmentInStackHasBackAction()) {
            if (backActionInFragmentFinished()) {
                if (mPage == mDefaultPage) {
                    super.onBackPressed();
                } else {
                    backStackBackAction();
                }
            }
        } else {
            if (mPage == mDefaultPage) {
                super.onBackPressed();
            } else {
                backStackBackAction();
            }
        }
    }

    private void backStackBackAction() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (mDefaultPage.fragmentClass == null) {
                return;
            }
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getClass().isAssignableFrom(mDefaultPage.fragmentClass)) {
                mPage = mDefaultPage;
            }
        } else {
            mNavigator.clearBackStack(false);
            showScreen(mDefaultPage);
        }
    }

    private boolean isFragmentInStackHasBackAction() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        return (fragment != null && fragment instanceof HasBackAction);
    }

    private boolean backActionInFragmentFinished() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        return ((HasBackAction) fragment).onBackPressed();
    }

    protected boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
