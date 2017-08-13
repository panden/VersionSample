package com.versionlib.presents.viewinface;

import com.versionlib.modal.NewVersionInfo;

import org.jetbrains.annotations.NotNull;

/**
 * Created by siwei.zhao on 2017/7/28.
 */

public interface OnCheckVersion {

    /**根据请求的地址解析出新版本信息*/
    @NotNull NewVersionInfo onInitNewVersion(String httpResult);

    /**根据app版本号去减产版本更新*/
    boolean onCheckVersionCode(int newVersionCode, int appVersionCode);

    /**根据版本名去检验版本更新*/
    boolean onCheckVersionName(String newVersionName, String appVersionName);
}
