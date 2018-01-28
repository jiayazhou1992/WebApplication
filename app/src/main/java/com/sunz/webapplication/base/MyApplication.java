package com.sunz.webapplication.base;

import android.app.Application;

import com.topsec.sslvpn.IVPNHelper;
import com.topsec.sslvpn.datadef.BaseConfigInfo;
import com.topsec.sslvpn.datadef.BaseModule;
import com.topsec.sslvpn.datadef.WorkModel;
import com.topsec.sslvpn.lib.VPNService;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/1/15 0015.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        ZXingLibrary.initDisplayOpinion(this);
        IVPNHelper ivpnHelper = VPNService.getVPNInstance(getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//              .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

}
