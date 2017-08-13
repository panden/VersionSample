package sample.version.com.versionsample;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;

import com.versionlib.modal.Builder;
import com.versionlib.modal.HttpBuilder;
import com.versionlib.modal.NewVersionInfo;
import com.versionlib.modal.VersinInfo;
import com.versionlib.modal.http.HttpAsyncTask;
import com.versionlib.presents.UpdateHelper;
import com.versionlib.view.VersionHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by siwei.zhao on 2017/8/10.
 */

public class VersionSample extends VersionHelper {

    private static VersionSample sVersionSample;

    public static void createHelper(Application app){
        if(sVersionSample==null){
            HttpBuilder httpBuilder=new HttpBuilder()
                    .setMethod(HttpAsyncTask.Method.Get)//请求方法，get/post
                    .setTimeOut(15*1000)//设置请求超时时间
                    .setUrl("http://www.netac.com/wifi/I370/APK/version.txt");//新版更新的地址
                    //.setValues();//配置请求需要携带的参数
            Builder builder=new Builder()
                    .setHttpBuilder(httpBuilder).setWifiAutoUpdate(true);
                    //设置自动更新，只要有新版本就会更新
                    //.setAutoUpdate(true);
                    //设置新版app下载存放的地址
                    //Context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/apk/apk文件名"
                    //.setNewApkPath("path")
                    //设置wifi下是否自动更新，已开启自动更新，则wifi自动更新也会被开启；当开启自动更新后，
                    // 就不会回调onShowUpdateDialog而是直接去自动下载和安装
                    //.setWifiAutoUpdate(true)
                    //设置检查是否进行版本更新回调，在VersionHelper中已进行设置，就不要重复设置了
                    //.setOnCheckVersion(null)
                    //设置版本更新相关UI操作回调
                    //.setOnVersionUpdate(null);

            sVersionSample=new VersionSample(app, builder);
        }
    }

    public static VersionSample getInstance(Activity activity){
        sVersionSample.mActivity=activity;
        return sVersionSample;
    }

    private Activity mActivity;


    private VersionSample(@NotNull Application context,@NotNull Builder builder) {
        super(context, builder);
    }

    @NotNull
    @Override
    public NewVersionInfo onInitNewVersion(String httpResult) {
        NewVersionInfo versionInfo=null;
        try {
            JSONObject jsonObject=new JSONObject(httpResult);
            versionInfo=new NewVersionInfo();
            JSONArray array=jsonObject.getJSONArray("version");
            jsonObject=array.getJSONObject(0);
            versionInfo.setVersionName(jsonObject.optString("version"));
            //versionInfo.setVersionCode(jsonObject.optInt("version_code"));
            //versionInfo.setMustUpdate(jsonObject.optBoolean("justUpdate"));
            //versionInfo.setUpdateContent(jsonObject.optString("content"));
            versionInfo.setDownloadUrl(jsonObject.getString("url"));
            versionInfo.setMd5(jsonObject.getString("md5"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return versionInfo;
    }

    @Override
    public boolean onCheckVersionCode(int newVersionCode, int appVersionCode) {
        return newVersionCode>appVersionCode;
    }

    @Override
    public boolean onCheckVersionName(String newVersionName, String appVersionName) {
        //V1.X.X.X; 转换成1XXXX的数字，然后再去比对版本名大小
        int newCode= 0;
        int appCode= 0;
        try {
            //replace在对小数点的时候，需要转意
            newCode = Integer.parseInt(newVersionName.toLowerCase().replaceAll("v","").replaceAll("\\.", ""));
            appCode = Integer.parseInt(appVersionName.toLowerCase().replaceAll("v","").replaceAll("\\.", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return newCode>appCode;
    }

    @Override
    public void onShowUpdateDialog(NewVersionInfo newVersionInfo, VersinInfo versinInfo, UpdateHelper helper) {
        //展示新版本更新内容，更新时调用onDialogUpdate去进行更新；不更新调用onDialogCancleUpdate去取消更新
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle("检测到版本更新")
                .setMessage(String.format("检测到新版本：%s\n更新内容：%s", newVersionInfo.getVersionName(), newVersionInfo.getUpdateContent()))
                .setNegativeButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogUpdate();
            }
        }).setNeutralButton("忽略", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogCancleUpdate();
            }
        }).create().show();
    }

    @Override
    public void onDownloadNewVersionPre(NewVersionInfo newVersion, long fileSize, String downloadPath) {
        System.out.println("开启下载新版App......");
    }

    @Override
    public void onDownloadNewVersion(NewVersionInfo newVersion, long fileSize, long downloadSize, String downloadPath) {
        System.out.println(String.format("开启下载 文件大小:%s 已下载：%s",
                Formatter.formatFileSize(mActivity, fileSize),
                Formatter.formatFileSize(mActivity, downloadSize)));
    }

    @Override
    public void onDownloadFaild(int faildCode) {
        //下载失败后尝试重新检查版本更新
        //checkNewVersion();
        System.out.println("下载失败...");
    }

    @Override
    public void onInstallAppPre(NewVersionInfo newVersionInfo, VersinInfo versinInfo, String appPath) {
        //安装前最好能比对下文件md5和接口返回的md5是否一致，防止服务器被攻击导致文件被替换
        installNewVersion(mActivity, appPath);
    }

    /**安装新版本程序*/
    public void installNewVersion(Activity activity, String apkFile){
        System.out.println("installNewVersion apkFile="+apkFile);
        Intent intent=new Intent(Intent.ACTION_VIEW);
        Uri uri=null;
        File file=new File(apkFile);
        System.out.println("file="+file);
        if(!file.exists())return;

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri= FileProvider.getUriForFile(activity.getApplicationContext(), "sample.version.com.versionlib.fileProvider", file);
        }else{
            uri=Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        activity.startActivity(intent);
    }
}
