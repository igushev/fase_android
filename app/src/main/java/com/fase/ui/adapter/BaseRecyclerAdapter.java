package com.fase.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fase.R;

import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseButterHolder<T>> {

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int previousSelectedPosition);
    }

    public interface Bindable<T> {
        void onBind(BaseButterHolder<T> vh, T item, int position);
    }

    protected List<T> mFilteredItems;
    protected List<T> mAllItems;
    protected int mSelectedPosition = -1;
    protected LayoutInflater mInflater;
    protected final BaseButterHolder.Factory<T> mFactory;
    protected final int mLayoutId;
    protected OnItemClickListener mOnItemClickListener;
    protected Bindable<T> mOnBindListener;

    public BaseRecyclerAdapter(Context context, List<T> items, BaseButterHolder.Factory<T> factory, int layoutId) {
        mInflater = LayoutInflater.from(context);
        mFilteredItems = new ArrayList<>();
        mAllItems = new ArrayList<>();
        if (items != null) {
            mFilteredItems.addAll(items);
            mAllItems.addAll(items);
        }
        mFactory = factory;
        mLayoutId = layoutId;
    }

    @Override
    public BaseButterHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseButterHolder<T> vh = mFactory.create(mInflater.inflate(mLayoutId, parent, false));

        initClickListener(vh);
        return vh;
    }

    protected void initClickListener(BaseButterHolder<T> vh) {
        vh.itemView.setOnClickListener(v -> {
            int position = (int) v.getTag(R.id.POSITION_KEY);
            int previousSelection = mSelectedPosition;

            mSelectedPosition = position;
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position, previousSelection);
            }
        });
    }

    @Override
    public void onBindViewHolder(BaseButterHolder<T> holder, int position) {
        if (mFilteredItems.get(position) != null) {
            holder.bind(mFilteredItems.get(position), position);
            holder.itemView.setTag(mFilteredItems.get(position));
        }
        holder.itemView.setTag(R.id.POSITION_KEY, position);

        if (mOnBindListener != null) {
            mOnBindListener.onBind(holder, mFilteredItems.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredItems.size();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public T getSelectedItem() {
        if (mSelectedPosition < 0 || mFilteredItems == null || mFilteredItems.size() == 0) {
            return null;
        } else {
            return mFilteredItems.get(mSelectedPosition);
        }
    }

    public void resetSelection() {
        mSelectedPosition = -1;
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
    }

    public List<T> getItems() {
        return mFilteredItems;
    }

    public static int getPosition(View v) {
        Object posTag = v.getTag(R.id.POSITION_KEY);
        if (posTag != null) return (Integer) posTag;
        else return -1;
    }

    public void replace(List<? extends T> list) {
        if (list != null) {
            mFilteredItems = new ArrayList<>(list);
            mAllItems = new ArrayList<>(list);
        } else {
            mFilteredItems = new ArrayList<>();
            mAllItems = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    protected void addItem(int position, T e) {
        mFilteredItems.add(position, e);
        notifyItemInserted(position);
    }

    protected void moveItem(int fromPosition, int toPosition) {
        final T e = mFilteredItems.remove(fromPosition);
        mFilteredItems.add(toPosition, e);
        notifyItemMoved(fromPosition, toPosition);
    }

    protected T removeItemByPosition(int position) {
        final T e = mFilteredItems.remove(position);
        notifyItemRemoved(position);
        return e;
    }

    protected int removeItemByObj(T obj) {
        int position = -1;
        for (int i = 0; i < mFilteredItems.size(); i++) {
            if (mFilteredItems.get(i).equals(obj)) {
                position = i;
            }
        }
        mFilteredItems.remove(position);
        notifyItemRemoved(position);
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnBindListener(Bindable<T> onBindListener) {
        mOnBindListener = onBindListener;
    }

    protected void animateTo(List<T> inList) {
        applyAndAnimateRemovals(inList);
        applyAndAnimateAdditions(inList);
        applyAndAnimateMovedItems(inList);
    }

    private void applyAndAnimateRemovals(List<T> inList) {
        for (int i = mFilteredItems.size() - 1; i >= 0; i--) {
            final T e = mFilteredItems.get(i);
            if (!inList.contains(e)) {
                removeItemByPosition(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> inList) {
        for (int i = 0, count = inList.size(); i < count; i++) {
            final T e = inList.get(i);
            if (!mFilteredItems.contains(e)) {
                addItem(i, e);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> inList) {
        for (int toPosition = inList.size() - 1; toPosition >= 0; toPosition--) {
            final T e = inList.get(toPosition);
            final int fromPosition = mFilteredItems.indexOf(e);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void filter(String query) {
    }
}
