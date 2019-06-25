package com.fase.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.fase.FaseApp;
import com.fase.R;
import com.fase.model.RequestContactDataHolder;
import com.fase.model.data.Contact;
import com.fase.model.enums.DateTimePickerType;
import com.fase.model.service.Response;
import com.fase.mvp.presenter.MainActivityPresenter;
import com.fase.mvp.view.MainActivityView;
import com.fase.ui.fragment.dialog.AlertDialogFragment;
import com.fase.ui.fragment.dialog.DatePickDialog;
import com.fase.ui.fragment.dialog.DateTimePickerResult;
import com.fase.ui.fragment.dialog.TimePickDialog;
import com.fase.ui.render.ViewRenderer;
import com.fase.ui.viewHolder.MainActivityVH;
import com.fase.util.RxUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import io.reactivex.disposables.Disposable;

public class MainActivity extends CommonActivity implements MainActivityView {

    public static final int GET_CONTACT = 7777;

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
    private RequestContactDataHolder mRequestContactDataHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FaseApp.getGoogleApiHelper().checkPlayServices(this)) {
            initRenderer();
            clearViewState();

            new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .doOnSubscribe(disposable -> mPresenter.getCompositeDisposable().add(disposable))
                    .subscribe(granted -> {
                        if (!granted) {
                            showError("Access rights not granted");
                            return;
                        }
                        mPresenter.initScreen(false);
                    });
        }
    }

    private void clearViewState() {
        mRequestContactDataHolder = null;
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

            @Override
            public void showDateTimePicker(DateTimePickerType type, Date date, DateTimePickerResult dateTimePickerResult) {
                if (type != null) {
                    switch (type) {
                        case DATE:
                            DatePickDialog datePickDialog = DatePickDialog.newInstance(date == null ? new Date() : date);
                            datePickDialog.setCallback(dateTimePickerResult);
                            datePickDialog.show(getSupportFragmentManager());
                            break;
                        case TIME:
                            TimePickDialog timePickDialog = TimePickDialog.newInstance(date == null ? new Date() : date);
                            timePickDialog.setCallback(dateTimePickerResult);
                            timePickDialog.show(getSupportFragmentManager());
                            break;
                        case DATETIME:
                            DatePickDialog dateDialog = DatePickDialog.newInstance(date == null ? new Date() : date);
                            dateDialog.setCallback(dateResult -> {
                                TimePickDialog timeDialog = TimePickDialog.newInstance(date == null ? new Date() : date);
                                timeDialog.setCallback(timeResult -> {
                                    Calendar dateCalendar = GregorianCalendar.getInstance();
                                    dateCalendar.setTime(dateResult);
                                    Calendar timeCalendar = GregorianCalendar.getInstance();
                                    timeCalendar.setTime(timeResult);
                                    Calendar calendar = new GregorianCalendar(dateCalendar.get(Calendar.YEAR),
                                            dateCalendar.get(Calendar.MONTH),
                                            dateCalendar.get(Calendar.DAY_OF_MONTH),
                                            timeCalendar.get(Calendar.HOUR_OF_DAY),
                                            timeCalendar.get(Calendar.MINUTE));
                                    dateTimePickerResult.onDateTimeSet(calendar.getTime());
                                });
                                timeDialog.show(getSupportFragmentManager());
                            });
                            dateDialog.show(getSupportFragmentManager());
                            break;
                    }
                }
            }

            @Override
            public void backPressed() {
                MainActivity.super.onBackPressed();
            }

            @Override
            public void requestContact(RequestContactDataHolder holder) {
                mRequestContactDataHolder = holder;
                new RxPermissions(MainActivity.this).request(Manifest.permission.READ_CONTACTS)
                        .doOnSubscribe(disposable -> mPresenter.getCompositeDisposable().add(disposable))
                        .subscribe(granted -> {
                            if (!granted) {
                                showError("Access rights not granted");
                                return;
                            }
                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            intent.putExtra("test", "test");
                            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(intent, GET_CONTACT);
                        });
            }

            @Override
            public void hideKeyboard() {
                MainActivity.this.hideKeyboard();
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
        RxUtil.safeUnSubscribe(mElementUpdateDisposable);
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
        mRenderer.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();
                if (contactUri != null) {
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                    Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int columnName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        String number = cursor.getString(columnNumber);
                        String name = cursor.getString(columnName);
                        cursor.close();

                        Contact contact = new Contact();
                        contact.setDisplayName(name);
                        contact.setPhoneNumber(number);

                        mPresenter.pickContact(mRequestContactDataHolder, contact);
                        if (mRequestContactDataHolder.getContactTextView() != null) {
                            mRequestContactDataHolder.getContactTextView().setText(name);
                        }
                    }
                }
            }
        }
    }
}
