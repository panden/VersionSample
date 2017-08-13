package com.versionlib.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

/**
 * Created by siwei.zhao on 2017/7/28.
 * WiFi状态监听广播
 */

public class WiFiStateReciver extends BroadcastReceiver {

    private static WiFiStateReciver mWiFiStateReciver;

    public static boolean registerBroadcast(@NotNull Context context, @NotNull OnWiFiStateListener listener){
        if(mWiFiStateReciver!=null || context==null)return false;
        mWiFiStateReciver=new WiFiStateReciver(context, listener);
        IntentFilter intent=new IntentFilter();
        intent.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mWiFiStateReciver, intent);
        return true;
    }

    public static boolean unRegisterBroadcast(){
        if(mWiFiStateReciver==null)return false;
        mWiFiStateReciver.mContext.unregisterReceiver(mWiFiStateReciver);
        return true;
    }

    private Context mContext;
    private OnWiFiStateListener mStateListener;

    private WiFiStateReciver(@NotNull Context context, @NotNull OnWiFiStateListener listener){
        this.mContext=context;
        this.mStateListener=listener;
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        System.out.println("action="+action);
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
            checkWiFiState(context, intent);
        }
    }

    private void checkWiFiState(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mStateListener != null)
            mStateListener.onWiFiStateChanged(wifiInfo.getState()== NetworkInfo.State.CONNECTED, wifiInfo.isConnected(), mobileInfo.isConnected());
    }

    public static interface OnWiFiStateListener{

        /**当网络连接状态发生改变
         * @param wifiNetConnected WiFi是否连接
         * @param mobileNetConnected 网络是否正常使用
         * */
        void onWiFiStateChanged(boolean wifiConnected, boolean wifiNetConnected, boolean mobileNetConnected);
    }
}
