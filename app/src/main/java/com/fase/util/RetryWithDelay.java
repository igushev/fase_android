package com.fase.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {

    private final int mMaxRetries;
    private final int mRetryDelayMillis;
    private int mRetryCount;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        this.mMaxRetries = maxRetries;
        this.mRetryDelayMillis = retryDelayMillis;
        this.mRetryCount = 0;
    }

    @Override
    public Observable<?> apply(final Observable<? extends Throwable> attempts) {
        return attempts
                .flatMap((Function<Throwable, Observable<?>>) throwable -> {
                    if (++mRetryCount < mMaxRetries) {
                        return Observable.timer(mRetryDelayMillis, TimeUnit.MILLISECONDS);
                    }
                    return Observable.error(throwable);
                });
    }
}