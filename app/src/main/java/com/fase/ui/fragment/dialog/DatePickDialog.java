package com.fase.ui.fragment.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.DatePicker;

import com.fase.R;
import com.fase.base.BaseDialogFragment;

import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class DatePickDialog extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String KEY_DATE = "DATE";

    private Date mPreviousDate;
    private DateTimePickerResult mCallback;

    public static DatePickDialog newInstance(@Nullable Date date) {
        DatePickDialog fragment = new DatePickDialog();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            mPreviousDate = (Date) getArguments().getSerializable(KEY_DATE);
        }
    }

    public void setCallback(DateTimePickerResult mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public String getFragmentTag() {
        return DatePickDialog.class.getSimpleName();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        if (mPreviousDate != null) {
            calendar.setTime(mPreviousDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Dialog picker = new DatePickerDialog(getActivity(), this, year, month, day);
        picker.setTitle(getString(R.string.choose_date_title));

        return picker;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

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
