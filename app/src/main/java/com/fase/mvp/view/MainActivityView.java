package com.fase.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.fase.base.BaseView;
import com.fase.model.element.Screen;
import com.fase.model.service.ElementsUpdate;

public interface MainActivityView extends BaseView {

    void render(Screen screen);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void updateElement(ElementsUpdate elementsUpdate);
}
