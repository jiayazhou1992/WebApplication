package com.sunz.webapplication.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.sunz.webapplication.R;
import com.sunz.webapplication.model.bean.NewMessageBean;
import com.sunz.webapplication.ui.activity.HomeActivity;
import com.sunz.webapplication.utils.ACache;
import com.sunz.webapplication.widget.http.HttpHelp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/25 0025.
 */

public class MessageService extends Service {

    private boolean isStart = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(intent, flags, startId);
        String cookie = intent.getStringExtra("cookie");
        Log.i("messageservice", "--------" + isStart);
        if (!TextUtils.isEmpty(cookie)&&!isStart) {
            //cookie = cookie.replace("JSESSIONID=","");
            cookie = "CSS=undefined;"+cookie;
            Map<String, String> heads = new HashMap<>();
            heads.put("cookie", cookie);
            //heads.put("accept","*/*");
            //heads.put("referer","xxx");
            //heads.put("accept-language","zh-cn");
            //heads.put("content-type","application/x-www-form-urlencoded,text/javascript");
            //heads.put("accept-encoding","gzip, deflate");
            heads.put("X-Requested-With", "XMLHttpRequest");
            HttpHelp.getNewMessages(heads, new HashMap<String, String>(), new HttpHelp.OnHttpCallback<NewMessageBean>() {
                @Override
                public void ok(NewMessageBean newMessageBean) {
                    showNotification(newMessageBean);
                }

                @Override
                public void onError() {

                }
            });
            isStart = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void showNotification(NewMessageBean newMessageBean){
        Log.i("showNotification","1-----------showNotification");
        if (newMessageBean!=null&&newMessageBean.getData()!=null&&newMessageBean.getSuccess()) {
            ACache aCache = ACache.get(getApplicationContext());
            //NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int i = 100;
            Log.i("showNotification","2-----------showNotification");
            for (NewMessageBean.DataBean dataBean : newMessageBean.getData()) {
                if (aCache.getAsObject(dataBean.getId())==null&&(TextUtils.isEmpty(dataBean.getReadtime()))||dataBean.getReadtime().equals("null")) {//
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    builder.setContentTitle(dataBean.getMsgtitle());
                    builder.setSmallIcon(R.mipmap.icon);
                    builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.icon));
                    builder.setContentText(dataBean.getMsgcontent());
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    builder.setAutoCancel(true);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,new Intent(MessageService.this, HomeActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    manager.notify(i, builder.build());
                    Log.i("showNotification","3-----------showNotification");
                    aCache.put(dataBean.getId(), dataBean);
                }
                i++;
            }
        }
    }
}
