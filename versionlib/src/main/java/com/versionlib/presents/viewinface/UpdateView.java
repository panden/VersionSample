package com.versionlib.presents.viewinface;

/**
 * Created by siwei.zhao on 2017/7/28.
 */

public interface UpdateView {

    /**检查新版本*/
    void checkNewVersion();

    /**在弹窗的时候进行版本更新*/
    void onDialogUpdate();

    /**在弹窗的时候取消版本更新*/
    void onDialogCancleUpdate();

    /**取消版本更新*/
    void cancleUpdate();
}
