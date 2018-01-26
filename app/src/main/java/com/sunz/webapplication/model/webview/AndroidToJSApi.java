package com.sunz.webapplication.model.webview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.sunz.webapplication.config.Config;
import com.sunz.webapplication.ui.activity.HomeActivity;
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
                        if (aBoolean){
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        }
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
                            dialog.setMessage("下载安装包");
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
                                    if (activity!=null)
                                        FunctionUtils.installNormal(activity,file.getPath());
                                }

                                @Override
                                public void onError() {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onProgress(float progress, long total) {
                                    dialog.setProgress((int) (progress*100));
                                }
                            });
                        }
                    }
                });
    }

    @JavascriptInterface
    public void sweepQrCode(){
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            Intent intent = new Intent(activity, CaptureActivity.class);
                            activity.startActivityForResult(intent, activity.SWEEP_QRCODE);
                        }
                    }
                });

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

    }
}
