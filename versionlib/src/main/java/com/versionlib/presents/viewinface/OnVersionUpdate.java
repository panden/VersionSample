package com.versionlib.presents.viewinface;

import com.versionlib.modal.NewVersionInfo;
import com.versionlib.modal.VersinInfo;
import com.versionlib.presents.UpdateHelper;

/**
 * Created by siwei.zhao on 2017/7/28.
 */

public interface OnVersionUpdate {

    /**弹出提示更新信息*/
    void onShowUpdateDialog(NewVersionInfo newVersionInfo, VersinInfo versinInfo, UpdateHelper helper);

    /**在开始自动下载新版本之前*/
    void onDownloadNewVersionPre(NewVersionInfo newVersion, long fileSize, String downloadPath);

    /**正在自动下载新版本程序的时候*/
    void onDownloadNewVersion(NewVersionInfo newVersion, long fileSize, long downloadSize, String downloadPath);

    /**自动下载失败*/
    void onDownloadFaild(int faildCode);

    /**用户安装新版程序前*/
    void onInstallAppPre(NewVersionInfo newVersionInfo, VersinInfo versinInfo, String appPath);
}
