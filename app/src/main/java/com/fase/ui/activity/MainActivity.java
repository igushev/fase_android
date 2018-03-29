package com.fase.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.fase.FaseApp;
import com.fase.R;
import com.fase.model.service.Response;
import com.fase.mvp.presenter.MainActivityPresenter;
import com.fase.mvp.view.MainActivityView;
import com.fase.ui.fragment.dialog.AlertDialogFragment;
import com.fase.ui.render.ViewRenderer;
import com.fase.ui.viewHolder.MainActivityVH;
import com.fase.util.RxUtil;
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends CommonActivity implements MainActivityView {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout vSwipeRefreshLayout;
    @BindView(R.id.container)
    FrameLayout vContentContainer;
    @BindView(R.id.navigationMenuLayout)
    LinearLayout vNavigationMenuLayout;
    @BindView(R.id.floatingButton)
    FloatingActionButton vFloatingActionButton;
    @BindView(R.id.buttonFirst)
    FrameLayout vButtonFirst;
    @BindView(R.id.buttonFirstImage)
    ImageView vButtonFirstImage;
    @BindView(R.id.buttonFirstText)
    TextView vButtonFirstText;
    @BindView(R.id.toolbarShadow)
    View vToolbarShadow;
    @BindView(R.id.bottom_sheet)
    LinearLayout vBottomSheet;

    @InjectPresenter
    MainActivityPresenter mPresenter;

    private ViewRenderer mRenderer;
    private Disposable mElementUpdateDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: check play services
        initRenderer();
        clearViewState();

        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnSubscribe(disposable -> mPresenter.getCompositeDisposable().add(disposable))
                .subscribe(granted -> {
                    if (!granted) {
                        showError("Access rights not granted");
                        return;
                    }
                    mPresenter.initScreen();
                });
    }

    private void clearViewState() {
        mRenderer.clearViewState();
        hideNavigationButton();
        mPresenter.clearValueUpdates();
    }

    private void initRenderer() {
        MainActivityVH viewHolder = new MainActivityVH(vSwipeRefreshLayout,
                vContentContainer,
                vNavigationMenuLayout,
                vFloatingActionButton,
                vButtonFirst,
                vButtonFirstImage,
                vButtonFirstText,
                vToolbarShadow,
                vBottomSheet);

        mRenderer = new ViewRenderer(this, viewHolder, new ViewRenderer.RendererCallback() {
            @Override
            public void showAlert(String message, String firstButtonName, List<String> firstButtonIdList, String firstButtonMethod, Boolean firstButtonRequestLocale,
                                  String secondButtonName, List<String> secondButtonIdList, String secondButtonMethod, Boolean secondButtonRequestLocale) {
                if (!TextUtils.isEmpty(firstButtonName) && !TextUtils.isEmpty(secondButtonName) && !TextUtils.isEmpty(firstButtonMethod) && !TextUtils.isEmpty(secondButtonMethod)) {
                    AlertDialogFragment.newInstance(message, firstButtonName, secondButtonName,
                            (dialogInterface, i) -> mPresenter.elementCallback(firstButtonIdList, firstButtonMethod, firstButtonRequestLocale),
                            (dialogInterface, i) -> mPresenter.elementCallback(secondButtonIdList, secondButtonMethod, secondButtonRequestLocale))
                            .show(getSupportFragmentManager());
                } else if (!TextUtils.isEmpty(firstButtonName) && !TextUtils.isEmpty(firstButtonMethod)) {
                    AlertDialogFragment.newInstance(message, firstButtonName, (dialogInterface, i) ->
                            mPresenter.elementCallback(firstButtonIdList, firstButtonMethod, firstButtonRequestLocale)).show(getSupportFragmentManager());
                } else {
                    AlertDialogFragment.newInstance(message).show(getSupportFragmentManager());
                }
            }

            @Override
            public void onFunctionCall(List<String> idList, String method, Boolean requestLocal) {
                hideKeyboard();
                mPresenter.elementCallback(idList, method, requestLocal);
            }

            @Override
            public void enableNavigationMenu() {
                showNavigationBurger();
            }

            @Override
            public void showError(String message) {
                MainActivity.this.showError(message);
            }

            @Override
            public void setScreenTitle(String title) {
                updateTitle(title);
            }
        });
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
    protected void holdDrawer(boolean hold) {
        if (vDrawerLayout != null) {
            vDrawerLayout.setDrawerLockMode(hold ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRenderer != null) {
            mRenderer.destroy();
            mRenderer = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mElementUpdateDisposable = mPresenter.intiElementUpdates();
        super.onResume();
    }

    @Override
    protected void onPause() {
        RxUtil.safeUnSubscribe(mElementUpdateDisposable);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        FaseApp.getGoogleApiHelper().connect();
    }

    @Override
    public void onStop() {
        FaseApp.getGoogleApiHelper().disconnect();
        super.onStop();
    }

    @Override
    public void render(Response response) {
        if (response.getScreen() != null) {
            clearViewState();
        }
        mRenderer.renderScreenState(response);
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
        vSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (mRenderer.hasPrevStepMenu() || mRenderer.hasPrevButton()) {
            mRenderer.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
