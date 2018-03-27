package com.fase.core.service;

import com.fase.core.manager.DataManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

public class FaseIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        DataManager.getInstance().setDeviceToken(refreshedToken);
        Timber.d("FCM token " + refreshedToken);
    }
}
