package com.versionlib.modal.http;

import android.os.AsyncTask;

import com.versionlib.presents.UpdateHelper;
import com.versionlib.presents.viewinface.OnVersionUpdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by siwei.zhao on 2017/8/9.
 */

public class DownloadAsyncTask extends AsyncTask<String, DownloadAsyncTask.FileDownloadInfo, String> {

    public static final int DOWNLOAD_BYTES_LENGTH=500*1024;//500K

    private UpdateHelper mHelper;

    public DownloadAsyncTask(UpdateHelper updateHelper){
        this.mHelper=updateHelper;
    }

    @Override
    protected String doInBackground(String... params) {
        String url=params[0];
        String filePath=params[1];
        //System.out.println("apk url="+url+" filePath="+filePath);
        if(httpDownload(url, filePath))return filePath;
        return null;
    }

    @Override
    protected void onProgressUpdate(FileDownloadInfo... values) {
        super.onProgressUpdate(values);
        FileDownloadInfo info=values[0];
        OnVersionUpdate versionUpdate=mHelper.getBuilder().getOnVersionUpdate();
        if(info.downloadStatus==info.DOWNLOAD_PRE){
            versionUpdate.onDownloadNewVersionPre(mHelper.getNewVersion(), info.fileSize, info.downloadPath);
        }else if(info.downloadStatus==info.DOWNLOAD){
            versionUpdate.onDownloadNewVersion(mHelper.getNewVersion(), info.fileSize, info.downloadSize, info.downloadPath);
        }else if(info.downloadStatus==info.DOWNLOAD_SUCCESS){
            //文件IO流尚未关闭，不能进行回调
        }else if(info.downloadStatus==info.DOWNLOAD_FAILD){
            versionUpdate.onDownloadFaild(-1);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s!=null){
            OnVersionUpdate versionUpdate=mHelper.getBuilder().getOnVersionUpdate();
            versionUpdate.onInstallAppPre(mHelper.getNewVersion(), mHelper.getAppVersin(), s);
        }
    }

    private boolean httpDownload(String url, String filePath){
        InputStream in=null;
        FileOutputStream out=null;
        FileDownloadInfo info=new FileDownloadInfo();
        long fileSize=0;
        long downloadSize=0;
        boolean isSuccess=false;
        try {
            URL url1=new URL(url);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url1.openConnection();
            httpURLConnection.setConnectTimeout(mHelper.getBuilder().getHttpBuilder().getTimeOut());
            httpURLConnection.setRequestMethod(HttpAsyncTask.Method.Get.toString(HttpAsyncTask.Method.Get));
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==200){
                fileSize=httpURLConnection.getContentLength();
                byte[] bs=new byte[DOWNLOAD_BYTES_LENGTH];
                int readSize=0;
                in=httpURLConnection.getInputStream();

                //开始进行下载
                info=new FileDownloadInfo(fileSize, downloadSize, filePath, info.DOWNLOAD_PRE);
                publishProgress(info);

                File downloadFile=new File(filePath);
                if(!downloadFile.getParentFile().exists())downloadFile.getParentFile().mkdirs();
                if(downloadFile.exists())downloadFile.delete();
                downloadFile.createNewFile();
                out=new FileOutputStream(downloadFile);
                while((readSize=in.read(bs))>0){
                    out.write(bs, 0, readSize);
                    downloadSize+=readSize;
                    info=new FileDownloadInfo(fileSize, downloadSize, filePath, info.DOWNLOAD);
                    publishProgress(info);
                }

                isSuccess=true;
                info=new FileDownloadInfo(fileSize, downloadSize, filePath, info.DOWNLOAD_SUCCESS);
                publishProgress(info);
            }
        } catch (Exception e) {
            info=new FileDownloadInfo(0, 0, filePath, info.DOWNLOAD_FAILD);
            publishProgress(info);
            e.printStackTrace();
        }finally {
            if(out!=null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    public class FileDownloadInfo{

        public final int DOWNLOAD_PRE=0;
        public final int DOWNLOAD =1;
        public final int DOWNLOAD_FAILD=2;
        public final int DOWNLOAD_SUCCESS=3;

        private long fileSize;
        private long downloadSize;
        private int downloadStatus;
        private String downloadPath;

        public FileDownloadInfo() {
        }

        public FileDownloadInfo(long fileSize, long downloadSize, String downloadPath, int downloadStatus) {
            this.fileSize = fileSize;
            this.downloadSize = downloadSize;
            this.downloadStatus = downloadStatus;
            this.downloadPath=downloadPath;
        }
    }
}
