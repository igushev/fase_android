package com.fase.util;

import com.fase.FaseApp;
import com.fase.base.BasePresenter;
import com.fase.base.BaseView;
import com.fase.model.exception.NoNetworkException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

    public static void safeUnSubscribe(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public static void safeUnSubscribe(CompositeDisposable compositeDisposable) {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

    public static <T> ObservableTransformer<T, T> applyIoAndMainSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> applySingleIoAndMainSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static CompletableTransformer applyCompletableIoAndMainSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> MaybeTransformer<T, T> applyMaybeIoAndMainSchedulers() {
        return tObservable -> tObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Compose actions for network calls:
     * - check network state (if no network - dispose chain and throws exception, that will be handled by GeneralErrorHandler with Retry dialog)
     * - add Disposable to CompositeDisposable in presenter instance
     * - showProgress on subscribe
     * - observeOn MainThread (RxJava2CallAdapterFactory for retrofit already created with Schedulers.io(), so no need to set it)
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * !!! Be sure that you add .subscribeOn(Schedulers.io()) to chain for noRetrofit actions and hide progress in the onComplete call!!!
     * !!! Be sure that you add hideProgress() to onNext or to onComplete call!!!
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */
    public static <T> ObservableTransformer<T, T> basicNetworkFlowObservableTransformer(@NonNull BasePresenter presenter, @NonNull BaseView view, boolean showProgress) {
        return upstream -> upstream.doOnSubscribe(disposable -> doOnSubscribe(presenter, view, showProgress, disposable))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static CompletableTransformer basicNetworkFlowCompletableTransformer(@NonNull BasePresenter presenter, @NonNull BaseView view, boolean showProgress) {
        return upstream -> upstream.doOnSubscribe(disposable -> doOnSubscribe(presenter, view, showProgress, disposable))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> basicNetworkFlowSingleTransformer(@NonNull BasePresenter presenter, @NonNull BaseView view, boolean showProgress) {
        return upstream -> upstream.doOnSubscribe(disposable -> doOnSubscribe(presenter, view, showProgress, disposable))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static void doOnSubscribe(@NonNull BasePresenter presenter, @NonNull BaseView view, boolean showProgress, Disposable disposable) throws NoNetworkException {
        disposeAndThrowNoNetwork(disposable);
        presenter.getCompositeDisposable().add(disposable);
        if (showProgress) {
            view.showProgress();
        }
    }

    public static <T> ObservableTransformer<T, T> addObservableToCompositeDisposable(@NonNull BasePresenter presenter) {
        return upstream -> upstream.doOnSubscribe(disposable ->
                presenter.getCompositeDisposable().add(disposable));
    }

    public static CompletableTransformer addCompletableToCompositeDisposable(@NonNull BasePresenter presenter) {
        return upstream -> upstream.doOnSubscribe(disposable ->
                presenter.getCompositeDisposable().add(disposable));
    }

    public static <T> SingleTransformer<T, T> addSingleToCompositeDisposable(@NonNull BasePresenter presenter) {
        return upstream -> upstream.doOnSubscribe(disposable ->
                presenter.getCompositeDisposable().add(disposable));
    }

    public static <T> ObservableTransformer<T, T> checkNetworkConnectionObservable() {
        return upstream -> upstream.doOnSubscribe(RxUtil::disposeAndThrowNoNetwork);
    }

    public static CompletableTransformer checkNetworkConnectionCompletable() {
        return upstream -> upstream.doOnSubscribe(RxUtil::disposeAndThrowNoNetwork);
    }

    public static <T> SingleTransformer<T, T> checkNetworkConnectionSingle() {
        return upstream -> upstream.doOnSubscribe(RxUtil::disposeAndThrowNoNetwork);
    }

    private static void disposeAndThrowNoNetwork(Disposable disposable) throws NoNetworkException {
        if (!NetworkUtil.hasInternetConnectivity(FaseApp.getApplication())) {
            disposable.dispose();
            throw new NoNetworkException("No network connection");
        }
    }

    /**
     * Receives a {@link Completable} and returns a {@link Single<Boolean>} that emits <code>true</code>
     * if the <code>Completable</code> completes successful, <code>false</code> otherwise.
     * An action to be performed in case or error can also be passed through the argument
     * <code>exceptionConsumer</code>.
     *
     * @param completable       The input {@link Completable}
     * @param exceptionConsumer A consumer to process the exception (eg log it) in case the {@link Completable}
     *                          terminates with an error.
     * @return The {@link Single<Boolean>}, as described above.
     */
    public static Single<Boolean> toSingle(@NonNull Completable completable,
                                           @Nullable Consumer<? super Throwable> exceptionConsumer) {
        return completable.doOnError(exceptionConsumer == null ? throwable -> {
        } : exceptionConsumer)
                .toSingleDefault(true)
                .onErrorReturnItem(false);
    }

    public static Single<Boolean> toSingle(@NonNull Completable completable) {
        return toSingle(completable, null);
    }
}
