package com.fase.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fase.R;
import com.fase.base.BaseDialogFragment;

public class AlertDialogFragment extends BaseDialogFragment {

    private String mMessage;
    private String mButtonText;
    private String mTitle;
    private DialogInterface.OnClickListener mOkListener;

    public static AlertDialogFragment newInstance(String message) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        return fragment;
    }

    public static AlertDialogFragment newInstance(String message, DialogInterface.OnClickListener okListener) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        fragment.mOkListener = okListener;
        return fragment;
    }

    public static AlertDialogFragment newInstance(String message, String buttonText, DialogInterface.OnClickListener okListener) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.mMessage = message;
        fragment.mOkListener = okListener;
        fragment.mButtonText = buttonText;
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
        alertDialogBuilder.setPositiveButton(TextUtils.isEmpty(mButtonText) ? getString(R.string.ok) : mButtonText, mOkListener);
        return alertDialogBuilder.create();
    }

    @Override
    public String getFragmentTag() {
        return AlertDialogFragment.class.getSimpleName();
    }
}
