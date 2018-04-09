package com.fase.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fase.ui.activity.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import timber.log.Timber;

public class GoogleApiHelper implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private int connectionsCount = 0;

    public GoogleApiHelper(Context context) {
        this.mContext = context;
        buildGoogleApiClient();
        connect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    public void connect() {
        connectionsCount++;
        if (connectionsCount == 1) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void disconnect() {
        connectionsCount--;
        if (connectionsCount == 0) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    public boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.i("Google API Client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended: googleApiClient.connect()");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d("onConnectionFailed: connectionResult = " + connectionResult.toString());
    }

    public boolean checkPlayServices(MainActivity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                activity.showError("This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
