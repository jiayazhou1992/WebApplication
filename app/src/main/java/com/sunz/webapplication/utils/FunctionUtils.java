package com.sunz.webapplication.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/1/23 0023.
 */

public class FunctionUtils {

    public static void installNormal(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(apkPath);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.sunz.webapplication.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 02. * 返回app运行状态
     * 03. * 1:程序在前台运行
     * 04. * 2:程序在后台运行
     * 05. * 3:程序未启动
     * 06. * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     * 07.
     */
    public static int[] getAppSatus(Context context, String pageName) {
        int[] result = new int[2];
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);

        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            result[0] = 1;
            return result;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    result[0] = 2;
                    result[1] = info.id;
                    return result;
                }
            }
            result[0] = 3;
            return result;//栈里找不到，返回3
        }
    }

    public static void openApp(Context context, String pageName){
        int[] staus = getAppSatus(context,pageName);
        switch (staus[0]){
            case 1:

                break;
            case 2:
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE) ;
                List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
                am.moveTaskToFront(staus[1], ActivityManager.MOVE_TASK_WITH_HOME);
                break;
            case 3:
                Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pageName);
                context.startActivity(LaunchIntent);
                break;
        }
    }

}
