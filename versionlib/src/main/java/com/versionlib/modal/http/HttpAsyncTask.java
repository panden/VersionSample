package com.versionlib.modal.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Build;

import com.versionlib.modal.HttpBuilder;
import com.versionlib.modal.HttpResult;
import com.versionlib.modal.NewVersionInfo;
import com.versionlib.modal.VersinInfo;
import com.versionlib.presents.UpdateHelper;
import com.versionlib.presents.viewinface.OnCheckVersion;
import com.versionlib.presents.viewinface.OnVersionUpdate;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;

import static com.versionlib.modal.HttpResult.HTTP_STATUS_ERROR;
import static com.versionlib.modal.HttpResult.HTTP_STATUS_FAILD;
import static com.versionlib.modal.HttpResult.HTTP_STATUS_SUCCESS;

/**
 * Created by siwei.zhao on 2017/7/28.
 * Http请求异步任务
 */

public class HttpAsyncTask extends AsyncTask<HttpBuilder, Void, UpdateHelper>{

    private UpdateHelper mUpdateHelper;

    public HttpAsyncTask(@NotNull UpdateHelper updateHelper){
        this.mUpdateHelper =updateHelper;
    }

    @Override
    protected UpdateHelper doInBackground(@NotNull HttpBuilder... params) {
        HttpBuilder builder=params[0];
        HttpResult result = httpRequest(builder.getUrl(), builder.getMethod(), builder.getValues());
        if(result.getStausCode()!=HTTP_STATUS_SUCCESS)return null;
        OnCheckVersion checkVersion=mUpdateHelper.getBuilder().getOnCheckVersion();
        if(checkVersion!=null){
            NewVersionInfo newVersionInfo=checkVersion.onInitNewVersion(result.getResult());
            if(newVersionInfo==null)return null;
            VersinInfo appVersion=mUpdateHelper.getAppVersin();
            mUpdateHelper.setNewVersion(newVersionInfo);
            mUpdateHelper.getUpdate().setCodeUpdate(checkVersion.onCheckVersionCode(newVersionInfo.getVersionCode(), appVersion.getVersionCode()));
            System.out.println(newVersionInfo.getVersionName()+"  |   "+appVersion.getVersionName());
            mUpdateHelper.getUpdate().setCodeNameUpdate(checkVersion.onCheckVersionName(newVersionInfo.getVersionName(), appVersion.getVersionName()));
            mUpdateHelper.setCanCheckUpdate(true);
        }
        return mUpdateHelper;
    }

    @Override
    protected void onPostExecute(UpdateHelper result) {
        super.onPostExecute(result);
        if(result!=null && result.isCanDownload() && result.isCanCheckUpdate() && result.hasNewVersion()){
            OnVersionUpdate versionUpdate=result.getBuilder().getOnVersionUpdate();
            //直接去进行更新
            if(result.getBuilder().isWifiAutoUpdate() || result.getBuilder().isNetAutoUpdate()){
                result.onDialogUpdate();
            }else{//非自动更新,需要弹窗显示更新内容，用户去选择是否需要去更新
                versionUpdate.onShowUpdateDialog(result.getNewVersion(), result.getAppVersin(), result);
            }
        }

    }

    private HttpResult httpRequest(String url, Method method, ContentValues values){
        HttpResult httpResult=null;
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                httpResult=httpConnectionRequest(url, method, values);
            }else{
                //httpClientRequest(url, method, values);
                httpResult=httpConnectionRequest(url, method, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpResult;
    }

    //Android 4.0以下使用httpClient进行请求
    private void httpClientRequest(String url,  Method method, ContentValues values){

    }

    private HttpResult httpConnectionRequest(String url, Method method, ContentValues values){
        URL url1=null;
        HttpResult httpResult=new HttpResult();
        InputStream in=null;
        ByteArrayOutputStream out=null;
        try {
            if(method==Method.Get)url1=new URL(appenGetUrl(url, values));
            else if(method==Method.Post)url1=new URL(url);
            HttpURLConnection connection= (HttpURLConnection) url1.openConnection();
            connection.setConnectTimeout(mUpdateHelper.getBuilder().getHttpBuilder().getTimeOut());
            connection.setRequestMethod(method.toString(method));
            if(method==Method.Post){
                connection.setDoInput(true);
                connection.setDoOutput(true);
                PrintWriter printWriter=new PrintWriter(connection.getOutputStream());
                printWriter.write(appendPostParams(values));
                printWriter.flush();
                printWriter.close();
            }

            connection.connect();
            httpResult.setFaildCode(connection.getResponseCode());
            if(connection.getResponseCode()==200){
                in=connection.getInputStream();
                out=new ByteArrayOutputStream();
                byte[] bs=new byte[500*1024];//500K
                ByteBuffer buffer;
                int readLength=0;
                while((readLength=in.read(bs))>0){
                    out.write(bs, 0, readLength);
                    out.flush();
                }
                String result=out.toString();
                httpResult.setStausCode(HTTP_STATUS_SUCCESS);
                httpResult.setResult(result);
            }else{
                httpResult.setStausCode(HTTP_STATUS_FAILD);
                httpResult.setResult("");
            }
        } catch (IOException e) {
            e.printStackTrace();
            httpResult.setStausCode(HTTP_STATUS_ERROR);
            httpResult.setStausCode(-1);
            httpResult.setResult("");
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpResult;
    }

    private interface HttpCallBack{

        void onCallback(HttpResult result);
    }

    private String appendPostParams(ContentValues values){
        StringBuffer buffer=null;
        if(values!=null){
            for(Map.Entry<String, Object> item : values.valueSet()){
                String key=item.getKey();
                Object valueObj=item.getValue();
                String value;

                if(valueObj instanceof byte[])value=new String(((byte[]) valueObj));
                else value=String.valueOf(valueObj);

                if(buffer==null)buffer=new StringBuffer("");
                else buffer.append("&");
                buffer.append(String.format("%s=%s", key, value));
            }
        }
        return buffer==null?"":buffer.toString();
    }

    private String appenGetUrl(String url, ContentValues values){
        StringBuffer buffer=null;
        if(values!=null){
            for(Map.Entry<String, Object> item : values.valueSet()){
                String key=item.getKey();
                Object valueObj=item.getValue();
                String value;

                if(valueObj instanceof byte[])value=new String(((byte[]) valueObj));
                else value=String.valueOf(valueObj);
                if(buffer==null){
                    buffer=new StringBuffer(url);
                    buffer.append("?");
                }else{
                    buffer.append("&");
                }
                buffer.append(String.format("%s=%s", key, value));
            }
        }
        return buffer==null?url:buffer.toString();
    }

    public static enum Method{

        Get,Post;

        public String toString(Method method) {
            int pos=compareTo(method);
            return pos==0?"GET":"POST";
        }
    }

}
