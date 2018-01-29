package com.sunz.webapplication.ui.activity;

import android.Manifest;
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
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
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
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.sunz.webapplication.R;
import com.sunz.webapplication.config.Config;
import com.sunz.webapplication.model.webview.AndroidToJSApi;
import com.sunz.webapplication.model.webview.MyWebChromeClient;
import com.sunz.webapplication.presenter.HomePresenter;
import com.sunz.webapplication.service.MessageService;
import com.sunz.webapplication.utils.ACache;
import com.sunz.webapplication.widget.window.SetVpnWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.topsec.sslvpn.IVPNHelper;
import com.topsec.sslvpn.lib.VPNService;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import io.reactivex.functions.Consumer;

public class HomeActivity extends AppCompatActivity {

    private String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    private final int FILE_CHOOSER_RESULT_CODE = 1;//选择文件
    public final int SWEEP_QRCODE = 2;//扫描二维码

    private HomePresenter homePresenter;
    private WebView webView;
    private AndroidToJSApi androidToJSApi;
    private MyWebChromeClient myWebChromeClient;

    private LocationClient locationClient;
    private IVPNHelper ivpnHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.setLocOption(initLocationClientOption());
        locationClient.registerLocationListener(new MyLocationListener());
        homePresenter = new HomePresenter(this);
        //ivpnHelper = VPNService.getVPNInstance(getApplicationContext());
        if (ivpnHelper!=null)
            ivpnHelper.startService();

        webView = (WebView) findViewById(R.id.home_webview);
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        initWebviewSettings(webView);
        initWebViewClient(webView);
        initWebChromeClient(webView);
        initDownloadListener(webView);
        addAndroidToJSApi(webView);

        /*webView.post(new Runnable() {
            @Override
            public void run() {
                homePresenter.showVpn(webView, new SetVpnWindow.OnClickListener() {
                    @Override
                    public void onCancle() {
                        webView.loadUrl(Config.home_url);
                    }

                    @Override
                    public void onOk() {
                        webView.loadUrl(Config.home_url);
                    }
                });

            }
        });*/
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
        settings.setGeolocationEnabled(true);
    }

    private void initWebViewClient(final WebView webView){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

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
                //开始载入页面调用的
                if (!url.equals(Config.home_url)) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookieStr = cookieManager.getCookie(url);
                    Log.i("activity", "--------" + cookieStr);
                    Intent intent = new Intent();
                    intent.setClass(HomeActivity.this, MessageService.class);
                    intent.putExtra("cookie",cookieStr);
                    startService(intent);
                    //homePresenter.showVpn(webView);
                }
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

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin,true,false);
                Log.i("location","----------------3");
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        };
        webView.setWebChromeClient(myWebChromeClient);
    }

    private void initDownloadListener(WebView webView){
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                androidToJSApi.download(url);
            }
        });
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

        //locationClient.disableAssistantLocation();
        if (locationClient.isStarted())
            locationClient.stop();
        if (SetVpnWindow.isLogin&&ivpnHelper!=null) {
            ivpnHelper.logoutVOne();
            SetVpnWindow.isLogin = false;
        }
        if (ivpnHelper!=null)
            ivpnHelper.closeService();
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
        if (ivpnHelper!=null)
            ivpnHelper.toGrantStartVpnService(requestCode);
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
            ACache aCache = ACache.get(HomeActivity.this);
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    aCache.put(Config.aceche_Qrcode,"-1");
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    aCache.put(Config.aceche_Qrcode,result);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                    aCache.put(Config.aceche_Qrcode,"-1");
                }
            }else {
                aCache.put(Config.aceche_Qrcode,"-1");
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
    private static final int BACK_PRESSED_INTERVAL = 3000;
    //重写onBackPressed()方法,继承自退出的方法
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()&&System.currentTimeMillis()- currentBackPressedTime > BACK_PRESSED_INTERVAL){
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

    //初始化百度
    private LocationClientOption initLocationClientOption(){
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("gcj02");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(10000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        return option;
    }

    private class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation!= null){
                ACache aCache = ACache.get(HomeActivity.this);
                Gson gson = new Gson();
                String location = gson.toJson(bdLocation);
                Log.i("location",location);
                aCache.put(Config.aceche_lastLocation,location);
                locationClient.stop();
            }
        }
    }

    public void satrtLocation(){
        RxPermissions rxPermissions = new RxPermissions(HomeActivity.this);
        rxPermissions.request(PERMISSIONS_CONTACT)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            Log.i("location","-----------1");
                            //locationClient.enableAssistantLocation(webView);
                            if (!locationClient.isStarted())
                                locationClient.start();
                            //ivpnHelper.startService(HomeActivity.this,null);
                        }else {
                            Log.i("location","-----------2");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
