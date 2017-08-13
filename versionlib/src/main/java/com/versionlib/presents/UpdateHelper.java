package com.versionlib.presents;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.versionlib.modal.Builder;
import com.versionlib.modal.NewVersionInfo;
import com.versionlib.modal.VersinInfo;
import com.versionlib.modal.http.DownloadAsyncTask;
import com.versionlib.modal.http.HttpAsyncTask;
import com.versionlib.presents.viewinface.UpdateView;
import com.versionlib.reciver.WiFiStateReciver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by siwei.zhao on 2017/7/28.
 */

public class UpdateHelper implements UpdateView, WiFiStateReciver.OnWiFiStateListener {

    private Context mContext;
    private VersinInfo mAppVersin;
    private NewVersionInfo mNewVersion;
    private Builder mBuilder;
    private WiFiStateReciver mWiFiStateReciver;
    private UpdateInfo mUpdate;
    private boolean mCanDownload;//能进行下载
    private boolean mCanCheckUpdate;//能进行版本检查
    private HttpAsyncTask mAsyncHttpTask;
    private DownloadAsyncTask mAsyncDownloadTask;

    public UpdateHelper(@NotNull Application context, @NotNull Builder builder){
        try {
            this.mContext=context;
            this.mBuilder=builder;
            mAppVersin =new VersinInfo();
            mUpdate=new UpdateInfo();
            PackageInfo pi=mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            mAppVersin.setVersionCode(pi.versionCode);
            mAppVersin.setVersionName(pi.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void checkNewVersion() {
        //检查版本更新
        mCanCheckUpdate=false;
        if(!WiFiStateReciver.registerBroadcast(mContext, UpdateHelper.this)){
            httpCheckVersion();
        }

    }

    @Override
    public void onDialogUpdate() {
        //进行新版本更新
        mAsyncHttpTask =null;//释放
        mAsyncDownloadTask=new DownloadAsyncTask(this);
        String downLoadPath=mBuilder.getNewApkPath();
        if(downLoadPath==null){
            String[] splites=mNewVersion.getDownloadUrl().split("/");
            downLoadPath= mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/apk/"+splites[splites.length-1];
//            downLoadPath= mContext.getFilesDir()+"/Download/apk/"+splites[splites.length-1];
            downLoadPath=downLoadPath.replace("file://","");
        }
        System.out.println("downLoadPath="+downLoadPath);
        mAsyncDownloadTask.execute(mNewVersion.getDownloadUrl(), downLoadPath);
    }

    @Override
    public void onDialogCancleUpdate() {
        mAsyncHttpTask =null;//释放
    }

    @Override
    public void cancleUpdate() {
        if(mAsyncHttpTask!=null){
            mAsyncHttpTask.cancel(true);
            mAsyncHttpTask=null;
        }

        if(mAsyncDownloadTask!=null){
            mAsyncDownloadTask.cancel(true);
            mAsyncDownloadTask=null;
        }

        WiFiStateReciver.unRegisterBroadcast();
    }

    public Builder getBuilder() {
        return mBuilder;
    }



    @Override
    public void onWiFiStateChanged(boolean wifiConnected, boolean wifiNetConnected, boolean mobileNetConnected) {
        if(mBuilder.isWifiAutoUpdate())mCanDownload =wifiNetConnected;
        else mCanDownload=mobileNetConnected || wifiNetConnected;
        System.out.println(String.format("onWiFiStateChanged mCanDownload=%s", mCanDownload));
        if(mCanDownload && !mCanCheckUpdate){//网络正常连接且未获取到版本信息
            httpCheckVersion();
        }
    }

    //线程去检查版本更新
    private void httpCheckVersion(){
        if(mAsyncHttpTask !=null) mAsyncHttpTask.cancel(true);
        mAsyncHttpTask =new HttpAsyncTask(UpdateHelper.this);
        mAsyncHttpTask.execute(mBuilder.getHttpBuilder());
    }

    public void setCanDownload(boolean canDownload) {
        mCanDownload = canDownload;
    }

    public boolean isCanCheckUpdate() {
        return mCanCheckUpdate;
    }

    public void setCanCheckUpdate(boolean canCheckUpdate) {
        mCanCheckUpdate = canCheckUpdate;
    }

    public boolean isCanDownload() {
        return mCanDownload;
    }

    public UpdateInfo getUpdate() {
        return mUpdate;
    }

    public boolean hasNewVersion(){
        return mUpdate.shouldUpdate();
    }

    public @Nullable NewVersionInfo getNewVersion(){
        return mNewVersion;
    }

    public VersinInfo getAppVersin() {
        return mAppVersin;
    }

    public void setNewVersion(NewVersionInfo newVersion) {
        mNewVersion = newVersion;
    }




    public class UpdateInfo{

        private boolean codeUpdate;
        private boolean codeNameUpdate;

        public boolean isCodeUpdate() {
            return codeUpdate;
        }

        public void setCodeUpdate(boolean codeUpdate) {
            this.codeUpdate = codeUpdate;
        }

        public boolean isCodeNameUpdate() {
            return codeNameUpdate;
        }

        public void setCodeNameUpdate(boolean codeNameUpdate) {
            this.codeNameUpdate = codeNameUpdate;
        }

        public boolean shouldUpdate(){
            return codeUpdate || codeNameUpdate;
        }

    }

}
