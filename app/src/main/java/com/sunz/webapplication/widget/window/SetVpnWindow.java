package com.sunz.webapplication.widget.window;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.sunz.webapplication.R;

/**
 * Created by jiayazhou on 2018/1/27.
 */

public class SetVpnWindow {

    private PopupWindow popupWindow;
    private Context context;
    private EditText et_vpnurl,et_username,et_userpass;
    private Button bt_vpnlg;

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
        bt_vpnlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }
}
