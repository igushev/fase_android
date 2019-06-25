package com.fase.ui.viewHolder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivityVH {

    public SwipeRefreshLayout vSwipeRefreshLayout;
    public FrameLayout vContentContainer;
    public LinearLayout vNavigationMenuLayout;
    public FloatingActionButton vFloatingActionButton;
    public FrameLayout vButtonFirst;
    public ImageView vButtonFirstImage;
    public TextView vButtonFirstText;
    public View vToolbarShadow;
    public LinearLayout vBottomSheet;

    public MainActivityVH(SwipeRefreshLayout vSwipeRefreshLayout, FrameLayout vContentContainer, LinearLayout vNavigationMenuLayout, FloatingActionButton vFloatingActionButton,
                          FrameLayout vButtonFirst, ImageView vButtonFirstImage, TextView vButtonFirstText, View vToolbarShadow, LinearLayout bottomSheet) {
        this.vSwipeRefreshLayout = vSwipeRefreshLayout;
        this.vContentContainer = vContentContainer;
        this.vNavigationMenuLayout = vNavigationMenuLayout;
        this.vFloatingActionButton = vFloatingActionButton;
        this.vButtonFirst = vButtonFirst;
        this.vButtonFirstImage = vButtonFirstImage;
        this.vButtonFirstText = vButtonFirstText;
        this.vToolbarShadow = vToolbarShadow;
        this.vBottomSheet = bottomSheet;
    }
}
