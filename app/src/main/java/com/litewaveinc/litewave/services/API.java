package com.litewaveinc.litewave.services;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import com.litewaveinc.litewave.R;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jonathan on 10/10/15.
 */


public final class API {

    static protected Context appContext;

    public static void init(Context context) {
        API.appContext = context;
    }

    public static void getEvents(IAPIResponse response) {
        API.get("clients/5260316cbf80240000000001/events", response);
    }

    public static void getEvent(String eventID, Context context, IAPIResponse response) {
        API.get("clients/5260316cbf80240000000001/events/" + eventID, response);
    }

    public static void getLevels(String stadiumID, Context context, IAPIResponse response) {
        API.get(context.getResources().getString(R.string.getStadiumLevels).replaceFirst("\\[stadiumID\\]", stadiumID), response);
    }

    private static void get(String url, IAPIResponse response) {
        API.request(url, "GET", new RequestParams(), response);
    }

    private static void post(String url, RequestParams params, IAPIResponse response) {
        API.request(url, "POST", params, response);
    }

    private static void put(String url, RequestParams params, IAPIResponse response) {
        API.request(url, "PUT", params, response);
    }

    private static void delete(String url, IAPIResponse response) {
        API.request(url, "DELETE", new RequestParams(), response);
    }

    private static void request(String url, String method, RequestParams params, final IAPIResponse apiResponse) {
        if (appContext == null) {
            Log.d("Debug", "Need to call init first.");
            throw new UnsupportedOperationException();
        }

        StringBuilder apiURL = new StringBuilder();
        apiURL.append(appContext.getString(R.string.apiURL));
        apiURL.append(url);

        ResponseHandlerInterface responseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                apiResponse.success(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                apiResponse.failure(errorResponse);
            }
        };

        AsyncHttpClient client = new AsyncHttpClient();
        switch (method) {
            case "GET":
                client.get(apiURL.toString(), params, responseHandler);
                break;
            case "POST":
                client.post(apiURL.toString(), params, responseHandler);
                break;
            case "PUT":
                client.put(apiURL.toString(), params, responseHandler);
                break;
            case "DELETE":
                client.delete(apiURL.toString(), responseHandler);
                break;

        }
    }
}
