package com.versionlib.modal;

/**
 * Created by siwei.zhao on 2017/7/28.
 * 新版本信息
 */

public class NewVersionInfo extends VersinInfo {

    private boolean mustUpdate;//必须更新
    private String downloadUrl;//新版下载地址
    private String updateContent;//更新内容
    private String md5;//新版本文件的md5，检查文件是否正确

    public boolean isMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(boolean mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
