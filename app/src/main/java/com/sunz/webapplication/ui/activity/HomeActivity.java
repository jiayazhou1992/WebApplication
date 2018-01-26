package com.sunz.webapplication.ui.activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.sunz.webapplication.R;
import com.sunz.webapplication.config.Config;
import com.sunz.webapplication.model.webview.AndroidToJSApi;
import com.sunz.webapplication.model.webview.MyWebChromeClient;
import com.sunz.webapplication.service.MessageService;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class HomeActivity extends AppCompatActivity {

    private final int FILE_CHOOSER_RESULT_CODE = 1;//选择文件
    public final int SWEEP_QRCODE = 2;//扫描二维码

    private WebView webView;
    private AndroidToJSApi androidToJSApi;
    private MyWebChromeClient myWebChromeClient;

    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        webView = (WebView) findViewById(R.id.home_webview);
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        initWebviewSettings(webView);
        initWebViewClient(webView);
        initWebChromeClient(webView);
        addAndroidToJSApi(webView);
        webView.loadUrl(Config.home_url);

    }

    private void initWebviewSettings(WebView webView){
        WebSettings settings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        settings.setJavaScriptEnabled(true);

        //支持插件
        //settings.setPluginsEnabled(true);

        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        settings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        settings.setDomStorageEnabled(true);
    }

    private void initWebViewClient(final WebView webView){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //开始载入页面调用的
                if (!url.equals(Config.home_url)) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookieStr = cookieManager.getCookie(url);
                    Log.i("activity", "--------" + cookieStr);
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, MessageService.class);
                    intent.putExtra("cookie",cookieStr);
                    startService(intent);
                }
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = "tel:";
                if (url.startsWith(scheme)){
                    if (url.length()==15){
                        String phone = url.substring(scheme.length());
                        androidToJSApi.call(phone);
                    }
                }else if (url.endsWith(".apk")){
                    androidToJSApi.download(url);
                }else {
                    webView.loadUrl(url);
                }
                Log.i("web","----------"+url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                locationClient.enableAssistantLocation(webView);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }
        });
    }

    private void initWebChromeClient(WebView webView){
        myWebChromeClient = new MyWebChromeClient(this){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        };
        webView.setWebChromeClient(myWebChromeClient);
    }

    private void addAndroidToJSApi(WebView webView){
        androidToJSApi = new AndroidToJSApi(this);
        webView.addJavascriptInterface(androidToJSApi,"app");
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        locationClient.disableAssistantLocation();
        super.onDestroy();
    }

    public void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == myWebChromeClient.getUploadMessage() && null == myWebChromeClient.getUploadMessageAboveL()) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (myWebChromeClient.getUploadMessageAboveL() != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (myWebChromeClient.getUploadMessage() != null) {
                myWebChromeClient.getUploadMessage().onReceiveValue(result);
                myWebChromeClient.setUploadMessage(null);
            }
        }else if (requestCode == SWEEP_QRCODE&&resultCode == RESULT_OK){
            //处理扫描结果
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || myWebChromeClient.getUploadMessageAboveL() == null)
            return;
        Uri[] results = null;
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        myWebChromeClient.getUploadMessageAboveL().onReceiveValue(results);
        myWebChromeClient.setUploadMessageAboveL(null);
    }

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;
    //重写onBackPressed()方法,继承自退出的方法
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
            return;
        }
        // 判断时间间隔
        if (System.currentTimeMillis()- currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
        } else {
            // 退出
            finish();
        }
    }

    private class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

        }
    }
}
