package com.fase.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

/**
 * Spinner extension that calls onItemSelected even when the selection is the same as its previous value
 */
public class ClickableSpinner extends AppCompatSpinner {

    public ClickableSpinner(Context context) {
        super(context);
    }

    public ClickableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void
    setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected && getOnItemSelectedListener() != null) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected && getOnItemSelectedListener() != null) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}


