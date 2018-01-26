package com.sunz.webapplication.model.webview;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.sunz.webapplication.ui.activity.HomeActivity;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class MyWebChromeClient extends WebChromeClient{
    private ValueCallback uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private HomeActivity activity;

    public MyWebChromeClient(HomeActivity activity) {
        this.activity = activity;
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        uploadMessage = valueCallback;
        activity.openImageChooserActivity();
    }

    // For Android  >= 3.0
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        uploadMessage = valueCallback;
        activity.openImageChooserActivity();
    }

    //For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        uploadMessage = valueCallback;
        activity.openImageChooserActivity();
    }

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        uploadMessageAboveL = filePathCallback;
        activity.openImageChooserActivity();
        return true;
    }

    public ValueCallback getUploadMessage() {
        return uploadMessage;
    }

    public ValueCallback<Uri[]> getUploadMessageAboveL() {
        return uploadMessageAboveL;
    }

    public void setUploadMessage(ValueCallback uploadMessage) {
        this.uploadMessage = uploadMessage;
    }

    public void setUploadMessageAboveL(ValueCallback<Uri[]> uploadMessageAboveL) {
        this.uploadMessageAboveL = uploadMessageAboveL;
    }
}
