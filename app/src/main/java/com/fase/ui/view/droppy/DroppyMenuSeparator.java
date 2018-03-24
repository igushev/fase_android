package com.fase.ui.view.droppy;

import android.content.Context;
import android.view.View;

import com.fase.ui.view.droppy.views.DroppyMenuSeparatorView;

public class DroppyMenuSeparator extends DroppyMenuItemAbstract {

    public DroppyMenuSeparator() {
        type = TYPE_MENU_SEPARATOR;
        setId(-1);
        isClickable = false;
    }

    @Override
    public View render(Context context) {
        if (renderedView == null) {
            renderedView = new DroppyMenuSeparatorView(context);
        }

        return renderedView;
    }
}
