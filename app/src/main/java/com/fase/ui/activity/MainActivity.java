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
import com.fase.R;
import com.fase.model.element.Screen;
import com.fase.model.enums.Screens;
import com.fase.model.service.ElementsUpdate;
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

        initRenderer();
        clearViewState();

        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(granted -> {
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

        mRenderer = new ViewRenderer(this, getScreenParams(), viewHolder, new ViewRenderer.RendererCallback() {
            @Override
            public void showAlert(String message, String buttonName, List<String> buttonIdList, String buttonMethod, Boolean requestLocale) {
                if (!TextUtils.isEmpty(buttonName) && !TextUtils.isEmpty(buttonMethod)) {
                    AlertDialogFragment.newInstance(message, buttonName, (dialogInterface, i) ->
                            mPresenter.elementCallback(buttonIdList, buttonMethod, requestLocale)).show(getSupportFragmentManager());
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

            @Override
            public void onBackPressed() {
                MainActivity.super.onBackPressed();
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
    public void render(Screen screen) {
        clearViewState();
//        FrameLayout.LayoutPУ arams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setLayoutParams(params);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        CustomDateTimePicker customDateTimePicker = new CustomDateTimePicker(this, new Date());
//        linearLayout.addView(customDateTimePicker);
//
//        vContentContainer.addView(linearLayout);
        vContentContainer.addView(mRenderer.renderScreenView(screen));
    }

    @Override
    public void updateElement(ElementsUpdate elementsUpdate) {
        mRenderer.updateElement(elementsUpdate);
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
        vSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (mRenderer.hasPrevStepButton()) {
            mRenderer.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
