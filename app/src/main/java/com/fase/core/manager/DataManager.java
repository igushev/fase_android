package com.fase.core.manager;

import com.fase.BuildConfig;
import com.fase.model.UpdateValue;
import com.fase.model.service.Command;
import com.fase.model.service.Device;
import com.fase.model.service.ElementCallback;
import com.fase.model.service.Response;
import com.fase.model.service.ScreenUpdate;
import com.fase.model.service.Status;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public class DataManager {

    private static volatile DataManager mSingleton;

    private SharedPrefManager mSharedPrefManager;
    private ApiManager mApiManager;
    private DbManager mDbManager;

    public static synchronized DataManager getInstance() {
        if (mSingleton == null) {
            synchronized (DataManager.class) {
                if (mSingleton == null) {
                    mSingleton = new DataManager();
                }
            }
        }
        return mSingleton;
    }

    private DataManager() {
        mSharedPrefManager = SharedPrefManager.getInstance();
        mApiManager = ApiManager.getInstance();
        mDbManager = DbManager.getInstance();
    }

    public Device getDevice() {
        Device device = new Device();
        device.setDeviceToken("Mock"); // FIXME
        device.setDeviceType(BuildConfig.DEVICE_TYPE);
        return device;
    }

    /**
     * Shared preferences methods
     */
    public void setDeviceToken(String deviceToken) {
        mSharedPrefManager.setDeviceToken(deviceToken);
    }

    public String getDeviceToken() {
        return mSharedPrefManager.getDeviceToken();
    }

    public void setCurrentScreenId(String screenId) {
        mSharedPrefManager.setCurrentScreenId(screenId);
    }

    public String getCurrentScreenId() {
        return mSharedPrefManager.getCurrentScreenId();
    }

    public void setCurrentSessionId(String sessionId) {
        mSharedPrefManager.setCurrentSessionId(sessionId);
    }

    public String getCurrentSessionId() {
        return mSharedPrefManager.getCurrentSessionId();
    }

    /**
     * Api methods
     */
    public Single<Response> getService() {
        return mApiManager.getService(getDevice());
    }

    public Single<Command> sendInternalCommand() {
        return mApiManager.sendInternalCommand();
    }

    public Single<Status> sendServiceCommand(Command command) {
        return mApiManager.sendServiceCommand(command);
    }

    public Single<Response> getScreen() {
        return mApiManager.getScreen(getCurrentSessionId(), getDevice());
    }

    public Single<Response> screenUpdate(ScreenUpdate screenUpdate) {
        return mApiManager.screenUpdate(getCurrentSessionId(), getCurrentScreenId(), screenUpdate);
    }

    public Single<Response> elementСallback(ElementCallback elementCallback) {
        return mApiManager.elementСallback(getCurrentSessionId(), getCurrentScreenId(), elementCallback);
    }

    public Single<ResponseBody> getResource(String fileName) {
        return mApiManager.getResource(fileName);
    }

    /**
     * Db methods
     */
    public Maybe<String> getResourcePath(String fileName) {
        return mDbManager.getResourcePath(fileName);
    }

    public Completable putResourceToDb(String fileName, String filePath) {
        return mDbManager.putResourceToDb(fileName, filePath);
    }

    public Single<Boolean> isResourceExist(String fileName) {
        return mDbManager.isResourceExist(fileName);
    }

    public Single<Boolean> deleteResources() {
        return mDbManager.getResourcesFilePathList()
                .flatMap(strings -> Observable.fromIterable(strings)
                        .map(filePath -> {
                            new File(filePath).delete();
                            return filePath;
                        }).toList())
                .flatMap(filePath -> mDbManager.deleteResources());
    }

    public Single<Boolean> putValueUpdate(String elementId, List<String> idList, String value) {
        return mDbManager.putValueUpdate(elementId, idList, value);
    }

    public Single<Boolean> deleteIfEquals(String elementId, String value) {
        return mDbManager.deleteIfEquals(elementId, value);
    }

    public Single<List<UpdateValue>> getUpdateValues() {
        return mDbManager.getUpdateValues();
    }

    public Completable clearValueUpdates() {
        return mDbManager.clearValueUpdates();
    }
}
