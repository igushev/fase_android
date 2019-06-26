package com.fase.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fase.R;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import timber.log.Timber;

public abstract class BaseDialogFragment extends DialogFragment {

    public abstract String getFragmentTag();

    public void show(FragmentManager manager) {
        if (isAdded()) {
            return;
        }
        try {
            super.show(manager, getFragmentTag());
        } catch (Exception ignored) {
            Timber.e(ignored, "Show dialog error: %s", getFragmentTag());
        }
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getDialog().getCurrentFocus();
        if (view != null) {
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .getAttributes().windowAnimations = R.style.FadeDialogAnimation;
        }
    }
}
