package com.fase.core.manager;

import com.fase.BuildConfig;
import com.fase.model.service.Command;
import com.fase.model.service.Device;
import com.fase.model.service.ElementCallback;
import com.fase.model.service.Response;
import com.fase.model.service.ScreenUpdate;
import com.fase.model.service.Status;

import io.reactivex.Single;
import okhttp3.ResponseBody;

public class DataManager {

    private static volatile DataManager mSingleton;

    private SharedPrefManager mSharedPrefManager;
    private ApiManager mApiManager;

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

    /**
     * Api methods
     */
    public Single<Response> getService() {
        Device device = new Device();
        device.setDeviceToken("Mock"); // FIXME
        device.setDeviceType(BuildConfig.DEVICE_TYPE);
        return mApiManager.getService(device);
    }

    public Single<Command> sendInternalCommand() {
        return mApiManager.sendInternalCommand();
    }

    public Single<Status> sendServiceCommand(Command command) {
        return mApiManager.sendServiceCommand(command);
    }

    public Single<Response> getScreen(String sessionId, Device device) {
        return mApiManager.getScreen(sessionId, device);
    }

    public Single<Response> screenUpdate(String sessionId, String screenId, ScreenUpdate screenUpdate) {
        return mApiManager.screenUpdate(sessionId, screenId, screenUpdate);
    }

    public Single<Response> screenUpdate(String sessionId, String screenId, ElementCallback elementCallback) {
        return mApiManager.screenUpdate(sessionId, screenId, elementCallback);
    }

    public Single<ResponseBody> getResource(String fileName) {
        return mApiManager.getResource(fileName);
    }
}
