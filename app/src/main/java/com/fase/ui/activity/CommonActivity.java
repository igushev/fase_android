package com.fase.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.fase.R;
import com.fase.base.BaseActivity;

import butterknife.BindView;

public abstract class CommonActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    @BindView(R.id.drawerLayout)
    DrawerLayout vDrawerLayout;
    @BindView(R.id.navigationView)
    NavigationView vNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void closeDrawer() {
        if (vDrawerLayout.isDrawerOpen(vNavigationView)) {
            vDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onBackPressed() {
        if (vDrawerLayout.isDrawerOpen(vNavigationView)) {
            vDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
