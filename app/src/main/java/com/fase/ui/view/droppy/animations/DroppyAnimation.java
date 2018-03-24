package com.fase.ui.view.droppy.animations;

import android.view.View;

import com.fase.ui.view.droppy.DroppyMenuPopup;
import com.fase.ui.view.droppy.views.DroppyMenuPopupView;

public interface DroppyAnimation {
    void animateShow(final DroppyMenuPopupView popup, final View anchor);

    void animateHide(final DroppyMenuPopup popup, final DroppyMenuPopupView popupView, final View anchor, final boolean itemSelected);
}
