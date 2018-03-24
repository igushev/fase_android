package com.fase.ui.render;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.fase.R;
import com.fase.core.manager.DataManager;
import com.fase.model.Entry;
import com.fase.model.element.Alert;
import com.fase.model.element.Button;
import com.fase.model.element.ContactPicker;
import com.fase.model.element.DateTimePicker;
import com.fase.model.element.Element;
import com.fase.model.element.Frame;
import com.fase.model.element.Image;
import com.fase.model.element.Label;
import com.fase.model.element.Menu;
import com.fase.model.element.MenuItem;
import com.fase.model.element.Navigation;
import com.fase.model.element.PlacePicker;
import com.fase.model.element.Screen;
import com.fase.model.element.Select;
import com.fase.model.element.Separator;
import com.fase.model.element.Slider;
import com.fase.model.element.Switch;
import com.fase.model.element.Text;
import com.fase.model.element.Tuple;
import com.fase.model.element.Web;
import com.fase.model.enums.fase.DateTimePickerType;
import com.fase.model.enums.fase.Orientation;
import com.fase.model.enums.fase.Size;
import com.fase.model.service.ElementsUpdate;
import com.fase.ui.adapter.SpinnerAdapter;
import com.fase.ui.adapter.SpinnerAdapterWithoutEmptyItem;
import com.fase.ui.view.ClickableSpinner;
import com.fase.ui.view.CustomDateTimePicker;
import com.fase.ui.viewHolder.MainActivityVH;
import com.fase.util.DateTimeUtil;
import com.fase.util.NetworkUtil;
import com.fase.util.RxUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxSeekBar;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class ViewRenderer {

    private static final String MAIN_BUTTON_ID = "main_button";
    private static final String NEXT_STEP_BUTTON_ID = "next_step_button";
    private static final String PREV_STEP_BUTTON_ID = "prev_step_button";
    private static final String CONTEXT_MENU = "context_menu";

    public interface RendererCallback {

        void showAlert(String message, String buttonName, List<String> buttonIdList, String buttonMethod, Boolean requestLocale);

        void onFunctionCall(List<String> idList, String method, Boolean requestLocale);

        void enableNavigationMenu();

        void showError(String message);

        void setScreenTitle(String title);

        void onBackPressed();
    }

    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private RendererCallback mRendererCallback;
    private CompositeDisposable mCompositeDisposable;
    private DataManager mDataManager;
    private MainActivityVH mViewHolder;
    private boolean hasPrevStepButton = false;
    private boolean contextViewRendered = false;
    private BottomSheetBehavior mSheetBehavior;

    public ViewRenderer(Context context, DisplayMetrics displayMetrics, MainActivityVH viewHolder, RendererCallback callback) {
        if (context == null) {
            throw new IllegalArgumentException("Context cant be null");
        }
        this.mContext = context;
        if (displayMetrics == null) {
            throw new IllegalArgumentException("DisplayMetrics cant be null");
        }
        this.mDisplayMetrics = displayMetrics;
        if (viewHolder == null) {
            throw new IllegalArgumentException("MainActivityVH must be specified");
        }
        this.mViewHolder = viewHolder;
        if (callback == null) {
            throw new IllegalArgumentException("RendererCallback must be specified");
        }
        this.mRendererCallback = callback;
        mCompositeDisposable = new CompositeDisposable();
        mDataManager = DataManager.getInstance();
        mSheetBehavior = BottomSheetBehavior.from(mViewHolder.vBottomSheet);

    }

    public void clearViewState() {
        mViewHolder.vContentContainer.removeAllViews();
        mViewHolder.vNavigationMenuLayout.removeAllViews();
        mViewHolder.vFloatingActionButton.hide();
        mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_TAG, null);
        mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_ID_TAG, null);
        mViewHolder.vBottomSheet.removeAllViews();
        mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        initActionButton(0, null, null);
        RxUtil.safeUnSubscribe(mCompositeDisposable);
    }

    public void destroy() {
        clearViewState();
        mContext = null;
        mDisplayMetrics = null;
        mRendererCallback = null;
        mDataManager = null;
        mCompositeDisposable = null;
    }

    private View renderView(Tuple tuple, Orientation orientation, ArrayList<String> idList) {
        boolean isOrientationVertical = orientation == null || orientation == Orientation.VERTICAL;

        // TODO: displayed

        Element element = tuple.getElement();
        if (element instanceof Frame) {
            Frame frameElement = (Frame) element;
            LinearLayout frameView = new LinearLayout(mContext);
//
//            Random rnd = new Random(); // TODO: for test
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            frameView.setBackgroundColor(color);

            frameView.setTag(R.id.ELEMENT_TAG, element);
            frameView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            for (Tuple itemTuple : frameElement.getIdElementList()) {
                renderViewElement(frameView, itemTuple, frameElement.getOrientation(), getIdListCopyForItem(tuple, idList));
            }

            if (frameElement.getOrientation() != null && frameElement.getOrientation() == Orientation.HORIZONTAL) {
                frameView.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                frameView.setOrientation(LinearLayout.VERTICAL); // if frame.orientation == null - VERTICAL by default
            }
            frameView.setLayoutParams(getParams(frameElement.getSize(), isOrientationVertical));

            if (frameElement.getOnClick() != null) {
                RxView.clicks(frameView)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), frameElement.getOnClick().getMethod(), frameElement.getRequestLocale()));
            }
            if (frameElement.getBorder() != null && frameElement.getBorder()) {
                frameView.setBackgroundResource(R.drawable.bg_shape_gray_border);
            }

            return frameView;
        } else if (element instanceof Label) {
            Label labelElement = (Label) element;
            TextView labelView = new TextView(mContext);
            labelView.setTag(R.id.ELEMENT_TAG, element);
            labelView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            labelView.setText(labelElement.getText());
            labelView.setLayoutParams(getParams(labelElement.getSize(), isOrientationVertical));

//            if (labelElement.getFont() != null) { TODO set proper size
//                float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, labelElement.getFont() * 10, mDisplayMetrics);
//                labelView.setTextSize(textSize);
//            }
            if (labelElement.getAlight() != null) {
                switch (labelElement.getAlight()) {
                    case CENTER:
                        labelView.setGravity(Gravity.CENTER);
                        break;
                    case LEFT:
                        labelView.setGravity(Gravity.START);
                        break;
                    case RIGHT:
                        labelView.setGravity(Gravity.END);
                        break;
                }
            }

            if (labelElement.getOnClick() != null) {
                RxView.clicks(labelView)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), labelElement.getOnClick().getMethod(), labelElement.getRequestLocale()));
            }

            return labelView;
        } else if (element instanceof Text) {
            Text textElement = (Text) element;
            EditText textView = new EditText(mContext);
            textView.setTag(R.id.ELEMENT_TAG, element);
            textView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            textView.setHint(textElement.getHint());
            textView.setText(textElement.getText());
            textView.setLayoutParams(getParams(textElement.getSize(), isOrientationVertical));

            if (textElement.getType() != null) {
                switch (textElement.getType()) {
                    case TEXT:
                        textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        break;
                    case EMAIL:
                        textView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case PHONE:
                        textView.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case DIGITS:
                        textView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                }
            } else {
                textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }

            if (textElement.getMultiline() != null && textElement.getMultiline()) {
                textView.setSingleLine(false);
                textView.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            }

            RxTextView.afterTextChangeEvents(textView)
                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                    .skip(1)
                    .flatMap(textViewAfterTextChangeEvent -> mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList),
                            textViewAfterTextChangeEvent != null && textViewAfterTextChangeEvent.editable() != null ? textViewAfterTextChangeEvent.editable().toString() : "")
                            .toObservable())
                    .compose(RxUtil.applyIoAndMainSchedulers())
                    .onErrorReturn(throwable -> false)
                    .subscribe();

            return textView;
        } else if (element instanceof Switch) {
            Switch switchElement = (Switch) element;
            android.widget.Switch switchView = new android.widget.Switch(mContext);
            switchView.setTag(R.id.ELEMENT_TAG, element);
            switchView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            switchView.setText(switchElement.getText());
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            if (switchElement.getAlight() != null) {
                switch (switchElement.getAlight()) {
                    case CENTER:
                        params.gravity = Gravity.CENTER;
                        break;
                    case LEFT:
                        params.gravity = Gravity.START;
                        break;
                    case RIGHT:
                        params.gravity = Gravity.END;
                        break;
                }
            }
            switchView.setLayoutParams(params);

            if (switchElement.getValue() != null) {
                switchView.setChecked(switchElement.getValue());
            }

            RxCompoundButton.checkedChanges(switchView)
                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                    .flatMap(checkChanges -> mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), String.valueOf(checkChanges))
                            .toObservable())
                    .compose(RxUtil.applyIoAndMainSchedulers())
                    .onErrorReturn(throwable -> false)
                    .subscribe();

            return switchView;
        } else if (element instanceof Select) {
            Select selectElement = (Select) element;
            ClickableSpinner selectView = new ClickableSpinner(mContext);
            selectView.setTag(R.id.ELEMENT_TAG, element);
            selectView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            if (selectElement.getAlight() != null) {
                switch (selectElement.getAlight()) {
                    case CENTER:
                        params.gravity = Gravity.CENTER;
                        break;
                    case LEFT:
                        params.gravity = Gravity.START;
                        break;
                    case RIGHT:
                        params.gravity = Gravity.END;
                        break;
                }
            }
            selectView.setLayoutParams(params);

            ArrayList<Entry<String, String>> itemsList = new ArrayList<>();
            if (selectElement.getItems() != null) {
                for (String item : selectElement.getItems()) {
                    itemsList.add(new Entry<>(item, item));
                }
            }

            if (!TextUtils.isEmpty(selectElement.getHint())) {
                SpinnerAdapter<String, String> entrySpinnerAdapter = new SpinnerAdapter<>(selectView, selectElement.getHint());
                entrySpinnerAdapter.setList(itemsList);
                if (!TextUtils.isEmpty(selectElement.getValue())) {
                    entrySpinnerAdapter.setSelection(selectElement.getValue());
                }
                RxAdapterView.itemSelections(selectView)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .flatMap(value -> mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), entrySpinnerAdapter.getSelectedId())
                                .toObservable())
                        .compose(RxUtil.applyIoAndMainSchedulers())
                        .onErrorReturn(throwable -> false)
                        .subscribe();
            } else {
                SpinnerAdapterWithoutEmptyItem<String, String> entrySpinnerAdapter = new SpinnerAdapterWithoutEmptyItem<>(selectView);
                entrySpinnerAdapter.setList(itemsList);
                if (!TextUtils.isEmpty(selectElement.getValue())) {
                    entrySpinnerAdapter.setSelection(selectElement.getValue());
                }

                RxAdapterView.itemSelections(selectView)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .flatMap(value -> mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), entrySpinnerAdapter.getSelectedId())
                                .toObservable())
                        .compose(RxUtil.applyIoAndMainSchedulers())
                        .onErrorReturn(throwable -> false)
                        .subscribe();
            }

            return selectView;
        } else if (element instanceof Slider) {
            Slider sliderElement = (Slider) element;
            SeekBar sliderView = new SeekBar(mContext);
            sliderView.setTag(R.id.ELEMENT_TAG, element);
            sliderView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            sliderView.setLayoutParams(getParams(null, isOrientationVertical));

            final int minValue = sliderElement.getMinValue() != null ? (int) (sliderElement.getMinValue() * 100) : 0;
            if (sliderElement.getMaxValue() != null) {
                sliderView.setMax((int) (sliderElement.getMinValue() * 100) + minValue);
            }
            if (sliderElement.getStep() != null) {
                sliderView.incrementProgressBy((int) (sliderElement.getStep() * 100));
            }
            if (sliderElement.getValue() != null) {
                sliderView.setProgress((int) (sliderElement.getValue() * 100));
            }

            RxSeekBar.changes(sliderView)
                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                    .flatMap(progress -> mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), String.valueOf((progress - minValue) / 100f))
                            .toObservable())
                    .compose(RxUtil.applyIoAndMainSchedulers())
                    .onErrorReturn(throwable -> false)
                    .subscribe();

            return sliderView;
        } else if (element instanceof Image) {
            Image imageElement = (Image) element;
            ImageView imageView = new ImageView(mContext);
            imageView.setTag(R.id.ELEMENT_TAG, element);
            imageView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            imageView.setLayoutParams(getParams(null, isOrientationVertical));
            if (!TextUtils.isEmpty(imageElement.getFilename())) {
                mDataManager.getResourcePath(imageElement.getFilename())
                        .doOnSubscribe(this::addDisposable)
                        .subscribe(filePath -> {
                                    if (!TextUtils.isEmpty(filePath)) {
                                        imageView.post(() -> Glide.with(imageView)
                                                .load(filePath)
                                                .into(imageView));
                                    }
                                },
                                throwable -> Timber.e(throwable, "Error getting image file path"));
            } else if (!TextUtils.isEmpty(imageElement.getUrl())) {
                imageView.post(() -> Glide.with(imageView)
                        .load(imageElement.getUrl())
                        .into(imageView));
            }

            return imageView;
        } else if (element instanceof Button) {
            Button buttonElement = (Button) element;
            switch (tuple.getElementId()) {
                case MAIN_BUTTON_ID: // configure FAB button
                    if (buttonElement.getIdElementList() != null && !buttonElement.getIdElementList().isEmpty()) {
                        Image image = (Image) buttonElement.getIdElementList().get(0).getElement();
                        if (image != null) {
                            if (!TextUtils.isEmpty(image.getFilename())) {
                                mDataManager.getResourcePath(image.getFilename())
                                        .doOnSubscribe(this::addDisposable)
                                        .subscribe(filePath -> {
                                                    if (!TextUtils.isEmpty(filePath)) {
                                                        mViewHolder.vFloatingActionButton.setImageDrawable(Drawable.createFromPath(filePath));
                                                    }
                                                },
                                                throwable -> Timber.e(throwable, "Error getting image file path"));

                                if (((Button) element).getOnClick() != null) {
                                    RxView.clicks(mViewHolder.vFloatingActionButton)
                                            .doOnSubscribe(this::addDisposable)
                                            .subscribe(o -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale()));
                                }
                                mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_TAG, element);
                                mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                                mViewHolder.vFloatingActionButton.show();
                            } else {
                                throw new IllegalArgumentException("Image filename empty");
                            }
                        }
                    }
                    return null;
                case NEXT_STEP_BUTTON_ID: // configure button in toolbar
                    if (buttonElement.getIdElementList() != null && !buttonElement.getIdElementList().isEmpty()) {
                        for (Tuple itemTuple : buttonElement.getIdElementList()) {
                            if (itemTuple.getElement() instanceof Image) {
                                Image imageElement = (Image) tuple.getElement();
                                if (imageElement != null) {
                                    mDataManager.getResourcePath(imageElement.getFilename())
                                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                            .subscribe(filePath -> initActionButton(0, Drawable.createFromPath(filePath), buttonElement.getText()),
                                                    throwable -> Timber.e(throwable, "Error getting image for FAB"));
                                } else {
                                    throw new IllegalArgumentException("Image filename empty");
                                }
                                if (buttonElement.getOnClick() != null) {
                                    RxView.clicks(mViewHolder.vButtonFirst)
                                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                            .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale()));
                                }
                            } else if (itemTuple.getElement() instanceof Menu) {
                                renderViewElement(null, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList));

                                if (buttonElement.getOnClick() != null) {
                                    RxView.clicks(mViewHolder.vButtonFirst)
                                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                            .subscribe(click -> {
                                                // TODO: toggle menu
                                            });
                                }
                            }
                        }
                    } else {
                        initActionButton(0, null, buttonElement.getText());
                    }

                    if (buttonElement.getOnClick() != null) {
                        RxView.clicks(mViewHolder.vButtonFirst)
                                .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale()));
                    }
                    return null;
                default:
                    if (buttonElement.getIdElementList() != null && !buttonElement.getIdElementList().isEmpty()) {
                        if (buttonElement.getIdElementList().get(0).getElement() instanceof Menu) {
                            renderViewElement(null, buttonElement.getIdElementList().get(0), Orientation.VERTICAL, getIdListCopyForItem(tuple, idList));
                        }
                        hasPrevStepButton = true;
                        return null;
                    } else {
                        hasPrevStepButton = true;
                    }

                    android.widget.Button buttonView = new android.widget.Button(mContext);
                    buttonView.setTag(R.id.ELEMENT_TAG, element);
                    buttonView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                    buttonView.setLayoutParams(getParams(null, isOrientationVertical));
                    buttonView.setText(buttonElement.getText());

                    if (buttonElement.getIdElementList() != null && !buttonElement.getIdElementList().isEmpty()) {
                        for (Tuple itemTuple : buttonElement.getIdElementList()) {
                            if (itemTuple.getElement() instanceof Image) {
                                Image imageElement = (Image) tuple.getElement();
                                if (imageElement != null) {
                                    mDataManager.getResourcePath(imageElement.getFilename())
                                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                            .subscribe(filePath -> {
                                                        buttonView.setCompoundDrawablePadding(16);
                                                        buttonView.setCompoundDrawablesWithIntrinsicBounds(Drawable.createFromPath(filePath), null, null, null);
                                                    },
                                                    throwable -> Timber.e(throwable, "Error getting image for FAB"));
                                } else {
                                    throw new IllegalArgumentException("Image filename empty");
                                }
                            } else if (itemTuple.getElement() instanceof Menu) {
                                renderViewElement(null, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList));
                            }
                        }
                    }

                    if (buttonElement.getOnClick() != null) {
                        if (!tuple.getElementId().equals(PREV_STEP_BUTTON_ID)) {
                            RxView.clicks(buttonView)
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale()));
                        } else {
                            RxView.clicks(buttonView)
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .subscribe(click -> mRendererCallback.onBackPressed());
                        }
                    }
                    return buttonView;
            }
        } else if (element instanceof DateTimePicker) {
            DateTimePicker dateTimePickerElement = (DateTimePicker) element;
            if (dateTimePickerElement.getType() != null && dateTimePickerElement.getType() != DateTimePickerType.DATETIME) {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(dateTimePickerElement.getDatetime() != null ? dateTimePickerElement.getDatetime() : new Date());
                switch (dateTimePickerElement.getType()) {
                    case DATE:
                        ContextThemeWrapper wrappedContext = new ContextThemeWrapper(mContext, R.style.DatePicker);
                        DatePicker datePickerView = new DatePicker(wrappedContext);
                        datePickerView.setTag(R.id.ELEMENT_TAG, element);
                        datePickerView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                        datePickerView.setLayoutParams(getParams(dateTimePickerElement.getSize(), isOrientationVertical));
                        datePickerView.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, year, month, day) -> {
                            Calendar date = GregorianCalendar.getInstance();
                            date.set(Calendar.YEAR, year);
                            date.set(Calendar.MONTH, month);
                            date.set(Calendar.DAY_OF_MONTH, day);

                            mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), DateTimeUtil.formatDate(date.getTime(), DateTimeUtil.APP_DATE_FORMAT))
                                    .toObservable()
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .compose(RxUtil.applyIoAndMainSchedulers())
                                    .onErrorReturn(throwable -> false)
                                    .subscribe();
                        });
                        return datePickerView;
                    case TIME:
                        TimePicker timePickerView = new TimePicker(mContext);
                        timePickerView.setTag(R.id.ELEMENT_TAG, element);
                        timePickerView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                        timePickerView.setLayoutParams(getParams(dateTimePickerElement.getSize(), isOrientationVertical));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePickerView.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                            timePickerView.setMinute(calendar.get(Calendar.MINUTE));
                        } else {
                            timePickerView.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                            timePickerView.setCurrentMinute(calendar.get(Calendar.MINUTE));
                        }
                        timePickerView.setOnTimeChangedListener((timePicker, hour, minute) -> {
                            Calendar date = GregorianCalendar.getInstance();
                            date.set(Calendar.HOUR_OF_DAY, hour);
                            date.set(Calendar.MINUTE, minute);

                            mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), DateTimeUtil.formatDate(date.getTime(), DateTimeUtil.APP_TIME_FORMAT))
                                    .toObservable()
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .compose(RxUtil.applyIoAndMainSchedulers())
                                    .onErrorReturn(throwable -> false)
                                    .subscribe();
                        });
                        return timePickerView;
                }
            } else {
                // DATETIME type by default
                CustomDateTimePicker customDateTimePickerView = new CustomDateTimePicker(mContext,
                        dateTimePickerElement.getDatetime() != null ? dateTimePickerElement.getDatetime() : new Date(),
                        date -> {
                            mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), DateTimeUtil.formatDate(date, DateTimeUtil.APP_DATE_TIME_FORMAT))
                                    .toObservable()
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .compose(RxUtil.applyIoAndMainSchedulers())
                                    .onErrorReturn(throwable -> false)
                                    .subscribe();
                        });
                customDateTimePickerView.setTag(R.id.ELEMENT_TAG, element);
                customDateTimePickerView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                customDateTimePickerView.setLayoutParams(getParams(dateTimePickerElement.getSize(), isOrientationVertical));
                return customDateTimePickerView;
            }
            return null;
        } else if (element instanceof PlacePicker) {
            // TODO: implement places picker
            /*
                private Place place;
                private Size size;
                private PlacePickerType type;
                private String hint;
             */
            return null;
        } else if (element instanceof Web) {
            Web webElement = (Web) element;
            WebView webView = new WebView(mContext);
            webView.setTag(R.id.ELEMENT_TAG, element);
            webView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            webView.setLayoutParams(getParams(webElement.getSize(), isOrientationVertical));

            WebSettings webSettings = webView.getSettings();
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setDomStorageEnabled(true);
            webSettings.setJavaScriptEnabled(true);

            // disable scroll on touch
            if (webElement.getScrollable() != null && !webElement.getScrollable()) {
                webView.setOnTouchListener((v, event) -> (event.getAction() == MotionEvent.ACTION_MOVE));
                webView.setVerticalScrollBarEnabled(false);
                webView.setHorizontalScrollBarEnabled(false);
            }

            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    // TODO: progress
//                    if (progress < 100 && vProgressBar.getVisibility() == ProgressBar.GONE) {
//                        vProgressBar.setVisibility(ProgressBar.VISIBLE);
//                    }
//                    vProgressBar.setProgress(progress);
//                    if (progress == 100) {
//                        vProgressBar.setVisibility(ProgressBar.GONE);
//                    }
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Timber.e("WebView error: %s", error.toString());
                    mRendererCallback.showError("WebView error: " + error.toString());
                }

                @SuppressWarnings("deprecation")
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    Timber.e("WebView error: " + errorCode + " : " + description);
                    mRendererCallback.showError("WebView error: " + errorCode + " : " + description);
                }

                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            if (!TextUtils.isEmpty(webElement.getUrl())) {
                webView.post(() -> NetworkUtil.checkNetworkWithRetryDialog(mContext, () -> webView.loadUrl(webElement.getUrl())));
            }
            return webView;
        } else if (element instanceof Navigation) {
            Navigation navigationElement = (Navigation) element;
            if (navigationElement.getIdElementList() != null) {
                for (Tuple itemTuple : navigationElement.getIdElementList()) {
                    // will change element type to MenuItem for proper rendering
                    Button button = (Button) itemTuple.getElement();
                    MenuItem menuItem = new MenuItem();
                    menuItem.setClassName(MenuItem.class.getSimpleName());
                    menuItem.setText(button.getText());
                    menuItem.setOnClick(button.getOnClick());
                    menuItem.setIdElementList(button.getIdElementList());
                    itemTuple.setElement(menuItem);

                    renderViewElement(mViewHolder.vNavigationMenuLayout, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList)); // Orientation must be vertical for navigation drawer
                }
            }
            mRendererCallback.enableNavigationMenu();
            return null;
        } else if (element instanceof ContactPicker) {
            /*
                private Contact contact;
                private Function onPick;
                private Size size;
                private String hint;
             */
            return null;
        } else if (element instanceof Menu) {
            Menu menuElement = (Menu) element;
            if (tuple.getElementId().equals(CONTEXT_MENU)) {
                if (menuElement.getIdElementList() != null) {
                    View shadowAbove = new View(mContext);
                    shadowAbove.setBackgroundResource(R.drawable.shadow_above);
                    mViewHolder.vBottomSheet.addView(shadowAbove);
                    shadowAbove.setLayoutParams(getParams(null, isOrientationVertical));
                    shadowAbove.getLayoutParams().height = 8;

                    for (Tuple itemTuple : menuElement.getIdElementList()) {
                        if (itemTuple.getElement() instanceof Button) {
                            // will change element type to MenuItem for proper rendering
                            Button button = (Button) itemTuple.getElement();
                            MenuItem menuItem = new MenuItem();
                            menuItem.setClassName(MenuItem.class.getSimpleName());
                            menuItem.setText(button.getText());
                            menuItem.setOnClick(button.getOnClick());
                            menuItem.setIdElementList(button.getIdElementList());
                            itemTuple.setElement(menuItem);
                        }
                        renderViewElement(mViewHolder.vBottomSheet, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList));
                    }
                }
                contextViewRendered = true;
            } else {
                for (Tuple itemTuple : menuElement.getIdElementList()) {
                    renderViewElement(null, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList));
                }
            }
            return null;
        } else if (element instanceof MenuItem) {
            MenuItem menuItemElement = (MenuItem) element;
            ContextThemeWrapper wrappedContext = new ContextThemeWrapper(mContext, R.style.NavigationTextView);
            TextView menuItemView = new TextView(wrappedContext);
            menuItemView.setText(menuItemElement.getText());
            if (menuItemElement.getOnClick() != null) {
                RxView.clicks(menuItemView)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), menuItemElement.getOnClick().getMethod(), menuItemElement.getRequestLocale()));
            }

            if (menuItemElement.getIdElementList() != null && !menuItemElement.getIdElementList().isEmpty()) {
                Image image = (Image) menuItemElement.getIdElementList().get(0).getElement();
                if (image != null) {
                    mDataManager.getResourcePath(image.getFilename())
                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                            .subscribe(filePath -> {
                                        menuItemView.setCompoundDrawablePadding(16);
                                        menuItemView.setCompoundDrawablesWithIntrinsicBounds(Drawable.createFromPath(filePath), null, null, null);
                                    },
                                    throwable -> Timber.e(throwable, "Error getting image for image"));
                } else {
                    throw new IllegalArgumentException("Image filename empty");
                }
            }
            return menuItemView;
        } else if (element instanceof Separator) {
            View separatorView = new View(mContext);
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            if (isOrientationVertical) {
                params.height = 1;
            } else {
                params.width = 1;
            }
            separatorView.setLayoutParams(params);
            return separatorView;
        } else if (element instanceof Alert) {
            Alert alertElement = (Alert) element;
            ArrayList<String> alertIdList = getIdListCopyForItem(tuple, idList);
            if (!TextUtils.isEmpty(alertElement.getText())) {
                if (alertElement.getIdElementList() != null && !alertElement.getIdElementList().isEmpty()) {
                    Tuple buttonTuple = alertElement.getIdElementList().get(0);
                    if (buttonTuple != null) {
                        Button buttonElement = (Button) buttonTuple.getElement();
                        if (buttonElement != null) {
                            mRendererCallback.showAlert(alertElement.getText(), buttonElement.getText(), getIdListCopyForItem(buttonTuple, alertIdList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale());
                        }
                    }
                } else {
                    mRendererCallback.showAlert(alertElement.getText(), null, null, null, null);
                }
            }
            return null;
        }
        return null;
    }

    @NonNull
    private ArrayList<String> getIdListCopyForItem(Tuple tuple, ArrayList<String> idList) {
        ArrayList<String> viewIds = new ArrayList<>(idList);
        viewIds.add(tuple.getElementId());
        return viewIds;
    }

    private LinearLayout.LayoutParams getParams(Size size, boolean orientationVertical) {
        if (size != null) {
            if (size == Size.MAX) {
                return new LinearLayout.LayoutParams(orientationVertical ? ViewGroup.LayoutParams.MATCH_PARENT : 0,
                        orientationVertical ? 0 : ViewGroup.LayoutParams.MATCH_PARENT, 1);
            } else {
                return new LinearLayout.LayoutParams(orientationVertical ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,
                        orientationVertical ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT);
            }
        } else {
            return new LinearLayout.LayoutParams(orientationVertical ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,
                    orientationVertical ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private View renderViewElement(ViewGroup viewGroup, Tuple tuple, Orientation orientation, ArrayList<String> idList) {
        if (tuple != null && tuple.getElement() != null) {
            View view = renderView(tuple, orientation, idList);
            if (view != null && viewGroup != null) {
                viewGroup.addView(view);
            }
            return view;
        }
        return null;
    }

    private View getRenderedLayout(List<Tuple> idElementList, boolean scrollable) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, scrollable ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (Tuple tuple : idElementList) {
            renderViewElement(linearLayout, tuple, Orientation.VERTICAL, new ArrayList<>());
        }
        return linearLayout;
    }

    public View renderScreenView(Screen screen) {
        hasPrevStepButton = false;
        if (screen.getOnRefresh() != null) {
            mViewHolder.vSwipeRefreshLayout.setEnabled(true);
            mViewHolder.vSwipeRefreshLayout.setOnRefreshListener(() -> {
                mRendererCallback.onFunctionCall(null, screen.getOnRefresh().getMethod(), screen.getRequestLocale());
            });
        } else {
            mViewHolder.vSwipeRefreshLayout.setOnRefreshListener(null);
            mViewHolder.vSwipeRefreshLayout.setEnabled(false);
        }
        if (!TextUtils.isEmpty(screen.getTitle())) {
            mRendererCallback.setScreenTitle(screen.getTitle());
        }

        // TODO: load more

        if (screen.getScrollable() != null && screen.getScrollable()) {
            ScrollView scrollView = new ScrollView(mContext);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            scrollView.setLayoutParams(params);
            scrollView.addView(getRenderedLayout(screen.getIdElementList(), true));
            return scrollView;
        } else {
            return getRenderedLayout(screen.getIdElementList(), false);
        }
    }

    private void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    private void initActionButton(@DrawableRes int buttonResId, Drawable drawable, String buttonText) {
        if (buttonResId != 0 || (drawable != null && TextUtils.isEmpty(buttonText))) {
            if (drawable != null) {
                mViewHolder.vButtonFirstImage.setImageDrawable(drawable);
            } else {
                mViewHolder.vButtonFirstImage.setImageResource(buttonResId);
            }
            mViewHolder.vButtonFirstImage.setVisibility(View.VISIBLE);
            mViewHolder.vButtonFirstText.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(buttonText)) {
            mViewHolder.vButtonFirstImage.setVisibility(View.GONE);
            mViewHolder.vButtonFirstText.setText(buttonText);
            mViewHolder.vButtonFirstText.setVisibility(View.VISIBLE);
            if (drawable != null) {
                mViewHolder.vButtonFirstText.setCompoundDrawablePadding(16);
                mViewHolder.vButtonFirstText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        } else {
            mViewHolder.vButtonFirstImage.setImageDrawable(null);
            mViewHolder.vButtonFirstImage.setImageResource(0);
            mViewHolder.vButtonFirstText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mViewHolder.vButtonFirstText.setText("");
        }
        mViewHolder.vButtonFirst.setVisibility((buttonResId != 0 || !TextUtils.isEmpty(buttonText) || drawable != null) ? View.VISIBLE : View.GONE);
        mViewHolder.vToolbarShadow.setVisibility((buttonResId != 0 || !TextUtils.isEmpty(buttonText) || drawable != null) ? View.VISIBLE : View.GONE);
    }

    public void updateElement(ElementsUpdate elementsUpdate) {
        if (elementsUpdate.getIdListList() != null && elementsUpdate.getValueList() != null) {
            List<List<String>> idListList = elementsUpdate.getIdListList();
            for (int i = 0; i < idListList.size(); i++) {
                List<String> strings = idListList.get(i);
                String value = elementsUpdate.getValueList().get(i);

                View resultView = mViewHolder.vContentContainer;
                for (String string : strings) {
                    resultView = getViewById(resultView, string);
                }
                if (resultView != null && resultView != mViewHolder.vContentContainer) {

                    // TODO: IF ELSE all items

                    if (resultView instanceof EditText) {
                        ((EditText) resultView).setText(value);
                    } else {
                        Timber.e("another instance %s", value);
                    }
                }
            }
        }
    }

    private View getViewById(View v, String id) {
        if (v == null || TextUtils.isEmpty(id)) {
            return null;
        }
        List<View> unvisited = new ArrayList<>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            String tag = (String) child.getTag(R.id.ELEMENT_ID_TAG);
            if (!TextUtils.isEmpty(tag) && tag.equals(id)) {
                return child;
            }
            if (!(child instanceof ViewGroup)) {
                continue;
            }
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View subChild = group.getChildAt(i);
                tag = (String) subChild.getTag(R.id.ELEMENT_ID_TAG);
                if (!TextUtils.isEmpty(tag) && tag.equals(id)) {
                    return subChild;
                }
                unvisited.add(subChild);
            }
        }

        return null;
    }

    public boolean hasPrevStepButton() {
        return hasPrevStepButton;
    }

    public void onBackPressed() {
        if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        // TODO: toggle context menu
    }
}
