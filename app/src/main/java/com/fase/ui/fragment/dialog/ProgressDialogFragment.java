package com.fase.ui.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fase.R;
import com.fase.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressDialogFragment extends BaseDialogFragment {

    @BindView(R.id.progressText)
    TextView vProgressBarText;
    @BindView(R.id.progressBar)
    ProgressBar vProgressBar;

    private DialogInterface.OnCancelListener cancelListener;

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.cancelListener = onCancelListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setCanceledOnTouchOutside(cancelListener != null);
        dialog.setCancelable(cancelListener != null);
        setCancelable(cancelListener != null);
        dialog.setIndeterminate(true);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        View progressView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_progress, null);
        ButterKnife.bind(this, progressView);
        vProgressBarText.setText(R.string.please_wait);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(vProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            vProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            vProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }

        if (getDialog() != null) {
            getDialog().setContentView(progressView);
        }
    }

    public void onResume() {
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                Point size = new Point();
                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);
                window.setLayout((int) (size.x * 0.5), (int) (size.y * 0.25));
                window.setGravity(Gravity.CENTER);
            }
        }

        super.onResume();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

    @Override
    public String getFragmentTag() {
        return ProgressDialogFragment.class.getSimpleName();
    }
}
