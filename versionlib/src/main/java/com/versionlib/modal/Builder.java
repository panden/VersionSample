package com.versionlib.modal;

import com.versionlib.presents.viewinface.OnCheckVersion;
import com.versionlib.presents.viewinface.OnVersionUpdate;

/**
 * Created by siwei.zhao on 2017/8/9.
 */

public class Builder {

    private boolean mWifiAutoUpdate;//WiFi自动更新
    private boolean mNetAutoUpdate;//使用流量下进行自动更新
    private HttpBuilder mHttpBuilder;//检查版本更新的地址
    private OnCheckVersion mOnCheckVersion;
    private OnVersionUpdate mOnVersionUpdate;
    private String newApkPath;//新版本程序安装地址

    /**设置版本更新http请求信息*/
    public Builder setHttpBuilder(HttpBuilder httpBuilder) {
        mHttpBuilder = httpBuilder;
        return this;
    }

    /**设置检查版本更新callback*/
    public Builder setOnCheckVersion(OnCheckVersion onCheckVersion) {
        mOnCheckVersion = onCheckVersion;
        return this;
    }

    /**设置版本更新callback*/
    public Builder setOnVersionUpdate(OnVersionUpdate onVersionUpdate) {
        mOnVersionUpdate = onVersionUpdate;
        return this;
    }

    /**设置是否在WiFi状态下去进行自动更新*/
    public Builder setWifiAutoUpdate(boolean wifiAutoUpdate) {
        this.mWifiAutoUpdate = wifiAutoUpdate;
        return this;
    }


    public boolean isNetAutoUpdate() {
        return mNetAutoUpdate;
    }

    /**设置是否有新版本自动更新*/
    public Builder setAutoUpdate(boolean autoUpdate){
        mNetAutoUpdate=autoUpdate;
        if(autoUpdate) mWifiAutoUpdate =autoUpdate;
        return this;
    }

    public boolean isWifiAutoUpdate() {
        return mWifiAutoUpdate;
    }

    public HttpBuilder getHttpBuilder() {
        return mHttpBuilder;
    }

    public OnCheckVersion getOnCheckVersion() {
        return mOnCheckVersion;
    }

    public OnVersionUpdate getOnVersionUpdate() {
        return mOnVersionUpdate;
    }

    public String getNewApkPath() {
        return newApkPath;
    }

    /**新版本程序保存地址*/
    public Builder setNewApkPath(String newApkPath) {
        this.newApkPath = newApkPath;
        return this;
    }
}
