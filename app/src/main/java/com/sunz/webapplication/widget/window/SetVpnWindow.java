package com.sunz.webapplication.widget.window;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sunz.webapplication.R;
import com.topsec.sslvpn.IVPNHelper;
import com.topsec.sslvpn.datadef.BaseAccountInfo;
import com.topsec.sslvpn.datadef.BaseConfigInfo;
import com.topsec.sslvpn.datadef.BaseModule;
import com.topsec.sslvpn.datadef.WorkModel;
import com.topsec.sslvpn.lib.VPNService;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jiayazhou on 2018/1/27.
 */

public class SetVpnWindow {

    private PopupWindow popupWindow;
    private Context context;
    private EditText et_vpnurl,et_username,et_userpass;
    private Button bt_vpnlg,bt_cancle;
    private OnClickListener onCancleListener;
    public static boolean isLogin = false;

    public SetVpnWindow(Context context) {
        this.context = context;
        initWidodw();
    }

    private void initWidodw(){
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        View rootView = initView();
        popupWindow.setContentView(rootView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (context!=null&&context instanceof AppCompatActivity){
                    AppCompatActivity activity = (AppCompatActivity) context;
                    WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                    params.alpha = 1.0f;
                    activity.getWindow().setAttributes(params);
                    if (onCancleListener!=null)
                        onCancleListener.onCancle();
                }
            }
        });
    }

    private View initView(){
        View rootView = LayoutInflater.from(context).inflate(R.layout.window_vpn_set,null);
        et_vpnurl = (EditText) rootView.findViewById(R.id.et_vpnurl);
        et_username = (EditText) rootView.findViewById(R.id.et_username);
        et_userpass = (EditText) rootView.findViewById(R.id.et_userpass);
        bt_vpnlg = (Button) rootView.findViewById(R.id.bt_vpnlg);
        bt_cancle = (Button) rootView.findViewById(R.id.bt_cancle);
        bt_vpnlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkHttp(et_vpnurl.getText().toString())){
                    Toast.makeText(context,"请输入正确的vpn地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_username.getText().toString().trim())){
                    Toast.makeText(context,"请输入的账号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_userpass.getText().toString().trim())){
                    Toast.makeText(context,"请输入的账号密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginVpn(et_vpnurl.getText().toString(),et_username.getText().toString(),et_userpass.getText().toString());
            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        return rootView;
    }

    public void show(View view){
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        if (context!=null&&context instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity) context;
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.alpha = 0.5f;
            activity.getWindow().setAttributes(params);
        }
    }

    public void hide(){
        popupWindow.dismiss();
        if (onCancleListener!=null){
            onCancleListener.onCancle();
        }
    }

    private boolean checkHttp(String url){
        if (TextUtils.isEmpty(url)) return false;
        if (!Patterns.WEB_URL.matcher(url).matches()) return false;
        return true;
    }

    private void loginVpn(String url,String name,String passWord){
        final IVPNHelper ivpnHelper = VPNService.getVPNInstance(context);
//        BaseConfigInfo baseConfigInfo = new BaseConfigInfo();
//        baseConfigInfo.m_blAutoReConnect=true;
//        baseConfigInfo.m_iRetryCount = 10;
//        baseConfigInfo.m_iTimeOut = 5;
//        baseConfigInfo.m_iEnableModule = BaseModule.SSLVPN_NETACCESS;
//        baseConfigInfo.m_iWorkMode = WorkModel.WORKMODE_DEFAULT;
//        ivpnHelper.setConfigInfo(baseConfigInfo);
        final BaseAccountInfo accountInfo = new BaseAccountInfo();
        //accountInfo.m_iAuthType = 0;
        //accountInfo.m_iLoginType = 1;
        accountInfo.m_strAccount= name;
        accountInfo.m_strCerPasswd = passWord;
        accountInfo.m_strCerPath=url;

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int i= -1;
                i = ivpnHelper.loginVOne(accountInfo);
                emitter.onNext(i);
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                int i=integer.intValue();
                Log.e("loginVOne","-----------"+i);
                if (i == 0) {
                    hide();
                    Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show();
                    isLogin = true;
                    if (onCancleListener != null)
                        onCancleListener.onOk();
                }else {
                    isLogin = false;
                    Toast.makeText(context,"登陆失败",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

    }

    public void setOnCancleListener(SetVpnWindow.OnClickListener onCancleListener) {
        this.onCancleListener = onCancleListener;
    }

    public static interface OnClickListener{
        void onCancle();
        void onOk();
    }
}
