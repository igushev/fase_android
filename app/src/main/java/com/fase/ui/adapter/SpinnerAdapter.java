package com.fase.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fase.R;
import com.fase.model.Entry;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter<ID, VAL> {

    private Spinner mSpinner;
    private final Entry<ID, VAL> mEmptyEntry = new Entry<>();

    private List<Entry<ID, VAL>> mList = new ArrayList<>();
    private ListAdapter<ID, VAL> mAdapter;

    {
        mList.add(mEmptyEntry);
    }

    public SpinnerAdapter(Spinner spinner, VAL emptyEntryName) {
        mSpinner = spinner;
        mAdapter = new ListAdapter<>(spinner.getContext(), mList);
        mSpinner.setAdapter(mAdapter);
        mEmptyEntry.name = emptyEntryName;
    }

    public void setList(List<Entry<ID, VAL>> list) {
        mList.clear();
        mList.add(mEmptyEntry);
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    public void addItem(Entry<ID, VAL> item) {
        mList.add(item);
        mAdapter.notifyDataSetChanged();
    }

    public ID getSelectedId() {
        int selectedPosition = mSpinner.getSelectedItemPosition();

        if (selectedPosition == -1) {
            return null;
        }

        Entry<ID, VAL> selectedEntry = mList.get(mSpinner.getSelectedItemPosition());

        return selectedEntry == mEmptyEntry ? null : selectedEntry.id;
    }

    public VAL getSelectedValue() {
        int selectedPosition = mSpinner.getSelectedItemPosition();

        if (selectedPosition == -1) {
            return null;
        }

        Entry<ID, VAL> selectedEntry = mList.get(mSpinner.getSelectedItemPosition());

        return selectedEntry == mEmptyEntry ? null : selectedEntry.name;
    }

    public void setSelection(ID id) {
        for (Entry<ID, VAL> idvalEntry : mList) {
            if (idvalEntry.id != null && id != null && id == idvalEntry.id) {
                int spinnerPosition = mAdapter.getPosition(idvalEntry);
                mSpinner.setSelection(spinnerPosition);
                break;
            }
        }
    }

    public void selectValue(VAL val) {
        for (Entry<ID, VAL> valEntry : mList) {
            if (valEntry.name != null && val != null && val.equals(valEntry.name)) {
                int spinnerPosition = mAdapter.getPosition(valEntry);
                mSpinner.setSelection(spinnerPosition);
                break;
            }
        }
    }

    public void clearSelection() {
        mSpinner.setSelection(0);
    }

    private static class ListAdapter<ID, VAL> extends ArrayAdapter<Entry<ID, VAL>> {

        public ListAdapter(Context context, List<Entry<ID, VAL>> objects) {
            super(context, R.layout.item_dropdown, objects);
        }

        @NonNull
        private View getViewImpl(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) super.getView(position, convertView, parent);

            if (position == 0) {
                tv.setTypeface(null, Typeface.ITALIC);
                tv.setTextColor(Color.GRAY);
            } else {
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setTextColor(Color.BLACK);
            }
            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getViewImpl(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getViewImpl(position, convertView, parent);
        }
    }
}
