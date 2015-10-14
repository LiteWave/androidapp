package com.litewaveinc.litewave.services;

import android.content.Context;

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

    public static void getEvents(Context context, IAPIResponse response) {
        API.get("clients/5260316cbf80240000000001/events", context, response);
    }

    public static void getEvent(String eventID, Context context, IAPIResponse response) {
        API.get("clients/5260316cbf80240000000001/events/" + eventID, context, response);
    }

    private static void get(String url, Context context, IAPIResponse response) {
        API.request(url, "GET", new RequestParams(), context, response);
    }

    private static void post(String url, RequestParams params, Context context, IAPIResponse response) {
        API.request(url, "POST", params, context, response);
    }

    private static void put(String url, RequestParams params, Context context, IAPIResponse response) {
        API.request(url, "PUT", params, context, response);
    }

    private static void delete(String url, Context context, IAPIResponse response) {
        API.request(url, "DELETE", new RequestParams(), context, response);
    }

    private static void request(String url, String method, RequestParams params, Context context, final IAPIResponse apiResponse) {

        StringBuilder apiURL = new StringBuilder();
        apiURL.append(context.getResources().getString(R.string.apiURL));
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
