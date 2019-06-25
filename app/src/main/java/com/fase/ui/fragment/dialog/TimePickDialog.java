package com.fase.ui.fragment.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import com.fase.R;
import com.fase.base.BaseDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

public class TimePickDialog extends BaseDialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {

    private static final String KEY_DATE = "DATE";

    private Date mPreviousTime;
    private DateTimePickerResult mCallback;

    public static TimePickDialog newInstance(@Nullable Date date) {
        TimePickDialog fragment = new TimePickDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            mPreviousTime = (Date) getArguments().getSerializable(KEY_DATE);
        }
    }

    public void setCallback(DateTimePickerResult mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public String getFragmentTag() {
        return TimePickDialog.class.getSimpleName();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        if (mPreviousTime != null) {
            calendar.setTime(mPreviousTime);
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        Dialog picker = new TimePickerDialog(getActivity(), this, hour, minute, false);
        picker.setTitle(getString(R.string.choose_time_title));

        return picker;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        if (mCallback != null) {
            mCallback.onDateTimeSet(calendar.getTime());
        } else if (getContext() instanceof DateTimePickerResult) {
            ((DateTimePickerResult) getContext()).onDateTimeSet(calendar.getTime());
        } else {
            Timber.e("Activity must implement DateTimePickerResult interface or need to set callback");
        }
    }

    @Override
    public void onDestroy() {
        mCallback = null;
        super.onDestroy();
    }
}
