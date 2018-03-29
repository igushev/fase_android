package com.fase.mvp.view;

import com.fase.base.BaseView;
import com.fase.model.service.Response;

public interface MainActivityView extends BaseView {

    void render(Response response);
}
