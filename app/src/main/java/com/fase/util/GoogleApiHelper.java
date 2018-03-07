package com.fase.util;

public class GoogleApiHelper /*implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks*/ {

    // TODO: possible inject to activity. Required AppContext
    // TODO: Usage - connect onStart() activity, disconnect before super.onStop().

//    private Context mContext;
//    private GoogleApiClient mGoogleApiClient;
//    private int connectionsCount = 0;
//
//    public GoogleApiHelper(Context context) {
//        this.mContext = context;
//        buildGoogleApiClient();
//        connect();
//    }
//
//    public GoogleApiClient getGoogleApiClient() {
//        return this.mGoogleApiClient;
//    }
//
//    public void connect() {
//        connectionsCount++;
//        if (connectionsCount == 1) {
//            if (mGoogleApiClient != null) {
//                mGoogleApiClient.connect();
//            }
//        }
//    }
//
//    public void disconnect() {
//        connectionsCount--;
//        if (connectionsCount == 0) {
//            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                mGoogleApiClient.disconnect();
//            }
//        }
//    }
//
//    public boolean isConnected() {
//        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
//    }
//
//    private void buildGoogleApiClient() {
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(mContext.getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        Timber.i("Google API Client connected");
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Timber.d("onConnectionSuspended: googleApiClient.connect()");
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Timber.d("onConnectionFailed: connectionResult = " + connectionResult.toString());
//    }
//
//    public boolean checkPlayServices(BaseMvpView view) {
//        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(view.getActivity());
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(view.getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                view.showError("This device is not supported.");
//            }
//            return false;
//        }
//        return true;
//    }
}
