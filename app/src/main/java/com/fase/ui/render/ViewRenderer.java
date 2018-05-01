package com.fase.ui.render;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fase.FaseApp;
import com.fase.R;
import com.fase.core.manager.DataManager;
import com.fase.model.Entry;
import com.fase.model.PrevStepButtonDataHolder;
import com.fase.model.RequestContactDataHolder;
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
import com.fase.model.enums.DateTimePickerType;
import com.fase.model.enums.Orientation;
import com.fase.model.enums.Size;
import com.fase.model.service.ElementsUpdate;
import com.fase.model.service.Response;
import com.fase.ui.adapter.CityAutocompleteAdapter;
import com.fase.ui.adapter.SpinnerAdapter;
import com.fase.ui.adapter.SpinnerAdapterWithoutEmptyItem;
import com.fase.ui.fragment.dialog.DateTimePickerResult;
import com.fase.ui.view.ClickableSpinner;
import com.fase.ui.viewHolder.MainActivityVH;
import com.fase.util.DateTimeUtil;
import com.fase.util.NetworkUtil;
import com.fase.util.PlaceUtils;
import com.fase.util.RxUtil;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class ViewRenderer {

    private static final String MAIN_BUTTON_ID = "main_button";
    private static final String NEXT_STEP_BUTTON_ID = "next_step_button";
    private static final String PREV_STEP_BUTTON_ID = "prev_step_button";
    private static final String CONTEXT_MENU = "context_menu";
    private Gson mGson = new Gson();

    public interface RendererCallback {

        void showAlert(String message, String firstButtonName, List<String> firstButtonIdList, String firstButtonMethod, Boolean firstButtonRequestLocale,
                       String secondButtonName, List<String> secondButtonIdList, String secondButtonMethod, Boolean secondButtonRequestLocale);

        void onFunctionCall(List<String> idList, String method, Boolean requestLocale);

        void enableNavigationMenu();

        void showError(String message);

        void setScreenTitle(String title);

        void showDateTimePicker(DateTimePickerType type, Date date, DateTimePickerResult dateTimePickerResult);

        void backPressed();

        void requestContact(RequestContactDataHolder holder);

        void hideKeyboard();
    }

    private Context mContext;
    private RendererCallback mRendererCallback;
    private CompositeDisposable mCompositeDisposable;
    private DataManager mDataManager;
    private MainActivityVH mViewHolder;
    private BottomSheetBehavior mSheetBehavior;
    private PrevStepButtonDataHolder mPrevStepButtonDataHolder;
    private PrevStepButtonDataHolder mPrevStepButtonMenuHolder;
    private boolean elementsUpdating = false;

    public ViewRenderer(Context context, MainActivityVH viewHolder, RendererCallback callback) {
        if (context == null) {
            throw new IllegalArgumentException("Context cant be null");
        }
        this.mContext = context;
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
        elementsUpdating = false;
        mPrevStepButtonDataHolder = null;
        mPrevStepButtonMenuHolder = null;
        mViewHolder.vContentContainer.removeAllViews();
        mViewHolder.vNavigationMenuLayout.removeAllViews();
        mViewHolder.vFloatingActionButton.hide();
        mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_TAG, null);
        mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_ID_TAG, null);
        mViewHolder.vBottomSheet.removeAllViews();
        mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mViewHolder.vSwipeRefreshLayout.setOnRefreshListener(null);
        mViewHolder.vSwipeRefreshLayout.setEnabled(false);

        initActionButton(0, null, null);
        RxUtil.safeUnSubscribe(mCompositeDisposable);
    }

    public void destroy() {
        clearViewState();
        mContext = null;
        mRendererCallback = null;
        mDataManager = null;
        mCompositeDisposable = null;
    }

    private View renderView(Tuple tuple, Orientation orientation, ArrayList<String> idList) {
        boolean isOrientationVertical = orientation == null || orientation == Orientation.VERTICAL;

        Element element = tuple.getElement();
        if (element instanceof Frame) {
            Frame frameElement = (Frame) element;
            LinearLayout frameView = new LinearLayout(mContext);
            frameView.setTag(R.id.ELEMENT_TAG, element);
            frameView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            frameView.setVisibility(frameElement.getDisplayed() != null && !frameElement.getDisplayed() ? View.GONE : View.VISIBLE);
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
            labelView.setTextSize(labelElement.getFont() == null ? 1 : labelElement.getFont() * 15); // 15 default size
            labelView.setVisibility(labelElement.getDisplayed() != null && !labelElement.getDisplayed() ? View.GONE : View.VISIBLE);

            if (labelElement.getAlign() != null) {
                switch (labelElement.getAlign()) {
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
            textView.setVisibility(textElement.getDisplayed() != null && !textElement.getDisplayed() ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(textElement.getText())) {
                textView.setSelection(textElement.getText().length());
            }
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

            if (textElement.getSize() == Size.MAX) {
                textView.setMinLines(3);
            }

            RxTextView.afterTextChangeEvents(textView)
                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                    .skip(1)
                    .filter(textViewAfterTextChangeEvent -> !elementsUpdating) // filter elementUpdates
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
            switchView.setVisibility(switchElement.getDisplayed() != null && !switchElement.getDisplayed() ? View.GONE : View.VISIBLE);
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            if (switchElement.getAlign() != null) {
                switch (switchElement.getAlign()) {
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

            switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!elementsUpdating) {
                    mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), String.valueOf(isChecked))
                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                            .compose(RxUtil.applySingleIoAndMainSchedulers())
                            .subscribe();
                }
            });

            return switchView;
        } else if (element instanceof Select) {
            Select selectElement = (Select) element;
            ClickableSpinner selectView = new ClickableSpinner(mContext);
            selectView.setTag(R.id.ELEMENT_TAG, element);
            selectView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            selectView.setVisibility(selectElement.getDisplayed() != null && !selectElement.getDisplayed() ? View.GONE : View.VISIBLE);
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            if (selectElement.getAlign() != null) {
                switch (selectElement.getAlign()) {
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

            final ArrayList<Entry<String, String>> itemsList = new ArrayList<>();
            if (selectElement.getItems() != null) {
                for (String item : selectElement.getItems()) {
                    itemsList.add(new Entry<>(item, item));
                }
            }

            if (!TextUtils.isEmpty(selectElement.getHint())) {
                SpinnerAdapter<String, String> entrySpinnerAdapter = new SpinnerAdapter<>(selectView, selectElement.getHint());
                entrySpinnerAdapter.setList(itemsList);
                if (!TextUtils.isEmpty(selectElement.getValue())) {
                    entrySpinnerAdapter.selectValue(selectElement.getValue());
                }
            } else {
                SpinnerAdapterWithoutEmptyItem<String, String> entrySpinnerAdapter = new SpinnerAdapterWithoutEmptyItem<>(selectView);
                entrySpinnerAdapter.setList(itemsList);
                if (!TextUtils.isEmpty(selectElement.getValue())) {
                    entrySpinnerAdapter.selectValue(selectElement.getValue());
                }
            }

            selectView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!elementsUpdating) {
                        if (selectView.getAdapter().getCount() != itemsList.size()) {
                            if (position != 0) {
                                return;
                            }
                            position = position - 1;  // -1 because have empty element
                        }
                        mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), itemsList.get(position).name)
                                .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                .compose(RxUtil.applySingleIoAndMainSchedulers())
                                .subscribe();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            return selectView;
        } else if (element instanceof Slider) {
            Slider sliderElement = (Slider) element;
            SeekBar sliderView = new SeekBar(mContext);
            sliderView.setTag(R.id.ELEMENT_TAG, element);
            sliderView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            sliderView.setVisibility(sliderElement.getDisplayed() != null && !sliderElement.getDisplayed() ? View.GONE : View.VISIBLE);
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            sliderView.setLayoutParams(params);

            final int minValue = sliderElement.getMinValue() != null ? (int) (sliderElement.getMinValue() * 100) : 0;
            if (sliderElement.getMaxValue() != null) {
                sliderView.setMax((int) (sliderElement.getMaxValue() * 100) + minValue);
            }
            if (sliderElement.getStep() != null) {
                sliderView.incrementProgressBy((int) (sliderElement.getStep() * 100));
            }
            if (sliderElement.getValue() != null) {
                sliderView.setProgress((int) (sliderElement.getValue() * 100) + minValue);
            }

            sliderView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!elementsUpdating) {
                        mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), String.valueOf((progress - minValue) / 100f))
                                .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                .compose(RxUtil.applySingleIoAndMainSchedulers())
                                .subscribe();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            return sliderView;
        } else if (element instanceof Image) {
            Image imageElement = (Image) element;
            ImageView imageView = new ImageView(mContext);
            imageView.setTag(R.id.ELEMENT_TAG, element);
            imageView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            imageView.setVisibility(imageElement.getDisplayed() != null && !imageElement.getDisplayed() ? View.GONE : View.VISIBLE);
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

            Tuple imageTuple = null;
            Tuple menuTuple = null;
            if (buttonElement.getIdElementList() != null && !buttonElement.getIdElementList().isEmpty()) {
                for (Tuple tuple1 : buttonElement.getIdElementList()) {
                    if (tuple1.getElement() instanceof Image) {
                        imageTuple = tuple1;
                    } else if (tuple1.getElement() instanceof Menu) {
                        menuTuple = tuple1;
                    } else {
                        Timber.e("Not Image or Menu. Something else");
                    }
                }
            }

            switch (tuple.getElementId()) {
                case MAIN_BUTTON_ID: // configure FAB button
                    if (imageTuple != null) {
                        Image image = (Image) imageTuple.getElement();
                        if (image != null) {
                            if (!TextUtils.isEmpty(image.getFilename())) {
                                mDataManager.getResourcePath(image.getFilename())
                                        .doOnSubscribe(this::addDisposable)
                                        .subscribe(filePath -> {
                                            if (!TextUtils.isEmpty(filePath)) {
                                                mViewHolder.vFloatingActionButton.setImageDrawable(Drawable.createFromPath(filePath));
                                            }
                                        }, throwable -> Timber.e(throwable, "Error getting image file path"));
                            } else {
                                mRendererCallback.showError("Image file path empty");
                            }
                        }
                    }

                    Tuple finalMenuTuple = menuTuple;
                    RxView.clicks(mViewHolder.vFloatingActionButton)
                            .doOnSubscribe(this::addDisposable)
                            .subscribe(o -> {
                                if (finalMenuTuple != null) {
                                    renderBottomSheetMenu(finalMenuTuple, getIdListCopyForItem(tuple, idList));
                                } else if (((Button) element).getOnClick() != null) {
                                    mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale());
                                }
                            });
                    mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_TAG, element);
                    mViewHolder.vFloatingActionButton.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                    if (buttonElement.getDisplayed() != null && !buttonElement.getDisplayed()) {
                        mViewHolder.vFloatingActionButton.hide();
                    } else {
                        mViewHolder.vFloatingActionButton.show();
                    }
                    return null;
                case NEXT_STEP_BUTTON_ID: // configure button in toolbar
                    if (imageTuple != null) {
                        Image imageElement = (Image) imageTuple.getElement();
                        if (imageElement != null && !TextUtils.isEmpty(imageElement.getFilename())) {
                            mDataManager.getResourcePath(imageElement.getFilename())
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .subscribe(filePath -> initActionButton(0, Drawable.createFromPath(filePath), buttonElement.getText()),
                                            throwable -> Timber.e(throwable, "Error getting image file path"));
                        } else {
                            mRendererCallback.showError("Image file path empty");
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
                case PREV_STEP_BUTTON_ID:
                    if (menuTuple != null) {
                        mPrevStepButtonMenuHolder = new PrevStepButtonDataHolder(menuTuple, getIdListCopyForItem(tuple, idList), buttonElement.getOnClick() != null ? buttonElement.getOnClick().getMethod() : null, buttonElement.getRequestLocale());
                    } else {
                        mPrevStepButtonDataHolder = new PrevStepButtonDataHolder(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale());
                    }
                    return null;
                default:
                    if (!TextUtils.isEmpty(buttonElement.getText())) {
                        android.widget.Button buttonView = new android.widget.Button(mContext);
                        buttonView.setTag(R.id.ELEMENT_TAG, element);
                        buttonView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                        buttonView.setLayoutParams(getParams(null, isOrientationVertical));
                        buttonView.setVisibility(buttonElement.getDisplayed() != null && !buttonElement.getDisplayed() ? View.GONE : View.VISIBLE);
                        buttonView.setText(buttonElement.getText());

                        if (imageTuple != null) {
                            Image imageElement = (Image) imageTuple.getElement();
                            if (imageElement != null && !TextUtils.isEmpty(imageElement.getFilename())) {
                                mDataManager.getResourcePath(imageElement.getFilename())
                                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                        .subscribe(filePath -> {
                                                    buttonView.setCompoundDrawablePadding(16);
                                                    buttonView.setCompoundDrawables(null, getDrawableWithBounds(filePath), null, null);
                                                },
                                                throwable -> Timber.e(throwable, "Error getting image file path"));
                            } else {
                                mRendererCallback.showError("Image file path empty");
                            }
                        }

                        Tuple finalMenuTuple1 = menuTuple;
                        RxView.clicks(buttonView)
                                .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                .subscribe(click -> {
                                    if (finalMenuTuple1 != null) {
                                        renderBottomSheetMenu(finalMenuTuple1, getIdListCopyForItem(tuple, idList));
                                    } else if (buttonElement.getOnClick() != null) {
                                        mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale());
                                    }
                                });

                        return buttonView;
                    } else {
                        ImageButton buttonView = new ImageButton(mContext);
                        buttonView.setTag(R.id.ELEMENT_TAG, element);
                        buttonView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
                        buttonView.setLayoutParams(getParams(null, isOrientationVertical));
                        buttonView.setVisibility(buttonElement.getDisplayed() != null && !buttonElement.getDisplayed() ? View.GONE : View.VISIBLE);

                        if (imageTuple != null) {
                            Image imageElement = (Image) imageTuple.getElement();
                            if (imageElement != null && !TextUtils.isEmpty(imageElement.getFilename())) {
                                mDataManager.getResourcePath(imageElement.getFilename())
                                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                        .subscribe(filePath -> buttonView.setImageDrawable(Drawable.createFromPath(filePath)),
                                                throwable -> Timber.e(throwable, "Error getting image file path"));
                            } else {
                                mRendererCallback.showError("Image file path empty");
                            }
                        }

                        Tuple finalMenuTuple1 = menuTuple;
                        RxView.clicks(buttonView)
                                .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                .subscribe(click -> {
                                    if (finalMenuTuple1 != null) {
                                        renderBottomSheetMenu(finalMenuTuple1, getIdListCopyForItem(tuple, idList));
                                    } else if (buttonElement.getOnClick() != null) {
                                        mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale());
                                    }
                                });

                        return buttonView;
                    }
            }
        } else if (element instanceof DateTimePicker) {
            DateTimePicker dateTimePickerElement = (DateTimePicker) element;
            EditText editText = new EditText(mContext);
            editText.setTag(R.id.ELEMENT_TAG, element);
            editText.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            editText.setHint(dateTimePickerElement.getHint());
            editText.setLayoutParams(getParams(dateTimePickerElement.getSize(), isOrientationVertical));
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setVisibility(dateTimePickerElement.getDisplayed() != null && !dateTimePickerElement.getDisplayed() ? View.GONE : View.VISIBLE);

            switch (dateTimePickerElement.getType()) {
                case DATE:
                    editText.setText(DateTimeUtil.formatDate(dateTimePickerElement.getDatetime(), DateTimeUtil.APP_DATE_FORMAT));
                    break;
                case TIME:
                    editText.setText(DateTimeUtil.formatDate(dateTimePickerElement.getDatetime(), DateTimeUtil.APP_TIME_FORMAT));
                    break;
                case DATETIME:
                    editText.setText(DateTimeUtil.formatDate(dateTimePickerElement.getDatetime(), DateTimeUtil.APP_DATE_TIME_FORMAT));
                    break;
            }

            if (!elementsUpdating) {
                RxTextView.afterTextChangeEvents(editText)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .skip(1)
                        .filter(textViewAfterTextChangeEvent -> !elementsUpdating) // filter elementUpdates
                        .map(textViewAfterTextChangeEvent -> {
                            String dateText = textViewAfterTextChangeEvent != null && textViewAfterTextChangeEvent.editable() != null ? textViewAfterTextChangeEvent.editable().toString() : "";
                            if (!TextUtils.isEmpty(dateText)) {
                                Date date = null;
                                switch (dateTimePickerElement.getType()) {
                                    case DATE:
                                        date = DateTimeUtil.toUtc(DateTimeUtil.formatDate(dateText, DateTimeUtil.APP_DATE_FORMAT));
                                        break;
                                    case TIME:
                                        date = DateTimeUtil.toUtc(DateTimeUtil.formatDate(dateText, DateTimeUtil.APP_TIME_FORMAT));
                                        break;
                                    case DATETIME:
                                        date = DateTimeUtil.toUtc(DateTimeUtil.formatDate(dateText, DateTimeUtil.APP_DATE_TIME_FORMAT));
                                        break;
                                }
                                return DateTimeUtil.formatDate(date, DateTimeUtil.SERVER_DATE_TIME);
                            }
                            return "";
                        })
                        .flatMap(result -> mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), result)
                                .toObservable())
                        .compose(RxUtil.applyIoAndMainSchedulers())
                        .onErrorReturn(throwable -> false)
                        .subscribe();
            }

            RxView.clicks(editText)
                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                    .subscribe(click -> mRendererCallback.showDateTimePicker(dateTimePickerElement.getType(), dateTimePickerElement.getDatetime(), date -> {
                        switch (dateTimePickerElement.getType()) {
                            case DATE:
                                editText.setText(DateTimeUtil.formatDate(date, DateTimeUtil.APP_DATE_FORMAT));
                                break;
                            case TIME:
                                editText.setText(DateTimeUtil.formatDate(date, DateTimeUtil.APP_TIME_FORMAT));
                                break;
                            case DATETIME:
                                editText.setText(DateTimeUtil.formatDate(date, DateTimeUtil.APP_DATE_TIME_FORMAT));
                                break;
                        }
                    }));
            return editText;
        } else if (element instanceof PlacePicker) {
            PlacePicker placesPickerElement = (PlacePicker) element;
            AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(mContext);
            autoCompleteTextView.setTag(R.id.ELEMENT_TAG, element);
            autoCompleteTextView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            autoCompleteTextView.setLayoutParams(getParams(placesPickerElement.getSize(), isOrientationVertical));
            autoCompleteTextView.setHint(placesPickerElement.getHint());
            autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            autoCompleteTextView.setSingleLine(true);
            autoCompleteTextView.setVisibility(placesPickerElement.getDisplayed() != null && !placesPickerElement.getDisplayed() ? View.GONE : View.VISIBLE);

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
            CityAutocompleteAdapter mAdapter = new CityAutocompleteAdapter(mContext, FaseApp.getGoogleApiHelper().getGoogleApiClient(), typeFilter);
            autoCompleteTextView.setAdapter(mAdapter);

            autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
                final AutocompletePrediction item = mAdapter.getItem(position);
                if (item != null) {
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                            .getPlaceById(FaseApp.getGoogleApiHelper().getGoogleApiClient(), item.getPlaceId());
                    placeResult.setResultCallback(places -> {
                        if (!places.getStatus().isSuccess()) {
                            Timber.e("Place query did not complete. Error: %s", places.getStatus().toString());
                            places.release();
                            return;
                        }

                        Place place = places.get(0);
                        com.fase.model.data.Place placeModel = PlaceUtils.getPlace(FaseApp.getApplication().getApplicationContext(), place);
                        if (!elementsUpdating) {
                            mDataManager.putValueUpdate(tuple.getElementId(), getIdListCopyForItem(tuple, idList), mGson.toJson(placeModel))
                                    .toObservable()
                                    .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                                    .compose(RxUtil.applyIoAndMainSchedulers())
                                    .onErrorReturn(throwable -> false)
                                    .subscribe();
                        }
                    });
                }
            });

            if (placesPickerElement.getPlace() != null) {
                com.fase.model.data.Place place = placesPickerElement.getPlace();
                autoCompleteTextView.setText(place.getCity());
                if (!TextUtils.isEmpty(place.getCity())) {
                    autoCompleteTextView.setSelection(place.getCity().length());
                }
            }

            return autoCompleteTextView;
        } else if (element instanceof Web) {
            Web webElement = (Web) element;
            WebView webView = new WebView(mContext);
            webView.setTag(R.id.ELEMENT_TAG, element);
            webView.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            webView.setVisibility(webElement.getDisplayed() != null && !webElement.getDisplayed() ? View.GONE : View.VISIBLE);
            LinearLayout.LayoutParams params = getParams(webElement.getSize(), isOrientationVertical);
//            params.height = 200;
//            webView.setMinimumHeight(200);

            webView.setLayoutParams(params);
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
                if (!webElement.getUrl().contains("http")) {
                    mRendererCallback.showError("Wrong web url format. Url must have http:// or https://");
                } else {
                    webView.post(() -> NetworkUtil.checkNetworkWithRetryDialog(mContext, () -> webView.loadUrl(webElement.getUrl())));
                }
            }
            return webView;
        } else if (element instanceof Navigation) {
            Navigation navigationElement = (Navigation) element;
            if (navigationElement.getIdElementList() != null) {
                for (Tuple itemTuple : navigationElement.getIdElementList()) {
                    // will change element type to MenuItem for proper rendering
                    convertButtonToMenuItem(itemTuple);
                    renderViewElement(mViewHolder.vNavigationMenuLayout, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList)); // Orientation must be vertical for navigation drawer
                }
            }
            mRendererCallback.enableNavigationMenu();
            return null;
        } else if (element instanceof ContactPicker) {
            ContactPicker contactPicker = (ContactPicker) element;

            EditText editText = new EditText(mContext);
            editText.setTag(R.id.ELEMENT_TAG, element);
            editText.setTag(R.id.ELEMENT_ID_TAG, tuple.getElementId());
            editText.setHint(contactPicker.getHint());
            editText.setLayoutParams(getParams(contactPicker.getSize(), isOrientationVertical));
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setVisibility(contactPicker.getDisplayed() != null && !contactPicker.getDisplayed() ? View.GONE : View.VISIBLE);

            if (contactPicker.getContact() != null) {
                if (!TextUtils.isEmpty(contactPicker.getContact().getDisplayName())) {
                    editText.setText(contactPicker.getContact().getDisplayName());
                }
            }

            if (contactPicker.getOnPick() != null) {
                RxView.clicks(editText)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .subscribe(click -> mRendererCallback.requestContact(new RequestContactDataHolder(editText, tuple.getElementId(),
                                getIdListCopyForItem(tuple, idList), contactPicker.getOnPick().getMethod(), contactPicker.getRequestLocale())));
            }
            return editText;
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
                            convertButtonToMenuItem(itemTuple);
                        }
                        renderViewElement(mViewHolder.vBottomSheet, itemTuple, Orientation.VERTICAL, getIdListCopyForItem(tuple, idList));
                    }
                }
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
            menuItemView.setVisibility(menuItemElement.getDisplayed() != null && !menuItemElement.getDisplayed() ? View.GONE : View.VISIBLE);

            if (menuItemElement.getOnClick() != null) {
                RxView.clicks(menuItemView)
                        .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                        .subscribe(click -> mRendererCallback.onFunctionCall(getIdListCopyForItem(tuple, idList), menuItemElement.getOnClick().getMethod(), menuItemElement.getRequestLocale()));
            }

            if (menuItemElement.getIdElementList() != null && !menuItemElement.getIdElementList().isEmpty()) {
                Image imageElement = (Image) menuItemElement.getIdElementList().get(0).getElement();
                if (imageElement != null && !TextUtils.isEmpty(imageElement.getFilename())) {
                    mDataManager.getResourcePath(imageElement.getFilename())
                            .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                            .subscribe(filePath -> {
                                        menuItemView.setCompoundDrawablePadding(16);
                                        menuItemView.setCompoundDrawables(getDrawableWithBounds(filePath), null, null, null);
                                    },
                                    throwable -> Timber.e(throwable, "Error getting image for image"));
                } else {
                    mRendererCallback.showError("Image file path empty");
                }
            }
            return menuItemView;
        } else if (element instanceof Separator) {
            Separator separatorElement = (Separator) element;
            View separatorView = new View(mContext);
            LinearLayout.LayoutParams params = getParams(null, isOrientationVertical);
            if (isOrientationVertical) {
                params.height = 1;
            } else {
                params.width = 1;
            }
            separatorView.setLayoutParams(params);
            separatorView.setVisibility(separatorElement.getDisplayed() != null && !separatorElement.getDisplayed() ? View.GONE : View.VISIBLE);

            return separatorView;
        } else if (element instanceof Alert) {
            Alert alertElement = (Alert) element;
            ArrayList<String> alertIdList = getIdListCopyForItem(tuple, idList);
            if (!TextUtils.isEmpty(alertElement.getText())) {
                if (alertElement.getIdElementList() != null && !alertElement.getIdElementList().isEmpty()) {
                    if (alertElement.getIdElementList().size() == 2) {
                        Tuple buttonFirstTuple = alertElement.getIdElementList().get(0);
                        Tuple buttonSecondTuple = alertElement.getIdElementList().get(1);
                        if (buttonFirstTuple != null && buttonSecondTuple != null) {
                            Button buttonFirstElement = (Button) buttonFirstTuple.getElement();
                            Button buttonSecondElement = (Button) buttonSecondTuple.getElement();
                            if (buttonFirstElement != null && buttonSecondElement != null) {
                                mRendererCallback.showAlert(alertElement.getText(), buttonFirstElement.getText(), getIdListCopyForItem(buttonFirstTuple, alertIdList),
                                        buttonFirstElement.getOnClick().getMethod(), buttonFirstElement.getRequestLocale(), buttonSecondElement.getText(),
                                        getIdListCopyForItem(buttonSecondTuple, alertIdList), buttonSecondElement.getOnClick().getMethod(), buttonSecondElement.getRequestLocale());
                            }
                        }
                    } else {
                        Tuple buttonTuple = alertElement.getIdElementList().get(0);
                        if (buttonTuple != null) {
                            Button buttonElement = (Button) buttonTuple.getElement();
                            if (buttonElement != null) {
                                mRendererCallback.showAlert(alertElement.getText(), buttonElement.getText(), getIdListCopyForItem(buttonTuple, alertIdList),
                                        buttonElement.getOnClick().getMethod(), buttonElement.getRequestLocale(), null,
                                        null, null, null);
                            }
                        }
                    }
                } else {
                    mRendererCallback.showAlert(alertElement.getText(), null, null, null, null,
                            null, null, null, null);
                }
            }
            return null;
        }
        return null;
    }

    @NonNull
    private Drawable getDrawableWithBounds(String filePath) {
        Drawable drawable = Drawable.createFromPath(filePath);
        int bound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, drawable.getIntrinsicHeight(), FaseApp.getRes().getDisplayMetrics());
        drawable.setBounds(0, 0, bound, bound);
        return drawable;
    }

    private void convertButtonToMenuItem(Tuple itemTuple) {
        Button button = (Button) itemTuple.getElement();
        MenuItem menuItem = new MenuItem();
        menuItem.setClassName(MenuItem.class.getSimpleName());
        menuItem.setText(button.getText());
        menuItem.setOnClick(button.getOnClick());
        menuItem.setIdElementList(button.getIdElementList());
        itemTuple.setElement(menuItem);
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
            return new LinearLayout.LayoutParams(orientationVertical ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.WRAP_CONTENT,
                    orientationVertical ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private View renderViewElement(ViewGroup viewGroup, Tuple tuple, Orientation orientation, ArrayList<String> idList) {
        if (tuple != null && tuple.getElement() != null) {
            View view = renderView(tuple, orientation, idList);
            if (view != null && viewGroup != null) {
                view.setId((int) SystemClock.currentThreadTimeMillis());
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

    public void renderScreenState(Response response) {
        if (response.getElementsUpdate() != null) {
            updateElement(response.getElementsUpdate());
        }
        if (response.getScreen() != null) {
            mRendererCallback.hideKeyboard();
            Screen screen = response.getScreen();
            if (screen.getOnRefresh() != null) {
                mViewHolder.vSwipeRefreshLayout.setEnabled(true);
                mViewHolder.vSwipeRefreshLayout.setOnRefreshListener(() -> mRendererCallback.onFunctionCall(null, screen.getOnRefresh().getMethod(), screen.getRequestLocale()));
            }
            if (!TextUtils.isEmpty(screen.getTitle())) {
                mRendererCallback.setScreenTitle(screen.getTitle());
            }

            // TODO: load more

            if (screen.getScrollable() != null && screen.getScrollable()) {
                NestedScrollView scrollView = new NestedScrollView(mContext);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                scrollView.setLayoutParams(params);
                scrollView.addView(getRenderedLayout(screen.getIdElementList(), true));
                if (mViewHolder.vFloatingActionButton.getVisibility() == View.VISIBLE) {
                    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, FaseApp.getRes().getDisplayMetrics());
                    scrollView.setPadding(0, 0, 0, padding);
                    scrollView.setClipToPadding(false);
                }
                mViewHolder.vContentContainer.addView(scrollView);
            } else {
                mViewHolder.vContentContainer.addView(getRenderedLayout(screen.getIdElementList(), false));
            }
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
                int bound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, drawable.getIntrinsicHeight(), FaseApp.getRes().getDisplayMetrics());
                drawable.setBounds(0, 0, bound, bound);
                mViewHolder.vButtonFirstText.setCompoundDrawables(drawable, null, null, null);
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

    private void updateElement(ElementsUpdate elementsUpdate) {
        elementsUpdating = true;
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
                    if (resultView instanceof AutoCompleteTextView) {
                        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) resultView;
                        if (!TextUtils.isEmpty(value)) {
                            com.fase.model.data.Place place = mGson.fromJson(value, com.fase.model.data.Place.class);
                            if (place != null) {
                                autoCompleteTextView.setText(place.getCity());
                            } else {
                                autoCompleteTextView.setText(value);
                            }
                        }
                        String editTextString = autoCompleteTextView.getText().toString();
                        if (!TextUtils.isEmpty(editTextString)) {
                            autoCompleteTextView.setSelection(editTextString.length());
                        }
                    } else if (resultView instanceof EditText) {
                        EditText editText = (EditText) resultView;
                        Object tag = editText.getTag(R.id.ELEMENT_TAG);
                        if (tag != null && tag instanceof DateTimePicker) {
                            editText.setText(!TextUtils.isEmpty(value) ? DateTimeUtil.convertDateFormat(value, DateTimeUtil.SERVER_DATE_TIME, DateTimeUtil.APP_DATE_TIME_FORMAT) : value);
                        } else {
                            editText.setText(value);
                        }

                        String editTextString = editText.getText().toString();
                        if (!TextUtils.isEmpty(editTextString)) {
                            editText.setSelection(editTextString.length());
                        }
                    } else if (resultView instanceof android.widget.Switch) {
                        Boolean boolValue = Boolean.parseBoolean(value);
                        ((android.widget.Switch) resultView).setChecked(boolValue);
                    } else if (resultView instanceof android.widget.Button) {
                        ((android.widget.Button) resultView).setText(value);
                    } else if (resultView instanceof TextView) {
                        ((TextView) resultView).setText(value);
                    } else if (resultView instanceof ClickableSpinner) {
                        ClickableSpinner spinner = (ClickableSpinner) resultView;
                        android.widget.SpinnerAdapter adapter = spinner.getAdapter();
                        for (int j = 0; j < adapter.getCount(); j++) {
                            if (adapter.getItem(j) instanceof Entry) {
                                if (((Entry) adapter.getItem(j)).name.equals(value)) {
                                    spinner.setSelection(j);
                                }
                            }
                        }
                    } else if (resultView instanceof SeekBar) {
                        Slider element = (Slider) resultView.getTag(R.id.ELEMENT_TAG);
                        final int minValue = element.getMinValue() != null ? (int) (element.getMinValue() * 100) : 0;
                        try {
                            ((SeekBar) resultView).setProgress((int) (Double.parseDouble(value) * 100) + minValue);
                        } catch (Exception ignored) {
                        }
                    } else if (resultView instanceof WebView) {
                        ((WebView) resultView).loadUrl(value);
                    } else {
                        Timber.e(new Exception("Another instance type ::: " + resultView.getClass().getSimpleName() + " with value ::: " + value));
                    }
                }
            }
        }
        elementsUpdating = false;
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

    public void onBackPressed() {
        if (mPrevStepButtonMenuHolder != null || mPrevStepButtonDataHolder != null) {
            if (mPrevStepButtonMenuHolder != null) {
                if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    renderBottomSheetMenu(mPrevStepButtonMenuHolder.getTuple(), mPrevStepButtonMenuHolder.getIdList());
                } else {
                    mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            } else {
                mRendererCallback.onFunctionCall(mPrevStepButtonDataHolder.getIdList(), mPrevStepButtonDataHolder.getMethod(), mPrevStepButtonDataHolder.getRequestLocale());
            }
        } else {
            if (mSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {
                mRendererCallback.backPressed();
            }
        }
    }

    private void renderBottomSheetMenu(Tuple tuple, ArrayList<String> idList) {
        mViewHolder.vBottomSheet.removeAllViews();
        renderViewElement(null, tuple, Orientation.VERTICAL, idList);
        mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
