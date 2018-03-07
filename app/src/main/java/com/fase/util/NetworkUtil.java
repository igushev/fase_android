package com.fase.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.fase.R;
import com.fase.ui.fragment.dialog.AlertDialogFragment;

public class NetworkUtil {

    public static boolean hasInternetConnectivity(@NonNull Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void checkNetworkWithRetryDialog(@NonNull final Context context, @NonNull final Runnable callback) {
        if (!hasInternetConnectivity(context)) {
            AlertDialogFragment.newInstance(context.getString(R.string.message_no_internet),
                    context.getString(R.string.retry), (dialog, which) -> new Handler(Looper.getMainLooper())
                            .postDelayed(() -> checkNetworkWithRetryDialog(context, callback), 500))
                    .show(((AppCompatActivity) context).getSupportFragmentManager());
        } else {
            callback.run();
        }
    }
}
