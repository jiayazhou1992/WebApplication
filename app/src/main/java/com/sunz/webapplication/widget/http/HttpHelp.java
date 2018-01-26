package com.sunz.webapplication.widget.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sunz.webapplication.config.Config;
import com.sunz.webapplication.model.bean.NewMessageBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/15 0015.
 */

public class HttpHelp {

    public static void download(String path, final OnHttpCallback2<File> onHttpCallback){
        String[] strings = path.split("/");
        String fileName = strings[strings.length-1];
        OkHttpUtils.get().url(path).tag(Config.tag_download).build().execute(new FileCallBack(Config.getFileDownLoad().getPath(),fileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (onHttpCallback!=null){
                    onHttpCallback.onError();
                }
            }

            @Override
            public void onResponse(File response, int id) {
                if (onHttpCallback!=null){
                    onHttpCallback.ok(response);
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (onHttpCallback!=null){
                    onHttpCallback.onProgress(progress,total);
                }
                super.inProgress(progress, total, id);
            }
        });
    }

    public static void getNewMessages(final Map<String,String> heads, final Map<String,String> params, final OnHttpCallback<NewMessageBean> callback){

        Observable.interval(1,30, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Long, Response>() {
                    @Override
                    public Response apply(@NonNull Long upstream) {
                        try {
                            Response response = OkHttpUtils.getInstance().get().url(Config.message_url).headers(heads).params(params).build().execute();
                            return response;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response o) throws Exception {
                        if (o!=null&&o.body()!=null){
                            //Log.i("httphelp","1-------"+o.body().string());
                            Gson gson = new Gson();
                            NewMessageBean newMessageBean=null;
                            try {
                                newMessageBean = gson.fromJson(o.body().string(), NewMessageBean.class);
                            }catch (JsonSyntaxException e){
                                Log.e("gson","------"+e.getMessage());
                            }
                            if (callback!=null)
                                callback.ok(newMessageBean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("httphelp","2-------"+throwable.getMessage()+throwable.getCause());
                        if (callback!=null)
                            callback.onError();
                    }
                });
    }

    public static interface OnHttpCallback<T>{
        void ok(T t);
        void onError();
    }

    public static interface OnHttpCallback2<T>{
        void ok(T t);
        void onError();
        void onProgress(float progress, long total);
    }
}
