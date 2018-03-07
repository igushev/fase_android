package com.fase.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseButterHolder<T> extends RecyclerView.ViewHolder {

    public interface Factory<T> {
        BaseButterHolder<T> create(View itemView);
    }

    public BaseButterHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    abstract public void bind(T obj, int position);

    protected Context getContext() {
        return itemView.getContext();
    }
}
