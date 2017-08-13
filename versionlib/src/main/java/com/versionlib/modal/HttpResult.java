package com.versionlib.modal;

/**
 * Created by siwei.zhao on 2017/8/9.
 */

public class HttpResult {

    public static final int HTTP_STATUS_SUCCESS=1;
    public static final int HTTP_STATUS_FAILD=2;
    public static final int HTTP_STATUS_ERROR=3;

    private String result;
    private int stausCode;
    private int faildCode;

    public int getFaildCode() {
        return faildCode;
    }

    public void setFaildCode(int faildCode) {
        this.faildCode = faildCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStausCode() {
        return stausCode;
    }

    public void setStausCode(int stausCode) {
        this.stausCode = stausCode;
    }

    public HttpResult(String result, int stausCode) {
        this.result = result;
        this.stausCode = stausCode;
    }

    public HttpResult() {
    }
}
