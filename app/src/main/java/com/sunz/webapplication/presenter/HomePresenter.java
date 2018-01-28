package com.sunz.webapplication.presenter;

import android.view.View;

import com.sunz.webapplication.ui.activity.HomeActivity;
import com.sunz.webapplication.widget.window.SetVpnWindow;

/**
 * Created by Administrator on 2018/1/14 0014.
 */

public class HomePresenter {

    private HomeActivity activity;

    public HomePresenter(HomeActivity activity) {
        this.activity = activity;
    }

    public void showVpn(View view,SetVpnWindow.OnClickListener onClickListener){
        SetVpnWindow setVpnWindow = new SetVpnWindow(activity);
        setVpnWindow.show(view);
        setVpnWindow.setOnCancleListener(onClickListener);
    }

}
