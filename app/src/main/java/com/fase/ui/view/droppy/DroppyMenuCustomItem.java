package com.fase.ui.view.droppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class DroppyMenuCustomItem extends DroppyMenuItemAbstract {

    public DroppyMenuCustomItem(int customResourceId) {
        type = TYPE_CUSTOM;
        customViewResourceId = customResourceId;
    }

    public DroppyMenuCustomItem(View customView) {
        type = TYPE_CUSTOM;
        renderedView = customView;
    }

    @Override
    public View render(Context context) {
        if (renderedView == null) {
            renderedView = LayoutInflater.from(context).inflate(customViewResourceId, null);
        }
        isClickable = renderedView.isClickable();

        return renderedView;
    }
}
