package com.fase.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.fase.R;
import com.fase.base.BaseFragment;
import com.fase.mvp.presenter.EmptyFragmentPresenter;
import com.fase.mvp.view.EmptyFragmentView;

public class EmptyFragment extends BaseFragment implements EmptyFragmentView {

    @InjectPresenter
    EmptyFragmentPresenter mPresenter;

    public static EmptyFragment newInstance() {
        return new EmptyFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_empty;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
