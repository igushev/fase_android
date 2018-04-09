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

    public void initScreen() {
        ignoreUpdates = false;
        Single<Response> screenRequest = TextUtils.isEmpty(mDataManager.getCurrentSessionId()) ? mDataManager.getService() : mDataManager.getScreen();
        screenRequest
                .flatMap(this::processResources)
                .map(storeSessionAndScreenIds())
                .compose(getBasicNetworkFlowSingleTransformer(true))
                .subscribe(response -> {
                    getViewState().hideProgress();
                    getViewState().render(response);
                }, new GeneralErrorHandler(getViewState(), null, this::initScreen));
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

                    return mDataManager.elementСallback(elementCallback)
                            .flatMap(this::processResources)
                            .flatMap(response -> Single.zip(Observable.fromIterable(updateValues == null ? new ArrayList<>() : updateValues)
                                    .flatMap(updateValue -> mDataManager.deleteIfEquals(updateValue.getElementId(), updateValue.getValue())
                                            .toObservable())
                                    .toList(), Single.just(response), (booleans, response1) -> response1));
                })
                .map(storeSessionAndScreenIds())
                .compose(getBasicNetworkFlowSingleTransformer(true))
                .subscribe(response -> {
                    getViewState().hideProgress();
                    getViewState().render(response);
                    ignoreUpdates = false;
                }, new GeneralErrorHandler(getViewState(), () -> ignoreUpdates = false, () -> elementCallback(idList, method, requestLocale)));
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
                }).map(storeSessionAndScreenIds())
                .compose(RxUtil.applyIoAndMainSchedulers())
                .subscribe(response -> getViewState().render(response),
                        throwable -> {
                            Timber.e(throwable, "Error getting data");
                            getViewState().showError("Error getting screen data");
                        });
    }

    @NonNull
    private Function<Response, Response> storeSessionAndScreenIds() {
        return response -> {
            if (response.getScreenInfo() != null) {
                mDataManager.setCurrentScreenId(response.getScreenInfo().getScreenId());
            }
            if (response.getSessionInfo() != null) {
                mDataManager.setCurrentSessionId(response.getSessionInfo().getSessionId());
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
        if (response.getResources() != null && response.getResources().getResourceList() != null && !response.getResources().getResourceList().isEmpty()) {
            return Single.zip(Single.just(response), Observable.fromIterable(response.getResources().getResourceList())
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

        //            ApiManager.callUrl("http://ip-api.com/json", new ApiCallback<String>() {
//                @Override
//                public void onCompleted(String s) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(s);
//                        iso = jsonObject.getString("countryCode");
////                        city = jsonObject.getString("addressName");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    refreshCountryByIso();
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    super.onError(throwable);
//                }
//            });

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
