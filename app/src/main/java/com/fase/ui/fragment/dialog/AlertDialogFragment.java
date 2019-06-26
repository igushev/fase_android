package com.fase.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.fase.R;
import com.fase.base.BaseDialogFragment;

import androidx.annotation.NonNull;

public class AlertDialogFragment extends BaseDialogFragment {

    private String mMessage;
    private String mTitle;
    private String mPositiveButtonText;
    private String mNegativeButtonText;
    private DialogInterface.OnClickListener mPositiveListener;
    private DialogInterface.OnClickListener mNegativeListener;

    public static AlertDialogFragment newInstance(String message) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        return fragment;
    }

    public static AlertDialogFragment newInstance(String message, DialogInterface.OnClickListener okListener) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        fragment.mPositiveListener = okListener;
        return fragment;
    }

    public static AlertDialogFragment newInstance(String message, String positiveButtonText, DialogInterface.OnClickListener okListener) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        fragment.mPositiveListener = okListener;
        fragment.mPositiveButtonText = positiveButtonText;
        return fragment;
    }

    public static AlertDialogFragment newInstance(String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        fragment.mPositiveButtonText = positiveButtonText;
        fragment.mNegativeButtonText = negativeButtonText;
        fragment.mPositiveListener = positiveListener;
        fragment.mNegativeListener = negativeListener;
        return fragment;
    }

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        fragment.mTitle = title;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setCancelable(false);
        if (!TextUtils.isEmpty(mTitle)) {
            alertDialogBuilder.setTitle(mTitle);
        }
        alertDialogBuilder.setMessage(mMessage);
        alertDialogBuilder.setPositiveButton(TextUtils.isEmpty(mPositiveButtonText) ? getString(R.string.ok) : mPositiveButtonText, mPositiveListener);
        if (mNegativeListener != null) {
            alertDialogBuilder.setNegativeButton(TextUtils.isEmpty(mNegativeButtonText) ? getString(R.string.cancel) : mNegativeButtonText, mNegativeListener);
        }
        return alertDialogBuilder.create();
    }

    @Override
    public String getFragmentTag() {
        return AlertDialogFragment.class.getSimpleName();
    }
}
