package com.fase.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.fase.BuildConfig;
import com.fase.FaseApp;
import com.fase.base.BasePresenter;
import com.fase.core.api.GeneralErrorHandler;
import com.fase.model.RequestContactDataHolder;
import com.fase.model.UpdateValue;
import com.fase.model.data.Contact;
import com.fase.model.data.Locale;
import com.fase.model.service.ElementCallback;
import com.fase.model.service.ElementsUpdate;
import com.fase.model.service.Response;
import com.fase.model.service.ScreenUpdate;
import com.fase.mvp.view.MainActivityView;
import com.fase.util.DownloadRawFileUtil;
import com.fase.util.FileUtils;
import com.fase.util.RxUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

@SuppressLint("CheckResult")
@InjectViewState
public class MainActivityPresenter extends BasePresenter<MainActivityView> {

    private boolean ignoreUpdates = false;
    private final GeneralErrorHandler mErrorHandler = new GeneralErrorHandler(getViewState(),
            () -> ignoreUpdates = false,
            () -> initScreen(true),
            () -> initScreen(false));

    public void initScreen(boolean restart) {
        if (restart) {
            mDataManager.setCurrentSessionId(null);
            ignoreUpdates = true;
        }
        Single<Response> screenRequest = TextUtils.isEmpty(mDataManager.getCurrentSessionId()) ? mDataManager.getService() : mDataManager.getScreen();
        screenRequest
                .flatMap(this::processResources)
                .map(storeHeadersData())
                .compose(getBasicNetworkFlowSingleTransformer(true))
                .subscribe(response -> {
                    getViewState().hideProgress();
                    getViewState().render(response);
                    ignoreUpdates = false;
                }, mErrorHandler);
    }

    public void elementCallback(List<String> idList, String method, Boolean requestLocale) {
        ignoreUpdates = true;
        mDataManager.getUpdateValues()
                .flatMap(updateValues -> {
                    ElementCallback elementCallback = new ElementCallback();
                    elementCallback.setDevice(mDataManager.getDevice());
                    elementCallback.setMethod(method);
                    elementCallback.setIdList(idList);

                    if (updateValues != null && !updateValues.isEmpty()) {
                        ElementsUpdate elementsUpdate = new ElementsUpdate();
                        ArrayList<List<String>> idListList = new ArrayList<>();
                        ArrayList<String> valuesList = new ArrayList<>();

                        for (UpdateValue updateValue : updateValues) {
                            idListList.add(updateValue.getIdList());
                            valuesList.add(updateValue.getValue());
                        }
                        elementsUpdate.setIdListList(idListList);
                        elementsUpdate.setValueList(valuesList);

                        elementCallback.setElementUpdate(elementsUpdate);
                    }

                    if (requestLocale != null && requestLocale) {
                        elementCallback.setLocale(getLocale());
                    }

                    return mDataManager.elementCallback(elementCallback)
                            .flatMap(this::processResources)
                            .flatMap(response -> Single.zip(Observable.fromIterable(updateValues == null ? new ArrayList<>() : updateValues)
                                    .flatMap(updateValue -> mDataManager.deleteIfEquals(updateValue.getElementId(), updateValue.getValue())
                                            .toObservable())
                                    .toList(), Single.just(response), (booleans, response1) -> response1));
                })
                .map(storeHeadersData())
                .compose(getBasicNetworkFlowSingleTransformer(true))
                .subscribe(response -> {
                    getViewState().hideProgress();
                    getViewState().render(response);
                    ignoreUpdates = false;
                }, mErrorHandler);
    }

    public Disposable intiElementUpdates() {
        return Observable.interval(BuildConfig.ELEMENT_UPDATE_TIME, BuildConfig.ELEMENT_UPDATE_TIME, TimeUnit.SECONDS)
                .filter(timer -> !ignoreUpdates)
                .flatMap(aLong -> {
                    if (TextUtils.isEmpty(mDataManager.getCurrentScreenId()) || TextUtils.isEmpty(mDataManager.getCurrentSessionId())) {
                        Timber.d("No screen and session params, job skipped");
                        return Observable.just(new Response());
                    }

                    return mDataManager.getUpdateValues()
                            .flatMap(updateValues -> {
                                ScreenUpdate screenUpdate = new ScreenUpdate();
                                screenUpdate.setDevice(mDataManager.getDevice());
                                if (updateValues != null && !updateValues.isEmpty()) {
                                    ElementsUpdate elementsUpdate = new ElementsUpdate();
                                    ArrayList<List<String>> idListList = new ArrayList<>();
                                    ArrayList<String> valuesList = new ArrayList<>();

                                    for (UpdateValue updateValue : updateValues) {
                                        idListList.add(updateValue.getIdList());
                                        valuesList.add(updateValue.getValue());
                                    }
                                    elementsUpdate.setIdListList(idListList);
                                    elementsUpdate.setValueList(valuesList);

                                    screenUpdate.setElementsUpdate(elementsUpdate);
                                }
                                return mDataManager.screenUpdate(screenUpdate)
                                        .onErrorReturnItem(new Response())
                                        .flatMap(this::processResources)
                                        .flatMap(response -> {
                                            if (response.getScreenInfo() == null) {
                                                return Single.just(response);
                                            }
                                            return Single.zip(Observable.fromIterable(updateValues == null ? new ArrayList<>() : updateValues)
                                                    .flatMap(updateValue -> mDataManager.deleteIfEquals(updateValue.getElementId(), updateValue.getValue())
                                                            .toObservable())
                                                    .toList(), Single.just(response), (booleans, response1) -> response1);
                                        });
                            }).toObservable();
                }).map(storeHeadersData())
                .compose(RxUtil.applyIoAndMainSchedulers())
                .onErrorReturnItem(new Response())
                .subscribe(response -> getViewState().render(response),
                        throwable -> {
                            Timber.e(throwable, "Error getting data");
                            getViewState().showError("Error getting screen data");
                        });
    }

    @NonNull
    private Function<Response, Response> storeHeadersData() {
        return response -> {
            if (response.getScreenInfo() != null) {
                mDataManager.setCurrentScreenId(response.getScreenInfo().getScreenId());
            }
            if (response.getSessionInfo() != null) {
                mDataManager.setCurrentSessionId(response.getSessionInfo().getSessionId());
            }
            if (response.getVersionInfo() != null) {
                mDataManager.setVersion(response.getVersionInfo().getVersion());
            }
            return response;
        };
    }

    public void clearValueUpdates() {
        mDataManager.clearValueUpdates()
                .doOnSubscribe(disposable -> getCompositeDisposable().add(disposable))
                .compose(RxUtil.applyCompletableIoAndMainSchedulers())
                .subscribe(() -> Timber.i("UpdateValues cleared"), throwable -> Timber.e(throwable, "UpdateValues clearing error"));
    }

    private SingleSource<? extends Response> processResources(Response response) {
        return Single.just(response)
                .flatMap(result -> {
                    if (result.getResources() != null && result.getResources().isResetResources()) {
                        return mDataManager.deleteResources()
                                .flatMap(aBoolean -> Single.just(response));
                    } else {
                        return Single.just(response);
                    }
                })
                .flatMap((Function<Response, SingleSource<Response>>) result -> {
                    if (result.getResources() != null && result.getResources().getResourceList() != null && !result.getResources().getResourceList().isEmpty()) {
                        return Single.zip(Single.just(response), Observable.fromIterable(result.getResources().getResourceList())
                                .concatMap(resource -> mDataManager.isResourceExist(resource.getFilename())
                                        .flatMap(isExist -> {
                                            File cache = FileUtils.createDefaultCacheDir(FaseApp.getApplication().getApplicationContext(), "images");
                                            String filename = resource.getFilename().substring(resource.getFilename().lastIndexOf("/") + 1);
                                            return isExist ? Single.just(new File(cache, filename))
                                                    : DownloadRawFileUtil.saveRawToFile(mDataManager.getResource(resource.getFilename()), new File(cache, filename));
                                        })
                                        .flatMap(file -> RxUtil.toSingle(mDataManager.putResourceToDb(resource.getFilename(), file.getPath())))
                                        .toObservable())
                                .toList(), (resultResponse, isAddedList) -> resultResponse);
                    } else {
                        return Single.just(response);
                    }
                });
    }

    private Locale getLocale() {
        TelephonyManager telephonyManager = (TelephonyManager) FaseApp.getApplication().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String iso = null;
        if (telephonyManager != null) {
            iso = telephonyManager.getNetworkCountryIso();
        }

        if (iso == null) {
            iso = FaseApp.getApplication().getResources().getConfiguration().locale.getCountry();
        }

        return new Locale(iso.toUpperCase());
    }

    public void pickContact(RequestContactDataHolder mRequestContactDataHolder, Contact contact) {
        mDataManager.putValueUpdate(mRequestContactDataHolder.getElementId(), mRequestContactDataHolder.getIdList(), new Gson().toJson(contact))
                .toObservable()
                .doOnSubscribe(disposable -> getCompositeDisposable().add(disposable))
                .compose(RxUtil.applyIoAndMainSchedulers())
                .onErrorReturn(throwable -> false)
                .subscribe(result -> elementCallback(mRequestContactDataHolder.getIdList(), mRequestContactDataHolder.getMethod(), mRequestContactDataHolder.getRequestLocale()));
    }
}
