package com.litewaveinc.litewave.services;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by davidanderson on 10/10/15.
 */
public interface IAPIResponse {
    void success(JSONArray content);
    void failure(JSONArray content);
}
