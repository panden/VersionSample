package com.versionlib.modal;

import android.content.ContentValues;

import com.versionlib.modal.http.HttpAsyncTask;

/**
 * Created by siwei.zhao on 2017/8/9.
 */

public class HttpBuilder {

    private String mUrl;
    private ContentValues mValues;
    private int mTimeOut=15*1000;
    private HttpAsyncTask.Method mMethod;

    public HttpBuilder setUrl(String url) {
        mUrl = url;
        return this;
    }

    public HttpBuilder setValues(ContentValues values) {
        mValues = values;
        return this;
    }

    public HttpBuilder setTimeOut(int timeOut) {
        mTimeOut = timeOut;
        return this;
    }

    public HttpBuilder setMethod(HttpAsyncTask.Method method) {
        mMethod = method;
        return this;
    }

    public String getUrl() {
        return mUrl;
    }

    public ContentValues getValues() {
        return mValues;
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public HttpAsyncTask.Method getMethod() {
        return mMethod;
    }
}
