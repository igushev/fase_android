package com.fase.ui.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fase.R;
import com.fase.ui.fragment.dialog.DateTimePickerResult;
import com.fase.util.DateTimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.transition.TransitionManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDateTimePicker extends FrameLayout {

    @BindView(R.id.backgroundTimeFrame)
    FrameLayout vBackgroundTimeFrame;
    @BindView(R.id.timeTextView)
    TextView vTimeTextView;
    @BindView(R.id.backgroundDateFrame)
    FrameLayout vBackgroundDateFrame;
    @BindView(R.id.dateTextView)
    TextView vDateTextView;
    @BindView(R.id.timePicker)
    TimePicker vTimePicker;
    @BindView(R.id.datePicker)
    DatePicker vDatePicker;

    private boolean isTimeStateCurrent = false;
    private Calendar mCurrentCalendar;
    private int accentColor;
    private int whiteColor;
    private int textColor;
    private DateTimePickerResult mCallback;

    public CustomDateTimePicker(@NonNull Context context, Date date, DateTimePickerResult callback) {
        super(context);
        initView(context, date);
        this.mCallback = callback;
    }

    public CustomDateTimePicker(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public CustomDateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, null);
    }

    public CustomDateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, null);
    }

    public CustomDateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, null);
    }

    private void initView(Context context, Date date) {
        LayoutInflater.from(context).inflate(R.layout.view_custom_date_time_picker, this, true);
        ButterKnife.bind(this);

        accentColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        whiteColor = ContextCompat.getColor(getContext(), android.R.color.white);
        textColor = ContextCompat.getColor(getContext(), android.R.color.black);

        mCurrentCalendar = GregorianCalendar.getInstance();
        if (date != null) {
            mCurrentCalendar.setTime(date);
        } else {
            mCurrentCalendar.setTime(new Date());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vTimePicker.setHour(mCurrentCalendar.get(Calendar.HOUR_OF_DAY));
            vTimePicker.setMinute(mCurrentCalendar.get(Calendar.MINUTE));
        } else {
            vTimePicker.setCurrentHour(mCurrentCalendar.get(Calendar.HOUR_OF_DAY));
            vTimePicker.setCurrentMinute(mCurrentCalendar.get(Calendar.MINUTE));
        }

        vTimePicker.setOnTimeChangedListener((timePicker, hour, minute) -> {
            mCurrentCalendar.set(Calendar.HOUR_OF_DAY, hour);
            mCurrentCalendar.set(Calendar.MINUTE, minute);
            updateTextViews();
            notifyListener();
        });

        vDatePicker.init(mCurrentCalendar.get(Calendar.YEAR), mCurrentCalendar.get(Calendar.MONTH), mCurrentCalendar.get(Calendar.DAY_OF_MONTH), (datePicker, year, month, day) -> {
            mCurrentCalendar.set(Calendar.YEAR, year);
            mCurrentCalendar.set(Calendar.MONTH, month);
            mCurrentCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateTextViews();
            notifyListener();
        });

        changeViewState(true);
        updateTextViews();
    }

    private void notifyListener() {
        if (mCallback != null) {
            mCallback.onDateTimeSet(mCurrentCalendar.getTime());
        }
    }

    private void updateTextViews() {
        Date currentDate = mCurrentCalendar.getTime();
        String time = DateTimeUtil.formatDate(currentDate, vTimePicker.is24HourView() ? DateTimeUtil.APP_TIME_FORMAT : DateTimeUtil.APP_12H_TIME_FORMAT);
        String date = DateTimeUtil.formatDate(currentDate, DateTimeUtil.APP_DATE_FORMAT);
        vTimeTextView.setText(time);
        vDateTextView.setText(date);
    }

    private void changeViewState(boolean isTimeState) {
        if (isTimeStateCurrent == isTimeState) {
            return;
        }
        TransitionManager.beginDelayedTransition(this);
        isTimeStateCurrent = isTimeState;

        vBackgroundTimeFrame.setBackgroundColor(isTimeStateCurrent ? accentColor : 0);
        vBackgroundDateFrame.setBackgroundColor(!isTimeStateCurrent ? accentColor : 0);
        vTimeTextView.setTextColor(isTimeStateCurrent ? whiteColor : textColor);
        vDateTextView.setTextColor(!isTimeStateCurrent ? whiteColor : textColor);
        vTimePicker.setVisibility(isTimeStateCurrent ? VISIBLE : GONE);
        vDatePicker.setVisibility(!isTimeStateCurrent ? VISIBLE : GONE);
    }

    @OnClick({R.id.timeTextView, R.id.dateTextView})
    void onClick(View view) {
        changeViewState(view.getId() == R.id.timeTextView);
    }

    public Date getDateTime() {
        return mCurrentCalendar.getTime();
    }

    public void setDateTime(Date dateTime) {
        mCurrentCalendar.setTime(dateTime);
        updateTextViews();

        vDatePicker.updateDate(mCurrentCalendar.get(Calendar.YEAR), mCurrentCalendar.get(Calendar.MONTH), mCurrentCalendar.get(Calendar.DAY_OF_MONTH));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vTimePicker.setHour(mCurrentCalendar.get(Calendar.HOUR_OF_DAY));
            vTimePicker.setMinute(mCurrentCalendar.get(Calendar.MINUTE));
        } else {
            vTimePicker.setCurrentHour(mCurrentCalendar.get(Calendar.HOUR_OF_DAY));
            vTimePicker.setCurrentMinute(mCurrentCalendar.get(Calendar.MINUTE));
        }
    }
}
