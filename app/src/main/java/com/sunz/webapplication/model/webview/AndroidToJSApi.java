package com.sunz.webapplication.model.webview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.sunz.webapplication.config.Config;
import com.sunz.webapplication.ui.activity.HomeActivity;
import com.sunz.webapplication.utils.ACache;
import com.sunz.webapplication.utils.FunctionUtils;
import com.sunz.webapplication.widget.http.HttpHelp;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/1/14 0014.
 */

public class AndroidToJSApi {

    private HomeActivity activity;
    private RxPermissions rxPermissions;

    public AndroidToJSApi(HomeActivity activity) {
        this.activity = activity;
        rxPermissions = new RxPermissions(activity);
    }

    /**打电话
     * @param phone
     */
    @JavascriptInterface
    public void call(final String phone) {
        rxPermissions.request(Manifest.permission.CALL_PHONE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

    }

    @JavascriptInterface
    public void download(final String path){

        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            final ProgressDialog dialog = new ProgressDialog(activity);
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            if (path.endsWith(".apk"))
                                dialog.setMessage("下载安装包");
                            else
                                dialog.setMessage("下载文件");
                            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    OkHttpUtils.getInstance().cancelTag(Config.tag_download);

                                }
                            });
                            dialog.setMax(100);
                            dialog.show();
                            HttpHelp.download(path, new HttpHelp.OnHttpCallback2<File>() {
                                @Override
                                public void ok(File file) {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "下载成功", Toast.LENGTH_SHORT).show();
                                    if (activity != null&&path.endsWith(".apk"))
                                        FunctionUtils.installNormal(activity, file.getPath());
                                }

                                @Override
                                public void onError() {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onProgress(float progress, long total) {
                                    dialog.setProgress((int) (progress * 100));
                                }
                            });
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @JavascriptInterface
    public String sweepQrCode(){
        final ACache aCache = ACache.get(activity);
        rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.VIBRATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent(activity, CaptureActivity.class);
                            activity.startActivityForResult(intent, activity.SWEEP_QRCODE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        aCache.put(Config.aceche_Qrcode,"-1");
                    }
                });
        String qrcode = null;
        while (true){
            qrcode = aCache.getAsString(Config.aceche_Qrcode);
            if (!TextUtils.isEmpty(qrcode)){
                if (qrcode.equals("-1"))
                    qrcode = null;
                aCache.remove(Config.aceche_Qrcode);
                break;
            }
        }
        return qrcode;
    }

    @JavascriptInterface
    public int getVersion(){
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @JavascriptInterface
    public String getVersionName(){
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @JavascriptInterface
    public void clearCache(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.clearCache();
            }
        });
    }

    @JavascriptInterface
    public String getPosition(){
        activity.satrtLocation();
        ACache aCache = ACache.get(activity);
        String location = null;
        for (int i = 0;i<10;i++){
            location = aCache.getAsString(Config.aceche_lastLocation);
            if (!TextUtils.isEmpty(location)){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return location;
    }
}
