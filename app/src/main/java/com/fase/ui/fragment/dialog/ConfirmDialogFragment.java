package com.fase.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.fase.R;
import com.fase.base.BaseDialogFragment;

public class ConfirmDialogFragment extends BaseDialogFragment {

    private String message;
    private String title;
    private DialogInterface.OnClickListener okListener;
    private DialogInterface.OnClickListener noListener;

    public static ConfirmDialogFragment newInstance(String message, DialogInterface.OnClickListener okListener) {
        return newInstance(null, message, okListener, null);
    }

    public static ConfirmDialogFragment newInstance(String title, String message, DialogInterface.OnClickListener okListener) {
        return newInstance(title, message, okListener, null);
    }

    public static ConfirmDialogFragment newInstance(String message, DialogInterface.OnClickListener okListener,
                                                    DialogInterface.OnClickListener noListener) {
        return newInstance(null, message, okListener, noListener);
    }

    public static ConfirmDialogFragment newInstance(String title, String message, DialogInterface.OnClickListener okListener,
                                                    DialogInterface.OnClickListener noListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.title = title;
        fragment.message = message;
        fragment.okListener = okListener;
        fragment.noListener = noListener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        if (!TextUtils.isEmpty(title)) {
            alertDialogBuilder.setTitle(title);
        }
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), okListener);
        alertDialogBuilder.setNegativeButton(getString(R.string.no), noListener);
        return alertDialogBuilder.create();
    }

    @Override
    public String getFragmentTag() {
        return ConfirmDialogFragment.class.getSimpleName();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
    }
}
