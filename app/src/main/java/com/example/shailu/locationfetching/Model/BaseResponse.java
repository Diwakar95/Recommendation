package com.example.shailu.locationfetching.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by shailu on 5/5/16.
 */
public class BaseResponse {

    public Integer status;

    public String message;

    public String getJson() {
        Gson gsonObj = new GsonBuilder().serializeNulls().create();
        return gsonObj.toJson(this);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
