package com.versionlib.view;

import android.app.Application;

import com.versionlib.modal.Builder;
import com.versionlib.presents.UpdateHelper;
import com.versionlib.presents.viewinface.OnCheckVersion;
import com.versionlib.presents.viewinface.OnVersionUpdate;

import org.jetbrains.annotations.NotNull;

/**
 * Created by siwei.zhao on 2017/8/10.<br>
 * 版本检查更新会在网络正常连接的状态下去检查新版本更新，下载新版本文件失败后就不会再次下载，除非重新调用checkNewVersion去检查版本更新<br>
 *<br>
 * 回调实现步骤：<br>
 * 实现步骤分为2部分：<br>
 * 1.Http请求版本信息，并检查是否需要进行更新<br>
 * 2.需要进行更新的时候就会进行更新以及更新相关的UI操作<br>
 *<br>
 * 回调生命周期：<br>
 * onInitNewVersion(异步线程)：请求版本信息成功会运行该方法，需要你解析版本信息并放入到NewVersionInfo中返回<br>
 * onCheckVersionCode(异步线程)和onCheckVersionName(异步线程)：根据提供的新旧版本号和版本名，判断是否需要进行更新<br>
 * onShowUpdateDialog(UI线程):在非自动更新状态下会执行该方法区弹窗提示新版本信息，否则不会执行，而是直接下载新版本文件<br>
 * onDownloadNewVersionPre(UI线程)：在下载新版本程序之前调用该方法<br>
 * onDownloadNewVersion(UI线程)：正在下载新版本程序文件的时候回调用该方法<br>
 * onDownloadFaild(UI线程):在下载过程中下载失败会调用该方法<br>
 * onInstallAppPre(UI线程):在安装新版本程序之前调用，在该方法中安装新版本程序<br>
 */

public abstract class VersionHelper extends UpdateHelper implements OnVersionUpdate, OnCheckVersion{

    public VersionHelper(@NotNull Application context, @NotNull Builder builder) {
        super(context, builder);
        builder.setOnVersionUpdate(VersionHelper.this);
        builder.setOnCheckVersion(VersionHelper.this);
    }

}
