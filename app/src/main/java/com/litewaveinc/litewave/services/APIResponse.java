package com.litewaveinc.litewave.services;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by davidanderson on 10/10/15.
 */
public class APIResponse implements IAPIResponse {
    public void success(JSONArray content) {

    }

    public void failure(JSONArray content) {
        System.out.println("An error has occured: " + content.toString());
    }
}
