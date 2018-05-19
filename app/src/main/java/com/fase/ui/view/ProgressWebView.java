package com.fase.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fase.R;
import com.fase.ui.render.ViewRenderer;
import com.fase.util.NetworkUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ProgressWebView extends LinearLayout {

    @BindView(R.id.progressBar)
    ProgressBar vProgressBar;
    @BindView(R.id.webView)
    WebView vWebView;

    private ViewRenderer.RendererCallback mRendererCallback;

    public ProgressWebView(Context context) {
        super(context);
    }

    public ProgressWebView(Context context, ViewRenderer.RendererCallback callback) {
        super(context);
        this.mRendererCallback = callback;
        LayoutInflater.from(context).inflate(R.layout.view_progress_webview, this, true);
        ButterKnife.bind(this);

        WebSettings webSettings = vWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);

        vWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && vProgressBar.getVisibility() == ProgressBar.GONE) {
                    vProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
                vProgressBar.setProgress(progress);
                if (progress == 100) {
                    vProgressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        vWebView.setWebViewClient(new WebViewClient() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Timber.e("WebView error: %s", error.toString());
                mRendererCallback.showError("WebView error: " + error.toString());
            }

            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Timber.e("WebView error: " + errorCode + " : " + description);
                mRendererCallback.showError("WebView error: " + errorCode + " : " + description);
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void disableScrollable() {
        vWebView.setOnTouchListener((v, event) -> (event.getAction() == MotionEvent.ACTION_MOVE));
        vWebView.setVerticalScrollBarEnabled(false);
        vWebView.setHorizontalScrollBarEnabled(false);
    }

    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.contains("http")) {
                mRendererCallback.showError("Wrong web url format. Url must have http:// or https://");
            } else {
                vWebView.post(() -> NetworkUtil.checkNetworkWithRetryDialog(getContext(), () -> vWebView.loadUrl(url)));
            }
        }
    }
}
