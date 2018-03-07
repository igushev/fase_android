package com.fase.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fase.R;
import com.fase.model.Entry;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapterWithoutEmptyItem<ID, VAL> {

    private Spinner mSpinner;

    private List<Entry<ID, VAL>> mList = new ArrayList<>();
    private ListAdapter<ID, VAL> mAdapter;

    public SpinnerAdapterWithoutEmptyItem(Spinner spinner) {
        mSpinner = spinner;
        mAdapter = new ListAdapter<>(spinner.getContext(), mList);
        mSpinner.setAdapter(mAdapter);
    }

    public void setList(List<Entry<ID, VAL>> list) {
        mList.clear();
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

        return selectedEntry.id;
    }

    public VAL getSelectedValue() {
        int selectedPosition = mSpinner.getSelectedItemPosition();

        if (selectedPosition == -1) {
            return null;
        }

        Entry<ID, VAL> selectedEntry = mList.get(mSpinner.getSelectedItemPosition());

        return selectedEntry.name;
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

    private static class ListAdapter<ID, VAL> extends ArrayAdapter<Entry<ID, VAL>> {

        public ListAdapter(Context context, List<Entry<ID, VAL>> objects) {
            super(context, R.layout.item_dropdown, objects);
        }

        @NonNull
        private View getViewImpl(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
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
