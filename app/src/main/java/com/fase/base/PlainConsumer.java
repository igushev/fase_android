package com.fase.base;

import io.reactivex.functions.Consumer;

public interface PlainConsumer<T> extends Consumer<T> {

    @Override
    void accept(T t);
}
