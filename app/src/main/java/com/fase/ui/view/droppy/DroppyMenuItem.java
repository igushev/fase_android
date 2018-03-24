package com.fase.ui.view.droppy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.fase.ui.view.droppy.views.DroppyMenuItemIconView;
import com.fase.ui.view.droppy.views.DroppyMenuItemTitleView;
import com.fase.ui.view.droppy.views.DroppyMenuItemView;

public class DroppyMenuItem extends DroppyMenuItemAbstract {

    private Drawable iconDrawable;
    protected DroppyMenuItemView renderedView;

    void initMenuItem(String title, int iconResourceId) {
        this.title = title;
        if (iconResourceId > 0) {
            this.icon = iconResourceId;
        }
    }

    public DroppyMenuItem(String title) {
        initMenuItem(title, -1);
    }

    public DroppyMenuItem(String title, int iconResourceId) {
        initMenuItem(title, iconResourceId);

    }

    public void setIcon(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    @Override
    public View render(Context context) {

        renderedView = new DroppyMenuItemView(context);

        if (this.icon != -1) {
            DroppyMenuItemIconView droppyMenuItemIcon = new DroppyMenuItemIconView(context);
            droppyMenuItemIcon.setImageResource(this.icon);
            renderedView.addView(droppyMenuItemIcon);
        } else if (this.iconDrawable != null) {
            DroppyMenuItemIconView droppyMenuItemIcon = new DroppyMenuItemIconView(context);
            droppyMenuItemIcon.setImageDrawable(iconDrawable);
            renderedView.addView(droppyMenuItemIcon);
        }

        DroppyMenuItemTitleView droppyMenuItemTitle = new DroppyMenuItemTitleView(context);
        droppyMenuItemTitle.setText(this.title);
        renderedView.addView(droppyMenuItemTitle);

        return renderedView;
    }
}
